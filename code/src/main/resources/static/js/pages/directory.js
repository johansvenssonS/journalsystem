import { getStaff, getStaffEmployments, getDepartments, getCurrentUser } from "../api.js";
import { loadCurrentUser } from "../auth.js";
import { safe, escapeHtml, loadingRow, emptyRow, errorRow, toMap, setTopbar } from "../ui.js";

export async function render(container) {
    const me = await loadCurrentUser(getCurrentUser);
    setTopbar("Personal & avdelningar", `Katalog över sjukhusets personal och avdelningar${me.departmentName ? ` — du tillhör ${me.departmentName}` : ""}`);

    container.innerHTML = `
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
