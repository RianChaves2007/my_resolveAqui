package FilterDTO;

public class UsuarioFilterDTO {
    private String nome;
    private String email;
    private String tipoUsuario;

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
    
    public String getTipoUsuario() { 
    	return tipoUsuario; 
    }
    
    public void setTipoUsuario(String tipoUsuario) { 
    	this.tipoUsuario = tipoUsuario; 
    }
    
}