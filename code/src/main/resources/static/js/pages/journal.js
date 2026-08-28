import { getStaffPatients, getCurrentUser } from "../api.js";
import { loadCurrentUser } from "../auth.js";
import { safe, formatDate, loadingRow, emptyRow, errorRow, setTopbar } from "../ui.js";
import { renderDetail } from "./patients.js";

// Quick-access journal view for doctors/nurses — only patients they're the
// responsible staff for on a care contact, so they can jump straight to
// writing/reading journal entries without searching the full patient list.
export async function render(container) {
    const me = await loadCurrentUser(getCurrentUser);
    await renderMyPatients(container, me);
}

async function renderMyPatients(container, me) {
    setTopbar("Journal", "Dina patienter — snabb åtkomst till journalanteckningar");

    if (!me.staffId) {
        container.innerHTML = '<div class="alert alert-error">Ditt konto är inte kopplat till en personalprofil.</div>';
        return;
    }

    container.innerHTML = `
        <div class="card">
            <div class="card-header">
                <h3>Mina patienter</h3>
                <span class="api-badge">GET /staff/{id}/patients</span>
            </div>
            <div class="table-wrap">
                <table>
                    <thead><tr><th>Namn</th><th>Personnummer</th><th>ID</th><th>Senaste vårdkontakt</th><th></th></tr></thead>
                    <tbody id="journal-patients-tbody">${loadingRow(4)}</tbody>
                </table>
            </div>
        </div>
    `;

    await loadMyPatients(container, me);
}

async function loadMyPatients(container, me) {
    const tbody = document.getElementById("journal-patients-tbody");
    try {
        const rows = await getStaffPatients(me.staffId);
        if (!tbody) return; // page navigated away while loading
        if (!rows || rows.length === 0) {
            tbody.innerHTML = emptyRow(4, "Inga patienter kopplade till dig ännu.");
            return;
        }
        tbody.innerHTML = rows.map(patientRow).join("");
        attachRowHandlers(container, me);
    } catch (err) {
        if (tbody) tbody.innerHTML = errorRow(4, err);
    }
}

function patientRow(p) {
    return `
        <tr class="clickable-row" data-id="${p.patientId}">
            <td><strong>${safe(p.firstName)} ${safe(p.lastName)}</strong></td>
            <td>${safe(p.personalNumber)}</td>
            <td>#${safe(p.patientId)}</td>
            <td>${[safe(p.reason), p.admitDate ? formatDate(p.admitDate) : ""].filter(Boolean).join(" · ")}</td>
            <td class="text-right"><button type="button" class="action-link">Öppna journal →</button></td>
        </tr>`;
}

function attachRowHandlers(container, me) {
    document.querySelectorAll("#journal-patients-tbody tr.clickable-row").forEach((row) => {
        row.addEventListener("click", () => openPatientJournal(container, me, row.dataset.id));
    });
}

async function openPatientJournal(container, me, patientId) {
    await renderDetail(container, me, patientId, {
        defaultTab: "journal",
        backLabel: "← Tillbaka till min journal",
        onBack: () => renderMyPatients(container, me),
    });
}
