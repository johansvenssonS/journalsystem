import { getPrescriptions, getPrescriptionsByPersonalNumber, createPrescription, getPatients, getCurrentUser } from "../api.js";
import { loadCurrentUser } from "../auth.js";
import { safe, formatDate, statusBadge, loadingRow, emptyRow, errorRow, toastSuccess } from "../ui.js";
import { can } from "../access.js";

export async function render(container) {
    const me = await loadCurrentUser(getCurrentUser);
    const canCreate = can.createPrescription(me);
    const canLookup = can.lookupPrescription(me);

    container.innerHTML = `
        <header class="page-header">
            <div>
                <h2>Recept</h2>
                <p>Skriv ut och hantera recept</p>
            </div>
        </header>

        ${canCreate ? `
        <div class="card mb-4">
            <div class="card-header">
                <h3>Nytt recept</h3>
                <span class="api-badge">POST /prescriptions</span>
            </div>
            <div id="rx-alert" class="alert alert-error" hidden></div>
            <form id="createRxForm">
                <div class="form-grid">
                    <div class="form-group full-width">
                        <label for="rxPatient">Patient</label>
                        <select id="rxPatient" class="form-control" required><option value="">Laddar patienter...</option></select>
                    </div>
                    <div class="form-group">
                        <label for="rxMedication">Läkemedel</label>
                        <input type="text" id="rxMedication" class="form-control" placeholder="t.ex. Metformin 500mg" required>
                    </div>
                    <div class="form-group">
                        <label for="rxDosage">Dosering</label>
                        <input type="text" id="rxDosage" class="form-control" placeholder="t.ex. 1 tablett × 2/dag">
                    </div>
                    <div class="form-group">
                        <label for="rxStart">Startdatum</label>
                        <input type="date" id="rxStart" class="form-control">
                    </div>
                    <div class="form-group">
                        <label for="rxEnd">Slutdatum</label>
                        <input type="date" id="rxEnd" class="form-control">
                    </div>
                    <div class="form-group">
                        <label for="rxActive">Status</label>
                        <select id="rxActive" class="form-control">
                            <option value="true">Aktivt</option>
                            <option value="false">Inaktivt</option>
                        </select>
                    </div>
                </div>
                <button type="submit" class="btn btn-primary">Spara recept</button>
            </form>
        </div>` : ""}

        ${canLookup ? `
        <div class="card mb-4">
            <div class="card-header">
                <h3>Slå upp recept på personnummer</h3>
                <span class="api-badge">GET /prescriptions/personal-number/{pn}</span>
            </div>
            <div class="search-row">
                <input type="text" id="rxLookupInput" class="form-control flex-grow" placeholder="Personnummer, t.ex. 19800101-1234">
                <button id="rxLookupBtn" class="btn btn-primary">Hämta</button>
                <button id="rxLookupClear" class="btn btn-secondary">Visa alla</button>
            </div>
        </div>` : ""}

        <div class="card">
            <div class="card-header">
                <h3>${canLookup ? "Recept" : "Alla recept"}</h3>
                <span class="api-badge">GET /prescriptions</span>
            </div>
            <div class="table-wrap">
                <table>
                    <thead><tr><th>Patient-ID</th><th>Läkemedel</th><th>Dosering</th><th>Startdatum</th><th>Slutdatum</th><th>Status</th></tr></thead>
                    <tbody id="rx-tbody">${loadingRow(6)}</tbody>
                </table>
            </div>
        </div>
    `;

    loadAll();

    if (canCreate) {
        loadPatientOptions();
        document.getElementById("createRxForm").addEventListener("submit", handleCreate);
    }

    if (canLookup) {
        document.getElementById("rxLookupBtn").addEventListener("click", handleLookup);
        document.getElementById("rxLookupClear").addEventListener("click", loadAll);
        document.getElementById("rxLookupInput").addEventListener("keydown", (e) => {
            if (e.key === "Enter") { e.preventDefault(); handleLookup(); }
        });
    }
}

function rxRow(p) {
    return `
        <tr>
            <td>#${safe(p.patientId)}</td>
            <td><strong>${safe(p.medication)}</strong></td>
            <td>${safe(p.dosage)}</td>
            <td>${formatDate(p.issuedDate)}</td>
            <td>${formatDate(p.endDate)}</td>
            <td>${statusBadge(p.active)}</td>
        </tr>`;
}

async function loadAll() {
    const tbody = document.getElementById("rx-tbody");
    tbody.innerHTML = loadingRow(6);
    try {
        const rows = await getPrescriptions();
        tbody.innerHTML = rows.length ? rows.map(rxRow).join("") : emptyRow(6);
    } catch (err) {
        tbody.innerHTML = errorRow(6, err);
    }
}

async function handleLookup() {
    const pn = document.getElementById("rxLookupInput").value.trim();
    const tbody = document.getElementById("rx-tbody");
    if (!pn) { loadAll(); return; }
    tbody.innerHTML = loadingRow(6, "Söker...");
    try {
        const rows = await getPrescriptionsByPersonalNumber(pn);
        tbody.innerHTML = rows.length ? rows.map(rxRow).join("") : emptyRow(6, "Inga recept hittades för det personnumret.");
    } catch (err) {
        tbody.innerHTML = errorRow(6, err);
    }
}

async function loadPatientOptions() {
    const select = document.getElementById("rxPatient");
    try {
        const patients = await getPatients();
        select.innerHTML = '<option value="">Välj patient...</option>' +
            patients.map((p) => `<option value="${p.id}">${p.firstName} ${p.lastName} (#${p.id})</option>`).join("");
    } catch {
        select.innerHTML = '<option value="">Kunde inte ladda patienter</option>';
    }
}

async function handleCreate(e) {
    e.preventDefault();
    const alertBox = document.getElementById("rx-alert");
    alertBox.hidden = true;

    const me = await loadCurrentUser(getCurrentUser);
    const payload = {
        patientId: Number(document.getElementById("rxPatient").value),
        prescribedBy: me.staffId,
        medication: document.getElementById("rxMedication").value.trim(),
        dosage: document.getElementById("rxDosage").value.trim(),
        issuedDate: document.getElementById("rxStart").value || null,
        endDate: document.getElementById("rxEnd").value || null,
        active: document.getElementById("rxActive").value === "true",
    };

    try {
        await createPrescription(payload);
        document.getElementById("createRxForm").reset();
        toastSuccess("Receptet skapades.");
        loadAll();
    } catch (err) {
        alertBox.textContent = err.message;
        alertBox.hidden = false;
    }
}
