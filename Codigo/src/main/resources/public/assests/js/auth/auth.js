// URL base da API — string vazia porque o servidor Spark serve
// tanto os arquivos estáticos quanto a API no mesmo host/porta.
const API_BASE_URL = "";
const LOGIN_BASE_URL = "/modulos/login.html";

/** Decodifica o payload de um JWT sem verificar assinatura (cliente). */
function decodeJWT(token) {
    try {
        const base64 = token.split(".")[1].replace(/-/g, "+").replace(/_/g, "/");
        return JSON.parse(
            decodeURIComponent(
                atob(base64)
                    .split("")
                    .map((c) => "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2))
                    .join("")
            )
        );
    } catch {
        return null;
    }
}

/** Redireciona para login se não houver token válido. */
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

/** Faz fetch adicionando o token JWT no header Authorization. */
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

/** Retorna o objeto de usuário salvo no localStorage após o login. */
function getUser() {
    return JSON.parse(localStorage.getItem("usuario"));
}

/** Remove token/usuário do localStorage e redireciona para login. */
function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("usuario");
    window.location.href = LOGIN_BASE_URL;
}
