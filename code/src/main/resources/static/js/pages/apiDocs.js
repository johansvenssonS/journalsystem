// Static endpoint registry — hand-authored from the controller source, since these routes are
// stable and there is no live introspection endpoint to build this from.

import { setTopbar } from "../ui.js";

const GROUPS = [
    {
        name: "Autentisering",
        endpoints: [
            { method: "POST", path: "/auth/register", role: null, desc: "Registrera nytt användarkonto kopplat till en personalprofil." },
            { method: "POST", path: "/auth/login", role: null, desc: "Logga in, returnerar en JWT." },
            { method: "GET", path: "/auth/me", role: "Inloggad", desc: "Hämta den inloggade användarens profil, roll och avdelning." },
        ],
    },
    {
        name: "Patienter",
        endpoints: [
            { method: "GET", path: "/patients", role: "Inloggad", desc: "Lista alla patienter." },
            { method: "GET", path: "/patients/{id}", role: "Inloggad", desc: "Hämta en patient via ID." },
            { method: "GET", path: "/patients/personal-number/{pn}", role: "Inloggad", desc: "Hämta en patient via personnummer." },
            { method: "GET", path: "/patients/{id}/details", role: "Inloggad", desc: "Patient inkl. kontaktuppgifter." },
            { method: "GET", path: "/patients/{id}/dashboard", role: "Inloggad", desc: "Vårdkontakter, journalposter och diagnoser för en patient." },
            { method: "POST", path: "/patients", role: "RECEPTIONIST", desc: "Registrera en ny patient." },
            { method: "GET", path: "/patient-contacts", role: "Inloggad", desc: "Lista kontaktuppgifter för alla patienter." },
            { method: "GET", path: "/patient-contacts/{id}", role: "Inloggad", desc: "Kontaktuppgifter för en patient." },
            { method: "PUT", path: "/patient-contacts/{id}", role: "Inloggad", desc: "Uppdatera en patients kontaktuppgifter." },
            { method: "GET", path: "/patient-medicals", role: "Inloggad", desc: "Lista medicinsk info (blodgrupp, allergier) för alla patienter." },
            { method: "GET", path: "/patient-medicals/{id}", role: "Inloggad", desc: "Medicinsk info för en patient." },
        ],
    },
    {
        name: "Journal, diagnoser & åtgärder",
        endpoints: [
            { method: "GET", path: "/journalentry", role: "Inloggad", desc: "Lista alla journalposter." },
            { method: "GET", path: "/journalentry/{id}", role: "Inloggad", desc: "Hämta en journalpost." },
            { method: "GET", path: "/diagnosis", role: "Inloggad", desc: "Lista alla diagnoser." },
            { method: "GET", path: "/diagnosis/{id}", role: "Inloggad", desc: "Hämta en diagnos." },
            { method: "POST", path: "/diagnosis", role: "doctor", desc: "Ställ en diagnos (ICD-10) kopplad till en journalpost." },
            { method: "GET", path: "/measure", role: "Inloggad", desc: "Lista alla åtgärder." },
            { method: "GET", path: "/measure/{id}", role: "Inloggad", desc: "Hämta en åtgärd." },
            { method: "POST", path: "/measure", role: "doctor, nurse", desc: "Registrera en utförd åtgärd kopplad till en journalpost." },
            { method: "GET", path: "/vårdkontakter", role: "Inloggad", desc: "Lista alla vårdkontakter." },
            { method: "GET", path: "/vårdkontakter/{id}", role: "Inloggad", desc: "Hämta en vårdkontakt." },
        ],
    },
    {
        name: "Recept",
        endpoints: [
            { method: "GET", path: "/prescriptions", role: "Inloggad", desc: "Lista alla recept." },
            { method: "GET", path: "/prescriptions/{id}", role: "Inloggad", desc: "Hämta ett recept." },
            { method: "GET", path: "/prescriptions/personal-number/{pn}", role: "pharmacy", desc: "Aktiva recept för en patient — apotekets integration (US-61)." },
            { method: "POST", path: "/prescriptions", role: "doctor", desc: "Skriv ut ett nytt recept." },
        ],
    },
    {
        name: "Remisser",
        endpoints: [
            { method: "GET", path: "/referrals", role: "Inloggad", desc: "Lista alla remisser." },
            { method: "GET", path: "/referrals/{id}", role: "Inloggad", desc: "Hämta en remiss." },
            { method: "POST", path: "/referrals", role: "doctor", desc: "Skicka en remiss från en avdelning till en annan." },
        ],
    },
    {
        name: "Bokningar",
        endpoints: [
            { method: "GET", path: "/appointments", role: "Inloggad", desc: "Lista alla bokningar." },
            { method: "GET", path: "/appointments/{id}", role: "Inloggad", desc: "Hämta en bokning." },
            { method: "GET", path: "/appointments/schedule?staffId=&date=", role: "Inloggad", desc: "Schema för en specifik personal en given dag." },
            { method: "POST", path: "/appointments", role: "RECEPTIONIST", desc: "Boka in en patient hos personal på en avdelning." },
        ],
    },
    {
        name: "Personal & organisation",
        endpoints: [
            { method: "GET", path: "/staff", role: "Inloggad", desc: "Lista all personal." },
            { method: "GET", path: "/staff/{id}", role: "Inloggad", desc: "Hämta en personalprofil." },
            { method: "GET", path: "/staff-contacts", role: "Inloggad", desc: "Kontaktuppgifter för all personal." },
            { method: "GET", path: "/staff-contacts/{id}", role: "Inloggad", desc: "Kontaktuppgifter för en personal." },
            { method: "GET", path: "/staff-employments", role: "Inloggad", desc: "Anställningar — kopplar personal till roll och avdelning." },
            { method: "GET", path: "/staff-employments/{id}", role: "Inloggad", desc: "Hämta en specifik anställning." },
            { method: "GET", path: "/departments", role: "Publik (GET)", desc: "Lista alla avdelningar." },
            { method: "GET", path: "/departments/{id}", role: "Inloggad", desc: "Hämta en avdelning." },
            { method: "GET", path: "/specializations", role: "Inloggad", desc: "Lista specialiseringar." },
            { method: "GET", path: "/specializations/{id}", role: "Inloggad", desc: "Hämta en specialisering." },
            { method: "GET", path: "/roles", role: "Publik (GET)", desc: "Lista tillgängliga yrkesroller — används av registreringsformuläret." },
            { method: "GET", path: "/roles/{id}", role: "Inloggad", desc: "Hämta en roll." },
        ],
    },
    {
        name: "Granskning & övrigt",
        endpoints: [
            { method: "GET", path: "/audit-logs", role: "RECEPTIONIST", desc: "Logg över åtkomst och ändringar av journaldata (US-50, US-53)." },
            { method: "GET", path: "/audit-logs/{id}", role: "RECEPTIONIST", desc: "Hämta en specifik loggpost." },
            { method: "GET", path: "/audit-logs/patient/{patientId}", role: "RECEPTIONIST", desc: "Loggposter för en specifik patient (US-53)." },
            { method: "GET", path: "/entities", role: "Inloggad", desc: "Interna entitetsreferenser." },
            { method: "GET", path: "/entities/{id}", role: "Inloggad", desc: "Hämta en entitetsreferens." },
        ],
    },
];

export async function render(container) {
    setTopbar("API-dokumentation", "Snabbreferens över journalsystemets backend-endpoints");

    container.innerHTML = `
        <div class="card mb-4">
            <input type="text" id="apiDocsSearch" class="form-control" placeholder="Sök på path eller beskrivning...">
        </div>

        <div id="apiDocsGroups"></div>
    `;

    const groupsEl = document.getElementById("apiDocsGroups");
    renderGroups(groupsEl, "");

    document.getElementById("apiDocsSearch").addEventListener("input", (e) => {
        renderGroups(groupsEl, e.target.value.trim().toLowerCase());
    });
}

function renderGroups(el, query) {
    const filtered = GROUPS.map((g) => ({
        ...g,
        endpoints: g.endpoints.filter((ep) =>
            !query || ep.path.toLowerCase().includes(query) || ep.desc.toLowerCase().includes(query)
        ),
    })).filter((g) => g.endpoints.length > 0);

    if (filtered.length === 0) {
        el.innerHTML = `<div class="card"><p class="text-muted">Inga endpoints matchade sökningen.</p></div>`;
        return;
    }

    el.innerHTML = filtered.map((g) => `
        <div class="card mb-4">
            <div class="card-header"><h3>${g.name}</h3></div>
            <div class="endpoint-list">
                ${g.endpoints.map(endpointCard).join("")}
            </div>
        </div>
    `).join("");
}

function endpointCard(ep) {
    return `
        <div class="endpoint-card">
            <span class="method-badge method-${ep.method}">${ep.method}</span>
            <div class="endpoint-info">
                <code class="endpoint-path">${ep.path}</code>
                <p class="endpoint-desc">${ep.desc}</p>
            </div>
            ${ep.role ? `<span class="endpoint-role">${ep.role}</span>` : ""}
        </div>
    `;
}
