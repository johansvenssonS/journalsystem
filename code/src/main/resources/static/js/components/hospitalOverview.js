// Clickable hospital overview for the receptionist's Startsida — one plain card per
// real department, styled like the rest of the app, aggregated from live data (no
// fabricated bed counts/phone numbers/load status): admitted/waiting counts from care
// contacts, today's bookings from appointments, and on-duty staff from staff +
// staff-employments + roles.
import { getDepartments, getAppointments, getCareContacts, getStaff, getStaffEmployments, getRoles } from "../api.js";
import { safe, toDateInputValue, toMap, roleLabel } from "../ui.js";
import { OPEN_DEPARTMENT_KEY } from "../app.js";

let zones = [];
let openId = null;
let containerEl = null;

export async function renderHospitalOverview(container) {
    containerEl = container;
    openId = null; // start closed on every fresh mount
    container.innerHTML = `<p class="text-muted" style="padding:8px 4px;">Laddar avdelningar...</p>`;
    try {
        await loadZones();
        renderAll();
    } catch (err) {
        if (containerEl === container) {
            container.innerHTML = `<p class="text-muted" style="padding:8px 4px;">Kunde inte ladda sjukhusöversikten: ${err.message}</p>`;
        }
    }
}

async function loadZones() {
    const [departments, appts, contacts, staffList, employments, roles] = await Promise.all([
        getDepartments(), getAppointments(), getCareContacts(), getStaff(), getStaffEmployments(), getRoles(),
    ]);

    const today = toDateInputValue();
    const staffMap = toMap(staffList);
    const roleTitleById = new Map(roles.map((r) => [r.id, r.title]));

    zones = departments
        .map((d) => {
            const deptContacts = contacts.filter((c) => c.departmentId === d.id);
            const admitted = deptContacts.filter((c) => c.status === "admitted").length;
            const waiting = deptContacts.filter((c) => c.status === "planned").length;
            const todaysApptsCount = appts.filter((a) => a.departmentId === d.id && a.scheduledAt && a.scheduledAt.startsWith(today)).length;

            const people = employments
                .filter((e) => e.departmentId === d.id)
                .map((e) => {
                    const person = staffMap[e.staffId];
                    if (!person) return null;
                    return { ...person, roleLabel: roleLabel((roleTitleById.get(e.roleId) || "").replace(/^ROLE_/, "")) };
                })
                .filter(Boolean);

            return { ...d, admitted, waiting, todaysApptsCount, staffCount: people.length, people };
        })
        .sort((a, b) => a.id - b.id);
}

function renderAll() {
    if (!containerEl) return;
    containerEl.innerHTML = `
        <div class="hosp-dept-grid">
            ${zones.length ? zones.map(deptCardHtml).join("") : '<p class="text-muted" style="padding:8px 4px;">Inga avdelningar hittades.</p>'}
        </div>
        ${openId != null ? modalHtml(zones.find((z) => z.id === openId)) : ""}
    `;
    wireCards();
    if (openId != null) wireModal();
}

function deptCardHtml(z) {
    return `
        <div class="card hosp-dept-card" data-dept-id="${z.id}">
            <div class="hosp-dept-head">
                <span class="hosp-dept-badge">${safe(z.id)}</span>
                <div class="hosp-dept-title">
                    <div class="hosp-dept-name">${safe(z.name)}</div>
                    <div class="hosp-dept-spec text-muted">${safe(z.specializationName)} · Plan ${safe(z.floor)}</div>
                </div>
            </div>
            <div class="hosp-dept-stats">
                <div class="hosp-dept-stat"><div class="hosp-dept-stat-label">Inskrivna</div><div class="hosp-dept-stat-value">${z.admitted}</div></div>
                <div class="hosp-dept-stat"><div class="hosp-dept-stat-label">Väntande</div><div class="hosp-dept-stat-value">${z.waiting}</div></div>
                <div class="hosp-dept-stat"><div class="hosp-dept-stat-label">Bokningar idag</div><div class="hosp-dept-stat-value">${z.todaysApptsCount}</div></div>
                <div class="hosp-dept-stat"><div class="hosp-dept-stat-label">Personal</div><div class="hosp-dept-stat-value">${z.staffCount}</div></div>
            </div>
        </div>`;
}

function personHtml(p) {
    const initials = [p.firstName, p.lastName].filter(Boolean).map((s) => s[0]).join("").toUpperCase();
    return `
        <div class="hosp-person">
            <span class="hosp-person-avatar">${safe(initials)}</span>
            <div>
                <div class="hosp-person-name">${safe(p.firstName)} ${safe(p.lastName)}</div>
                <div class="hosp-person-role">${safe(p.roleLabel)}</div>
            </div>
        </div>`;
}

function modalHtml(z) {
    if (!z) return "";
    return `
        <div class="hosp-modal-overlay" id="hospModalOverlay">
            <div class="hosp-modal" id="hospModal">
                <div class="hosp-modal-head">
                    <div class="hosp-modal-bar"></div>
                    <div class="hosp-modal-title-wrap">
                        <div class="hosp-modal-eyebrow">Plan ${safe(z.floor)} · ${safe(z.specializationName)}</div>
                        <div class="hosp-modal-title">${safe(z.name)}</div>
                    </div>
                    <button type="button" class="hosp-modal-close" id="hospModalClose">×</button>
                </div>

                <div class="hosp-modal-stats">
                    <div class="hosp-modal-stat"><div class="hosp-modal-stat-label">Inskrivna</div><div class="hosp-modal-stat-value">${z.admitted}</div></div>
                    <div class="hosp-modal-stat"><div class="hosp-modal-stat-label">Väntande</div><div class="hosp-modal-stat-value">${z.waiting}</div></div>
                    <div class="hosp-modal-stat"><div class="hosp-modal-stat-label">Bokningar idag</div><div class="hosp-modal-stat-value">${z.todaysApptsCount}</div></div>
                    <div class="hosp-modal-stat"><div class="hosp-modal-stat-label">Personal</div><div class="hosp-modal-stat-value">${z.staffCount}</div></div>
                </div>

                <div class="hosp-modal-body">
                    <div>
                        <div class="hosp-modal-section-title">Personal i tjänst</div>
                        ${z.people.length ? `<div class="hosp-person-list">${z.people.map(personHtml).join("")}</div>` : '<p class="text-muted">Ingen personal registrerad.</p>'}
                    </div>
                    <div>
                        <div class="hosp-modal-section-title">Om avdelningen</div>
                        <div class="hosp-fact-row"><span>Specialisering</span><span class="mono">${safe(z.specializationName)}</span></div>
                        <div class="hosp-fact-row"><span>Plan</span><span class="mono">${safe(z.floor)}</span></div>
                        <div class="hosp-fact-row"><span>Avdelnings-ID</span><span class="mono">#${safe(z.id)}</span></div>
                    </div>
                </div>

                <div class="hosp-modal-footer">
                    <button type="button" class="btn btn-primary btn-sm" id="hospOpenBookings">Öppna bokningar för avdelningen</button>
                    <div style="flex:1;"></div>
                    <span class="api-badge">GET /departments/${z.id}/patients</span>
                </div>
            </div>
        </div>`;
}

function wireCards() {
    containerEl.querySelectorAll(".hosp-dept-card").forEach((card) => {
        card.addEventListener("click", () => {
            openId = Number(card.dataset.deptId);
            renderAll();
        });
    });
}

function wireModal() {
    const overlay = document.getElementById("hospModalOverlay");
    const modal = document.getElementById("hospModal");
    if (!overlay || !modal) return;
    overlay.addEventListener("click", closeModal);
    modal.addEventListener("click", (e) => e.stopPropagation());
    document.getElementById("hospModalClose").addEventListener("click", closeModal);
    document.getElementById("hospOpenBookings").addEventListener("click", () => {
        sessionStorage.setItem(OPEN_DEPARTMENT_KEY, String(openId));
        document.querySelector('.menu-item[data-target="appointments"]')?.click();
    });
}

function closeModal() {
    openId = null;
    renderAll();
}
