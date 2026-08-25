import { getToken, goToLogin } from "./auth.js";

async function parseBody(res) {
    const contentType = res.headers.get("content-type") || "";
    if (contentType.includes("application/json")) {
        try {
            return await res.json();
        } catch {
            return null;
        }
    }
    return await res.text();
}

function messageFromErrorBody(body) {
    if (body && typeof body === "object") {
        if (body.message) return body.message;
        if (Array.isArray(body.errors) && body.errors.length > 0) return body.errors.join(", ");
    }
    return null;
}

// ---- Public (unauthenticated) requests — login & register ----
export async function publicFetch(path, { method = "GET", body } = {}) {
    const res = await fetch(path, {
        method,
        headers: body ? { "Content-Type": "application/json" } : undefined,
        body: body ? JSON.stringify(body) : undefined,
    });
    const parsed = await parseBody(res);
    if (!res.ok) {
        throw new Error(messageFromErrorBody(parsed) || `HTTP ${res.status} ${res.statusText}`);
    }
    return parsed;
}

// ---- Authenticated requests — everything behind the Bearer token ----
export async function apiFetch(path, { method = "GET", body } = {}) {
    const token = getToken();
    const headers = { Authorization: `Bearer ${token}` };
    if (body !== undefined) headers["Content-Type"] = "application/json";

    const res = await fetch(path, {
        method,
        headers,
        body: body !== undefined ? JSON.stringify(body) : undefined,
    });

    if (res.status === 401) {
        goToLogin();
        throw new Error("Sessionen har gått ut. Logga in igen.");
    }

    const parsed = await parseBody(res);
    if (!res.ok) {
        throw new Error(messageFromErrorBody(parsed) || `HTTP ${res.status} ${res.statusText}`);
    }
    return parsed;
}

// ================= Auth =================
export const login = (username, password) =>
    publicFetch("/auth/login", { method: "POST", body: { username, password } });

export const register = (payload) =>
    publicFetch("/auth/register", { method: "POST", body: payload });

export const getCurrentUser = () => apiFetch("/auth/me");

// ================= Reference data =================
export const getRoles = () => publicFetch("/roles");
export const getDepartments = () => apiFetch("/departments");
export const getDepartmentsPublic = () => publicFetch("/departments");
export const getStaff = () => apiFetch("/staff");
export const getStaffEmployments = () => apiFetch("/staff-employments");

// ================= Patients =================
export const getPatients = () => apiFetch("/patients");
export const getPatientById = (id) => apiFetch(`/patients/${id}`);
export const getPatientByPersonalNumber = (pn) => apiFetch(`/patients/personal-number/${encodeURIComponent(pn)}`);
export const getPatientDetails = (id) => apiFetch(`/patients/${id}/details`);
export const getPatientDashboard = (id) => apiFetch(`/patients/${id}/dashboard`);
export const createPatient = (payload) => apiFetch("/patients", { method: "POST", body: payload });

export const getPatientMedical = (patientId) => apiFetch(`/patient-medicals/${patientId}`);
export const updatePatientContact = (patientId, payload) =>
    apiFetch(`/patient-contacts/${patientId}`, { method: "PUT", body: payload });

// ================= Appointments =================
export const getAppointments = () => apiFetch("/appointments");
export const getScheduleFor = (staffId, date) =>
    apiFetch(`/appointments/schedule?${new URLSearchParams({ staffId: String(staffId), date })}`);
export const createAppointment = (payload) => apiFetch("/appointments", { method: "POST", body: payload });

// ================= Prescriptions =================
export const getPrescriptions = () => apiFetch("/prescriptions");
export const getPrescriptionsByPersonalNumber = (pn) =>
    apiFetch(`/prescriptions/personal-number/${encodeURIComponent(pn)}`);
export const createPrescription = (payload) => apiFetch("/prescriptions", { method: "POST", body: payload });

// ================= Referrals =================
export const getReferrals = () => apiFetch("/referrals");
export const createReferral = (payload) => apiFetch("/referrals", { method: "POST", body: payload });

// ================= Journal entries (US-13, US-22) =================
export const createJournalEntry = (payload) => apiFetch("/journalentry", { method: "POST", body: payload });

// ================= Diagnoses & measures =================
export const getDiagnoses = () => apiFetch("/diagnosis");
export const createDiagnosis = (payload) => apiFetch("/diagnosis", { method: "POST", body: payload });

export const getMeasures = () => apiFetch("/measure");
export const createMeasure = (payload) => apiFetch("/measure", { method: "POST", body: payload });

// ================= Audit log =================
export const getAuditLogs = () => apiFetch("/audit-logs");
