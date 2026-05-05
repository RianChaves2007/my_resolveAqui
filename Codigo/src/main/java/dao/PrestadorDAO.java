package dao;

import responseDTO.PrestadorDTO;
import java.sql.*;

/**
 * DAO responsável pelas operações na tabela Prestador.
 * Herda de DAO (conexão base) — não herda de UsuarioDAO,
 * pois Prestador e Usuario são entidades distintas no banco.
 */
public class PrestadorDAO extends DAO {

    public PrestadorDAO() {
        super();
        conectar();
    }

    /**
     * Insere um novo registro na tabela Prestador vinculado ao usuário já criado.
     *
     * @param idUsuario ID do usuário recém-cadastrado em Usuario
     * @param dto       dados específicos do prestador
     * @return true em caso de sucesso, false em caso de erro
     */
    public boolean inserir(int idUsuario, PrestadorDTO dto) {
        String sql = "INSERT INTO prestador (id_usuario, descricao, areaatuacao, categoria) "
                   + "VALUES (?, ?, ?, ?)";

        try {
            PreparedStatement st = conexao.prepareStatement(sql);
            st.setInt(1, idUsuario);
            st.setString(2, dto.getDescricao());
            st.setInt(3, dto.getAreaAtuacao());
            st.setInt(4, dto.getCategoria());

            st.executeUpdate();
            st.close();
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao inserir prestador: " + e.getMessage());
            return false;
        }
    }
}
