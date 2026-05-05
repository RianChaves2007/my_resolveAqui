const API_BASE_URL = "http://127.0.0.1:8080";
const LOGIN_BASE_URL = "/modulos/login.html";

function decodeJWT(token) {
  try {
    const base64 = token.split(".")[1].replace(/-/g, "+").replace(/_/g, "/");
    return JSON.parse(
      decodeURIComponent(
        atob(base64)
          .split("")
          .map((c) => "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2))
          .join(""),
      ),
    );
  } catch {
    return null;
  }
}

function validarAutenticacao() {
  const token = localStorage.getItem("token");
  if (!token) {
    window.location.href = LOGIN_BASE_URL;
    return;
  }
  const decoded = decodeJWT(token);
  if (!decoded || decoded.exp * 1000 < Date.now()) {
    localStorage.removeItem("token");
    window.location.href = LOGIN_BASE_URL;
  }
}

async function fetchAutenticado(url, options = {}) {
  const token = localStorage.getItem("token");
  const response = await fetch(url, {
    ...options,
    headers: {
      "Content-Type": "application/json",
      Authorization: "Bearer " + token,
      ...options.headers,
    },
  });
  if (response.status === 401) {
    localStorage.removeItem("token");
    window.location.href = LOGIN_BASE_URL;
    return;
  }
  return response;
}

function getUser() {
  return JSON.parse(localStorage.getItem("usuario"));
}

function logout() {
  localStorage.removeItem("token");
  localStorage.removeItem("usuario");
  window.location.href = LOGIN_BASE_URL;
}
