import {
    getPatients,
    getPatientById,
    getAppointmentsByStaffAndDate
} from "./api.js";

// Helper Functions (Kept from your original file)
function safe(val) {
    return val ?? "-";
}

function toDateInputValue(d = new Date()) {
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, "0");
    const day = String(d.getDate()).padStart(2, "0");
    return `${year}-${month}-${day}`;
}

// 1. Navigation Logic (SPA)
document.addEventListener('DOMContentLoaded', () => {
    const menuItems = document.querySelectorAll('.menu-item');
    const pages = document.querySelectorAll('.page');

    menuItems.forEach(item => {
        item.addEventListener('click', (e) => {
            e.preventDefault();

            // Handle Visual Active State
            menuItems.forEach(nav => nav.classList.remove('active'));
            item.classList.add('active');

            // Switch Pages
            const targetPageId = item.getAttribute('data-target');
            pages.forEach(page => {
                if (page.id === targetPageId) {
                    page.classList.add('active');
                } else {
                    page.classList.remove('active');
                }
            });

            // Trigger Fetching Data When a Tab is Opened
            loadDataForPage(targetPageId);
        });
    });

    // Load Default Data on Startup
    loadDataForPage('startsida');

    // Attach Search Button Listener on Patients Page
    const searchBtn = document.getElementById("searchPatientBtn");
    if(searchBtn) {
        searchBtn.addEventListener("click", handleSearchPatient);
    }
});

function loadDataForPage(pageId) {
    if (pageId === 'startsida') {
        renderDashboard();
    } else if (pageId === 'patienter') {
        renderPatientsList();
    }
}

// 2. Dashboard Render (Dagens Bokningar)
async function renderDashboard() {
    const tbody = document.getElementById("dashboard-tbody");
    tbody.innerHTML = `<tr><td colspan="4">Laddar bokningar...</td></tr>`;

    try {
        const today = toDateInputValue();
        // Uses your function from api.js (Hardcoded to Staff ID 1 for testing)
        const appointments = await getAppointmentsByStaffAndDate(1, today);

        tbody.innerHTML = ""; // Clear loader

        if (!appointments || appointments.length === 0) {
            tbody.innerHTML = `<tr><td colspan="4">Inga bokningar för idag.</td></tr>`;
            return;
        }

        appointments.forEach(a => {
            const when = a.scheduledAt ? new Date(a.scheduledAt).toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'}) : "-";
            const tr = document.createElement("tr");

            tr.innerHTML = `
                <td><strong>${when}</strong></td>
                <td>ID: ${safe(a.patientId)}</td>
                <td><span class="badge badge-gray">${safe(a.status)}</span></td>
                <td>${safe(a.note)}</td>
            `;
            tbody.appendChild(tr);
        });

    } catch (err) {
        tbody.innerHTML = `<tr><td colspan="4" style="color:red;">Kunde inte ladda bokningar: ${err.message}</td></tr>`;
    }
}

// 3. Patients Render (Mina Patienter)
async function renderPatientsList() {
    const tbody = document.getElementById("patients-tbody");
    tbody.innerHTML = `<tr><td colspan="5">Laddar patienter...</td></tr>`;

    try {
        // Uses your function from api.js
        const patients = await getPatients();
        tbody.innerHTML = "";

        if (!patients || patients.length === 0) {
            tbody.innerHTML = `<tr><td colspan="5">Inga patienter hittades.</td></tr>`;
            return;
        }

        patients.forEach(p => appendPatientRow(tbody, p));

    } catch (err) {
        tbody.innerHTML = `<tr><td colspan="5" style="color:red;">Kunde inte ladda patienter: ${err.message}</td></tr>`;
    }
}

// 4. Search Single Patient Feature
async function handleSearchPatient() {
    const input = document.getElementById("searchPatientInput").value;
    const tbody = document.getElementById("patients-tbody");

    if (!input) {
        renderPatientsList(); // Reload all if empty
        return;
    }

    try {
        tbody.innerHTML = `<tr><td colspan="5">Söker...</td></tr>`;
        const patient = await getPatientById(Number(input));

        tbody.innerHTML = "";
        appendPatientRow(tbody, patient);

    } catch(err) {
        tbody.innerHTML = `<tr><td colspan="5" style="color:red;">Ingen patient hittades med det ID:t.</td></tr>`;
    }
}

// Helper to draw a single patient row
function appendPatientRow(tbody, p) {
    const tr = document.createElement("tr");
    tr.innerHTML = `
        <td><strong>${safe(p.firstName)} ${safe(p.lastName)}</strong></td>
        <td>${safe(p.personalNumber)}</td>
        <td>${safe(p.id)}</td>
        <td><span class="badge badge-green">Aktiv</span></td>
        <td class="text-right"><a href="#" class="action-link">Öppna</a></td>
    `;
    tbody.appendChild(tr);
}