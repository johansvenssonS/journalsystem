import { getStaff, getDepartments } from "../api.js";
import { safe, loadingRow, emptyRow, errorRow } from "../ui.js";

export async function render(container) {
    container.innerHTML = `
        <header class="page-header">
            <div>
                <h2>Personal &amp; avdelningar</h2>
                <p>Katalog över sjukhusets personal och avdelningar</p>
            </div>
        </header>

        <div class="grid-2">
            <div class="card">
                <div class="card-header">
                    <h3>Personal</h3>
                    <span class="api-badge">GET /staff</span>
                </div>
                <div class="table-wrap">
                    <table>
                        <thead><tr><th>Namn</th><th>Personnummer</th><th>ID</th></tr></thead>
                        <tbody id="staff-tbody">${loadingRow(3)}</tbody>
                    </table>
                </div>
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

    loadStaff();
    loadDepartments();
}

async function loadStaff() {
    const tbody = document.getElementById("staff-tbody");
    try {
        const rows = await getStaff();
        tbody.innerHTML = rows.length
            ? rows.map((s) => `<tr><td><strong>${safe(s.firstName)} ${safe(s.lastName)}</strong></td><td>${safe(s.personalNumber)}</td><td>#${safe(s.id)}</td></tr>`).join("")
            : emptyRow(3);
    } catch (err) {
        tbody.innerHTML = errorRow(3, err);
    }
}

async function loadDepartments() {
    const tbody = document.getElementById("dept-tbody");
    try {
        const rows = await getDepartments();
        tbody.innerHTML = rows.length
            ? rows.map((d) => `<tr><td><strong>${safe(d.name)}</strong></td><td>Plan ${safe(d.floor)}</td><td>${safe(d.specializationName)}</td></tr>`).join("")
            : emptyRow(3);
    } catch (err) {
        tbody.innerHTML = errorRow(3, err);
    }
}
