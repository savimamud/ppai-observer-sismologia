package Persistencia.Mappers;

import Modelo.EstacionSismologica;
import Modelo.Sismografo;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SismografoMapper {
    private Connection connection;

    public SismografoMapper(Connection connection) {
        this.connection = connection;
    }

    public List<Sismografo> buscarTodos(Map<Integer, EstacionSismologica> estacionesMap) {
        List<Sismografo> sismografos = new ArrayList<>();
        String sql = "SELECT * FROM sismografos";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                sismografos.add(map(rs, estacionesMap));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sismografos;
    }

    private Sismografo map(ResultSet rs, Map<Integer, EstacionSismologica> estacionesMap) throws SQLException {
        String identificador = rs.getString("identificador");
        String descripcion = rs.getString("descripcion");
        String marca = rs.getString("marca");
        String modelo = rs.getString("modelo");
        int estacionId = rs.getInt("estacion_id");

        EstacionSismologica estacion = estacionesMap.get(estacionId);

        // El constructor necesita la estación, pero la relación es bidireccional
        // La seteamos después para evitar problemas
        Sismografo sismografo = new Sismografo(identificador, descripcion, null, marca, modelo);
        sismografo.setEstacionSismologica(estacion);

        if (estacion != null) {
            estacion.setSismografo(sismografo);
        }

        return sismografo;
    }
}
