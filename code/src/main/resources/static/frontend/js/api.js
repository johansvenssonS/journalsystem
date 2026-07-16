import { API_BASE_URL } from "./config.js";

async function handleResponse(res) {
    const contentType = res.headers.get("content-type") || "";
    const isJson = contentType.includes("application/json");
    const body = isJson ? await res.json() : await res.text();

    if (!res.ok) {
        const errorMessage =
            (isJson && (body.message || body.error)) ||
            `HTTP ${res.status} ${res.statusText}`;
        throw new Error(errorMessage);
    }

    return body;
}

// ---- Patients ----
export async function getPatients() {
    const res = await fetch(`${API_BASE_URL}/patients`);
    return handleResponse(res);
}

export async function getPatientById(id) {
    const res = await fetch(`${API_BASE_URL}/patients/${id}`);
    return handleResponse(res);
}

// ---- Patient contacts ----
export async function getPatientContactById(id) {
    const res = await fetch(`${API_BASE_URL}/patient-contacts/${id}`);
    return handleResponse(res);
}

// ---- Roles ----
export async function getRoleById(id) {
    const res = await fetch(`${API_BASE_URL}/roles/${id}`);
    return handleResponse(res);
}

// ---- Patient medical ----
export async function getPatientMedicalById(id) {
    const res = await fetch(`${API_BASE_URL}/patient-medicals/${id}`);
    return handleResponse(res);
}

// NOTE:
// createPatient/createEntry were removed because backend currently has no POST endpoints.
// Add back later when your controllers include @PostMapping.

export async function getStaff() {
    const res = await fetch(`${API_BASE_URL}/staff`);
    return handleResponse(res);
}

export async function getAppointmentsByStaffAndDate(staffId, date) {
    const params = new URLSearchParams({
        staffId: String(staffId),
        date
    });
    const res = await fetch(`${API_BASE_URL}/appointments/schedule?${params.toString()}`);
    return handleResponse(res);
}