# Resolve Aqui — Documentação Técnica

## 1. Visão Geral da Arquitetura

O sistema é uma aplicação web **Java + Spark Framework**, onde o mesmo servidor HTTP atende tanto a API REST quanto os arquivos estáticos (HTML/CSS/JS).

```
Navegador  ──HTTP──►  Spark (porta 5000)  ──JDBC──►  PostgreSQL
                       │
                       ├─ GET  /  →  index.html (feed)
                       ├─ GET  /modulos/*.html  →  páginas estáticas
                       ├─ POST /login            →  UsuarioService
                       ├─ POST /cadastro         →  UsuarioService
                       ├─ POST /cadastro-prestador → PrestadorService  ← NOVO
                       ├─ GET  /areas-atuacao    →  PrestadorService  ← NOVO
                       ├─ GET  /tipos            →  PrestadorService  ← NOVO
                       ├─ GET  /usuarios         →  UsuarioService
                       ├─ GET  /usuario/:id      →  UsuarioService
                       ├─ PUT  /usuario/:id      →  UsuarioService
                       └─ DELETE /usuario/:id   →  UsuarioService
```

---

## 2. Modelo de Dados (Banco PostgreSQL)

### Tabelas e relacionamentos

```
Usuario (IdUsuario PK IDENTITY, Email UNIQUE, Senha, Nome, TipoUsuario, Telefone, Foto, Local)
    │
    └─ Prestador (IdPrestador PK IDENTITY, Id_Usuario FK, Descricao, AreaAtuacao FK, Categoria FK)
                         │                                     │                  │
                         │                           Area_Atuacao (IdArea PK)   Tipo (IdTipo PK)
                         │
                         └─ Posts (IdPost PK IDENTITY, IdPrestador FK, titulo, Legenda, DataEnvio)

Usuario ◄── Conversa (IdPrestador FK, IdUsuario FK — PK composta, Mensagem, DataEnvio)
Prestador ◄─┘
```

**Ponto importante:** `Prestador.Id_Usuario` é uma FK para `Usuario.IdUsuario`. Isso significa que todo prestador é também um usuário no sistema — o cadastro de prestador cria **dois registros**: um em `Usuario` e um em `Prestador`.

---

## 3. Estrutura de Pacotes Java

```
app/
  Aplicacao.java           → Ponto de entrada; declara todas as rotas Spark

dao/
  DAO.java                 → Conexão base com PostgreSQL via variáveis de ambiente PG*
  UsuarioDAO.java          → CRUD de Usuario (+ inserirRetornandoId — NOVO)
  PrestadorDAO.java        → INSERT em Prestador (reescrito — CORRIGIDO)
  AreaTipoDAO.java         → Listagem de Area_Atuacao e Tipo (NOVO)

model/
  Usuario.java             → Entidade de domínio Usuario
  Prestador.java           → Entidade de domínio Prestador (tipos FK corrigidos — CORRIGIDO)

responseDTO/
  UsuarioDTO.java          → DTO de entrada/saída para Usuario
  PrestadorDTO.java        → DTO de entrada para cadastro de Prestador (NOVO)
  LoginResponseDTO.java    → DTO de resposta do login

FilterDTO/
  LoginDTO.java            → DTO de credenciais para login
  UsuarioFilterDTO.java    → DTO de filtros para listagem de usuários

service/
  UsuarioService.java      → Lógica de negócio de Usuario
  PrestadorService.java    → Lógica de cadastro de Prestador + listagem auxiliar (NOVO)

util/
  JwtUtil.java             → Geração e validação de tokens JWT
  AuthFilter.java          → Filtro Spark de autenticação via Bearer token
```

---

## 4. Fluxo Completo — Cadastro de Prestador

### 4.1 Navegação no front-end

```
/modulos/cadastro.html
   │  Usuário preenche: Nome, Email, Senha, Confirmação de senha
   │  Clica em "Seja um profissional"
   ▼
cadastro.js :: doRegister('prestador')
   │  Valida campos básicos
   │  Salva { nome, email, senha } em sessionStorage['profeed_reg_temp']
   │  Redireciona para →  /modulos/cadastro-prestador.html
   ▼
/modulos/cadastro-prestador.html  (carregado)
   │  auth.js é carregado (define API_BASE_URL = "")
   │  cadastro-prestador.js é carregado:
   │    • Lê sessionStorage e pré-preenche Nome/Email/Senha
   │    • Chama GET /areas-atuacao  →  popula <select id="inp-area">
   │    • Chama GET /tipos          →  popula <select id="inp-categoria">
   │  Usuário preenche: Telefone, Senha (confirmação), Descrição,
   │                    Área de Atuação, Categoria, CEP, Endereço,
   │                    Cidade, Estado, Foto (opcional)
   │  Clica em "Registrar"
   ▼
cadastro-prestador.js :: doRegister()
   │  Valida todos os campos
   │  Chama POST /cadastro-prestador com JSON:
   │  {
   │    nome, email, senha, telefone, local,
   │    descricao, areaAtuacao (int), categoria (int)
   │  }
   ▼  (sucesso)
   Redireciona para /modulos/login.html
```

### 4.2 Processamento no back-end

```
POST /cadastro-prestador
   ▼
PrestadorService :: cadastrar(request, response)
   │  Deserializa JSON → PrestadorDTO
   │  Valida: nome, email, senha obrigatórios; areaAtuacao e categoria > 0
   │
   │  Monta UsuarioDTO com tipoUsuario = "prestador"
   │
   ├─ UsuarioDAO :: inserirRetornandoId(usuarioDTO)
   │    SQL: INSERT INTO usuario (...) VALUES (?,?,?,?,?,?,?) RETURNING idusuario
   │    Retorna: int idUsuario (ou -1 se erro/email duplicado)
   │
   ├─ PrestadorDAO :: inserir(idUsuario, prestadorDTO)
   │    SQL: INSERT INTO prestador (id_usuario, descricao, areaatuacao, categoria)
   │         VALUES (?, ?, ?, ?)
   │
   └─ Resposta: 201 { "sucesso": true, "mensagem": "Prestador cadastrado com sucesso" }
                ou 4xx/5xx em caso de erro
```

### 4.3 Endpoints auxiliares

| Método | Rota             | Retorno                              | Uso                          |
|--------|------------------|--------------------------------------|------------------------------|
| GET    | /areas-atuacao   | `[{"id":1,"area":"Tecnologia..."}]`  | Popula select de área        |
| GET    | /tipos           | `[{"id":1,"tipo":"Freelancer"}]`     | Popula select de categoria   |

---

## 5. O Que Foi Alterado e Por Quê

### 5.1 `auth.js` — correção de `API_BASE_URL`
**Antes:** `const API_BASE_URL = "http://127.0.0.1:8080";`
**Depois:** `const API_BASE_URL = "";`

**Motivo:** O Spark serve os arquivos estáticos E a API no mesmo host/porta. Com a URL absoluta `8080`, as chamadas falhavam no Replit (que roteia tudo pela porta 5000 via proxy). Com string vazia, todas as chamadas `fetch(API_BASE_URL + "/rota")` usam o mesmo origin da página — funcionando em qualquer ambiente.

---

### 5.2 `dao/PrestadorDAO.java` — reescrita completa
**Antes:** Estendia `UsuarioDAO`, chamava `conectar()` duas vezes, e tentava inserir colunas de usuario (`nome, email, senha...`) na tabela `prestador` que não as possui.

**Depois:** Estende `DAO` diretamente, uma única conexão, e insere corretamente nas colunas da tabela `prestador`: `id_usuario`, `descricao`, `areaatuacao`, `categoria`.

---

### 5.3 `dao/UsuarioDAO.java` — adição de `inserirRetornandoId`
Novo método que usa `RETURNING idusuario` do PostgreSQL para obter o ID gerado imediatamente após o INSERT. Necessário para criar o registro de `Prestador` com a FK correta.

---

### 5.4 `model/Prestador.java` — correção de tipos
**Antes:** `areaatuacao` e `categoria` eram `String`.
**Depois:** São `int`, refletindo o que o banco armazena (IDs inteiros de FK).

---

### 5.5 `responseDTO/PrestadorDTO.java` — criado
DTO que reúne os campos do Usuario (`nome, email, senha, telefone, local`) com os campos específicos do Prestador (`descricao, areaAtuacao, categoria`). Permite receber o cadastro completo em uma única requisição JSON.

---

### 5.6 `dao/AreaTipoDAO.java` — criado
Consulta as tabelas `Area_Atuacao` e `Tipo` para retornar as listas usadas nos selects do formulário. As opções são carregadas dinamicamente do banco, garantindo consistência com as FKs.

---

### 5.7 `service/PrestadorService.java` — criado
Orquestra o cadastro de prestador em dois passos (cria Usuário, depois cria Prestador) e expõe os métodos que respondem às rotas `/cadastro-prestador`, `/areas-atuacao` e `/tipos`.

---

### 5.8 `app/Aplicacao.java` — novas rotas adicionadas
Registradas as três novas rotas de prestador:
- `POST /cadastro-prestador`
- `GET  /areas-atuacao`
- `GET  /tipos`

---

### 5.9 `modulos/cadastro-prestador.html` — reformulado
**Antes:** O `<script>` apontava para `auth.js` com caminho relativo errado; o formulário não chamava a API, salvando dados apenas no `localStorage`.

**Depois:**
- Scripts carregados com caminhos absolutos corretos (`/assests/js/...`)
- Campos "Área de atuação" e "Categoria" viraram `<select>` populados via API
- Adicionado campo `<textarea>` para Descrição profissional
- Adicionado campo Telefone
- A função `doRegister()` foi movida para o arquivo externo `cadastro-prestador.js` e faz chamada real à API

---

### 5.10 `assests/js/cadastro-prestador/cadastro-prestador.js` — criado
Arquivo JS dedicado ao formulário de cadastro de prestador. Responsabilidades:
- Pré-preencher campos com dados do `sessionStorage`
- Carregar listas de áreas e tipos via API
- Validar todos os campos antes de submeter
- Fazer `POST /cadastro-prestador` com os dados completos
- Redirecionar para login após sucesso

---

### 5.11 `dump.sql` — corrigido
- `Usuario.IdUsuario` e `Prestador.IdPrestador` usam `GENERATED BY DEFAULT AS IDENTITY` (autoincremento nativo PostgreSQL) em vez de depender de ALTER separado
- `Posts.IdPost` também recebeu autoincremento
- Adicionada constraint `UNIQUE` em `Usuario.Email`
- PKs e FKs declaradas inline na criação das tabelas (mais legível)
- Removida a seção separada de ALTER para PKs (consolidada no CREATE TABLE)

---

## 6. Resumo das Regras de Negócio

1. **Todo prestador é um usuário:** o cadastro insere nas duas tabelas; `tipoUsuario` fica como `'prestador'`.
2. **Email único:** a constraint `UNIQUE` em `Usuario.Email` impede duplicatas no banco.
3. **Área e Categoria obrigatórias:** o back-end valida que `areaAtuacao > 0` e `categoria > 0` antes de aceitar o cadastro.
4. **Senha mínima de 6 caracteres:** validada no front-end antes de enviar.
5. **Listas de referência:** `Area_Atuacao` e `Tipo` são tabelas fixas com 20 registros cada, populadas via `dump.sql`.
