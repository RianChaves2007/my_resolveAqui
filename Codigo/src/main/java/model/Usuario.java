package model;

import java.util.Objects;

public class Usuario {
	
	private int idusuario;
	private String nome;
	private String email;
	private String senha;
	private String telefone;
	private String local;
	private String foto;
	private String tipoUsuario;
	
	public Usuario() {}
	
	public Usuario(int idusuario, String nome, String email, String senha, String telefone, String local, String foto, String tipoUsuario) {
		super();
		this.idusuario = idusuario;
		this.nome = nome;
		this.email = email;
		this.senha = senha;
		this.telefone = telefone; 
		this.local = local;
		this.foto = foto;
		this.tipoUsuario = tipoUsuario;
	}

	public int getIdUsuario() {
		return idusuario;
	}

	public void setIdUsuario(int idusuario) {
		this.idusuario = idusuario;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}
    
	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}
	
	public String getLocal() {
		return local;
	}
	
	public void setLocal(String local) {
		this.local = local;
	}
	
	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}

	public String gettipoUsuario() {
		return tipoUsuario;
	}

	public void setTipousuario(String tipoUsuario) {
		this.tipoUsuario = tipoUsuario;
	}
	
	@Override
    public String toString(){
		return email;
    }
    
    @Override
	public boolean equals(Object obj) {	
		return (this.getIdUsuario() == ((Usuario) obj).getIdUsuario());
    }
}
