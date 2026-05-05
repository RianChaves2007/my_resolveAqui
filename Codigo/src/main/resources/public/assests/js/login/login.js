async function login(event) {

    if (event) event.preventDefault();

    const email = document.getElementById("email").value.trim();
    const senha = document.getElementById("password").value;

    if (!email || !senha) {
        alert("Preencha email e senha");
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/login`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email, senha })
        });

        const data = await response.json(); 

        if (response.ok) {
            localStorage.setItem('token', data.token);
            const usuario = decodeJWT(data.token);
            localStorage.setItem('usuario', JSON.stringify(usuario));
            window.location.href = "/index.html";
        } else {
            alert(data.mensagem || "Email ou senha inválidos");
        }
    } catch (error) {
        alert("Erro na conexão. Verifique se o servidor está rodando.");
    }
}