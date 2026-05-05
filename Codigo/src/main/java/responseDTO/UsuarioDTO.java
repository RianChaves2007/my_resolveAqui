package responseDTO;

public class UsuarioDTO {
	
    private int id;
    private String nome;
    private String email;
    private String senha;
    private String local;
    private String telefone;
    private String foto;
    private String tipoUsuario;

    // Getters e Setters
    public int getId() {
    	return id; 
    }
    
    public void setId(int id) { 
    	this.id = id; 
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
    
    public String getLocal() { 
    	return local; 
    }
    
    public void setLocal(String local) { 
    	this.local = local; 
    }
    
    public String getTelefone() { 
    	return telefone; 
    }
    
    public void setTelefone(String telefone) { 
    	this.telefone = telefone; 
    }
    
    public String getFoto() { 
    	return foto; 
    }
    
    public void setFoto(String foto) { 
    	this.foto = foto; 
    }
    
    public String getTipoUsuario() { 
    	return tipoUsuario; 
    }
    
    public void setTipoUsuario(String tipoUsuario) { 
    	this.tipoUsuario = tipoUsuario; 
    }
    
}