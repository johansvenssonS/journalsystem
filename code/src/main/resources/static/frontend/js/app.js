import {
    getPatients,
    getPatientById,
    getPatientContactById,
    getRoleById,
    getPatientMedicalById,
    getStaff,
    getAppointmentsByStaffAndDate
} from "./api.js";

// Elements
const loadPatientsBtn = document.getElementById("loadPatientsBtn");
const patientsList = document.getElementById("patientsList");

const patientByIdForm = document.getElementById("patientByIdForm");
const patientIdInput = document.getElementById("patientIdInput");
const patientByIdResult = document.getElementById("patientByIdResult");

const patientContactByIdForm = document.getElementById("patientContactByIdForm");
const patientContactIdInput = document.getElementById("patientContactIdInput");
const patientContactByIdResult = document.getElementById("patientContactByIdResult");

const roleByIdForm = document.getElementById("roleByIdForm");
const roleIdInput = document.getElementById("roleIdInput");
const roleByIdResult = document.getElementById("roleByIdResult");

const patientMedicalByIdForm = document.getElementById("patientMedicalByIdForm");
const patientMedicalIdInput = document.getElementById("patientMedicalIdInput");
const patientMedicalByIdResult = document.getElementById("patientMedicalByIdResult");

const scheduleForm = document.getElementById("scheduleForm");
const staffSelect = document.getElementById("staffSelect");
const scheduleDateInput = document.getElementById("scheduleDateInput");
const scheduleMsg = document.getElementById("scheduleMsg");
const scheduleList = document.getElementById("scheduleList");

let staffCache = [];

// Helpers
function safe(val) {
    return val ?? "-";
}

function showText(el, text, isError = false) {
    el.textContent = text;
    el.className = isError ? "msg error" : "msg ok";
}

function renderPatients(patients) {
    patientsList.innerHTML = "";
    patients.forEach((p) => {
        const li = document.createElement("li");
        li.textContent = `${safe(p.id)} | ${safe(p.firstName)} ${safe(p.lastName)} | PN: ${safe(p.personalNumber)}`;
        patientsList.appendChild(li);
    });
}

function toDateInputValue(d = new Date()) {
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, "0");
    const day = String(d.getDate()).padStart(2, "0");
    return `${year}-${month}-${day}`;
}

function buildStaffName(s) {
    return `${safe(s.firstName)} ${safe(s.lastName)}`.trim();
}

function populateStaffSelect(staffList) {
    staffSelect.innerHTML = `<option value="">-- Select staff --</option>`;
    staffList.forEach((s) => {
        const option = document.createElement("option");
        option.value = s.id;
        option.textContent = `${buildStaffName(s)} (PN: ${safe(s.personalNumber)})`;
        staffSelect.appendChild(option);
    });
}

function renderSchedule(appointments) {
    scheduleList.innerHTML = "";

    if (!appointments.length) {
        const li = document.createElement("li");
        li.textContent = "No appointments for selected staff/date.";
        scheduleList.appendChild(li);
        return;
    }

    const sorted = [...appointments].sort((a, b) =>
        (a.scheduledAt || "").localeCompare(b.scheduledAt || "")
    );

    sorted.forEach((a) => {
        const li = document.createElement("li");
        const when = a.scheduledAt ? new Date(a.scheduledAt).toLocaleString() : "-";
        li.textContent =
            `${when} | Patient ID: ${safe(a.patientId)} | Status: ${safe(a.status)} | Note: ${safe(a.note)}`;
        scheduleList.appendChild(li);
    });
}

async function loadStaffForSchedule() {
    try {
        staffCache = await getStaff();
        populateStaffSelect(staffCache);
        scheduleDateInput.value = toDateInputValue();
        showText(scheduleMsg, "Staff loaded.");
    } catch (err) {
        showText(scheduleMsg, `Failed to load staff: ${err.message}`, true);
    }
}

// Events
loadPatientsBtn.addEventListener("click", async () => {
    try {
        const patients = await getPatients();
        renderPatients(patients);
    } catch (err) {
        alert(`Failed to load patients: ${err.message}`);
    }
});

patientByIdForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    try {
        const id = Number(patientIdInput.value);
        if (!id) return;
        const p = await getPatientById(id);
        showText(
            patientByIdResult,
            `ID: ${safe(p.id)} | Name: ${safe(p.firstName)} ${safe(p.lastName)} | Personal number: ${safe(p.personalNumber)}`
        );
    } catch (err) {
        showText(patientByIdResult, err.message, true);
    }
});

patientContactByIdForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    try {
        const id = Number(patientContactIdInput.value);
        if (!id) return;
        const c = await getPatientContactById(id);
        showText(
            patientContactByIdResult,
            `ID: ${safe(c.id)} | Phone: ${safe(c.phone)} | Email: ${safe(c.email)} | Address: ${safe(c.address)}`
        );
    } catch (err) {
        showText(patientContactByIdResult, err.message, true);
    }
});

roleByIdForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    try {
        const id = Number(roleIdInput.value);
        if (!id) return;
        const r = await getRoleById(id);
        showText(roleByIdResult, `ID: ${safe(r.id)} | Title: ${safe(r.title)}`);
    } catch (err) {
        showText(roleByIdResult, err.message, true);
    }
});

patientMedicalByIdForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    try {
        const id = Number(patientMedicalIdInput.value);
        if (!id) return;
        const m = await getPatientMedicalById(id);
        showText(
            patientMedicalByIdResult,
            `ID: ${safe(m.id)} | Allergies: ${safe(m.allergies)} | Blood type: ${safe(m.bloodType)}`
        );
    } catch (err) {
        showText(patientMedicalByIdResult, err.message, true);
    }
});

scheduleForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    scheduleList.innerHTML = "";

    try {
        const staffId = Number(staffSelect.value);
        const date = scheduleDateInput.value;

        if (!staffId || !date) {
            showText(scheduleMsg, "Please select staff and date.", true);
            return;
        }

        const data = await getAppointmentsByStaffAndDate(staffId, date);
        renderSchedule(data);

        const selected = staffCache.find(s => Number(s.id) === staffId);
        const staffLabel = selected ? `${buildStaffName(selected)}` : `Staff ID ${staffId}`;
        showText(scheduleMsg, `Schedule loaded for ${staffLabel}.`);
    } catch (err) {
        showText(scheduleMsg, err.message, true);
    }
});

loadStaffForSchedule();
