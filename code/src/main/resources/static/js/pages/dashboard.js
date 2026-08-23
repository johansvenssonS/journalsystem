import { getScheduleFor, getPatients, getPrescriptions, getReferrals, getAuditLogs, getAppointments, getCurrentUser } from "../api.js";
import { loadCurrentUser } from "../auth.js";
import { safe, formatDateTime, roleLabel, toDateInputValue, statusBadge, loadingRow, emptyRow, errorRow } from "../ui.js";

export async function render(container) {
    const me = await loadCurrentUser(getCurrentUser);

    container.innerHTML = `
        <header class="page-header">
            <div>
                <h2>Startsida</h2>
                <p>Välkommen tillbaka, ${safe(me.firstName)} ${safe(me.lastName)} — ${roleLabel(me.role)}${me.departmentName ? " · " + me.departmentName : ""}</p>
            </div>
        </header>

        <div class="stats-grid">
            <div class="stat-card"><h3 id="stat-patients">-</h3><p>Patienter i systemet</p></div>
            <div class="stat-card"><h3 id="stat-appointments">-</h3><p>Bokningar idag</p></div>
            <div class="stat-card"><h3 id="stat-prescriptions">-</h3><p>Aktiva recept</p></div>
            <div class="stat-card"><h3 id="stat-referrals">-</h3><p>Öppna remisser</p></div>
        </div>

        <div class="grid-2">
            <div class="card">
                <div class="card-header">
                    <h3>${me.staffId ? "Din schemalagda dag" : "Dagens bokningar"}</h3>
                    <span class="api-badge">GET /appointments/schedule</span>
                </div>
                <div class="table-wrap">
                    <table>
                        <thead><tr><th>Tid</th><th>Patient-ID</th><th>Status</th><th>Notering</th></tr></thead>
                        <tbody id="dashboard-schedule-body">${loadingRow(4)}</tbody>
                    </table>
                </div>
            </div>

            <div class="card">
                <div class="card-header">
                    <h3>Senaste aktivitet</h3>
                    <span class="api-badge">GET /audit-logs</span>
                </div>
                <div class="table-wrap">
                    <table>
                        <thead><tr><th>Tid</th><th>Händelse</th><th>Personal</th></tr></thead>
                        <tbody id="dashboard-audit-body">${loadingRow(3)}</tbody>
                    </table>
                </div>
            </div>
        </div>
    `;

    loadStats();
    loadSchedule(me);
    loadActivity();
}

async function loadStats() {
    getPatients()
        .then((rows) => (document.getElementById("stat-patients").textContent = rows.length))
        .catch(() => (document.getElementById("stat-patients").textContent = "-"));

    loadTodayAppointmentCount();

    getPrescriptions()
        .then((rows) => (document.getElementById("stat-prescriptions").textContent = rows.filter((p) => p.active).length))
        .catch(() => (document.getElementById("stat-prescriptions").textContent = "-"));

    getReferrals()
        .then((rows) => {
            const open = rows.filter((r) => (r.status || "").toUpperCase() !== "ACCEPTED" && (r.status || "").toUpperCase() !== "ACCEPTERAD");
            document.getElementById("stat-referrals").textContent = open.length;
        })
        .catch(() => (document.getElementById("stat-referrals").textContent = "-"));
}

async function loadTodayAppointmentCount() {
    // No dedicated "count today" endpoint — approximate via /appointments and filter client-side.
    try {
        const rows = await getAppointments();
        const today = toDateInputValue();
        const count = rows.filter((a) => a.scheduledAt && a.scheduledAt.startsWith(today)).length;
        document.getElementById("stat-appointments").textContent = count;
    } catch {
        document.getElementById("stat-appointments").textContent = "-";
    }
}

async function loadSchedule(me) {
    const tbody = document.getElementById("dashboard-schedule-body");
    if (!me.staffId) {
        tbody.innerHTML = '<tr class="state-row"><td colspan="4">Ditt konto är inte kopplat till en personalprofil.</td></tr>';
        return;
    }
    try {
        const rows = await getScheduleFor(me.staffId, toDateInputValue());
        if (!rows || rows.length === 0) {
            tbody.innerHTML = emptyRow(4, "Inga bokningar för idag.");
            return;
        }
        tbody.innerHTML = rows
            .map((a) => `
                <tr>
                    <td><strong>${a.scheduledAt ? new Date(a.scheduledAt).toLocaleTimeString("sv-SE", { hour: "2-digit", minute: "2-digit" }) : "-"}</strong></td>
                    <td>#${safe(a.patientId)}</td>
                    <td>${statusBadge(a.status)}</td>
                    <td>${safe(a.note)}</td>
                </tr>`)
            .join("");
    } catch (err) {
        tbody.innerHTML = errorRow(4, err);
    }
}

async function loadActivity() {
    const tbody = document.getElementById("dashboard-audit-body");
    try {
        const rows = await getAuditLogs();
        if (!rows || rows.length === 0) {
            tbody.innerHTML = emptyRow(3, "Ingen aktivitet loggad ännu.");
            return;
        }
        const latest = [...rows]
            .sort((a, b) => new Date(b.occurredAt) - new Date(a.occurredAt))
            .slice(0, 8);
        tbody.innerHTML = latest
            .map((log) => `
                <tr>
                    <td>${formatDateTime(log.occurredAt)}</td>
                    <td>${safe(log.event)}</td>
                    <td>${log.staffId ? "Personal #" + log.staffId : "-"}</td>
                </tr>`)
            .join("");
    } catch (err) {
        tbody.innerHTML = errorRow(3, err);
    }
}
