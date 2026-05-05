package app;

import service.UsuarioService;
import static spark.Spark.*;

public class Aplicacao {
	
	private static UsuarioService usuarioService = new UsuarioService();
	
	public static void main(String[] args) {
		port(8080);
		staticFiles.location("/public"); 
		
		before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,OPTIONS");
            response.header("Access-Control-Allow-Headers", "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin");
        });
		
		options("/*", (req, res) -> { res.status(200); return "OK"; });

		post("/login", (resquest, response) -> usuarioService.login(resquest, response));
		post("/cadastro", (resquest, response) -> usuarioService.cadastro(resquest, response));
	
		get("/usuarios",  (resquest, response) -> usuarioService.listarComFiltro(resquest, response));
		get("/usuario/:id", (resquest, response) -> usuarioService.buscarPorId(resquest, response));
		put("/usuario/:id", (resquest, response) -> usuarioService.atualizar(resquest, response));
		delete("/usuario/:id",(resquest, response) -> usuarioService.deletar(resquest, response));
	}	
}
