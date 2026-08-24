export function safe(val) {
    return val === null || val === undefined || val === "" ? "-" : val;
}

// Sets the persistent top-bar title/subtitle — call once near the top of a page's
// render() in place of the old inline .page-header block.
export function setTopbar(title, sub = "") {
    const titleEl = document.getElementById("topbar-title");
    const subEl = document.getElementById("topbar-sub");
    if (titleEl) titleEl.textContent = title;
    if (subEl) subEl.textContent = sub;
}

export function escapeHtml(str) {
    return String(str ?? "").replace(/[&<>"']/g, (c) => ({
        "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;",
    }[c]));
}

export function formatDate(value) {
    if (!value) return "-";
    const d = new Date(value);
    if (isNaN(d.getTime())) return String(value);
    return d.toLocaleDateString("sv-SE");
}

export function formatDateTime(value) {
    if (!value) return "-";
    const d = new Date(value);
    if (isNaN(d.getTime())) return String(value);
    return d.toLocaleDateString("sv-SE") + " " + d.toLocaleTimeString("sv-SE", { hour: "2-digit", minute: "2-digit" });
}

export function toDateInputValue(d = new Date()) {
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, "0");
    const day = String(d.getDate()).padStart(2, "0");
    return `${year}-${month}-${day}`;
}

export function toDateTimeLocalValue(d = new Date()) {
    return `${toDateInputValue(d)}T${String(d.getHours()).padStart(2, "0")}:${String(d.getMinutes()).padStart(2, "0")}`;
}

// Human-readable Swedish labels for the raw role titles (already stripped of "ROLE_" by the backend).
export const ROLE_LABELS = {
    doctor: "Läkare",
    nurse: "Sjuksköterska",
    assistant_nurse: "Undersköterska",
    RECEPTIONIST: "Receptionist",
    pharmacy: "Apotek",
};

export function roleLabel(role) {
    return ROLE_LABELS[role] || role || "Okänd roll";
}

const STATUS_BADGE = {
    ACTIVE: "badge-green", ACCEPTED: "badge-green", ACCEPTERAD: "badge-green", KLAR: "badge-green",
    COMPLETED: "badge-green", CONFIRMED: "badge-green", TRUE: "badge-green",
    PENDING: "badge-yellow", WAITING: "badge-yellow", SCHEDULED: "badge-yellow", BOOKED: "badge-blue",
    CANCELLED: "badge-red", CANCELED: "badge-red", DECLINED: "badge-red", REJECTED: "badge-red", FALSE: "badge-gray",
};

export function statusBadgeClass(status) {
    if (status === true) return "badge-green";
    if (status === false) return "badge-gray";
    const key = String(status ?? "").toUpperCase();
    return STATUS_BADGE[key] || "badge-gray";
}

export function badge(text, cssClass) {
    return `<span class="badge ${cssClass}">${escapeHtml(text)}</span>`;
}

// Builds an id -> item lookup map, e.g. toMap(departments) for resolving departmentId to a name.
export function toMap(list, key = "id") {
    return Object.fromEntries((list || []).map((item) => [item[key], item]));
}

export function statusBadge(status) {
    if (status === null || status === undefined) return badge("-", "badge-gray");
    const label = typeof status === "boolean" ? (status ? "Aktivt" : "Inaktivt") : status;
    return badge(label, statusBadgeClass(status));
}

// ---- Table state rows (loading / empty / error) ----
export function stateRow(colspan, message, variant = "") {
    return `<tr class="state-row ${variant}"><td colspan="${colspan}">${escapeHtml(message)}</td></tr>`;
}

export function loadingRow(colspan, label = "Laddar...") {
    return stateRow(colspan, label);
}

export function emptyRow(colspan, label = "Inga poster hittades.") {
    return stateRow(colspan, label);
}

export function errorRow(colspan, err) {
    return stateRow(colspan, `Kunde inte hämta data: ${err.message || err}`, "error");
}

// ---- Toasts ----
function toastStack() {
    let stack = document.getElementById("toast-stack");
    if (!stack) {
        stack = document.createElement("div");
        stack.id = "toast-stack";
        stack.className = "toast-stack";
        document.body.appendChild(stack);
    }
    return stack;
}

export function toast(message, type = "") {
    const stack = toastStack();
    const el = document.createElement("div");
    el.className = `toast ${type ? "toast-" + type : ""}`;
    el.textContent = message;
    stack.appendChild(el);
    setTimeout(() => el.remove(), 4000);
}

export function toastError(err) {
    toast(err.message || String(err), "error");
}

export function toastSuccess(message) {
    toast(message, "success");
}
