import { getAuditLogs } from "../api.js";
import { safe, formatDateTime, loadingRow, emptyRow, errorRow } from "../ui.js";

export async function render(container) {
    container.innerHTML = `
        <header class="page-header">
            <div>
                <h2>Granskningslogg</h2>
                <p>Spårbarhet över åtkomst och händelser i journalsystemet</p>
            </div>
        </header>

        <div class="card">
            <div class="card-header">
                <h3>Loggade händelser</h3>
                <span class="api-badge">GET /audit-logs</span>
            </div>
            <div class="table-wrap">
                <table>
                    <thead><tr><th>Tidpunkt</th><th>Händelse</th><th>Personal</th><th>Patient</th><th>Post-ID</th><th>IP-adress</th></tr></thead>
                    <tbody id="audit-tbody">${loadingRow(6)}</tbody>
                </table>
            </div>
        </div>
    `;

    const tbody = document.getElementById("audit-tbody");
    try {
        const rows = await getAuditLogs();
        const sorted = [...rows].sort((a, b) => new Date(b.occurredAt) - new Date(a.occurredAt));
        tbody.innerHTML = sorted.length
            ? sorted.map((log) => `
                <tr>
                    <td>${formatDateTime(log.occurredAt)}</td>
                    <td><strong>${safe(log.event)}</strong></td>
                    <td>${log.staffId ? "Personal #" + log.staffId : "-"}</td>
                    <td>${log.patientId ? "Patient #" + log.patientId : "-"}</td>
                    <td>${safe(log.entityId)}</td>
                    <td>${safe(log.ipAddress)}</td>
                </tr>`).join("")
            : emptyRow(6);
    } catch (err) {
        tbody.innerHTML = errorRow(6, err);
    }
}
