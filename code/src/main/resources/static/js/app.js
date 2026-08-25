import { requireAuth, logout, loadCurrentUser } from "./auth.js";
import { getCurrentUser, getReferrals, getPatientById, getPatientByPersonalNumber } from "./api.js";
import { roleLabel, toastError } from "./ui.js";
import { navForRole, canAccessPage, firstAllowedPage } from "./access.js";

requireAuth();

const content = document.getElementById("content");
const menu = document.getElementById("menu");
const searchForm = document.getElementById("topbarSearchForm");
const searchInput = document.getElementById("topbarSearchInput");

const PAGES = {
    dashboard: () => import("./pages/dashboard.js"),
    patients: () => import("./pages/patients.js"),
    journal: () => import("./pages/journal.js"),
    prescriptions: () => import("./pages/prescriptions.js"),
    referrals: () => import("./pages/referrals.js"),
    appointments: () => import("./pages/appointments.js"),
    directory: () => import("./pages/directory.js"),
    audit: () => import("./pages/audit.js"),
    "api-docs": () => import("./pages/apiDocs.js"),
};

// sessionStorage key patients.js checks on mount to auto-open a patient found via
// the top-bar search — keeps app.js and patients.js decoupled (no shared state file).
export const OPEN_PATIENT_KEY = "mj_open_patient_query";

// sessionStorage key appointments.js checks on mount to focus its view on a single
// department — set by the Sjukhusöversikt panel when its "Öppna bokningar" is clicked.
export const OPEN_DEPARTMENT_KEY = "mj_open_dept_appointments";

let activePage = null;
let currentUser = null;

function renderMenu(me, badges = {}) {
    menu.innerHTML = navForRole(me)
        .map((item) => `
            <a href="#" class="menu-item" data-target="${item.id}">
                <span class="icon">${item.icon}</span>
                <span>${item.label}</span>
                ${badges[item.id] ? `<span class="menu-item-badge">${badges[item.id]}</span>` : ""}
            </a>`)
        .join("");

    menu.querySelectorAll(".menu-item").forEach((item) => {
        item.addEventListener("click", (e) => {
            e.preventDefault();
            setActiveMenuItem(item.dataset.target);
            mountPage(item.dataset.target);
        });
    });
}

function setActiveMenuItem(pageId) {
    menu.querySelectorAll(".menu-item").forEach((nav) => nav.classList.toggle("active", nav.dataset.target === pageId));
}

async function mountPage(pageId) {
    if (!canAccessPage(currentUser, pageId)) {
        pageId = firstAllowedPage(currentUser);
        if (!pageId) return;
        setActiveMenuItem(pageId);
    }
    activePage = pageId;
    content.innerHTML = '<p class="text-muted">Laddar...</p>';
    try {
        const module = await PAGES[pageId]();
        if (activePage !== pageId) return; // a newer nav click won already
        await module.render(content);
    } catch (err) {
        if (activePage !== pageId) return;
        content.innerHTML = `<div class="alert alert-error">Kunde inte ladda sidan: ${err.message}</div>`;
        toastError(err);
    }
}

document.getElementById("logoutBtn").addEventListener("click", logout);

function renderCurrentUserBadge(me) {
    const fullName = [me.firstName, me.lastName].filter(Boolean).join(" ") || me.username;
    document.getElementById("current-user-name").textContent = fullName;
    document.getElementById("current-user-role").textContent =
        [roleLabel(me.role), me.departmentName].filter(Boolean).join(" · ") || "Ingen roll tilldelad";
    document.getElementById("user-avatar").textContent = fullName
        .split(" ")
        .filter(Boolean)
        .slice(0, 2)
        .map((p) => p[0].toUpperCase())
        .join("") || "?";
}

async function loadNavBadges(me) {
    const badges = {};
    if (canAccessPage(me, "referrals")) {
        try {
            const rows = await getReferrals();
            const open = rows.filter((r) => !["ACCEPTED", "ACCEPTERAD"].includes((r.status || "").toUpperCase()));
            if (open.length) badges.referrals = open.length;
        } catch {
            // nav badge is a nice-to-have — a failed count just means no badge shown
        }
    }
    return badges;
}

function setupTopbarSearch(me) {
    if (!canAccessPage(me, "patients")) return;
    searchForm.hidden = false;

    searchForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        const query = searchInput.value.trim();
        if (!query) return;

        try {
            const looksLikeId = /^\d{1,7}$/.test(query);
            const patient = looksLikeId ? await getPatientById(query) : await getPatientByPersonalNumber(query);
            sessionStorage.setItem(OPEN_PATIENT_KEY, String(patient.id));
            searchInput.value = "";
            setActiveMenuItem("patients");
            mountPage("patients");
        } catch {
            toastError(new Error(`Ingen patient hittades för ”${query}”.`));
        }
    });
}

async function bootstrap() {
    try {
        currentUser = await loadCurrentUser(getCurrentUser);
        renderCurrentUserBadge(currentUser);
    } catch (err) {
        document.getElementById("current-user-name").textContent = "Okänd användare";
        toastError(err);
        currentUser = { role: null };
    }

    const badges = await loadNavBadges(currentUser);
    renderMenu(currentUser, badges);
    setupTopbarSearch(currentUser);

    const startPage = firstAllowedPage(currentUser) || "dashboard";
    setActiveMenuItem(startPage);
    mountPage(startPage);
}

bootstrap();
