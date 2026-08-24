import { getAppointments, getScheduleFor, createAppointment, getPatients, getStaff, getDepartments, getCurrentUser } from "../api.js";
import { loadCurrentUser } from "../auth.js";
import { safe, formatDateTime, toDateInputValue, toDateTimeLocalValue, statusBadge, loadingRow, emptyRow, errorRow, toastSuccess, toMap } from "../ui.js";
import { can } from "../access.js";
import { renderWeekCalendar, startOfWeek, addDays, weekRangeLabel } from "../components/calendar.js";

let staffList = [];
let staffMap = {};
let departmentMap = {};
let allAppointments = [];
let me = null;

let viewMode = "week"; // "week" | "list"
let deptScope = "mine"; // "mine" | "all"
let weekStart = startOfWeek(new Date());
let scheduleSearchRows = null; // set when the staff-schedule search is active; overrides the list view
let scheduleSearchLabel = "";

export async function render(container) {
    me = await loadCurrentUser(getCurrentUser);
    const canCreate = can.createAppointment(me);
    deptScope = me.departmentId != null && !can.viewAllAppointments(me) ? "mine" : "all";

    container.innerHTML = `
        <header class="page-header">
            <div>
                <h2>Bokningar</h2>
                <p>Boka och sök i schemalagda besök</p>
            </div>
        </header>

        ${canCreate ? `
        <div class="card mb-4">
            <div class="card-header">
                <h3>Ny bokning</h3>
                <span class="api-badge">POST /appointments</span>
            </div>
            <div id="appt-alert" class="alert alert-error" hidden></div>
            <form id="createApptForm">
                <div class="form-grid">
                    <div class="form-group">
                        <label for="apPatient">Patient</label>
                        <select id="apPatient" class="form-control" required><option value="">Laddar...</option></select>
                    </div>
                    <div class="form-group">
                        <label for="apStaff">Personal</label>
                        <select id="apStaff" class="form-control" required><option value="">Laddar...</option></select>
                    </div>
                    <div class="form-group">
                        <label for="apDepartment">Avdelning</label>
                        <select id="apDepartment" class="form-control" required><option value="">Laddar...</option></select>
                    </div>
                    <div class="form-group">
                        <label for="apWhen">Tid</label>
                        <input type="datetime-local" id="apWhen" class="form-control" value="${toDateTimeLocalValue()}" required>
                    </div>
                    <div class="form-group">
                        <label for="apStatus">Status</label>
                        <select id="apStatus" class="form-control">
                            <option value="BOOKED">Bokad</option>
                            <option value="CONFIRMED">Bekräftad</option>
                            <option value="CANCELLED">Avbokad</option>
                        </select>
                    </div>
                    <div class="form-group full-width">
                        <label for="apNote">Notering</label>
                        <input type="text" id="apNote" class="form-control" placeholder="t.ex. Återbesök">
                    </div>
                </div>
                <button type="submit" class="btn btn-primary">Boka</button>
            </form>
        </div>` : ""}

        <div class="card mb-4">
            <div class="card-header">
                <h3>Sök schema för personal</h3>
                <span class="api-badge">GET /appointments/schedule</span>
            </div>
            <div class="search-row">
                <select id="scheduleStaff" class="form-control" style="max-width:280px;"><option value="">Välj personal...</option></select>
                <input type="date" id="scheduleDate" class="form-control" style="max-width:200px;" value="${toDateInputValue()}">
                <button id="scheduleBtn" class="btn btn-primary">Visa schema</button>
                <button id="scheduleClear" class="btn btn-secondary" hidden>Rensa sökning</button>
            </div>
        </div>

        <div class="card">
            <div class="card-header">
                <h3 id="appt-list-title">Bokningar</h3>
                <div class="toolbar">
                    ${myDeptToggleHtml()}
                    <div class="segmented" id="viewModeToggle">
                        <button type="button" class="segmented-btn ${viewMode === "week" ? "active" : ""}" data-mode="week">Vecka</button>
                        <button type="button" class="segmented-btn ${viewMode === "list" ? "active" : ""}" data-mode="list">Lista</button>
                    </div>
                </div>
            </div>
            <div id="weekNav" class="week-nav" ${viewMode === "list" ? "hidden" : ""}>
                <button type="button" class="btn btn-secondary btn-sm" id="weekPrev">← Föreg. vecka</button>
                <span id="weekLabel" class="week-label"></span>
                <button type="button" class="btn btn-secondary btn-sm" id="weekToday">Idag</button>
                <button type="button" class="btn btn-secondary btn-sm" id="weekNext">Nästa vecka →</button>
            </div>
            <div id="apptBody"></div>
        </div>
    `;

    loadStaffOptions().then(loadDepartmentMap).then(loadAll);

    wireToolbar();
    document.getElementById("scheduleBtn").addEventListener("click", handleScheduleSearch);
    document.getElementById("scheduleClear").addEventListener("click", clearScheduleSearch);

    if (canCreate) {
        loadCreateFormOptions();
        document.getElementById("createApptForm").addEventListener("submit", handleCreate);
    }
}

function myDeptToggleHtml() {
    if (me.departmentId == null) return "";
    return `
        <div class="segmented" id="deptScopeToggle">
            <button type="button" class="segmented-btn ${deptScope === "mine" ? "active" : ""}" data-scope="mine">Min avdelning</button>
            <button type="button" class="segmented-btn ${deptScope === "all" ? "active" : ""}" data-scope="all">Alla avdelningar</button>
        </div>`;
}

function wireToolbar() {
    document.querySelectorAll("#viewModeToggle .segmented-btn").forEach((btn) => {
        btn.addEventListener("click", () => {
            viewMode = btn.dataset.mode;
            document.querySelectorAll("#viewModeToggle .segmented-btn").forEach((b) => b.classList.toggle("active", b === btn));
            document.getElementById("weekNav").hidden = viewMode !== "week";
            renderBody();
        });
    });

    const deptToggle = document.getElementById("deptScopeToggle");
    if (deptToggle) {
        deptToggle.querySelectorAll(".segmented-btn").forEach((btn) => {
            btn.addEventListener("click", () => {
                deptScope = btn.dataset.scope;
                deptToggle.querySelectorAll(".segmented-btn").forEach((b) => b.classList.toggle("active", b === btn));
                renderBody();
            });
        });
    }

    document.getElementById("weekPrev").addEventListener("click", () => { weekStart = addDays(weekStart, -7); renderBody(); });
    document.getElementById("weekNext").addEventListener("click", () => { weekStart = addDays(weekStart, 7); renderBody(); });
    document.getElementById("weekToday").addEventListener("click", () => { weekStart = startOfWeek(new Date()); renderBody(); });
}

function staffName(id) {
    const s = staffMap[id];
    return s ? `${s.firstName} ${s.lastName}` : id ? "Personal #" + id : "-";
}

function deptName(id) {
    return departmentMap[id]?.name || (id ? "Avd #" + id : "-");
}

function scopedRows() {
    if (scheduleSearchRows) return scheduleSearchRows;
    if (deptScope === "mine" && me.departmentId != null) {
        return allAppointments.filter((a) => a.departmentId === me.departmentId);
    }
    return allAppointments;
}

function renderBody() {
    document.getElementById("appt-list-title").textContent = scheduleSearchRows ? scheduleSearchLabel : "Bokningar";
    document.getElementById("weekNav").hidden = viewMode !== "week" || !!scheduleSearchRows;

    const rows = scopedRows();
    const body = document.getElementById("apptBody");

    if (viewMode === "week" && !scheduleSearchRows) {
        document.getElementById("weekLabel").textContent = weekRangeLabel(weekStart);
        body.innerHTML = `<div id="calContainer" class="cal-wrap"></div>`;
        renderWeekCalendar(document.getElementById("calContainer"), {
            appointments: rows,
            weekStart,
            staffName,
            deptName,
        });
        return;
    }

    body.innerHTML = `
        <div class="table-wrap">
            <table>
                <thead><tr><th>Tid</th><th>Patient</th><th>Personal</th><th>Avdelning</th><th>Status</th><th>Notering</th></tr></thead>
                <tbody>${rows.length ? [...rows].sort((a, b) => new Date(b.scheduledAt) - new Date(a.scheduledAt)).map(apptRow).join("") : emptyRow(6)}</tbody>
            </table>
        </div>
    `;
}

function apptRow(a) {
    const touchesMine = me.departmentId != null && a.departmentId === me.departmentId;
    return `
        <tr class="${touchesMine ? "row-highlight" : ""}">
            <td><strong>${formatDateTime(a.scheduledAt)}</strong></td>
            <td>#${safe(a.patientId)}</td>
            <td>${staffName(a.staffId)}</td>
            <td>${deptName(a.departmentId)}</td>
            <td>${statusBadge(a.status)}</td>
            <td>${safe(a.note)}</td>
        </tr>`;
}

async function loadAll() {
    const body = document.getElementById("apptBody");
    body.innerHTML = loadingRow(6);
    try {
        allAppointments = await getAppointments();
        renderBody();
    } catch (err) {
        body.innerHTML = errorRow(6, err);
    }
}

function clearScheduleSearch() {
    scheduleSearchRows = null;
    document.getElementById("scheduleClear").hidden = true;
    renderBody();
}

async function handleScheduleSearch() {
    const staffId = document.getElementById("scheduleStaff").value;
    const date = document.getElementById("scheduleDate").value;
    if (!staffId || !date) return;

    viewMode = "list";
    document.querySelectorAll("#viewModeToggle .segmented-btn").forEach((b) => b.classList.toggle("active", b.dataset.mode === "list"));
    scheduleSearchLabel = `Schema — ${staffName(Number(staffId))}, ${date}`;
    document.getElementById("apptBody").innerHTML = loadingRow(6, "Söker...");
    try {
        scheduleSearchRows = await getScheduleFor(staffId, date);
        document.getElementById("scheduleClear").hidden = false;
        renderBody();
    } catch (err) {
        document.getElementById("apptBody").innerHTML = errorRow(6, err);
    }
}

async function loadStaffOptions() {
    try {
        staffList = await getStaff();
        staffMap = toMap(staffList);
        const select = document.getElementById("scheduleStaff");
        select.innerHTML = '<option value="">Välj personal...</option>' +
            staffList.map((s) => `<option value="${s.id}">${s.firstName} ${s.lastName}</option>`).join("");
    } catch {
        staffList = [];
    }
}

async function loadDepartmentMap() {
    try {
        departmentMap = toMap(await getDepartments());
    } catch {
        departmentMap = {};
    }
}

async function loadCreateFormOptions() {
    const patientSelect = document.getElementById("apPatient");
    const staffSelect = document.getElementById("apStaff");
    const deptSelect = document.getElementById("apDepartment");

    try {
        const patients = await getPatients();
        patientSelect.innerHTML = '<option value="">Välj patient...</option>' +
            patients.map((p) => `<option value="${p.id}">${p.firstName} ${p.lastName} (#${p.id})</option>`).join("");
    } catch {
        patientSelect.innerHTML = '<option value="">Kunde inte ladda patienter</option>';
    }

    if (staffList.length === 0) await loadStaffOptions();
    staffSelect.innerHTML = '<option value="">Välj personal...</option>' +
        staffList.map((s) => `<option value="${s.id}">${s.firstName} ${s.lastName}</option>`).join("");

    try {
        if (Object.keys(departmentMap).length === 0) await loadDepartmentMap();
        const list = Object.values(departmentMap);
        deptSelect.innerHTML = '<option value="">Välj avdelning...</option>' +
            list.map((d) => `<option value="${d.id}" ${me.departmentId === d.id ? "selected" : ""}>${d.name}</option>`).join("");
    } catch {
        deptSelect.innerHTML = '<option value="">Kunde inte ladda avdelningar</option>';
    }
}

async function handleCreate(e) {
    e.preventDefault();
    const alertBox = document.getElementById("appt-alert");
    alertBox.hidden = true;

    const payload = {
        patientId: Number(document.getElementById("apPatient").value),
        staffId: Number(document.getElementById("apStaff").value),
        departmentId: Number(document.getElementById("apDepartment").value),
        scheduledAt: new Date(document.getElementById("apWhen").value).toISOString(),
        note: document.getElementById("apNote").value.trim(),
        status: document.getElementById("apStatus").value,
    };

    try {
        await createAppointment(payload);
        document.getElementById("createApptForm").reset();
        toastSuccess("Bokningen skapades.");
        loadAll();
    } catch (err) {
        alertBox.textContent = err.message;
        alertBox.hidden = false;
    }
}
