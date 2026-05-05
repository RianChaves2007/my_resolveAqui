package service;

import dao.AreaTipoDAO;
import dao.PrestadorDAO;
import dao.UsuarioDAO;
import responseDTO.PrestadorDTO;
import responseDTO.UsuarioDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import spark.Request;
import spark.Response;

/**
 * Service responsável pela lógica de negócio relacionada ao Prestador:
 * cadastro completo (cria Usuário + Prestador em sequência) e listagem
 * das tabelas auxiliares Area_Atuacao e Tipo.
 */
public class PrestadorService {

    private final UsuarioDAO  usuarioDAO  = new UsuarioDAO();
    private final PrestadorDAO prestadorDAO = new PrestadorDAO();
    private final AreaTipoDAO  areaTipoDAO  = new AreaTipoDAO();

    private final JsonMapper mapper = JsonMapper.builder()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .build();

    private Object erro(String mensagem, int status, Response response) {
        response.status(status);
        return "{\"sucesso\": false, \"mensagem\": \"" + mensagem + "\"}";
    }

    /**
     * POST /cadastro-prestador
     * Recebe os dados do usuário + dados do prestador em um único JSON,
     * cria o registro em Usuario e depois em Prestador dentro de uma transação.
     */
    public Object cadastrar(Request request, Response response) {
        response.type("application/json");

        PrestadorDTO dto;
        try {
            dto = mapper.readValue(request.body(), new TypeReference<PrestadorDTO>() {});
        } catch (JsonProcessingException e) {
            return erro("Formato JSON inválido", 400, response);
        }

        // Validações dos campos obrigatórios
        if (dto.getNome() == null || dto.getNome().isBlank()) {
            return erro("Nome é obrigatório", 400, response);
        }
        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            return erro("Email é obrigatório", 400, response);
        }
        if (dto.getSenha() == null || dto.getSenha().isBlank()) {
            return erro("Senha é obrigatória", 400, response);
        }
        if (dto.getAreaAtuacao() <= 0) {
            return erro("Área de atuação é obrigatória", 400, response);
        }
        if (dto.getCategoria() <= 0) {
            return erro("Categoria é obrigatória", 400, response);
        }

        // 1. Cria o Usuário com tipoUsuario = 'prestador'
        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setNome(dto.getNome());
        usuarioDTO.setEmail(dto.getEmail());
        usuarioDTO.setSenha(dto.getSenha());
        usuarioDTO.setTelefone(dto.getTelefone());
        usuarioDTO.setLocal(dto.getLocal());
        usuarioDTO.setTipoUsuario("prestador");

        int idUsuario = usuarioDAO.inserirRetornandoId(usuarioDTO);
        if (idUsuario < 0) {
            return erro("Erro ao cadastrar usuário. O e-mail pode já estar em uso.", 409, response);
        }

        // 2. Cria o Prestador vinculado ao Usuário recém-criado
        boolean sucesso = prestadorDAO.inserir(idUsuario, dto);
        if (!sucesso) {
            return erro("Usuário criado, mas erro ao salvar dados do prestador.", 500, response);
        }

        response.status(201);
        return "{\"sucesso\": true, \"mensagem\": \"Prestador cadastrado com sucesso\"}";
    }

    /**
     * GET /areas-atuacao
     * Retorna a lista de áreas de atuação para popular o select no front-end.
     */
    public Object listarAreas(Request request, Response response) {
        response.type("application/json");
        try {
            return mapper.writeValueAsString(areaTipoDAO.listarAreas());
        } catch (JsonProcessingException e) {
            return erro("Erro ao serializar áreas", 500, response);
        }
    }

    /**
     * GET /tipos
     * Retorna a lista de tipos de prestador para popular o select no front-end.
     */
    public Object listarTipos(Request request, Response response) {
        response.type("application/json");
        try {
            return mapper.writeValueAsString(areaTipoDAO.listarTipos());
        } catch (JsonProcessingException e) {
            return erro("Erro ao serializar tipos", 500, response);
        }
    }
}