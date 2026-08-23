const TOKEN_KEY = "journalsystem_token";

export function getToken() {
    return localStorage.getItem(TOKEN_KEY);
}

export function setToken(token) {
    localStorage.setItem(TOKEN_KEY, token);
}

export function clearToken() {
    localStorage.removeItem(TOKEN_KEY);
}

export function isLoggedIn() {
    return !!getToken();
}

// Redirects to the login page if there's no token. Call at the top of every protected page.
export function requireAuth() {
    if (!isLoggedIn()) {
        window.location.href = "login.html";
        throw new Error("Not authenticated");
    }
}

export function goToLogin() {
    clearToken();
    window.location.href = "login.html";
}

export function logout() {
    clearToken();
    window.location.href = "login.html";
}

let currentUserPromise = null;

// Cached for the lifetime of the page load — call invalidateCurrentUser() if it ever needs a refetch.
export function loadCurrentUser(fetchMeFn) {
    if (!currentUserPromise) {
        currentUserPromise = fetchMeFn();
    }
    return currentUserPromise;
}

export function invalidateCurrentUser() {
    currentUserPromise = null;
}
