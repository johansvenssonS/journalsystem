import { getAppointments, getScheduleFor, createAppointment, getPatients, getStaff, getDepartments, getCurrentUser } from "../api.js";
import { loadCurrentUser } from "../auth.js";
import { safe, formatDateTime, toDateInputValue, toDateTimeLocalValue, statusBadgeClass, loadingRow, errorRow, toastSuccess, toMap, setTopbar } from "../ui.js";
import { can } from "../access.js";
import { renderWeekCalendar, renderListView, startOfWeek, addDays, weekRangeLabel } from "../components/calendar.js";

let staffList = [];
let staffMap = {};
let departmentMap = {};
let allAppointments = [];
let me = null;

let viewMode = "week"; // "week" | "list"
let deptScope = "mine"; // "mine" | "all"
let weekStart = startOfWeek(new Date());
let scheduleSearchRows = null; // set when the staff-schedule search is active; overrides the normal view
let scheduleSearchLabel = "";

export async function render(container) {
    me = await loadCurrentUser(getCurrentUser);
    setTopbar("Bokningar", "Boka och sök i schemalagda besök");
    const canCreate = can.createAppointment(me);
    deptScope = me.departmentId != null && !can.viewAllAppointments(me) ? "mine" : "all";

    container.innerHTML = `
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
                            <option value="scheduled">Bokad</option>
                            <option value="completed">Genomförd</option>
                            <option value="cancelled_by_staff">Avbokad</option>
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
                <button type="button" class="week-nav-btn" id="weekPrev">← Föreg. vecka</button>
                <span id="weekLabel" class="week-label"></span>
                <button type="button" class="week-nav-btn" id="weekToday">Idag</button>
                <button type="button" class="week-nav-btn" id="weekNext">Nästa vecka →</button>
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
    const titleEl = document.getElementById("appt-list-title");
    if (!titleEl) return; // page navigated away before this async render landed
    titleEl.textContent = scheduleSearchRows ? scheduleSearchLabel : "Bokningar";
    document.getElementById("weekNav").hidden = viewMode !== "week" || !!scheduleSearchRows;

    const rows = scopedRows();
    const body = document.getElementById("apptBody");

    if (scheduleSearchRows) {
        body.innerHTML = rows.length
            ? `<div class="row-list">${rows.map(searchRow).join("")}</div>`
            : `<p class="text-muted" style="padding:8px 4px;">Inga bokningar för det valet.</p>`;
        return;
    }

    if (viewMode === "week") {
        document.getElementById("weekLabel").textContent = weekRangeLabel(weekStart);
        body.innerHTML = `<div id="calContainer" class="cal-wrap"></div>`;
        renderWeekCalendar(document.getElementById("calContainer"), { appointments: rows, weekStart, staffName, deptName });
        return;
    }

    body.innerHTML = `<div id="calListContainer"></div>`;
    renderListView(document.getElementById("calListContainer"), { appointments: rows, weekStart, staffName, deptName });
}

function searchRow(a) {
    const accent = statusBadgeClass(a.status).replace("badge-", "");
    return `
        <div class="row-list-item">
            <div class="row-list-time mono">${formatDateTime(a.scheduledAt)}</div>
            <div class="row-list-accent row-list-accent-${accent}"></div>
            <div class="row-list-body">
                <div class="row-list-title">Patient #${safe(a.patientId)}</div>
                <div class="row-list-meta">${[staffName(a.staffId), deptName(a.departmentId), a.note].filter(Boolean).join(" · ")}</div>
            </div>
            <span class="badge ${statusBadgeClass(a.status)}">${safe(a.status)}</span>
        </div>`;
}

async function loadAll() {
    const body = document.getElementById("apptBody");
    if (!body) return; // page navigated away while staff/department options were loading
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

    scheduleSearchLabel = `Schema — ${staffName(Number(staffId))}, ${date}`;
    document.getElementById("apptBody").innerHTML = loadingRow(6, "Söker...");
    try {
        scheduleSearchRows = await getScheduleFor(staffId, date);
        const clearBtn = document.getElementById("scheduleClear");
        if (clearBtn) clearBtn.hidden = false;
        renderBody();
    } catch (err) {
        const body = document.getElementById("apptBody");
        if (body) body.innerHTML = errorRow(6, err);
    }
}

async function loadStaffOptions() {
    try {
        staffList = await getStaff();
        staffMap = toMap(staffList);
        const select = document.getElementById("scheduleStaff");
        if (!select) return; // page navigated away while staff were loading
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
