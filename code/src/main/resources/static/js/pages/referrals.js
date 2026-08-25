import { getReferrals, createReferral, respondToReferral, getPatients, getDepartments, getCurrentUser } from "../api.js";
import { loadCurrentUser } from "../auth.js";
import { safe, formatDateTime, statusBadge, loadingRow, emptyRow, errorRow, toastSuccess, toastError, toMap, setTopbar } from "../ui.js";
import { can } from "../access.js";

let departmentMap = {};
let myDepartmentId = null;
let canRespond = false;

export async function render(container) {
    const me = await loadCurrentUser(getCurrentUser);
    const canCreate = can.createReferral(me);
    canRespond = can.respondReferral(me);
    myDepartmentId = me.departmentId ?? null;
    setTopbar("Remisser", "Skicka och följ upp remisser mellan avdelningar");

    container.innerHTML = `
        ${canCreate ? `
        <div class="card mb-4">
            <div class="card-header">
                <h3>Ny remiss</h3>
                <span class="api-badge">POST /referrals</span>
            </div>
            <div id="ref-alert" class="alert alert-error" hidden></div>
            <form id="createReferralForm">
                <div class="form-grid">
                    <div class="form-group full-width">
                        <label for="refPatient">Patient</label>
                        <select id="refPatient" class="form-control" required><option value="">Laddar patienter...</option></select>
                    </div>
                    <div class="form-group">
                        <label for="refFrom">Från avdelning</label>
                        <select id="refFrom" class="form-control" required><option value="">Laddar...</option></select>
                    </div>
                    <div class="form-group">
                        <label for="refTo">Till avdelning</label>
                        <select id="refTo" class="form-control" required><option value="">Laddar...</option></select>
                    </div>
                    <div class="form-group full-width">
                        <label for="refReason">Remisstext / orsak</label>
                        <textarea id="refReason" class="form-control" placeholder="Beskriv orsak till remiss..."></textarea>
                    </div>
                </div>
                <button type="submit" class="btn btn-primary">Skicka remiss</button>
            </form>
        </div>` : ""}

        <div class="card">
            <div class="card-header">
                <h3>Alla remisser</h3>
                <span class="api-badge">GET /referrals</span>
            </div>
            <div class="table-wrap">
                <table>
                    <thead><tr><th>Patient</th><th>Från</th><th>Till</th><th>Skickad</th><th>Status</th><th>Svar</th>${canRespond ? "<th></th>" : ""}</tr></thead>
                    <tbody id="ref-tbody">${loadingRow(canRespond ? 7 : 6)}</tbody>
                </table>
            </div>
        </div>
    `;

    await loadDepartmentMap();
    loadAll();

    if (canCreate) {
        loadFormOptions(me);
        document.getElementById("createReferralForm").addEventListener("submit", handleCreate);
    }
}

async function loadDepartmentMap() {
    try {
        const departments = await getDepartments();
        departmentMap = toMap(departments);
    } catch {
        departmentMap = {};
    }
}

function deptName(id) {
    return departmentMap[id]?.name || (id ? "Avd #" + id : "-");
}

function referralRow(r) {
    const touchesMine = myDepartmentId != null && (r.fromDepartmentId === myDepartmentId || r.toDepartmentId === myDepartmentId);
    // US-62 — only the receiving department's doctors get a "Svara" control, and only while pending.
    const showRespond = canRespond && r.status === "pending" && myDepartmentId != null && r.toDepartmentId === myDepartmentId;
    return `
        <tr class="${touchesMine ? "row-highlight" : ""}">
            <td>#${safe(r.patientId)}</td>
            <td>${deptName(r.fromDepartmentId)}</td>
            <td>${deptName(r.toDepartmentId)}</td>
            <td>${formatDateTime(r.sentAt)}</td>
            <td>${statusBadge(r.status)}</td>
            <td>${safe(r.response)}</td>
            ${canRespond ? `<td class="text-right">${showRespond ? `<button type="button" class="btn btn-secondary btn-sm respond-toggle" data-ref-id="${r.id}">Svara</button>` : ""}</td>` : ""}
        </tr>
        ${showRespond ? `
        <tr class="inline-form-row" id="respond-row-${r.id}" hidden>
            <td colspan="${canRespond ? 7 : 6}">
                <form id="respondForm-${r.id}" class="inline-form">
                    <div class="form-grid">
                        <div class="form-group">
                            <label>Beslut</label>
                            <select class="form-control respond-status" required>
                                <option value="accepted">Acceptera</option>
                                <option value="declined">Neka</option>
                            </select>
                        </div>
                        <div class="form-group full-width">
                            <label>Svar / kommentar</label>
                            <input type="text" class="form-control respond-text" placeholder="t.ex. Bokad tid 2026-09-01">
                        </div>
                    </div>
                    <button type="submit" class="btn btn-primary btn-sm">Skicka svar</button>
                </form>
            </td>
        </tr>` : ""}`;
}

async function loadAll() {
    const tbody = document.getElementById("ref-tbody");
    const colspan = canRespond ? 7 : 6;
    tbody.innerHTML = loadingRow(colspan);
    try {
        const rows = await getReferrals();
        tbody.innerHTML = rows.length ? rows.map(referralRow).join("") : emptyRow(colspan);
        attachRespondHandlers();
    } catch (err) {
        tbody.innerHTML = errorRow(colspan, err);
    }
}

function attachRespondHandlers() {
    document.querySelectorAll(".respond-toggle").forEach((btn) => {
        btn.addEventListener("click", () => {
            const row = document.getElementById(`respond-row-${btn.dataset.refId}`);
            if (row) row.hidden = !row.hidden;
        });
    });
    document.querySelectorAll("[id^='respondForm-']").forEach((form) => {
        const referralId = form.id.replace("respondForm-", "");
        form.addEventListener("submit", (e) => handleRespond(e, referralId));
    });
}

async function handleRespond(e, referralId) {
    e.preventDefault();
    const form = e.target;
    const payload = {
        status: form.querySelector(".respond-status").value,
        response: form.querySelector(".respond-text").value.trim(),
    };
    try {
        await respondToReferral(referralId, payload);
        toastSuccess("Remissen besvarades.");
        loadAll();
    } catch (err) {
        toastError(err);
    }
}

async function loadFormOptions(me) {
    const patientSelect = document.getElementById("refPatient");
    const fromSelect = document.getElementById("refFrom");
    const toSelect = document.getElementById("refTo");

    try {
        const patients = await getPatients();
        patientSelect.innerHTML = '<option value="">Välj patient...</option>' +
            patients.map((p) => `<option value="${p.id}">${p.firstName} ${p.lastName} (#${p.id})</option>`).join("");
    } catch {
        patientSelect.innerHTML = '<option value="">Kunde inte ladda patienter</option>';
    }

    const options = Object.entries(departmentMap)
        .map(([id, name]) => `<option value="${id}">${name}</option>`)
        .join("");
    fromSelect.innerHTML = options || '<option value="">Inga avdelningar</option>';
    toSelect.innerHTML = options || '<option value="">Inga avdelningar</option>';
    if (me.departmentId) fromSelect.value = String(me.departmentId);
}

async function handleCreate(e) {
    e.preventDefault();
    const alertBox = document.getElementById("ref-alert");
    alertBox.hidden = true;

    const me = await loadCurrentUser(getCurrentUser);
    const payload = {
        patientId: Number(document.getElementById("refPatient").value),
        fromDepartmentId: Number(document.getElementById("refFrom").value),
        toDepartmentId: Number(document.getElementById("refTo").value),
        sentBy: me.staffId,
        reason: document.getElementById("refReason").value.trim(),
        sentAt: new Date().toISOString(),
    };

    try {
        await createReferral(payload);
        document.getElementById("createReferralForm").reset();
        toastSuccess("Remissen skickades.");
        loadAll();
    } catch (err) {
        alertBox.textContent = err.message;
        alertBox.hidden = false;
    }
}
