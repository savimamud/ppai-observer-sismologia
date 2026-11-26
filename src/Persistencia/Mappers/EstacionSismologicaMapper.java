package Persistencia.Mappers;

import Modelo.EstacionSismologica;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class EstacionSismologicaMapper {
    private Connection connection;

    public EstacionSismologicaMapper(Connection connection) {
        this.connection = connection;
    }

    public EstacionSismologica buscarPorId(int id) {
        String sql = "SELECT * FROM estaciones WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return map(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Este método devuelve un Mapa de Estaciones, usando el ID de la BD como clave.
     */
    public Map<Integer, EstacionSismologica> buscarTodosConId() {
        Map<Integer, EstacionSismologica> estacionesMap = new HashMap<>();
        String sql = "SELECT * FROM estaciones";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                EstacionSismologica estacion = map(rs);
                estacionesMap.put(id, estacion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return estacionesMap;
    }

    private EstacionSismologica map(ResultSet rs) throws SQLException {
        String codigo = rs.getString("codigo");
        String nombre = rs.getString("nombre");
        double latitud = rs.getDouble("latitud");
        double longitud = rs.getDouble("longitud");
        return new EstacionSismologica(codigo, nombre, latitud, longitud);
    }
}