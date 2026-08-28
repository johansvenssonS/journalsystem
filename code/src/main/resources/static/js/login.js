import { login, register, getRoles, getDepartmentsPublic } from "./api.js";
import { setToken, isLoggedIn } from "./auth.js";
import { roleLabel } from "./ui.js";

if (isLoggedIn()) {
    window.location.href = "index.html";
}

const tabButtons = document.querySelectorAll(".auth-tab");
const tabLogin = document.getElementById("tab-login");
const tabRegister = document.getElementById("tab-register");

tabButtons.forEach((btn) => {
    btn.addEventListener("click", () => {
        tabButtons.forEach((b) => b.classList.remove("active"));
        btn.classList.add("active");
        const showRegister = btn.dataset.tab === "register";
        tabLogin.hidden = showRegister;
        tabRegister.hidden = !showRegister;
    });
});

function showAlert(el, message) {
    el.textContent = message;
    el.hidden = false;
}
function hideAlert(el) {
    el.hidden = true;
}

// ---- Login ----
const loginForm = document.getElementById("loginForm");
const loginAlert = document.getElementById("login-alert");

loginForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    hideAlert(loginAlert);

    const username = document.getElementById("loginUsername").value.trim();
    const password = document.getElementById("loginPassword").value;
    const submitBtn = loginForm.querySelector("button[type=submit]");
    submitBtn.disabled = true;

    try {
        const { token } = await login(username, password);
        setToken(token);
        window.location.href = "index.html";
    } catch (err) {
        showAlert(loginAlert, err.message);
    } finally {
        submitBtn.disabled = false;
    }
});

// ---- Register ----
const registerForm = document.getElementById("registerForm");
const registerAlert = document.getElementById("register-alert");
const registerSuccess = document.getElementById("register-success");
const roleSelect = document.getElementById("regRole");
const departmentSelect = document.getElementById("regDepartment");

async function populateDropdowns() {
    try {
        const [roles, departments] = await Promise.all([getRoles(), getDepartmentsPublic()]);

        roleSelect.innerHTML = '<option value="">Välj roll...</option>' + roles
            .map((r) => `<option value="${r.title}">${roleLabel(r.title.replace(/^ROLE_/, ""))}</option>`)
            .join("");

        departmentSelect.innerHTML = '<option value="">Välj avdelning...</option>' + departments
            .map((d) => `<option value="${d.id}">${d.name}${d.floor != null ? " (plan " + d.floor + ")" : ""}</option>`)
            .join("");
    } catch (err) {
        showAlert(registerAlert, "Kunde inte hämta roller/avdelningar: " + err.message);
    }
}
populateDropdowns();

registerForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    hideAlert(registerAlert);
    registerSuccess.hidden = true;

    const payload = {
        firstName: document.getElementById("regFirstName").value.trim(),
        lastName: document.getElementById("regLastName").value.trim(),
        personalNumber: document.getElementById("regPersonalNumber").value.trim(),
        roleTitle: roleSelect.value,
        departmentId: departmentSelect.value ? Number(departmentSelect.value) : null,
        username: document.getElementById("regUsername").value.trim(),
        email: document.getElementById("regEmail").value.trim(),
        password: document.getElementById("regPassword").value,
    };

    const submitBtn = registerForm.querySelector("button[type=submit]");
    submitBtn.disabled = true;

    try {
        await register(payload);
        showAlert(registerSuccess, "Kontot skapades! Du kan nu logga in.");
        registerForm.reset();
        document.querySelector('.auth-tab[data-tab="login"]').click();
        document.getElementById("loginUsername").value = payload.username;
    } catch (err) {
        showAlert(registerAlert, err.message);
    } finally {
        submitBtn.disabled = false;
    }
});
