import {
    getPatients, getPatientById, getPatientByPersonalNumber, createPatient,
    getPatientDetails, getPatientDashboard, getPatientMedical, updatePatientContact, updatePatientMedical,
    getMeasures, createDiagnosis, createMeasure, createJournalEntry,
    getDepartments, getStaff, getStaffEmployments, getRoles, createCareContact, admitCareContact, dischargeCareContact, getCurrentUser,
    getStaffPatients,
} from "../api.js";
import { loadCurrentUser } from "../auth.js";
import {
    safe, escapeHtml, formatDate, formatDateTime, toDateInputValue,
    loadingRow, emptyRow, errorRow, toastError, toastSuccess, setTopbar, roleLabel,
} from "../ui.js";
import { can, ROLES } from "../access.js";
import { OPEN_PATIENT_KEY } from "../app.js";

// Doctors/nurses only see patients they're actually responsible for (via a care
// contact), not the whole patient register — reception and assistant nurses still
// see everyone since they work across patients that aren't "theirs" specifically.
function isScopedToOwnPatients(me) {
    return me.role === ROLES.DOCTOR || me.role === ROLES.NURSE;
}

async function fetchMyPatients(me) {
    if (!me.staffId) return [];
    const rows = await getStaffPatients(me.staffId);
    return rows.map((p) => ({ id: p.patientId, firstName: p.firstName, lastName: p.lastName, personalNumber: p.personalNumber, deletedAt: null }));
}

async function isMyPatient(me, patientId) {
    if (!me.staffId) return false;
    try {
        const rows = await getStaffPatients(me.staffId);
        return rows.some((p) => String(p.patientId) === String(patientId));
    } catch {
        return false;
    }
}

export async function render(container) {
    const me = await loadCurrentUser(getCurrentUser);
    const scoped = isScopedToOwnPatients(me);

    const pendingId = sessionStorage.getItem(OPEN_PATIENT_KEY);
    if (pendingId) {
        sessionStorage.removeItem(OPEN_PATIENT_KEY);
        if (scoped && !(await isMyPatient(me, pendingId))) {
            await renderList(container, me);
            toastError(new Error("Patienten är inte kopplad till dig."));
            return;
        }
        await renderDetail(container, me, pendingId);
        return;
    }

    await renderList(container, me);
}

// ================= LIST VIEW =================

async function renderList(container, me) {
    const canCreate = can.createPatient(me);
    const scoped = isScopedToOwnPatients(me);
    setTopbar("Patienter", scoped ? "Dina patienter — sök och visa" : "Sök, visa och registrera patienter");

    container.innerHTML = `
        ${canCreate ? `
        <div class="card mb-4">
            <div class="card-header">
                <h3>Ny patient</h3>
                <span class="api-badge">POST /patients</span>
            </div>
            <div id="create-patient-alert" class="alert alert-error" hidden></div>
            <form id="createPatientForm">
                <div class="form-grid">
                    <div class="form-group">
                        <label for="patientFirstName">Förnamn</label>
                        <input type="text" id="patientFirstName" class="form-control" required>
                    </div>
                    <div class="form-group">
                        <label for="patientLastName">Efternamn</label>
                        <input type="text" id="patientLastName" class="form-control" required>
                    </div>
                    <div class="form-group full-width">
                        <label for="patientPersonalNumber">Personnummer</label>
                        <input type="text" id="patientPersonalNumber" class="form-control" placeholder="YYYYMMDD-XXXX" required>
                    </div>
                </div>
                <button type="submit" class="btn btn-primary">Spara patient</button>
            </form>
        </div>` : ""}

        <div class="card mb-4">
            <div class="card-header">
                <h3>Sök patient</h3>
                <span class="api-badge">GET /patients/{id} · /patients/personal-number/{pn}</span>
            </div>
            <div class="search-row">
                <input type="text" id="searchPatientInput" placeholder="Sök på patient-ID eller personnummer..." class="form-control flex-grow">
                <button id="searchPatientBtn" class="btn btn-primary">Sök</button>
                <button id="clearSearchBtn" class="btn btn-secondary">Visa alla</button>
            </div>
        </div>

        <div class="card">
            <div class="card-header">
                <h3>${scoped ? "Mina patienter" : "Alla patienter"}</h3>
                <span class="api-badge">${scoped ? "GET /staff/{id}/patients" : "GET /patients"}</span>
            </div>
            <div class="table-wrap">
                <table>
                    <thead><tr><th>Namn</th><th>Personnummer</th><th>ID</th><th>Status</th><th></th></tr></thead>
                    <tbody id="patients-tbody">${loadingRow(5)}</tbody>
                </table>
            </div>
        </div>
    `;

    loadAllPatients(me, scoped);

    document.getElementById("searchPatientBtn").addEventListener("click", () => handleSearch(me, container, scoped));
    document.getElementById("searchPatientInput").addEventListener("keydown", (e) => {
        if (e.key === "Enter") { e.preventDefault(); handleSearch(me, container, scoped); }
    });
    document.getElementById("clearSearchBtn").addEventListener("click", () => loadAllPatients(me, scoped));

    const createForm = document.getElementById("createPatientForm");
    if (createForm) createForm.addEventListener("submit", (e) => handleCreatePatient(e, me, container));
}

async function loadAllPatients(me, scoped) {
    const tbody = document.getElementById("patients-tbody");
    if (!tbody) return; // page navigated away before this reload landed
    tbody.innerHTML = loadingRow(5);
    try {
        const patients = scoped ? await fetchMyPatients(me) : await getPatients();
        if (!patients || patients.length === 0) {
            tbody.innerHTML = emptyRow(5, scoped ? "Inga patienter kopplade till dig ännu." : "Inga patienter hittades.");
            return;
        }
        tbody.innerHTML = patients.map(patientRow).join("");
        attachRowHandlers();
    } catch (err) {
        tbody.innerHTML = errorRow(5, err);
    }
}

function patientRow(p) {
    const status = p.deletedAt ? '<span class="badge badge-gray">Inaktiv</span>' : '<span class="badge badge-green">Aktiv</span>';
    return `
        <tr class="clickable-row" data-id="${p.id}">
            <td><strong>${safe(p.firstName)} ${safe(p.lastName)}</strong></td>
            <td>${safe(p.personalNumber)}</td>
            <td>#${safe(p.id)}</td>
            <td>${status}</td>
            <td class="text-right"><button type="button" class="action-link">Öppna →</button></td>
        </tr>`;
}

function attachRowHandlers() {
    document.querySelectorAll("#patients-tbody tr.clickable-row").forEach((row) => {
        row.addEventListener("click", () => openPatientFromRow(row.dataset.id));
    });
}

async function openPatientFromRow(id) {
    const me = await loadCurrentUser(getCurrentUser);
    renderDetail(document.getElementById("content"), me, id);
}

async function handleSearch(me, container, scoped) {
    const raw = document.getElementById("searchPatientInput").value.trim();
    const tbody = document.getElementById("patients-tbody");
    if (!raw) { loadAllPatients(me, scoped); return; }

    tbody.innerHTML = loadingRow(5, "Söker...");
    try {
        const looksLikeId = /^\d{1,7}$/.test(raw);
        const patient = looksLikeId ? await getPatientById(raw) : await getPatientByPersonalNumber(raw);
        if (scoped && !(await isMyPatient(me, patient.id))) {
            tbody.innerHTML = emptyRow(5, "Ingen patient hittades för ”" + raw + "”.");
            return;
        }
        tbody.innerHTML = patientRow(patient);
        attachRowHandlers();
    } catch (err) {
        tbody.innerHTML = emptyRow(5, "Ingen patient hittades för ”" + raw + "”.");
    }
}

async function handleCreatePatient(e, me, container) {
    e.preventDefault();
    const alertBox = document.getElementById("create-patient-alert");
    alertBox.hidden = true;

    const payload = {
        firstName: document.getElementById("patientFirstName").value.trim(),
        lastName: document.getElementById("patientLastName").value.trim(),
        personalNumber: document.getElementById("patientPersonalNumber").value.trim(),
    };

    try {
        await createPatient(payload);
        document.getElementById("createPatientForm").reset();
        toastSuccess("Patienten skapades.");
        loadAllPatients();
    } catch (err) {
        alertBox.textContent = err.message;
        alertBox.hidden = false;
    }
}

// ================= DETAIL VIEW =================

export async function renderDetail(container, me, patientId, opts = {}) {
    container.innerHTML = `<p class="text-muted">Laddar patient...</p>`;

    let details, dashboard, medical;
    try {
        [details, dashboard, medical] = await Promise.all([
            getPatientDetails(patientId),
            getPatientDashboard(patientId),
            getPatientMedical(patientId).catch(() => null),
        ]);
    } catch (err) {
        container.innerHTML = `<div class="alert alert-error">Kunde inte hämta patienten: ${err.message}</div>`;
        return;
    }

    const canEditContact = can.editPatientContact(me);
    const canDiagnose = can.diagnose(me);
    const canMeasure = can.registerMeasure(me);
    const canCreateJournal = can.createJournalEntry(me);
    const canCreateCareContact = can.createCareContact(me);
    const canAdmitCareContact = can.admitCareContact(me);
    const canDischargeCareContact = can.dischargeCareContact(me);
    const showMedical = can.viewPatientMedical(me);
    const canEditMedical = can.editPatientMedical(me);
    const showJournal = can.viewPatientJournal(me);
    // Receptionist can't otherwise see care contacts, but still needs this tab to create one (US-12).
    const showCare = can.viewCareContacts(me) || canCreateCareContact;

    let departments = [];
    let staffByDept = new Map(); // departmentId -> [{ id, label }] — only staff actually employed there
    if (canCreateCareContact) {
        try {
            const [depts, staffList, employments, roles] = await Promise.all([
                getDepartments(), getStaff(), getStaffEmployments(), getRoles(),
            ]);
            departments = depts;

            const staffById = new Map(staffList.map((s) => [s.id, s]));
            const roleLabelById = new Map(roles.map((r) => [r.id, roleLabel(r.title.replace(/^ROLE_/, ""))]));

            employments.forEach((emp) => {
                const staff = staffById.get(emp.staffId);
                if (!staff) return;
                if (!staffByDept.has(emp.departmentId)) staffByDept.set(emp.departmentId, []);
                staffByDept.get(emp.departmentId).push({
                    id: staff.id,
                    label: `${escapeHtml(staff.firstName)} ${escapeHtml(staff.lastName)} (#${staff.id}) — ${escapeHtml(roleLabelById.get(emp.roleId) || "Okänd roll")}`,
                });
            });
        } catch {
            // form below falls back to empty selects — submit will simply fail with a clear backend error
        }
    }

    const tabs = [
        { id: "contact", label: "Kontaktuppgifter", show: true },
        { id: "medical", label: "Medicinskt", show: showMedical },
        { id: "care", label: "Vårdkontakter", show: showCare },
        { id: "journal", label: "Journal & diagnoser", show: showJournal },
    ].filter((t) => t.show);

    const defaultTab = opts.defaultTab && tabs.some((t) => t.id === opts.defaultTab) ? opts.defaultTab : tabs[0].id;

    setTopbar(`${safe(details.firstName)} ${safe(details.lastName)}`, `#${safe(details.id)} · ${safe(details.personalNumber)}${details.deletedAt ? " · Inaktiv" : ""}`);

    container.innerHTML = `
        <button type="button" class="back-link" id="backToList">${opts.backLabel || "← Tillbaka till patienter"}</button>

        <div class="tabs">
            ${tabs.map((t) => `<button class="tab-btn ${t.id === defaultTab ? "active" : ""}" data-tab="${t.id}">${t.label}</button>`).join("")}
        </div>

        <div class="tab-panel ${defaultTab === "contact" ? "active" : ""}" id="tab-contact">
            <div class="card">
                <div class="card-header">
                    <h3>Kontaktuppgifter</h3>
                    <span class="api-badge">GET /patients/{id}/details · PUT /patient-contacts/{id}</span>
                </div>
                ${canEditContact ? contactEditForm(details) : contactReadOnly(details)}
            </div>
        </div>

        ${showMedical ? `
        <div class="tab-panel ${defaultTab === "medical" ? "active" : ""}" id="tab-medical">
            <div class="card">
                <div class="card-header">
                    <h3>Medicinsk information</h3>
                    <span class="api-badge">GET /patient-medicals/{id}${canEditMedical ? " · PUT /patient-medicals/{id}" : ""}</span>
                </div>
                ${canEditMedical ? medicalEditForm(medical) : medicalReadOnly(medical)}
            </div>
        </div>` : ""}

        ${showCare ? `
        <div class="tab-panel ${defaultTab === "care" ? "active" : ""}" id="tab-care">
            ${canCreateCareContact ? `
            <div class="card mb-4">
                <div class="card-header">
                    <h3>Ny vårdkontakt</h3>
                    <span class="api-badge">POST /care-contacts</span>
                </div>
                ${careContactForm(departments, staffByDept)}
            </div>` : ""}
            <div class="card">
                <div class="card-header">
                    <h3>Vårdkontakter</h3>
                    <span class="api-badge">GET /patients/{id}/dashboard · PATCH /care-contacts/{id}/admitted · PATCH /care-contacts/{id}/discharged</span>
                </div>
                ${renderCareContacts(dashboard.careContacts, canAdmitCareContact, canDischargeCareContact)}
            </div>
        </div>` : ""}

        ${showJournal ? `
        <div class="tab-panel ${defaultTab === "journal" ? "active" : ""}" id="tab-journal">
            <div class="card mb-4">
                <div class="card-header">
                    <h3>Ny journalpost</h3>
                    <span class="api-badge">POST /journalentry</span>
                </div>
                ${journalEntryForm(dashboard.careContacts, canCreateJournal, me.role)}
            </div>
            <div class="card">
                <div class="card-header">
                    <h3>Journalanteckningar, diagnoser &amp; åtgärder</h3>
                    <span class="api-badge">GET /patients/{id}/dashboard · POST /diagnosis · POST /measure</span>
                </div>
                <div id="journal-timeline">${loadingRow(1)}</div>
            </div>
        </div>` : ""}
    `;

    document.getElementById("backToList").addEventListener("click", () => {
        if (opts.onBack) opts.onBack();
        else renderList(container, me);
    });

    container.querySelectorAll(".tab-btn").forEach((btn) => {
        btn.addEventListener("click", () => {
            container.querySelectorAll(".tab-btn").forEach((b) => b.classList.remove("active"));
            container.querySelectorAll(".tab-panel").forEach((p) => p.classList.remove("active"));
            btn.classList.add("active");
            document.getElementById(`tab-${btn.dataset.tab}`).classList.add("active");
        });
    });

    if (canEditContact) {
        document.getElementById("contactEditForm").addEventListener("submit", (e) => handleUpdateContact(e, patientId));
    }

    if (canEditMedical) {
        document.getElementById("medicalEditForm").addEventListener("submit", (e) => handleUpdateMedical(e, patientId));
    }

    const journalForm = document.getElementById("createJournalEntryForm");
    if (journalForm) journalForm.addEventListener("submit", (e) => handleCreateJournalEntry(e, me, patientId));

    const careContactForm_ = document.getElementById("createCareContactForm");
    if (careContactForm_) careContactForm_.addEventListener("submit", (e) => handleCreateCareContact(e, me, patientId));

    const ccDepartmentSelect = document.getElementById("ccDepartment");
    if (ccDepartmentSelect) {
        ccDepartmentSelect.addEventListener("change", () => {
            document.getElementById("ccResponsible").innerHTML = staffOptionsForDept(staffByDept, ccDepartmentSelect.value);
        });
    }

    if (canAdmitCareContact) {
        container.querySelectorAll(".admit-btn").forEach((btn) => {
            btn.addEventListener("click", () => handleAdmit(btn.dataset.ccId, me, patientId));
        });
    }

    if (canDischargeCareContact) {
        container.querySelectorAll(".discharge-btn").forEach((btn) => {
            btn.addEventListener("click", () => handleDischarge(btn.dataset.ccId, me, patientId));
        });
    }

    if (showJournal) renderJournalTimeline(dashboard, patientId, me, canDiagnose, canMeasure);
}

function contactReadOnly(d) {
    return `
        <div class="form-grid">
            <div class="form-group"><label>Telefon</label><div>${safe(d.phone)}</div></div>
            <div class="form-group"><label>E-post</label><div>${safe(d.email)}</div></div>
            <div class="form-group full-width"><label>Adress</label><div>${safe(d.address)}</div></div>
            <div class="form-group"><label>Anhörig (namn)</label><div>${safe(d.emergencyName)}</div></div>
            <div class="form-group"><label>Anhörig (telefon)</label><div>${safe(d.emergencyPhone)}</div></div>
        </div>
        <p class="hint text-muted" style="margin-top:12px;">Endast receptionist kan uppdatera kontaktuppgifter.</p>
    `;
}

function contactEditForm(d) {
    return `
        <div id="contact-alert" class="alert alert-error" hidden></div>
        <form id="contactEditForm">
            <div class="form-grid">
                <div class="form-group">
                    <label for="cPhone">Telefon</label>
                    <input type="text" id="cPhone" class="form-control" value="${escapeHtml(d.phone || "")}">
                </div>
                <div class="form-group">
                    <label for="cEmail">E-post</label>
                    <input type="email" id="cEmail" class="form-control" value="${escapeHtml(d.email || "")}">
                </div>
                <div class="form-group full-width">
                    <label for="cAddress">Adress</label>
                    <input type="text" id="cAddress" class="form-control" value="${escapeHtml(d.address || "")}">
                </div>
                <div class="form-group">
                    <label for="cEmergencyName">Anhörig (namn)</label>
                    <input type="text" id="cEmergencyName" class="form-control" value="${escapeHtml(d.emergencyName || "")}">
                </div>
                <div class="form-group">
                    <label for="cEmergencyPhone">Anhörig (telefon)</label>
                    <input type="text" id="cEmergencyPhone" class="form-control" value="${escapeHtml(d.emergencyPhone || "")}">
                </div>
            </div>
            <button type="submit" class="btn btn-primary">Spara kontaktuppgifter</button>
        </form>
    `;
}

async function handleUpdateContact(e, patientId) {
    e.preventDefault();
    const alertBox = document.getElementById("contact-alert");
    alertBox.hidden = true;

    const payload = {
        phone: document.getElementById("cPhone").value.trim(),
        email: document.getElementById("cEmail").value.trim(),
        address: document.getElementById("cAddress").value.trim(),
        emergencyName: document.getElementById("cEmergencyName").value.trim(),
        emergencyPhone: document.getElementById("cEmergencyPhone").value.trim(),
    };

    try {
        await updatePatientContact(patientId, payload);
        toastSuccess("Kontaktuppgifter uppdaterade.");
    } catch (err) {
        alertBox.textContent = err.message;
        alertBox.hidden = false;
    }
}

function medicalReadOnly(m) {
    if (!m || (!m.bloodType && !m.allergies)) {
        return '<p class="text-muted">Ingen medicinsk information registrerad.</p>';
    }
    return `
        <div class="form-grid">
            <div class="form-group"><label>Blodgrupp</label><div>${safe(m.bloodType)}</div></div>
            <div class="form-group full-width"><label>Allergier</label><div>${safe(m.allergies)}</div></div>
        </div>
    `;
}

function medicalEditForm(m) {
    return `
        <div id="medical-alert" class="alert alert-error" hidden></div>
        <form id="medicalEditForm">
            <div class="form-grid">
                <div class="form-group">
                    <label for="mBloodType">Blodgrupp</label>
                    <input type="text" id="mBloodType" class="form-control" placeholder="t.ex. A+" value="${escapeHtml(m?.bloodType || "")}">
                </div>
                <div class="form-group full-width">
                    <label for="mAllergies">Allergier</label>
                    <input type="text" id="mAllergies" class="form-control" placeholder="t.ex. Penicillin" value="${escapeHtml(m?.allergies || "")}">
                </div>
            </div>
            <button type="submit" class="btn btn-primary">Spara medicinsk information</button>
        </form>
    `;
}

async function handleUpdateMedical(e, patientId) {
    e.preventDefault();
    const alertBox = document.getElementById("medical-alert");
    alertBox.hidden = true;

    const payload = {
        bloodType: document.getElementById("mBloodType").value.trim(),
        allergies: document.getElementById("mAllergies").value.trim(),
    };

    try {
        await updatePatientMedical(patientId, payload);
        toastSuccess("Medicinsk information uppdaterad.");
    } catch (err) {
        alertBox.textContent = err.message;
        alertBox.hidden = false;
    }
}

function renderCareContacts(contacts, canAdmit, canDischarge) {
    if (!contacts || contacts.length === 0) {
        return '<p class="text-muted">Inga vårdkontakter registrerade.</p>';
    }
    const showActions = canAdmit || canDischarge;
    return `
        <div class="table-wrap">
            <table>
                <thead><tr><th>Inskriven</th><th>Avdelning-ID</th><th>Ansvarig</th><th>Orsak</th><th>Status</th>${showActions ? "<th></th>" : ""}</tr></thead>
                <tbody>
                    ${contacts.map((c) => `
                        <tr>
                            <td>${formatDateTime(c.admitDate)}</td>
                            <td>#${safe(c.departmentId)}</td>
                            <td>${c.responsibleId ? "Personal #" + c.responsibleId : "-"}</td>
                            <td>${safe(c.reason)}</td>
                            <td><span class="badge badge-blue">${safe(c.status)}</span></td>
                            ${showActions ? `<td class="text-right">
                                ${canAdmit && c.status === "planned" ? `<button type="button" class="btn btn-secondary btn-sm admit-btn" data-cc-id="${c.id}">Lägg in</button>` : ""}
                                ${canDischarge && c.status !== "discharged" ? `<button type="button" class="btn btn-secondary btn-sm discharge-btn" data-cc-id="${c.id}">Skriv ut</button>` : ""}
                            </td>` : ""}
                        </tr>`).join("")}
                </tbody>
            </table>
        </div>
    `;
}

// Ansvarig personal is scoped to whichever avdelning is selected, so reception picks from
// staff actually employed there instead of guessing — see staffOptionsForDept() below.
function staffOptionsForDept(staffByDept, departmentId) {
    const list = staffByDept.get(Number(departmentId)) || [];
    if (!list.length) return '<option value="">Ingen personal på avdelningen</option>';
    return '<option value="">Välj personal...</option>' + list.map((s) => `<option value="${s.id}">${s.label}</option>`).join("");
}

// US-12 — receptionisten (admin-rollen i systemet) skapar vårdkontakten vid inskrivning.
function careContactForm(departments, staffByDept) {
    if (!departments.length || staffByDept.size === 0) {
        return '<p class="text-muted">Kunde inte ladda avdelningar/personal — försök ladda om sidan.</p>';
    }
    return `
        <div id="cc-alert" class="alert alert-error" hidden></div>
        <form id="createCareContactForm">
            <div class="form-grid">
                <div class="form-group">
                    <label for="ccDepartment">Avdelning</label>
                    <select id="ccDepartment" class="form-control" required>
                        ${departments.map((d) => `<option value="${d.id}">${safe(d.name)}</option>`).join("")}
                    </select>
                </div>
                <div class="form-group">
                    <label for="ccResponsible">Ansvarig personal</label>
                    <select id="ccResponsible" class="form-control" required>
                        ${staffOptionsForDept(staffByDept, departments[0].id)}
                    </select>
                </div>
                <div class="form-group full-width">
                    <label for="ccReason">Orsak</label>
                    <input type="text" id="ccReason" class="form-control" placeholder="t.ex. Inskrivning för observation">
                </div>
            </div>
            <button type="submit" class="btn btn-primary btn-sm">Skapa vårdkontakt</button>
        </form>
    `;
}

async function handleCreateCareContact(e, me, patientId) {
    e.preventDefault();
    const alertBox = document.getElementById("cc-alert");
    alertBox.hidden = true;

    const payload = {
        patientId: Number(patientId),
        departmentId: Number(document.getElementById("ccDepartment").value),
        responsibleStaffId: Number(document.getElementById("ccResponsible").value),
        reason: document.getElementById("ccReason").value.trim(),
    };

    try {
        await createCareContact(payload);
        toastSuccess("Vårdkontakten skapades.");
        renderDetail(document.getElementById("content"), me, patientId);
    } catch (err) {
        alertBox.textContent = err.message;
        alertBox.hidden = false;
    }
}

// Läkaren lägger in patienten från en planerad vårdkontakt.
async function handleAdmit(careContactId, me, patientId) {
    try {
        await admitCareContact(careContactId);
        toastSuccess("Patienten lades in.");
        renderDetail(document.getElementById("content"), me, patientId);
    } catch (err) {
        toastError(err);
    }
}

// US-17 — läkare skriver ut patienten från en aktiv vårdkontakt.
async function handleDischarge(careContactId, me, patientId) {
    if (!confirm("Skriva ut patienten från denna vårdkontakt?")) return;
    try {
        await dischargeCareContact(careContactId);
        toastSuccess("Patienten skrevs ut.");
        renderDetail(document.getElementById("content"), me, patientId);
    } catch (err) {
        toastError(err);
    }
}

// US-13 — doctors may log any entry type; US-22 — nurses are limited to "note" (backend re-checks this too).
function journalEntryForm(careContacts, canCreate, role) {
    if (!canCreate) return "";
    if (!careContacts || careContacts.length === 0) {
        return '<p class="text-muted">Ingen vårdkontakt registrerad — en journalpost måste kopplas till en vårdkontakt.</p>';
    }

    const typeOptions = role === ROLES.NURSE
        ? '<option value="note">Anteckning</option>'
        : `
            <option value="note">Anteckning</option>
            <option value="examination">Undersökning</option>
            <option value="operation">Operation</option>`;

    return `
        <div id="je-alert" class="alert alert-error" hidden></div>
        <form id="createJournalEntryForm">
            <div class="form-grid">
                <div class="form-group full-width">
                    <label for="jeCareContact">Vårdkontakt</label>
                    <select id="jeCareContact" class="form-control" required>
                        ${careContacts.map((c) => `<option value="${c.id}">#${c.id} — ${safe(c.reason)} (${formatDate(c.admitDate)})</option>`).join("")}
                    </select>
                </div>
                <div class="form-group">
                    <label for="jeType">Typ</label>
                    <select id="jeType" class="form-control" required>${typeOptions}</select>
                </div>
                <div class="form-group full-width">
                    <label for="jeContent">Anteckning</label>
                    <textarea id="jeContent" class="form-control" required></textarea>
                </div>
            </div>
            <button type="submit" class="btn btn-primary btn-sm">Spara journalpost</button>
        </form>
    `;
}

async function handleCreateJournalEntry(e, me, patientId) {
    e.preventDefault();
    const alertBox = document.getElementById("je-alert");
    alertBox.hidden = true;

    const payload = {
        careContactId: Number(document.getElementById("jeCareContact").value),
        createdBy: me.staffId,
        type: document.getElementById("jeType").value,
        content: document.getElementById("jeContent").value.trim(),
    };

    try {
        await createJournalEntry(payload);
        toastSuccess("Journalposten skapades.");
        renderDetail(document.getElementById("content"), me, patientId);
    } catch (err) {
        alertBox.textContent = err.message;
        alertBox.hidden = false;
    }
}

async function renderJournalTimeline(dashboard, patientId, me, canDiagnose, canMeasure) {
    const el = document.getElementById("journal-timeline");
    const entries = dashboard.journalEntries || [];
    const diagnoses = dashboard.diagnoses || [];

    if (entries.length === 0) {
        el.innerHTML = '<p class="text-muted">Inga journalanteckningar registrerade för patienten.</p>';
        return;
    }

    let measures = [];
    try {
        measures = await getMeasures();
    } catch {
        measures = [];
    }

    el.innerHTML = `<div class="timeline">${entries.map((entry) => renderJournalEntry(entry, diagnoses, measures, canDiagnose, canMeasure)).join("")}</div>`;

    entries.forEach((entry) => {
        const diagBtn = document.getElementById(`diag-toggle-${entry.id}`);
        if (diagBtn) diagBtn.addEventListener("click", () => toggleInlineForm(`diag-form-${entry.id}`));
        const measureBtn = document.getElementById(`measure-toggle-${entry.id}`);
        if (measureBtn) measureBtn.addEventListener("click", () => toggleInlineForm(`measure-form-${entry.id}`));

        const diagForm = document.getElementById(`diagForm-${entry.id}`);
        if (diagForm) diagForm.addEventListener("submit", (e) => handleCreateDiagnosis(e, entry.id, me.staffId, patientId));

        const measureForm = document.getElementById(`measureForm-${entry.id}`);
        if (measureForm) measureForm.addEventListener("submit", (e) => handleCreateMeasure(e, entry.id, me.staffId, patientId));
    });
}

function renderJournalEntry(entry, diagnoses, measures, canDiagnose, canMeasure) {
    const entryDiagnoses = diagnoses.filter((d) => d.journalEntryId === entry.id);
    const entryMeasures = measures.filter((m) => m.journalEntryId === entry.id);

    return `
        <div class="timeline-item">
            <div class="timeline-item-head">
                <strong>${safe(entry.type)}</strong>
                <span class="date">${formatDateTime(entry.createdAt)}</span>
            </div>
            <p>${safe(entry.content)}</p>

            ${entryDiagnoses.length || entryMeasures.length ? `
                <div class="timeline-sub-list">
                    ${entryDiagnoses.map((d) => `
                        <div class="timeline-sub-item">
                            <span>🩺 <strong>${safe(d.icd10Code)}</strong> — ${safe(d.name)}${d.description ? ": " + escapeHtml(d.description) : ""}</span>
                            <span class="text-muted">${formatDate(d.diagnosedDate)}</span>
                        </div>`).join("")}
                    ${entryMeasures.map((m) => `
                        <div class="timeline-sub-item">
                            <span>⚕ ${safe(m.description)}</span>
                            <span class="text-muted">${formatDate(m.performedDate)}</span>
                        </div>`).join("")}
                </div>
            ` : ""}

            <div class="form-actions" style="margin-top:12px;">
                ${canDiagnose ? `<button type="button" class="btn btn-secondary btn-sm" id="diag-toggle-${entry.id}">+ Diagnos</button>` : ""}
                ${canMeasure ? `<button type="button" class="btn btn-secondary btn-sm" id="measure-toggle-${entry.id}">+ Åtgärd</button>` : ""}
            </div>

            ${canDiagnose ? `
                <div class="inline-form" id="diag-form-${entry.id}" hidden>
                    <form id="diagForm-${entry.id}">
                        <div class="form-grid">
                            <div class="form-group">
                                <label>ICD-10 kod</label>
                                <input type="text" class="form-control diag-icd10" placeholder="t.ex. E11.9" required>
                            </div>
                            <div class="form-group">
                                <label>Namn</label>
                                <input type="text" class="form-control diag-name" required>
                            </div>
                            <div class="form-group full-width">
                                <label>Beskrivning</label>
                                <textarea class="form-control diag-description"></textarea>
                            </div>
                            <div class="form-group">
                                <label>Diagnosdatum</label>
                                <input type="date" class="form-control diag-date" value="${toDateInputValue()}">
                            </div>
                        </div>
                        <button type="submit" class="btn btn-primary btn-sm">Spara diagnos</button>
                    </form>
                </div>` : ""}

            ${canMeasure ? `
                <div class="inline-form" id="measure-form-${entry.id}" hidden>
                    <form id="measureForm-${entry.id}">
                        <div class="form-grid">
                            <div class="form-group full-width">
                                <label>Beskrivning av åtgärd</label>
                                <textarea class="form-control measure-description" required></textarea>
                            </div>
                            <div class="form-group">
                                <label>Utförd datum</label>
                                <input type="date" class="form-control measure-date" value="${toDateInputValue()}">
                            </div>
                        </div>
                        <button type="submit" class="btn btn-primary btn-sm">Spara åtgärd</button>
                    </form>
                </div>` : ""}
        </div>
    `;
}

function toggleInlineForm(id) {
    const el = document.getElementById(id);
    if (el) el.hidden = !el.hidden;
}

async function handleCreateDiagnosis(e, journalEntryId, staffId, patientId) {
    e.preventDefault();
    const form = e.target;
    const payload = {
        journalEntryId,
        setBy: staffId,
        icd10Code: form.querySelector(".diag-icd10").value.trim(),
        name: form.querySelector(".diag-name").value.trim(),
        description: form.querySelector(".diag-description").value.trim(),
        diagnosedDate: form.querySelector(".diag-date").value || null,
    };
    try {
        await createDiagnosis(payload);
        toastSuccess("Diagnos registrerad.");
        const me = await loadCurrentUser(getCurrentUser);
        renderDetail(document.getElementById("content"), me, patientId);
    } catch (err) {
        toastError(err);
    }
}

async function handleCreateMeasure(e, journalEntryId, staffId, patientId) {
    e.preventDefault();
    const form = e.target;
    const payload = {
        journalEntryId,
        performedBy: staffId,
        description: form.querySelector(".measure-description").value.trim(),
        performedDate: form.querySelector(".measure-date").value || null,
    };
    try {
        await createMeasure(payload);
        toastSuccess("Åtgärd registrerad.");
        const me = await loadCurrentUser(getCurrentUser);
        renderDetail(document.getElementById("content"), me, patientId);
    } catch (err) {
        toastError(err);
    }
}
