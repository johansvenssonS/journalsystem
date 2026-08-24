import { requireAuth, logout, loadCurrentUser } from "./auth.js";
import { getCurrentUser } from "./api.js";
import { roleLabel, toastError } from "./ui.js";
import { navForRole, canAccessPage, firstAllowedPage } from "./access.js";

requireAuth();

const content = document.getElementById("content");
const menu = document.getElementById("menu");

const PAGES = {
    dashboard: () => import("./pages/dashboard.js"),
    patients: () => import("./pages/patients.js"),
    prescriptions: () => import("./pages/prescriptions.js"),
    referrals: () => import("./pages/referrals.js"),
    appointments: () => import("./pages/appointments.js"),
    directory: () => import("./pages/directory.js"),
    audit: () => import("./pages/audit.js"),
    "api-docs": () => import("./pages/apiDocs.js"),
};

let activePage = null;
let currentUser = null;

function renderMenu(me) {
    menu.innerHTML = navForRole(me)
        .map((item) => `<a href="#" class="menu-item" data-target="${item.id}"><span class="icon">${item.icon}</span> ${item.label}</a>`)
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

async function bootstrap() {
    try {
        currentUser = await loadCurrentUser(getCurrentUser);
        renderCurrentUserBadge(currentUser);
    } catch (err) {
        document.getElementById("current-user-name").textContent = "Okänd användare";
        toastError(err);
        currentUser = { role: null };
    }

    renderMenu(currentUser);
    const startPage = firstAllowedPage(currentUser) || "dashboard";
    setActiveMenuItem(startPage);
    mountPage(startPage);
}

bootstrap();
