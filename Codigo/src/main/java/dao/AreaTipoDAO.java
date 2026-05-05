package dao;

import java.sql.*;
import java.util.*;

/**
 * DAO responsável por buscar as listas de Area_Atuacao e Tipo,
 * utilizadas para popular os selects no front-end de cadastro de prestador.
 */
public class AreaTipoDAO extends DAO {

    public AreaTipoDAO() {
        super();
        conectar();
    }

    /** Retorna todas as áreas de atuação ordenadas alfabeticamente. */
    public List<Map<String, Object>> listarAreas() {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT idarea, area FROM area_atuacao ORDER BY area";

        try {
            PreparedStatement st = conexao.prepareStatement(sql);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("id",   rs.getInt("idarea"));
                item.put("area", rs.getString("area"));
                lista.add(item);
            }
            st.close();
        } catch (SQLException e) {
            System.err.println("Erro ao listar áreas de atuação: " + e.getMessage());
        }
        return lista;
    }

    /** Retorna todos os tipos de prestador ordenados alfabeticamente. */
    public List<Map<String, Object>> listarTipos() {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT idtipo, tipo FROM tipo ORDER BY tipo";

        try {
            PreparedStatement st = conexao.prepareStatement(sql);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("id",   rs.getInt("idtipo"));
                item.put("tipo", rs.getString("tipo"));
                lista.add(item);
            }
            st.close();
        } catch (SQLException e) {
            System.err.println("Erro ao listar tipos: " + e.getMessage());
        }
        return lista;
    }
}
