package app;

import service.PrestadorService;
import service.UsuarioService;
import static spark.Spark.*;

public class Aplicacao {

    private static UsuarioService   usuarioService   = new UsuarioService();
    private static PrestadorService prestadorService = new PrestadorService();

    public static void main(String[] args) {
        port(8080);
        staticFiles.location("/public");

        // CORS
        before((request, response) -> {
            response.header("Access-Control-Allow-Origin",  "*");
            response.header("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,OPTIONS");
            response.header("Access-Control-Allow-Headers",
                    "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin");
        });

        options("/*", (req, res) -> { res.status(200); return "OK"; });

        // Rotas de Usuário
        post("/login",        (req, res) -> usuarioService.login(req, res));
        post("/cadastro",     (req, res) -> usuarioService.cadastro(req, res));

        get("/usuarios",      (req, res) -> usuarioService.listarComFiltro(req, res));
        get("/usuario/:id",   (req, res) -> usuarioService.buscarPorId(req, res));
        put("/usuario/:id",   (req, res) -> usuarioService.atualizar(req, res));
        delete("/usuario/:id",(req, res) -> usuarioService.deletar(req, res));

        // Rotas de Prestador
        post("/cadastro-prestador", (req, res) -> prestadorService.cadastrar(req, res));

        // Rotas auxiliares (listas para os selects do front-end)
        get("/areas-atuacao", (req, res) -> prestadorService.listarAreas(req, res));
        get("/tipos",         (req, res) -> prestadorService.listarTipos(req, res));
    }
}
