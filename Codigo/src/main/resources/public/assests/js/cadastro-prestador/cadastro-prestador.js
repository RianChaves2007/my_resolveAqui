/**
 * cadastro-prestador.js
 * Lógica do formulário de cadastro de prestador de serviço.
 * Depende de auth.js (API_BASE_URL, getUser, setUser, decodeJWT).
 */

// ─── Pré-preenche campos com dados vindos do cadastro.html ──────────────────
const tempData = JSON.parse(sessionStorage.getItem('profeed_reg_temp') || '{}');

if (tempData.nome)  document.getElementById('inp-nome').value  = tempData.nome;
if (tempData.email) document.getElementById('inp-email').value = tempData.email;
if (tempData.senha) {
    document.getElementById('inp-pw').value  = tempData.senha;
    document.getElementById('inp-pw2').value = tempData.senha;
}

// ─── Toggle de visibilidade da senha ────────────────────────────────────────
function togglePw(id, btn) {
    const inp = document.getElementById(id);
    const isHidden = inp.type === 'password';
    inp.type = isHidden ? 'text' : 'password';
    btn.querySelector('svg').style.opacity = isHidden ? '0.5' : '1';
}

// ─── Máscaras de input ───────────────────────────────────────────────────────
document.getElementById('inp-cpf').addEventListener('input', function () {
    let v = this.value.replace(/\D/g, '').slice(0, 11);
    v = v.replace(/(\d{3})(\d)/, '$1.$2');
    v = v.replace(/(\d{3})(\d)/, '$1.$2');
    v = v.replace(/(\d{3})(\d{1,2})$/, '$1-$2');
    this.value = v;
});

document.getElementById('inp-cep').addEventListener('input', function () {
    let v = this.value.replace(/\D/g, '').slice(0, 8);
    v = v.replace(/(\d{5})(\d)/, '$1-$2');
    this.value = v;
});

// ─── Preview de foto de perfil ───────────────────────────────────────────────
let fotoBase64 = '';

function previewFoto(input) {
    const file = input.files[0];
    if (!file) return;
    const reader = new FileReader();
    reader.onload = function (e) {
        fotoBase64 = e.target.result;
        const img = document.getElementById('foto-img');
        img.src = fotoBase64;
        img.style.display = 'block';
        const svgIcon = document.querySelector('.photo-circle svg');
        if (svgIcon) svgIcon.style.display = 'none';
    };
    reader.readAsDataURL(file);
}

// ─── Popula os selects de Área de Atuação e Categoria via API ────────────────
async function carregarSelects() {
    try {
        const [resAreas, resTipos] = await Promise.all([
            fetch(`${API_BASE_URL}/areas-atuacao`),
            fetch(`${API_BASE_URL}/tipos`)
        ]);

        const areas = await resAreas.json();
        const tipos = await resTipos.json();

        const selArea = document.getElementById('inp-area');
        areas.forEach(a => {
            const opt = document.createElement('option');
            opt.value = a.id;
            opt.textContent = a.area;
            selArea.appendChild(opt);
        });

        const selTipo = document.getElementById('inp-categoria');
        tipos.forEach(t => {
            const opt = document.createElement('option');
            opt.value = t.id;
            opt.textContent = t.tipo;
            selTipo.appendChild(opt);
        });
    } catch (err) {
        console.error('Erro ao carregar áreas/tipos:', err);
    }
}

carregarSelects();

// ─── Submissão do formulário ──────────────────────────────────────────────────
async function doRegister() {
    const nome      = document.getElementById('inp-nome').value.trim();
    const email     = document.getElementById('inp-email').value.trim();
    const senha     = document.getElementById('inp-pw').value;
    const senha2    = document.getElementById('inp-pw2').value;
    const telefone  = document.getElementById('inp-telefone').value.trim();
    const cep       = document.getElementById('inp-cep').value.trim();
    const end       = document.getElementById('inp-end').value.trim();
    const num       = document.getElementById('inp-num').value.trim();
    const cidade    = document.getElementById('inp-cidade').value.trim();
    const estado    = document.getElementById('inp-estado').value;
    const descricao = document.getElementById('inp-descricao').value.trim();
    const area      = parseInt(document.getElementById('inp-area').value, 10);
    const categoria = parseInt(document.getElementById('inp-categoria').value, 10);

    const errEl  = document.getElementById('error-msg');

    // ── Validações ─────────────────────────────────────────────────────
    if (!nome || !email || !senha || !senha2) {
        errEl.textContent = 'Preencha todos os campos básicos.';
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

    if (!cep || !end || !cidade || !estado) {
        errEl.textContent = 'Preencha todos os dados de endereço.';
        errEl.style.display = 'block';
        return;
    }

    if (!area || area <= 0) {
        errEl.textContent = 'Selecione uma área de atuação.';
        errEl.style.display = 'block';
        return;
    }

    if (!categoria || categoria <= 0) {
        errEl.textContent = 'Selecione uma categoria.';
        errEl.style.display = 'block';
        return;
    }

    errEl.style.display = 'none';

    // ── Monta localização completa ─────────────────────────────────────
    const local = `${end}, ${num ? num + ', ' : ''}${cidade} - ${estado}, CEP: ${cep}`;

    // ── Chamada à API ──────────────────────────────────────────────────
    try {
        const response = await fetch(`${API_BASE_URL}/cadastro-prestador`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                nome,
                email,
                senha,
                telefone,
                local,
                descricao,
                areaAtuacao: area,
                categoria
            })
        });

        const data = await response.json();

        if (response.ok) {
            sessionStorage.removeItem('profeed_reg_temp');
            alert('Cadastro realizado com sucesso! Faça login para continuar.');
            window.location.href = '/modulos/login.html';
        } else {
            errEl.textContent = data.mensagem || 'Erro ao cadastrar. Tente novamente.';
            errEl.style.display = 'block';
        }
    } catch (err) {
        errEl.textContent = 'Erro na conexão com o servidor. Verifique se ele está rodando.';
        errEl.style.display = 'block';
        console.error(err);
    }
}
