import { getAppointments, getScheduleFor, createAppointment, getPatients, getStaff, getDepartments, getCurrentUser } from "../api.js";
import { loadCurrentUser } from "../auth.js";
import { safe, formatDateTime, toDateInputValue, toDateTimeLocalValue, statusBadge, loadingRow, emptyRow, errorRow, toastSuccess } from "../ui.js";

let staffList = [];

export async function render(container) {
    const me = await loadCurrentUser(getCurrentUser);
    const canCreate = me.role === "RECEPTIONIST";

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
                <button id="scheduleClear" class="btn btn-secondary">Visa alla bokningar</button>
            </div>
        </div>

        <div class="card">
            <div class="card-header">
                <h3 id="appt-list-title">Alla bokningar</h3>
                <span class="api-badge">GET /appointments</span>
            </div>
            <div class="table-wrap">
                <table>
                    <thead><tr><th>Tid</th><th>Patient</th><th>Personal</th><th>Avdelning</th><th>Status</th><th>Notering</th></tr></thead>
                    <tbody id="appt-tbody">${loadingRow(6)}</tbody>
                </table>
            </div>
        </div>
    `;

    loadStaffOptions();
    loadAll();

    document.getElementById("scheduleBtn").addEventListener("click", handleScheduleSearch);
    document.getElementById("scheduleClear").addEventListener("click", () => {
        document.getElementById("appt-list-title").textContent = "Alla bokningar";
        loadAll();
    });

    if (canCreate) {
        loadCreateFormOptions();
        document.getElementById("createApptForm").addEventListener("submit", handleCreate);
    }
}

function apptRow(a) {
    return `
        <tr>
            <td><strong>${formatDateTime(a.scheduledAt)}</strong></td>
            <td>#${safe(a.patientId)}</td>
            <td>${staffName(a.staffId)}</td>
            <td>#${safe(a.departmentId)}</td>
            <td>${statusBadge(a.status)}</td>
            <td>${safe(a.note)}</td>
        </tr>`;
}

function staffName(id) {
    const s = staffList.find((st) => st.id === id);
    return s ? `${s.firstName} ${s.lastName}` : id ? "Personal #" + id : "-";
}

async function loadAll() {
    const tbody = document.getElementById("appt-tbody");
    tbody.innerHTML = loadingRow(6);
    try {
        const rows = await getAppointments();
        rows.sort((a, b) => new Date(b.scheduledAt) - new Date(a.scheduledAt));
        tbody.innerHTML = rows.length ? rows.map(apptRow).join("") : emptyRow(6);
    } catch (err) {
        tbody.innerHTML = errorRow(6, err);
    }
}

async function handleScheduleSearch() {
    const staffId = document.getElementById("scheduleStaff").value;
    const date = document.getElementById("scheduleDate").value;
    const tbody = document.getElementById("appt-tbody");
    if (!staffId || !date) return;

    document.getElementById("appt-list-title").textContent = `Schema — ${staffName(Number(staffId))}, ${date}`;
    tbody.innerHTML = loadingRow(6, "Söker...");
    try {
        const rows = await getScheduleFor(staffId, date);
        tbody.innerHTML = rows.length ? rows.map(apptRow).join("") : emptyRow(6, "Inga bokningar för det valet.");
    } catch (err) {
        tbody.innerHTML = errorRow(6, err);
    }
}

async function loadStaffOptions() {
    try {
        staffList = await getStaff();
        const select = document.getElementById("scheduleStaff");
        select.innerHTML = '<option value="">Välj personal...</option>' +
            staffList.map((s) => `<option value="${s.id}">${s.firstName} ${s.lastName}</option>`).join("");
    } catch {
        staffList = [];
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

    // staffList populated by loadStaffOptions, called in parallel — wait for it if not ready yet.
    if (staffList.length === 0) await loadStaffOptions();
    staffSelect.innerHTML = '<option value="">Välj personal...</option>' +
        staffList.map((s) => `<option value="${s.id}">${s.firstName} ${s.lastName}</option>`).join("");

    try {
        const departments = await getDepartments();
        deptSelect.innerHTML = '<option value="">Välj avdelning...</option>' +
            departments.map((d) => `<option value="${d.id}">${d.name}</option>`).join("");
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
