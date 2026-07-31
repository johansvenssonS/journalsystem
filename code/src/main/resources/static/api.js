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
    const res = await fetch(`/patients`);
    return handleResponse(res);
}

export async function getPatientById(id) {
    const res = await fetch(`/patients/${id}`);
    return handleResponse(res);
}

// ---- Patient contacts ----
export async function getPatientContactById(id) {
    const res = await fetch(`/patient-contacts/${id}`);
    return handleResponse(res);
}

// ---- Roles ----
export async function getRoleById(id) {
    const res = await fetch(`/roles/${id}`);
    return handleResponse(res);
}

// ---- Patient medical ----
export async function getPatientMedicalById(id) {
    const res = await fetch(`/patient-medicals/${id}`);
    return handleResponse(res);
}

// ---- Staff ----
export async function getStaff() {
    const res = await fetch(`/staff`);
    return handleResponse(res);
}

export async function getAppointmentsByStaffAndDate(staffId, date) {
    const params = new URLSearchParams({
        staffId: String(staffId),
        date
    });
    const res = await fetch(`/appointments/schedule?${params.toString()}`);
    return handleResponse(res);
}

// ---- User account ----
export async function getCurrentUser(){
    const res = await fetch('auth/me');
    return handleResponse(res);
}








