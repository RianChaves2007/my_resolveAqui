package model;

public class Prestador extends Usuario {

    private int    idPrestador;
    private String descricao;
    private int    areaAtuacao; // FK para Area_Atuacao(IdArea)
    private int    categoria;   // FK para Tipo(IdTipo)

    public Prestador() {}

    public Prestador(
            int    idUsuario,
            String nome,
            String email,
            String senha,
            String telefone,
            String local,
            String foto,
            String tipoUsuario,
            int    idPrestador,
            String descricao,
            int    areaAtuacao,
            int    categoria) {

        super(idUsuario, nome, email, senha, telefone, local, foto, tipoUsuario);
        this.idPrestador  = idPrestador;
        this.descricao    = descricao;
        this.areaAtuacao  = areaAtuacao;
        this.categoria    = categoria;
    }

    // Getters
    public int    getIdPrestador()  { return idPrestador; }
    public String getDescricao()    { return descricao; }
    public int    getAreaAtuacao()  { return areaAtuacao; }
    public int    getCategoria()    { return categoria; }

    // Setters
    public void setIdPrestador(int idPrestador)    { this.idPrestador = idPrestador; }
    public void setDescricao(String descricao)     { this.descricao   = descricao; }
    public void setAreaAtuacao(int areaAtuacao)    { this.areaAtuacao = areaAtuacao; }
    public void setCategoria(int categoria)        { this.categoria   = categoria; }
}
