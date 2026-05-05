package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import responseDTO.UsuarioDTO;

public class PrestadorDAO extends UsuarioDAO{
    
    public PrestadorDAO(){
        super();
        conectar();
    }

    public boolean inserir(UsuarioDTO dto) {
        String sql = "INSERT INTO prestador (nome, email, senha, local, telefone, foto, tipoUsuario) VALUES (?, ?, ?, ?, ?, ?, ?)";

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
}