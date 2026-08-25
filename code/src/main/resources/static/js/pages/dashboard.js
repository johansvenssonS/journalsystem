import { getScheduleFor, getPatients, getPrescriptions, getReferrals, getAuditLogs, getAppointments, getCurrentUser } from "../api.js";
import { loadCurrentUser } from "../auth.js";
import { safe, formatDateTime, roleLabel, toDateInputValue, statusBadgeClass, loadingRow, emptyRow, errorRow, setTopbar } from "../ui.js";
import { can, canAccessPage } from "../access.js";

function goToPage(pageId) {
    document.querySelector(`.menu-item[data-target="${pageId}"]`)?.click();
}

function quickActionsFor(me) {
    const candidates = [
        can.createAppointment(me) && { label: "Ny bokning", page: "appointments" },
        can.createPrescription(me) && { label: "Skriv recept", page: "prescriptions" },
        can.createReferral(me) && { label: "Skapa remiss", page: "referrals" },
        can.createPatient(me) && { label: "Registrera patient", page: "patients" },
        can.lookupPrescription(me) && { label: "Slå upp recept", page: "prescriptions" },
        canAccessPage(me, "patients") && { label: "Öppna patienter", page: "patients" },
        canAccessPage(me, "appointments") && !can.createAppointment(me) && { label: "Öppna bokningar", page: "appointments" },
    ].filter(Boolean);
    return candidates.slice(0, 3);
}

export async function render(container) {
    const me = await loadCurrentUser(getCurrentUser);
    setTopbar("Startsida", `Välkommen tillbaka, ${safe(me.firstName)} ${safe(me.lastName)} — ${roleLabel(me.role)}${me.departmentName ? " · " + me.departmentName : ""}`);

    const actions = quickActionsFor(me);

    container.innerHTML = `
        <div class="stats-grid">
            <div class="stat-card"><div class="stat-label">Patienter i systemet</div><div class="stat-value-row"><h3 id="stat-patients">-</h3></div></div>
            <div class="stat-card"><div class="stat-label">Bokningar idag</div><div class="stat-value-row"><h3 id="stat-appointments">-</h3></div></div>
            <div class="stat-card"><div class="stat-label">Aktiva recept</div><div class="stat-value-row"><h3 id="stat-prescriptions">-</h3></div></div>
            <div class="stat-card"><div class="stat-label">Öppna remisser</div><div class="stat-value-row"><h3 id="stat-referrals">-</h3></div></div>
            ${me.departmentId ? `<div class="stat-card stat-card-accent"><div class="stat-label">Bokningar idag · ${safe(me.departmentName)}</div><div class="stat-value-row"><h3 id="stat-dept-today">-</h3></div></div>` : ""}
        </div>

        <div class="grid-2">
            <div class="card">
                <div class="card-header">
                    <h3>${me.staffId ? "Din schemalagda dag" : "Dagens bokningar"}</h3>
                    <span class="api-badge">GET /appointments/schedule</span>
                </div>
                <div class="row-list" id="dashboard-schedule-body">${loadingRow(1)}</div>
            </div>

            <div style="display:grid; gap:14px; align-content:start;">
                <div class="card">
                    <div class="card-header">
                        <h3>Senaste aktivitet</h3>
                        <span class="api-badge">GET /audit-logs</span>
                    </div>
                    <div class="row-list" id="dashboard-audit-body">${loadingRow(1)}</div>
                </div>

                ${actions.length ? `
                <div class="card">
                    <div class="card-header"><h3>Snabbåtgärder</h3></div>
                    <div class="quick-actions">
                        ${actions.map((a, i) => `<a href="#" class="quick-action ${i === 0 ? "quick-action-primary" : ""}" data-page="${a.page}">${a.label}</a>`).join("")}
                    </div>
                </div>` : ""}
            </div>
        </div>
    `;

    container.querySelectorAll(".quick-action").forEach((el) => {
        el.addEventListener("click", (e) => { e.preventDefault(); goToPage(el.dataset.page); });
    });

    loadStats();
    loadSchedule(me);
    loadActivity();
}

async function loadStats() {
    getPatients()
        .then((rows) => (document.getElementById("stat-patients").textContent = rows.length))
        .catch(() => (document.getElementById("stat-patients").textContent = "-"));

    loadTodayAppointmentCounts();

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

async function loadTodayAppointmentCounts() {
    // No dedicated "count today" endpoint — approximate via /appointments and filter client-side.
    try {
        const me = await loadCurrentUser(getCurrentUser);
        const rows = await getAppointments();
        const today = toDateInputValue();
        const todayRows = rows.filter((a) => a.scheduledAt && a.scheduledAt.startsWith(today));
        document.getElementById("stat-appointments").textContent = todayRows.length;

        const deptStat = document.getElementById("stat-dept-today");
        if (deptStat) deptStat.textContent = todayRows.filter((a) => a.departmentId === me.departmentId).length;
    } catch {
        document.getElementById("stat-appointments").textContent = "-";
        const deptStat = document.getElementById("stat-dept-today");
        if (deptStat) deptStat.textContent = "-";
    }
}

async function loadSchedule(me) {
    const el = document.getElementById("dashboard-schedule-body");
    if (!me.staffId) {
        el.innerHTML = '<p class="text-muted" style="padding:8px 4px;">Ditt konto är inte kopplat till en personalprofil.</p>';
        return;
    }
    try {
        const rows = await getScheduleFor(me.staffId, toDateInputValue());
        if (!rows || rows.length === 0) {
            el.innerHTML = '<p class="text-muted" style="padding:8px 4px;">Inga bokningar för idag.</p>';
            return;
        }
        el.innerHTML = rows.map((a) => scheduleRow(a)).join("");
    } catch (err) {
        el.innerHTML = `<p class="text-muted" style="padding:8px 4px;">Kunde inte hämta schemat: ${err.message}</p>`;
    }
}

function scheduleRow(a) {
    const accent = statusBadgeClass(a.status).replace("badge-", "");
    const time = a.scheduledAt ? new Date(a.scheduledAt).toLocaleTimeString("sv-SE", { hour: "2-digit", minute: "2-digit" }) : "-";
    return `
        <div class="row-list-item">
            <div class="row-list-time mono">${time}</div>
            <div class="row-list-accent row-list-accent-${accent}"></div>
            <div class="row-list-body">
                <div class="row-list-title">Patient #${safe(a.patientId)}</div>
                <div class="row-list-meta">${safe(a.note)}</div>
            </div>
            <span class="badge ${statusBadgeClass(a.status)}">${safe(a.status)}</span>
        </div>`;
}

async function loadActivity() {
    const el = document.getElementById("dashboard-audit-body");
    try {
        const rows = await getAuditLogs();
        if (!rows || rows.length === 0) {
            el.innerHTML = '<p class="text-muted" style="padding:8px 4px;">Ingen aktivitet loggad ännu.</p>';
            return;
        }
        const latest = [...rows]
            .sort((a, b) => new Date(b.occurredAt) - new Date(a.occurredAt))
            .slice(0, 6);
        el.innerHTML = latest.map((log) => `
            <div class="row-list-item">
                <div class="row-list-time mono">${formatDateTime(log.occurredAt).split(" ")[1] || ""}</div>
                <div class="row-list-accent row-list-accent-purple"></div>
                <div class="row-list-body">
                    <div class="row-list-title">${safe(log.event)}</div>
                    <div class="row-list-meta">${log.staffId ? "Personal #" + log.staffId : "-"}</div>
                </div>
            </div>`).join("");
    } catch (err) {
        el.innerHTML = `<p class="text-muted" style="padding:8px 4px;">Kunde inte hämta aktivitet: ${err.message}</p>`;
    }
}
