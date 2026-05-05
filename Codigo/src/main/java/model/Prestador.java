package model;

public class Prestador extends Usuario {
	private int idprestador;
	private String descrissao;
	private String areaatuacao;
	private String categoria;
	
	public Prestador() {}
	
	public Prestador(
			int idusuario, 
			String nome, 
			String email, 
			String senha, 
			String telefone, 
			String local, 
			String foto, 
			String tipoUsuario,
			int idprestador,
			String descrissao, 
			String areaatuacao, 
			String categoria) {
		
		super(  idusuario, 
				nome,
				email,
				senha,
				telefone, 
				local, 
				foto, 
				tipoUsuario);
		this.idprestador = idprestador;
		this.descrissao = descrissao;
		this.areaatuacao = areaatuacao;
		this.categoria = categoria;
	}
	
	public Prestador(int id, String descrissao, String area, String categoria) {
		this.idprestador = id;
		this.descrissao = descrissao;
		this.areaatuacao = area;
		this.categoria = categoria;
	}
	
	// getters
	public int getIdPrestador() { return idprestador; }
	public String getDescrissao() { return descrissao; }
	public String getAreaAtuacao() { return areaatuacao; }
	public String getCategoria() { return categoria; }
	
	//setters
	public void setIdPrestador(int id) { this.idprestador = id; }
	public void setDescrissao(String descrissao) {  this.descrissao = descrissao; }
	public void setAreaAtuacao(String areaatuacao) { this.areaatuacao = areaatuacao; }
	public void setCategoria(String categoria) { this.categoria = categoria; }
}

