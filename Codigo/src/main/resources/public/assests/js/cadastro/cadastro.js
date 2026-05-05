async function doRegister(tipo) {
    const nome   = document.getElementById("inp-nome").value.trim();
    const email  = document.getElementById("inp-email").value.trim();
    const senha  = document.getElementById("inp-pw").value;
    const senha2 = document.getElementById("inp-pw2").value;
    const errEl  = document.getElementById("error-msg");

    if (!nome || !email || !senha || !senha2) {
        errEl.textContent = 'Preencha todos os campos.';
        errEl.style.display = 'block';
        return;
    }

    if (senha !== senha2) {
        errEl.textContent = 'As senhas não coincidem.';
        errEl.style.display = 'block';
        return;
    }

    if (senha.length < 6) {
        errEl.textContent = 'A senha deve ter pelo menos 6 caracteres.';
        errEl.style.display = 'block';
        return;
    }

    errEl.style.display = 'none';

    if (tipo === 'prestador') {
        sessionStorage.setItem('profeed_reg_temp', JSON.stringify({ nome, email, senha }));
        window.location.href = '/modulos/cadastro-prestador.html';
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/cadastro`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ nome, email, senha, tipoUsuario: tipo })
        });

        const data = await response.json();

        if (response.ok) {
            alert("Cadastro realizado com sucesso!");
            window.location.href = "/modulos/login.html";
        } else {
            errEl.textContent = data.mensagem || "Erro ao cadastrar.";
            errEl.style.display = 'block';
        }
    } catch (error) {
        alert("Erro na conexão. Verifique se o servidor está rodando.");
    }
}