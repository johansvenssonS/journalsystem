import { getStaff, getStaffEmployments, getDepartments, getDepartmentPatients, getCurrentUser } from "../api.js";
import { loadCurrentUser } from "../auth.js";
import { safe, escapeHtml, formatDateTime, loadingRow, emptyRow, errorRow, toMap, setTopbar } from "../ui.js";
import { can } from "../access.js";

export async function render(container) {
    const me = await loadCurrentUser(getCurrentUser);
    const canViewPatients = can.viewDepartmentPatients(me);
    setTopbar("Personal & avdelningar", `Katalog över sjukhusets personal och avdelningar${me.departmentName ? ` — du tillhör ${me.departmentName}` : ""}`);

    container.innerHTML = `
        ${canViewPatients ? `
        <div class="card mb-4">
            <div class="card-header">
                <h3>Patienter på avdelning</h3>
                <span class="api-badge">GET /departments/{id}/patients</span>
            </div>
            <div class="search-row">
                <select id="deptPatientsSelect" class="form-control flex-grow"><option value="">Laddar avdelningar...</option></select>
            </div>
            <div class="table-wrap" style="margin-top:16px;">
                <table>
                    <thead><tr><th>Namn</th><th>Personnummer</th><th>Vårdkontakt-ID</th><th>Orsak</th><th>Inskriven</th></tr></thead>
                    <tbody id="dept-patients-tbody"><tr><td colspan="5" class="text-muted">Välj en avdelning.</td></tr></tbody>
                </table>
            </div>
        </div>` : ""}

        <div class="grid-2">
            <div class="card">
                <div class="card-header">
                    <h3>Personal per avdelning</h3>
                    <span class="api-badge">GET /staff · GET /staff-employments</span>
                </div>
                <div id="staff-by-dept">${loadingRow(1)}</div>
            </div>

            <div class="card">
                <div class="card-header">
                    <h3>Avdelningar</h3>
                    <span class="api-badge">GET /departments</span>
                </div>
                <div class="table-wrap">
                    <table>
                        <thead><tr><th>Namn</th><th>Plan</th><th>Specialisering</th></tr></thead>
                        <tbody id="dept-tbody">${loadingRow(3)}</tbody>
                    </table>
                </div>
            </div>
        </div>
    `;

    loadStaffByDepartment(me);
    loadDepartments(me);
    if (canViewPatients) loadDeptPatientsForm(me);
}

// US-20 — vårdpersonal ser vilka patienter som just nu är inskrivna på en given avdelning.
async function loadDeptPatientsForm(me) {
    const select = document.getElementById("deptPatientsSelect");
    try {
        const departments = await getDepartments();
        select.innerHTML = '<option value="">Välj avdelning...</option>' +
            departments.map((d) => `<option value="${d.id}">${escapeHtml(d.name)}</option>`).join("");
        if (me.departmentId) select.value = String(me.departmentId);
        if (select.value) loadDeptPatients(select.value);
    } catch {
        select.innerHTML = '<option value="">Kunde inte ladda avdelningar</option>';
    }

    select.addEventListener("change", () => {
        if (select.value) loadDeptPatients(select.value);
    });
}

async function loadDeptPatients(departmentId) {
    const tbody = document.getElementById("dept-patients-tbody");
    tbody.innerHTML = loadingRow(5);
    try {
        const rows = await getDepartmentPatients(departmentId);
        tbody.innerHTML = rows.length ? rows.map((p) => `
            <tr>
                <td><strong>${safe(p.firstName)} ${safe(p.lastName)}</strong></td>
                <td>${safe(p.personalNumber)}</td>
                <td>#${safe(p.careContactId)}</td>
                <td>${safe(p.reason)}</td>
                <td>${formatDateTime(p.admitDate)}</td>
            </tr>`).join("") : emptyRow(5, "Inga patienter inskrivna på avdelningen.");
    } catch (err) {
        tbody.innerHTML = errorRow(5, err);
    }
}

async function loadStaffByDepartment(me) {
    const el = document.getElementById("staff-by-dept");
    try {
        const [staff, employments, departments] = await Promise.all([getStaff(), getStaffEmployments(), getDepartments()]);
        const staffMap = toMap(staff);
        const deptMap = toMap(departments);

        const byDept = new Map();
        const unassigned = [];
        employments.forEach((emp) => {
            const person = staffMap[emp.staffId];
            if (!person) return;
            if (emp.departmentId == null) { unassigned.push(person); return; }
            if (!byDept.has(emp.departmentId)) byDept.set(emp.departmentId, []);
            byDept.get(emp.departmentId).push(person);
        });

        const groupIds = [...byDept.keys()].sort((a, b) => {
            if (a === me.departmentId) return -1;
            if (b === me.departmentId) return 1;
            return (deptMap[a]?.name || "").localeCompare(deptMap[b]?.name || "");
        });

        if (groupIds.length === 0 && unassigned.length === 0) {
            el.innerHTML = emptyRow(1, "Ingen personal hittades.");
            return;
        }

        el.innerHTML = `
            <div class="dept-groups">
                ${groupIds.map((deptId) => deptGroupHtml(deptMap[deptId]?.name || `Avdelning #${deptId}`, byDept.get(deptId), deptId === me.departmentId)).join("")}
                ${unassigned.length ? deptGroupHtml("Utan avdelning", unassigned, false) : ""}
            </div>
        `;
    } catch (err) {
        el.innerHTML = errorRow(1, err);
    }
}

function deptGroupHtml(name, people, isMine) {
    return `
        <details class="dept-group ${isMine ? "dept-group-mine" : ""}" ${isMine ? "open" : ""}>
            <summary>
                <span>${escapeHtml(name)}</span>
                <span class="dept-group-meta">${isMine ? '<span class="badge badge-blue">Din avdelning</span>' : ""} ${people.length} st</span>
            </summary>
            <ul class="staff-chip-list">
                ${people.map((p) => `<li class="staff-chip">${safe(p.firstName)} ${safe(p.lastName)} <span class="text-muted">#${safe(p.id)}</span></li>`).join("")}
            </ul>
        </details>
    `;
}

async function loadDepartments(me) {
    const tbody = document.getElementById("dept-tbody");
    try {
        const rows = await getDepartments();
        tbody.innerHTML = rows.length
            ? rows.map((d) => `
                <tr class="${d.id === me.departmentId ? "row-highlight" : ""}">
                    <td><strong>${safe(d.name)}</strong>${d.id === me.departmentId ? ' <span class="badge badge-blue">Din</span>' : ""}</td>
                    <td>Plan ${safe(d.floor)}</td>
                    <td>${safe(d.specializationName)}</td>
                </tr>`).join("")
            : emptyRow(3);
    } catch (err) {
        tbody.innerHTML = errorRow(3, err);
    }
}
