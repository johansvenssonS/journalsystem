import { getAuditLogs, getAuditLogsByPatient } from "../api.js";
import { safe, formatDateTime, loadingRow, emptyRow, errorRow, setTopbar } from "../ui.js";

export async function render(container) {
    setTopbar("Granskningslogg", "Spårbarhet över åtkomst och händelser i journalsystemet");

    container.innerHTML = `
        <div class="card mb-4">
            <div class="card-header">
                <h3>Filtrera på patient</h3>
                <span class="api-badge" id="audit-api-badge">GET /audit-logs</span>
            </div>
            <div class="search-row">
                <input type="text" id="auditPatientIdInput" placeholder="Patient-ID..." class="form-control flex-grow">
                <button id="auditFilterBtn" class="btn btn-primary">Visa patientens logg</button>
                <button id="auditClearBtn" class="btn btn-secondary">Visa alla</button>
            </div>
        </div>

        <div class="card">
            <div class="card-header">
                <h3>Loggade händelser</h3>
            </div>
            <div class="table-wrap">
                <table>
                    <thead><tr><th>Tidpunkt</th><th>Händelse</th><th>Personal</th><th>Patient</th><th>Typ</th><th>IP-adress</th></tr></thead>
                    <tbody id="audit-tbody">${loadingRow(6)}</tbody>
                </table>
            </div>
        </div>
    `;

    await loadAllLogs();

    document.getElementById("auditFilterBtn").addEventListener("click", () => handleFilter());
    document.getElementById("auditPatientIdInput").addEventListener("keydown", (e) => {
        if (e.key === "Enter") { e.preventDefault(); handleFilter(); }
    });
    document.getElementById("auditClearBtn").addEventListener("click", () => loadAllLogs());
}

async function handleFilter() {
    const input = document.getElementById("auditPatientIdInput");
    const patientId = input.value.trim();
    if (!patientId) {
        await loadAllLogs();
        return;
    }
    await loadLogs(() => getAuditLogsByPatient(patientId), `GET /audit-logs/patient/${patientId}`);
}

async function loadAllLogs() {
    await loadLogs(() => getAuditLogs(), "GET /audit-logs");
}

async function loadLogs(fetcher, badgeLabel) {
    const badge = document.getElementById("audit-api-badge");
    const tbody = document.getElementById("audit-tbody");
    if (badge) badge.textContent = badgeLabel;
    if (tbody) tbody.innerHTML = loadingRow(6);

    try {
        const rows = await fetcher();
        if (!tbody) return; // page navigated away while loading
        const sorted = [...rows].sort((a, b) => new Date(b.occurredAt) - new Date(a.occurredAt));
        tbody.innerHTML = sorted.length
            ? sorted.map((log) => `
                <tr>
                    <td>${formatDateTime(log.occurredAt)}</td>
                    <td><strong>${safe(log.event)}</strong></td>
                    <td>${log.staffName ? safe(log.staffName) : (log.staffId ? "Personal #" + log.staffId : "-")}</td>
                    <td>${log.patientName ? safe(log.patientName) : (log.patientId ? "Patient #" + log.patientId : "-")}</td>
                    <td>${safe(log.entityType)}</td>
                    <td>${safe(log.ipAddress)}</td>
                </tr>`).join("")
            : emptyRow(6);
    } catch (err) {
        if (tbody) tbody.innerHTML = errorRow(6, err);
    }
}
