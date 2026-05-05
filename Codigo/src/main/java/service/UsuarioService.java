package service;

import dao.UsuarioDAO;
import responseDTO.UsuarioDTO;
import FilterDTO.LoginDTO;
import FilterDTO.UsuarioFilterDTO;
import util.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import spark.Request;
import spark.Response;
import java.util.List;

public class UsuarioService {

    private final UsuarioDAO UsuarioDAO = new UsuarioDAO();

    private final JsonMapper mapper = JsonMapper.builder()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .build();

    private Object erro(String mensagem, int status, Response response) {
        response.status(status);
        return "{\"sucesso\": false, \"mensagem\": \"" + mensagem + "\"}";
    }

    public Object login(Request request, Response response) {
        response.type("application/json");

        LoginDTO loginDTO;
        try {
            loginDTO = mapper.readValue(request.body(), new TypeReference<LoginDTO>() {});
        } catch (JsonProcessingException e) {
            return erro("Formato JSON inválido", 400, response);
        }

        if (loginDTO.getEmail() == null || loginDTO.getSenha() == null) {
            return erro("Email e senha são obrigatórios", 400, response);
        }

        UsuarioDTO usuario = UsuarioDAO.login(loginDTO);

        if (usuario == null) {
            return erro("Credenciais inválidas", 401, response);
        }

        String token = JwtUtil.gerarToken(
            usuario.getId(),
            usuario.getEmail(),
            usuario.getTipoUsuario()
        );

        response.status(200);
        return "{\"token\": \"" + token + "\"}";
    }

    public Object cadastro(Request request, Response response) {
        response.type("application/json");

        UsuarioDTO dto;
        try {
            dto = mapper.readValue(request.body(), new TypeReference<UsuarioDTO>() {});
        } catch (JsonMappingException e) {
            return erro("Formato JSON inválido", 400, response);
        } catch (JsonProcessingException e) {
            return erro("Erro ao processar JSON", 400, response);
        }

        if (dto.getNome() == null || dto.getEmail() == null || dto.getSenha() == null) {
            return erro("Nome, email e senha são obrigatórios", 400, response);
        }

        boolean sucesso = UsuarioDAO.inserir(dto);

        if (sucesso) {
            response.status(201);
            return "{\"sucesso\": true, \"mensagem\": \"Usuário cadastrado com sucesso\"}";
        }
        return erro("Erro ao cadastrar usuário", 500, response);
    }

    public Object listarComFiltro(Request request, Response response) {
        response.type("application/json");

        UsuarioFilterDTO filtro = new UsuarioFilterDTO();
        filtro.setNome(request.queryParams("nome"));
        filtro.setEmail(request.queryParams("email"));
        filtro.setTipoUsuario(request.queryParams("tipoUsuario"));

        try {
            List<UsuarioDTO> lista = UsuarioDAO.listarComFiltro(filtro);
            return mapper.writeValueAsString(lista);
        } catch (JsonProcessingException e) {
            return erro("Erro ao serializar resposta", 500, response);
        }
    }

    public Object buscarPorId(Request request, Response response) {
        response.type("application/json");

        try {
            int id = Integer.parseInt(request.params(":id"));
            UsuarioDTO dto = UsuarioDAO.buscarPorId(id);

            if (dto != null) {
                return mapper.writeValueAsString(dto);
            }
            return erro("Usuário não encontrado", 404, response);
        } catch (NumberFormatException e) {
            return erro("ID inválido", 400, response);
        } catch (JsonProcessingException e) {
            return erro("Erro ao serializar resposta", 500, response);
        }
    }

    public Object atualizar(Request request, Response response) {
        response.type("application/json");

        try {
            int id = Integer.parseInt(request.params(":id"));
            UsuarioDTO dto = mapper.readValue(request.body(), new TypeReference<UsuarioDTO>() {});
            dto.setId(id);

            boolean sucesso = UsuarioDAO.atualizar(dto);

            if (sucesso) {
                return "{\"sucesso\": true, \"mensagem\": \"Usuário atualizado com sucesso\"}";
            }
            return erro("Usuário não encontrado", 404, response);
        } catch (JsonProcessingException e) {
            return erro("Formato JSON inválido", 400, response);
        }
    }

    public Object deletar(Request request, Response response) {
        response.type("application/json");

        try {
            int id = Integer.parseInt(request.params(":id"));
            boolean sucesso = UsuarioDAO.deletar(id);

            if (sucesso) {
                return "{\"sucesso\": true, \"mensagem\": \"Usuário deletado com sucesso\"}";
            }
            return erro("Usuário não encontrado", 404, response);
        } catch (NumberFormatException e) {
            return erro("ID inválido", 400, response);
        }
    }
}