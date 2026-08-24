import { getReferrals, createReferral, getPatients, getDepartments, getCurrentUser } from "../api.js";
import { loadCurrentUser } from "../auth.js";
import { safe, formatDateTime, statusBadge, loadingRow, emptyRow, errorRow, toastSuccess, toMap } from "../ui.js";
import { can } from "../access.js";

let departmentMap = {};
let myDepartmentId = null;

export async function render(container) {
    const me = await loadCurrentUser(getCurrentUser);
    const canCreate = can.createReferral(me);
    myDepartmentId = me.departmentId ?? null;

    container.innerHTML = `
        <header class="page-header">
            <div>
                <h2>Remisser</h2>
                <p>Skicka och följ upp remisser mellan avdelningar</p>
            </div>
        </header>

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
                    <thead><tr><th>Patient</th><th>Från</th><th>Till</th><th>Skickad</th><th>Status</th><th>Svar</th></tr></thead>
                    <tbody id="ref-tbody">${loadingRow(6)}</tbody>
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
    return `
        <tr class="${touchesMine ? "row-highlight" : ""}">
            <td>#${safe(r.patientId)}</td>
            <td>${deptName(r.fromDepartmentId)}</td>
            <td>${deptName(r.toDepartmentId)}</td>
            <td>${formatDateTime(r.sentAt)}</td>
            <td>${statusBadge(r.status)}</td>
            <td>${safe(r.response)}</td>
        </tr>`;
}

async function loadAll() {
    const tbody = document.getElementById("ref-tbody");
    tbody.innerHTML = loadingRow(6);
    try {
        const rows = await getReferrals();
        tbody.innerHTML = rows.length ? rows.map(referralRow).join("") : emptyRow(6);
    } catch (err) {
        tbody.innerHTML = errorRow(6, err);
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
