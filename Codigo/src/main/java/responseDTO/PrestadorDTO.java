package responseDTO;

public class PrestadorDTO {

    // Campos do Usuário (necessários para criar o usuário junto)
    private String nome;
    private String email;
    private String senha;
    private String telefone;
    private String local;

    // Campos do Prestador
    private String descricao;
    private int    areaAtuacao; // FK para Area_Atuacao(IdArea)
    private int    categoria;   // FK para Tipo(IdTipo)

    // --- Getters e Setters ---

    public String getNome()             { return nome; }
    public void   setNome(String nome)  { this.nome = nome; }

    public String getEmail()              { return email; }
    public void   setEmail(String email)  { this.email = email; }

    public String getSenha()              { return senha; }
    public void   setSenha(String senha)  { this.senha = senha; }

    public String getTelefone()                 { return telefone; }
    public void   setTelefone(String telefone)  { this.telefone = telefone; }

    public String getLocal()              { return local; }
    public void   setLocal(String local)  { this.local = local; }

    public String getDescricao()                { return descricao; }
    public void   setDescricao(String descricao){ this.descricao = descricao; }

    public int  getAreaAtuacao()              { return areaAtuacao; }
    public void setAreaAtuacao(int areaAtuacao){ this.areaAtuacao = areaAtuacao; }

    public int  getCategoria()            { return categoria; }
    public void setCategoria(int categoria){ this.categoria = categoria; }
}
