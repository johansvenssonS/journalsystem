async function handleResponse(res) {
    const contentType = res.headers.get("content-type") || "";
    const isJson = contentType.includes("application/json");
    const body = isJson ? await res.json() : await res.text();

    // Success case — nothing special to do, just return the data.
    if (res.ok) {
        return body;
    }

    // From here, something went wrong. Figure out the best message to show.
    let errorMessage;

    if (isJson && body.message) {
        // Our backend now always fills "message" with something useful,
        // e.g. "personnummer: must not be blank, email: must be a valid email"
        errorMessage = body.message;

    } else if (isJson && Array.isArray(body.errors) && body.errors.length > 0) {
        // Fallback: if "message" is ever missing but "errors" exists, join it ourselves
        errorMessage = body.errors.join(", ");

    } else {
        // Last resort: nothing usable in the body
        errorMessage = `HTTP ${res.status} ${res.statusText}`;
    }

    throw new Error(errorMessage);
}

// Helper for JSON requests
async function postJSON(url, data) {
    const res = await fetch(url, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
    });
    return handleResponse(res);
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

export async function createPatient(patientData) {
    return postJSON(`/patients`, patientData);
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








