package Persistencia.Mappers;

import Modelo.Empleado;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class EmpleadoMapper {

    private Connection connection;

    public EmpleadoMapper(Connection connection) {
        this.connection = connection;
    }

    public Empleado buscarPorId(int id) {
        String sql = "SELECT * FROM empleados WHERE id = ?";
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
     *Este método devuelve un Mapa de Empleados, usando el ID de la BD como clave.
     */
    public Map<Integer, Empleado> buscarTodosConId() {
        Map<Integer, Empleado> empleadosMap = new HashMap<>();
        String sql = "SELECT * FROM empleados";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                Empleado empleado = map(rs);
                empleadosMap.put(id, empleado);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return empleadosMap;
    }

    private Empleado map(ResultSet rs) throws SQLException {
        String nombre = rs.getString("nombre");
        String apellido = rs.getString("apellido");
        String mail = rs.getString("mail");
        String telefono = rs.getString("telefono");
        Empleado empleado = new Empleado(nombre, apellido, mail, "");
        empleado.setTelefono(telefono);
        return empleado;
    }
}