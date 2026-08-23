import { requireAuth, logout, loadCurrentUser } from "./auth.js";
import { getCurrentUser } from "./api.js";
import { roleLabel, toastError } from "./ui.js";

requireAuth();

const content = document.getElementById("content");
const menuItems = document.querySelectorAll(".menu-item");

const PAGES = {
    dashboard: () => import("./pages/dashboard.js"),
    patients: () => import("./pages/patients.js"),
    prescriptions: () => import("./pages/prescriptions.js"),
    referrals: () => import("./pages/referrals.js"),
    appointments: () => import("./pages/appointments.js"),
    directory: () => import("./pages/directory.js"),
    audit: () => import("./pages/audit.js"),
};

let activePage = null;

async function mountPage(pageId) {
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

menuItems.forEach((item) => {
    item.addEventListener("click", (e) => {
        e.preventDefault();
        menuItems.forEach((nav) => nav.classList.remove("active"));
        item.classList.add("active");
        mountPage(item.dataset.target);
    });
});

document.getElementById("logoutBtn").addEventListener("click", logout);

async function renderCurrentUser() {
    try {
        const me = await loadCurrentUser(getCurrentUser);
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
        return me;
    } catch (err) {
        document.getElementById("current-user-name").textContent = "Okänd användare";
        toastError(err);
        return null;
    }
}

renderCurrentUser();
mountPage("dashboard");
