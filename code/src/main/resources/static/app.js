import {
    getPatients,
    getPatientById,
    getAppointmentsByStaffAndDate,
    getCurrentUser,
    createPatient,
    getPatientDashboard
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
    // Attach Create Patient Form Listener
    const createForm = document.getElementById("createPatientForm");
    if (createForm) {
        createForm.addEventListener("submit", handleCreatePatient);
    }

    // Patient Dashboard Modal close handlers
    const pdModal = document.getElementById("patientDashboardModal");
    const pdClose = document.getElementById("pd-close");
    if (pdClose) {
        pdClose.addEventListener("click", closePatientDashboard);
    }
    if (pdModal) {
        // Close when clicking the dark overlay itself, not the modal content
        pdModal.addEventListener("click", (e) => {
            if (e.target === pdModal) closePatientDashboard();
        });
    }
    document.addEventListener("keydown", (e) => {
        if (e.key === "Escape") closePatientDashboard();
    });
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
// 3. Form Submit Handler Function
async function handleCreatePatient(event) {
    event.preventDefault();

    const firstName = document.getElementById("patientFirstName").value.trim();
    const lastName = document.getElementById("patientLastName").value.trim();
    const personalNumber = document.getElementById("patientPersonalNumber").value.trim();

    const payload = {
        firstName,
        lastName,
        personalNumber
    };

    try {
        await createPatient(payload);

        // Reset form and reload list upon success
        document.getElementById("createPatientForm").reset();
        await renderPatientsList();
        alert("Patienten skapades!");
    } catch (err) {
        alert(`Kunde inte skapa patient: ${err.message}`);
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
        <td class="text-right"><a href="#" class="action-link" data-patient-id="${p.id}">Öppna</a></td>
    `;
    tr.querySelector(".action-link").addEventListener("click", (e) => {
        e.preventDefault();
        openPatientDashboard(p.id);
    });
    tbody.appendChild(tr);
}

// ---- 5. Patient Dashboard (US-32 / US-33) ----

function openPatientDashboard(patientId) {
    const modal = document.getElementById("patientDashboardModal");
    const loading = document.getElementById("pd-loading");
    const errorEl = document.getElementById("pd-error");
    const content = document.getElementById("pd-content");

    // Reset modal to loading state and show it
    loading.style.display = "block";
    errorEl.style.display = "none";
    content.style.display = "none";
    modal.classList.add("open");

    loadPatientDashboard(patientId);
}

function closePatientDashboard() {
    const modal = document.getElementById("patientDashboardModal");
    modal.classList.remove("open");
}

async function loadPatientDashboard(patientId) {
    const loading = document.getElementById("pd-loading");
    const errorEl = document.getElementById("pd-error");
    const content = document.getElementById("pd-content");

    try {
        const dashboard = await getPatientDashboard(patientId);

        // NOTE: field names below (patient.firstName, careContacts, journalEntries,
        // diagnoses, and each item's sub-fields) are best-guess based on the DTO
        // getters used server-side. Adjust here if your actual DTO fields differ.
        const patient = dashboard.patient ?? {};
        const careContacts = dashboard.careContacts ?? [];
        const journalEntries = dashboard.journalEntries ?? [];
        const diagnoses = dashboard.diagnoses ?? [];

        document.getElementById("pd-name").textContent =
            `${safe(patient.firstName)} ${safe(patient.lastName)}`;
        document.getElementById("pd-fullname").textContent =
            `${safe(patient.firstName)} ${safe(patient.lastName)}`;
        document.getElementById("pd-personalnumber").textContent = safe(patient.personalNumber);
        document.getElementById("pd-id").textContent = safe(patient.id ?? patientId);

        renderCareContacts(careContacts);
        renderJournalEntries(journalEntries);
        renderDiagnoses(diagnoses);

        loading.style.display = "none";
        content.style.display = "block";

    } catch (err) {
        loading.style.display = "none";
        errorEl.style.display = "block";
        errorEl.textContent = `Kunde inte ladda patientöversikt: ${err.message}`;
    }
}

function renderCareContacts(careContacts) {
    const tbody = document.getElementById("pd-carecontacts-tbody");
    tbody.innerHTML = "";

    if (!careContacts || careContacts.length === 0) {
        tbody.innerHTML = `<tr><td colspan="5">Inga vårdkontakter registrerade.</td></tr>`;
        return;
    }

    careContacts.forEach(c => {
        const tr = document.createElement("tr");
        const date = c.admitDate ? new Date(c.admitDate).toLocaleDateString() : "-";
        tr.innerHTML = `
            <td>${date}</td>
            <td>${safe(c.departmentId)}</td>
            <td>${safe(c.responsibleId)}</td>
            <td>${safe(c.reason)}</td>
            <td><span class="badge badge-gray">${safe(c.status)}</span></td>
        `;
        tbody.appendChild(tr);
    });
}

function renderJournalEntries(journalEntries) {
    const tbody = document.getElementById("pd-journal-tbody");
    tbody.innerHTML = "";

    if (!journalEntries || journalEntries.length === 0) {
        tbody.innerHTML = `<tr><td colspan="3">Inga journalposter registrerade.</td></tr>`;
        return;
    }

    journalEntries.forEach(j => {
        const tr = document.createElement("tr");
        const date = j.createdAt ? new Date(j.createdAt).toLocaleDateString() : "-";
        tr.innerHTML = `
            <td>${date}</td>
            <td><span class="badge badge-purple">${safe(j.type)}</span></td>
            <td>${safe(j.content)}</td>
        `;
        tbody.appendChild(tr);
    });
}

function renderDiagnoses(diagnoses) {
    const tbody = document.getElementById("pd-diagnoses-tbody");
    tbody.innerHTML = "";

    if (!diagnoses || diagnoses.length === 0) {
        tbody.innerHTML = `<tr><td colspan="4">Inga diagnoser registrerade.</td></tr>`;
        return;
    }

    diagnoses.forEach(d => {
        const tr = document.createElement("tr");
        const date = d.diagnosedDate ? new Date(d.diagnosedDate).toLocaleDateString() : "-";
        tr.innerHTML = `
            <td>${date}</td>
            <td>${safe(d.icd10Code)}</td>
            <td>${safe(d.name)}</td>
            <td>${safe(d.description)}</td>
        `;
        tbody.appendChild(tr);
    });
}
// only for Dr. make it dynamic by adding role info to the whoAmI function
async function renderCurrentUser(){
    const username = document.getElementById("current-user-name");

// 1. Get raw string from backend ("magnus.karlsson.")
    const rawUser = await getCurrentUser();

    // 2. Format: "magnus.karlsson." ➔ "Dr. Magnus Karlsson"
    const formattedName = "Dr. " + rawUser
        .split('.')
        .filter(Boolean) // Removes trailing empty strings
        .map(part => part.charAt(0).toUpperCase() + part.slice(1).toLowerCase())
        .join(' ');

    // 3. Update the DOM
    username.textContent = formattedName;
}



document.addEventListener("DOMContentLoaded", renderCurrentUser);