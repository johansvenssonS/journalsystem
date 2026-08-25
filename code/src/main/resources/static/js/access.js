// Central role/permission source of truth. Every page and the sidebar nav reads from here
// instead of sprinkling `me.role === "..."` checks around — keeps role logic in one place.

export const ROLES = {
    DOCTOR: "doctor",
    NURSE: "nurse",
    ASSISTANT_NURSE: "assistant_nurse",
    RECEPTIONIST: "RECEPTIONIST",
    PHARMACY: "pharmacy",
};

// RECEPTIONIST doubles as the admin-equivalent role in this system — there is no dedicated
// ADMIN role in the backend, and audit/API-docs access has been assigned to reception.
export const NAV_CONFIG = [
    { id: "dashboard", label: "Startsida", icon: "⌂", roles: null },
    { id: "patients", label: "Patienter", icon: "☺", roles: [ROLES.DOCTOR, ROLES.NURSE, ROLES.ASSISTANT_NURSE, ROLES.RECEPTIONIST] },
    { id: "prescriptions", label: "Recept", icon: "℞", roles: [ROLES.DOCTOR, ROLES.PHARMACY] },
    { id: "referrals", label: "Remisser", icon: "→", roles: [ROLES.DOCTOR] },
    { id: "appointments", label: "Bokningar", icon: "🗓", roles: [ROLES.DOCTOR, ROLES.NURSE, ROLES.ASSISTANT_NURSE, ROLES.RECEPTIONIST] },
    { id: "directory", label: "Personal & avdelningar", icon: "☰", roles: null },
    { id: "audit", label: "Granskningslogg", icon: "◷", roles: [ROLES.RECEPTIONIST] },
    { id: "api-docs", label: "API-dokumentation", icon: "⌘", roles: [ROLES.RECEPTIONIST] },
];

// null roles list = every authenticated role may see it.
export function canAccessPage(me, pageId) {
    const entry = NAV_CONFIG.find((p) => p.id === pageId);
    if (!entry) return false;
    if (!entry.roles) return true;
    return entry.roles.includes(me?.role);
}

export function navForRole(me) {
    return NAV_CONFIG.filter((entry) => !entry.roles || entry.roles.includes(me?.role));
}

export function firstAllowedPage(me) {
    const nav = navForRole(me);
    return nav.length ? nav[0].id : null;
}

// ---- Fine-grained capability checks, reused by individual pages ----
export const can = {
    createPatient: (me) => me?.role === ROLES.RECEPTIONIST,
    editPatientContact: (me) => me?.role === ROLES.RECEPTIONIST,
    viewPatientMedical: (me) => me?.role === ROLES.DOCTOR || me?.role === ROLES.NURSE,
    editPatientMedical: (me) => me?.role === ROLES.DOCTOR || me?.role === ROLES.NURSE,
    viewPatientJournal: (me) => me?.role === ROLES.DOCTOR || me?.role === ROLES.NURSE,
    viewCareContacts: (me) => me?.role === ROLES.DOCTOR || me?.role === ROLES.NURSE || me?.role === ROLES.ASSISTANT_NURSE,
    // US-13/US-22 — doctors may create any entry type, nurses only "note" (enforced again server-side).
    createJournalEntry: (me) => me?.role === ROLES.DOCTOR || me?.role === ROLES.NURSE,
    diagnose: (me) => me?.role === ROLES.DOCTOR,
    registerMeasure: (me) => me?.role === ROLES.DOCTOR || me?.role === ROLES.NURSE,
    createPrescription: (me) => me?.role === ROLES.DOCTOR,
    lookupPrescription: (me) => me?.role === ROLES.PHARMACY,
    createReferral: (me) => me?.role === ROLES.DOCTOR,
    // US-62 — samma roll som får skicka en remiss får ta emot/svara på en.
    respondReferral: (me) => me?.role === ROLES.DOCTOR,
    createAppointment: (me) => me?.role === ROLES.RECEPTIONIST,
    viewAllAppointments: (me) => me?.role === ROLES.RECEPTIONIST,
    // US-12 — receptionisten (admin-rollen i systemet) skapar vårdkontakten vid inskrivning.
    createCareContact: (me) => me?.role === ROLES.RECEPTIONIST,
    // Läkaren lägger in patienten från en planerad vårdkontakt.
    admitCareContact: (me) => me?.role === ROLES.DOCTOR,
    // US-17 — läkare skriver ut patienten.
    dischargeCareContact: (me) => me?.role === ROLES.DOCTOR,
    // US-20 — vårdpersonal ser vilka patienter som är inskrivna på en avdelning.
    viewDepartmentPatients: (me) => me?.role === ROLES.DOCTOR || me?.role === ROLES.NURSE,
};
