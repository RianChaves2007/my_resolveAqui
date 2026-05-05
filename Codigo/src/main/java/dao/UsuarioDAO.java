package dao;

import FilterDTO.LoginDTO;
import FilterDTO.UsuarioFilterDTO;
import responseDTO.UsuarioDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO extends DAO {

    public UsuarioDAO() {
        super();
        conectar();
    }

    public boolean inserir(UsuarioDTO dto) {
        String sql = "INSERT INTO usuario (nome, email, senha, local, telefone, foto, tipousuario) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement st = conexao.prepareStatement(sql);
            st.setString(1, dto.getNome());
            st.setString(2, dto.getEmail());
            st.setString(3, dto.getSenha());
            st.setString(4, dto.getLocal());
            st.setString(5, dto.getTelefone());
            st.setString(6, null);
            st.setString(7, dto.getTipoUsuario());

            st.executeUpdate();
            st.close();
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao inserir: " + e.getMessage());
            return false;
        }
    }

    /**
     * Insere um novo usuário e retorna o ID gerado automaticamente.
     * Utilizado pelo PrestadorService para encadear a criação de Prestador.
     *
     * @return ID gerado (>= 1) ou -1 em caso de erro
     */
    public int inserirRetornandoId(UsuarioDTO dto) {
        String sql = "INSERT INTO usuario (nome, email, senha, local, telefone, foto, tipousuario) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING idusuario";

        try {
            PreparedStatement st = conexao.prepareStatement(sql);
            st.setString(1, dto.getNome());
            st.setString(2, dto.getEmail());
            st.setString(3, dto.getSenha());
            st.setString(4, dto.getLocal());
            st.setString(5, dto.getTelefone());
            st.setString(6, null);
            st.setString(7, dto.getTipoUsuario());

            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("idusuario");
                st.close();
                return id;
            }
            st.close();
        } catch (SQLException e) {
            System.err.println("Erro ao inserir usuário (retornando id): " + e.getMessage());
        }
        return -1;
    }

    public UsuarioDTO login(LoginDTO loginDTO) {
        String sql = "SELECT * FROM usuario WHERE email = ? AND senha = ?";

        try {
            PreparedStatement st = conexao.prepareStatement(sql);
            st.setString(1, loginDTO.getEmail());
            st.setString(2, loginDTO.getSenha());
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                UsuarioDTO dto = new UsuarioDTO();
                dto.setId(rs.getInt("idusuario"));
                dto.setNome(rs.getString("nome"));
                dto.setEmail(rs.getString("email"));
                dto.setTipoUsuario(rs.getString("tipoUsuario"));
                return dto;
            }
            st.close();
        } catch (SQLException e) {
            System.err.println("Erro no login: " + e.getMessage());
        }
        return null;
    }

    public List<UsuarioDTO> listarComFiltro(UsuarioFilterDTO filtro) {
        List<UsuarioDTO> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM usuario WHERE 1=1");

        if (filtro.getNome() != null)        sql.append(" AND nome ILIKE ?");
        if (filtro.getEmail() != null)       sql.append(" AND email = ?");
        if (filtro.getTipoUsuario() != null) sql.append(" AND tipoUsuario = ?");

        try {
            PreparedStatement st = conexao.prepareStatement(sql.toString());
            int i = 1;

            if (filtro.getNome() != null)        st.setString(i++, "%" + filtro.getNome() + "%");
            if (filtro.getEmail() != null)       st.setString(i++, filtro.getEmail());
            if (filtro.getTipoUsuario() != null) st.setString(i++, filtro.getTipoUsuario());

            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                UsuarioDTO dto = new UsuarioDTO();
                dto.setId(rs.getInt("idusuario"));
                dto.setNome(rs.getString("nome"));
                dto.setEmail(rs.getString("email"));
                dto.setLocal(rs.getString("local"));
                dto.setTelefone(rs.getString("telefone"));
                dto.setFoto(rs.getString("foto"));
                dto.setTipoUsuario(rs.getString("tipoUsuario"));
                lista.add(dto);
            }
            st.close();
        } catch (Exception e) {
            System.err.println("Erro ao buscar: " + e.getMessage());
        }
        return lista;
    }

    public UsuarioDTO buscarPorId(int id) {
        String sql = "SELECT * FROM usuario WHERE idusuario = ?";

        try {
            PreparedStatement st = conexao.prepareStatement(sql);
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                UsuarioDTO dto = new UsuarioDTO();
                dto.setId(rs.getInt("idusuario"));
                dto.setNome(rs.getString("nome"));
                dto.setEmail(rs.getString("email"));
                dto.setLocal(rs.getString("local"));
                dto.setTelefone(rs.getString("telefone"));
                dto.setFoto(rs.getString("foto"));
                dto.setTipoUsuario(rs.getString("tipoUsuario"));
                return dto;
            }
            st.close();
        } catch (SQLException e) {
            System.err.println("Erro ao buscar: " + e.getMessage());
        }
        return null;
    }

    public boolean atualizar(UsuarioDTO dto) {
        String sql = "UPDATE usuario SET nome=?, email=?, senha=?, local=?, telefone=?, foto=?, tipoUsuario=? WHERE idusuario=?";

        try {
            PreparedStatement st = conexao.prepareStatement(sql);
            st.setString(1, dto.getNome());
            st.setString(2, dto.getEmail());
            st.setString(3, dto.getSenha());
            st.setString(4, dto.getLocal());
            st.setString(5, dto.getTelefone());
            st.setString(6, dto.getFoto());
            st.setString(7, dto.getTipoUsuario());
            st.setInt(8, dto.getId());
            
            int linhasAfetadas = st.executeUpdate();
            
            st.close();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar: " + e.getMessage());
            return false;
        }
    }

    public boolean deletar(int id) {
        String sql = "DELETE FROM usuario WHERE idusuario = ?";

        try(PreparedStatement st = conexao.prepareStatement(sql)) {
            st.setInt(1, id);
            int linhasAfetadas = st.executeUpdate();
            st.close();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao deletar: " + e.getMessage());
            return false;
        }
    }
}