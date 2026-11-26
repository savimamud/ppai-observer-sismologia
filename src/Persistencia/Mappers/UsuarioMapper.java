package Persistencia.Mappers;

import Modelo.Empleado;
import Modelo.Usuario;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UsuarioMapper {

    private Connection connection;

    public UsuarioMapper(Connection connection) {
        this.connection = connection;
    }

    /**
     * Recibe el mapa de empleados para evitar consultas repetidas a la BD.
     */
    public List<Usuario> buscarTodos(Map<Integer, Empleado> empleadosMap) {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                usuarios.add(map(rs, empleadosMap));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarios;
    }

    private Usuario map(ResultSet rs, Map<Integer, Empleado> empleadosMap) throws SQLException {
        String nombre = rs.getString("nombre");
        String contrasena = rs.getString("contrasena");
        int empleadoId = rs.getInt("empleado_id");

        Empleado empleado = empleadosMap.get(empleadoId); // Búsqueda eficiente en el mapa

        return new Usuario(nombre, contrasena, empleado);
    }
}