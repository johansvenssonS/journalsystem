import { getScheduleFor, getPatients, getPrescriptions, getReferrals, getAuditLogs, getAppointments, getDepartmentPatients, getStaff, getStaffEmployments, getCurrentUser } from "../api.js";
import { loadCurrentUser } from "../auth.js";
import { safe, formatDateTime, roleLabel, toDateInputValue, statusBadgeClass, loadingRow, emptyRow, errorRow, setTopbar, toMap } from "../ui.js";
import { can, canAccessPage, ROLES } from "../access.js";
import { renderWeekCalendar, startOfWeek, addDays, weekRangeLabel } from "../components/calendar.js";

let weekStart = startOfWeek(new Date());
let myAppointments = [];
let patientMap = {};

function patientName(id) {
    const p = patientMap[id];
    return p ? `${p.firstName} ${p.lastName}` : null;
}

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

function activityCardHtml() {
    return `
        <div class="card">
            <div class="card-header">
                <h3>Senaste aktivitet</h3>
                <span class="api-badge">GET /audit-logs</span>
            </div>
            <div class="row-list" id="dashboard-audit-body">${loadingRow(1)}</div>
        </div>`;
}

function quickActionsCardHtml(actions) {
    if (!actions.length) return "";
    return `
        <div class="card mb-4">
            <div class="card-header"><h3>Snabbåtgärder</h3></div>
            <div class="quick-actions">
                ${actions.map((a, i) => `<a href="#" class="quick-action ${i === 0 ? "quick-action-primary" : ""}" data-page="${a.page}">${a.label}</a>`).join("")}
            </div>
        </div>`;
}

// Read-only, own-department slice of what used to be the standalone Bokningar/directory
// pages — doctors/nurses no longer get those nav items, so this replaces them here.
function deptPatientsCardHtml() {
    return `
        <div class="card mb-4">
            <div class="card-header">
                <h3>Patienter på avdelningen</h3>
                <span class="api-badge">GET /departments/{id}/patients</span>
            </div>
            <div class="table-wrap">
                <table>
                    <thead><tr><th>Namn</th><th>Personnummer</th><th>Vårdkontakt-ID</th><th>Orsak</th><th>Inskriven</th></tr></thead>
                    <tbody id="dash-dept-patients-tbody">${loadingRow(5)}</tbody>
                </table>
            </div>
        </div>`;
}

function staffByDeptCardHtml() {
    return `
        <div class="card">
            <div class="card-header">
                <h3>Personal på avdelningen</h3>
                <span class="api-badge">GET /staff · GET /staff-employments</span>
            </div>
            <div id="dash-staff-by-dept">${loadingRow(1)}</div>
        </div>`;
}

export async function render(container) {
    const me = await loadCurrentUser(getCurrentUser);
    setTopbar("Startsida", `Välkommen tillbaka, ${safe(me.firstName)} ${safe(me.lastName)} — ${roleLabel(me.role)}${me.departmentName ? " · " + me.departmentName : ""}`);

    const actions = quickActionsFor(me);
    // Doctors/nurses get their own weekly schedule as a calendar (like reception's Bokningar view);
    // other staff-linked roles (e.g. receptionist) keep the plain today's-bookings list.
    const hasOwnSchedule = me.role === ROLES.DOCTOR || me.role === ROLES.NURSE;

    container.innerHTML = hasOwnSchedule ? `
        ${quickActionsCardHtml(actions)}

        <div class="card mb-4">
            <div class="card-header">
                <h3>Ditt schema</h3>
                <div class="toolbar">
                    <div class="week-nav" id="dashWeekNav" style="margin-bottom:0;">
                        <button type="button" class="week-nav-btn" id="dashWeekPrev">← Föreg. vecka</button>
                        <span id="dashWeekLabel" class="week-label"></span>
                        <button type="button" class="week-nav-btn" id="dashWeekToday">Idag</button>
                        <button type="button" class="week-nav-btn" id="dashWeekNext">Nästa vecka →</button>
                    </div>
                    <span class="api-badge">GET /appointments</span>
                </div>
            </div>
            <div id="dashboard-schedule-body">${loadingRow(1)}</div>
        </div>

        ${deptPatientsCardHtml()}

        ${staffByDeptCardHtml()}
    ` : `
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
                    <h3>Dagens bokningar</h3>
                    <span class="api-badge">GET /appointments/schedule</span>
                </div>
                <div class="row-list" id="dashboard-schedule-body">${loadingRow(1)}</div>
            </div>

            <div style="display:grid; gap:14px; align-content:start;">
                ${activityCardHtml()}
                ${quickActionsCardHtml(actions)}
            </div>
        </div>`;

    container.querySelectorAll(".quick-action").forEach((el) => {
        el.addEventListener("click", (e) => { e.preventDefault(); goToPage(el.dataset.page); });
    });

    if (hasOwnSchedule) {
        document.getElementById("dashWeekPrev").addEventListener("click", () => { weekStart = addDays(weekStart, -7); renderMySchedule(); });
        document.getElementById("dashWeekNext").addEventListener("click", () => { weekStart = addDays(weekStart, 7); renderMySchedule(); });
        document.getElementById("dashWeekToday").addEventListener("click", () => { weekStart = startOfWeek(new Date()); renderMySchedule(); });
    }

    loadSchedule(me, hasOwnSchedule);
    if (hasOwnSchedule) {
        loadDeptPatients(me);
        loadStaffOnMyDepartment(me);
    } else {
        // Stats grid and audit-log activity only exist on the plain dashboard layout —
        // doctors/nurses have their own schedule/department view instead.
        loadStats();
        loadActivity();
    }
}

async function loadDeptPatients(me) {
    const tbody = document.getElementById("dash-dept-patients-tbody");
    if (!tbody) return;
    if (!me.departmentId) {
        tbody.innerHTML = emptyRow(5, "Du är inte kopplad till någon avdelning.");
        return;
    }
    try {
        const rows = await getDepartmentPatients(me.departmentId);
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

async function loadStaffOnMyDepartment(me) {
    const el = document.getElementById("dash-staff-by-dept");
    if (!el) return;
    if (!me.departmentId) {
        el.innerHTML = emptyRow(1, "Du är inte kopplad till någon avdelning.");
        return;
    }
    try {
        const [staff, employments] = await Promise.all([getStaff(), getStaffEmployments()]);
        const staffMap = toMap(staff);
        const people = employments
            .filter((e) => e.departmentId === me.departmentId)
            .map((e) => staffMap[e.staffId])
            .filter(Boolean);
        el.innerHTML = people.length
            ? `<ul class="staff-chip-list">${people.map((p) => `<li class="staff-chip">${safe(p.firstName)} ${safe(p.lastName)} <span class="text-muted">#${safe(p.id)}</span></li>`).join("")}</ul>`
            : emptyRow(1, "Ingen personal hittades.");
    } catch (err) {
        el.innerHTML = errorRow(1, err);
    }
}

function setStatText(id, text) {
    const el = document.getElementById(id);
    if (el) el.textContent = text; // page may have navigated away while this stat was loading
}

async function loadStats() {
    getPatients()
        .then((rows) => setStatText("stat-patients", rows.length))
        .catch(() => setStatText("stat-patients", "-"));

    loadTodayAppointmentCounts();

    getPrescriptions()
        .then((rows) => setStatText("stat-prescriptions", rows.filter((p) => p.active).length))
        .catch(() => setStatText("stat-prescriptions", "-"));

    getReferrals()
        .then((rows) => {
            const open = rows.filter((r) => (r.status || "").toUpperCase() !== "ACCEPTED" && (r.status || "").toUpperCase() !== "ACCEPTERAD");
            setStatText("stat-referrals", open.length);
        })
        .catch(() => setStatText("stat-referrals", "-"));
}

async function loadTodayAppointmentCounts() {
    // No dedicated "count today" endpoint — approximate via /appointments and filter client-side.
    try {
        const me = await loadCurrentUser(getCurrentUser);
        const rows = await getAppointments();
        const today = toDateInputValue();
        const todayRows = rows.filter((a) => a.scheduledAt && a.scheduledAt.startsWith(today));
        setStatText("stat-appointments", todayRows.length);
        setStatText("stat-dept-today", todayRows.filter((a) => a.departmentId === me.departmentId).length);
    } catch {
        setStatText("stat-appointments", "-");
        setStatText("stat-dept-today", "-");
    }
}

async function loadSchedule(me, hasOwnSchedule) {
    const el = document.getElementById("dashboard-schedule-body");
    if (!me.staffId) {
        if (el) el.innerHTML = '<p class="text-muted" style="padding:8px 4px;">Ditt konto är inte kopplat till en personalprofil.</p>';
        return;
    }

    if (hasOwnSchedule) {
        try {
            const [appts, patients] = await Promise.all([getAppointments(), getPatients()]);
            myAppointments = appts.filter((a) => a.staffId === me.staffId);
            patientMap = toMap(patients);
            renderMySchedule();
        } catch (err) {
            if (el) el.innerHTML = `<p class="text-muted" style="padding:8px 4px;">Kunde inte hämta schemat: ${err.message}</p>`;
        }
        return;
    }

    try {
        const [rows, patients] = await Promise.all([getScheduleFor(me.staffId, toDateInputValue()), getPatients()]);
        patientMap = toMap(patients);
        if (!rows || rows.length === 0) {
            if (el) el.innerHTML = '<p class="text-muted" style="padding:8px 4px;">Inga bokningar för idag.</p>';
            return;
        }
        if (el) el.innerHTML = rows.map((a) => scheduleRow(a)).join("");
    } catch (err) {
        if (el) el.innerHTML = `<p class="text-muted" style="padding:8px 4px;">Kunde inte hämta schemat: ${err.message}</p>`;
    }
}

function scheduleRow(a) {
    const accent = statusBadgeClass(a.status).replace("badge-", "");
    const time = a.scheduledAt ? new Date(a.scheduledAt).toLocaleTimeString("sv-SE", { hour: "2-digit", minute: "2-digit" }) : "-";
    const title = patientName(a.patientId) || `Patient #${safe(a.patientId)}`;
    return `
        <div class="row-list-item">
            <div class="row-list-time mono">${time}</div>
            <div class="row-list-accent row-list-accent-${accent}"></div>
            <div class="row-list-body">
                <div class="row-list-title">${title}</div>
                <div class="row-list-meta">${safe(a.note)}</div>
            </div>
            <span class="badge ${statusBadgeClass(a.status)}">${safe(a.status)}</span>
        </div>`;
}

function renderMySchedule() {
    const el = document.getElementById("dashboard-schedule-body");
    if (!el) return; // page navigated away before this render landed
    const label = document.getElementById("dashWeekLabel");
    if (label) label.textContent = weekRangeLabel(weekStart);
    el.innerHTML = `<div class="cal-wrap" id="dashCalContainer"></div>`;
    renderWeekCalendar(document.getElementById("dashCalContainer"), { appointments: myAppointments, weekStart, patientName });
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
