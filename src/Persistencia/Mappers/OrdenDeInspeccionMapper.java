//Se encarga de establecer y gestionar la conexión fisica con el archivo de la base de datos sismologia.db.
// También se encarga de construir la base de datos por primera vez (crear las tablas y llenarlas con datos iniciales).
package Persistencia.Mappers;

import Modelo.Empleado;
import Modelo.EstacionSismologica;
import Modelo.Estado;
import Modelo.OrdenDeInspeccion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrdenDeInspeccionMapper {
    private Connection connection;

    public OrdenDeInspeccionMapper(Connection connection) {
        this.connection = connection;
    }

    /**
     * Actualiza una orden de inspección en la base de datos.
     * @param orden El objeto OrdenDeInspeccion con los datos actualizados.
     */
    public void actualizar(OrdenDeInspeccion orden) {
        // Se añade la columna 'observacion_cierre' a la consulta UPDATE.
        String sql = "UPDATE ordenes_inspeccion SET estado_nombre = ?, fecha_hora_cierre = ?, observacion_cierre = ? WHERE numero_orden = ?";
        // Usa la conexión que le pasaron al crearlo para preparar la consulta SQL
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            // Se establecen los parámetros en el orden correcto
            stmt.setString(1, orden.getEstado().getNombre());
            stmt.setString(2, orden.getFechaHoraCierre() != null ? orden.getFechaHoraCierre().toString() : null);
            stmt.setString(3, orden.getObservacionCierre());
            stmt.setString(4, orden.getNumeroOrden());

            stmt.executeUpdate(); //Ejecuta la consulta
            System.out.println("PERSISTENCIA: Orden " + orden.getNumeroOrden() + " actualizada en la base de datos.");

        } catch (SQLException e) {
            System.err.println("Error al actualizar la orden de inspección: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<OrdenDeInspeccion> buscarTodos(
            Map<Integer, Empleado> empleadosMap,
            Map<Integer, EstacionSismologica> estacionesMap,
            Map<String, Estado> estadosMap
    ) {
        List<OrdenDeInspeccion> ordenes = new ArrayList<>();
        String sql = "SELECT * FROM ordenes_inspeccion";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ordenes.add(map(rs, empleadosMap, estacionesMap, estadosMap));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ordenes;
    }

    private OrdenDeInspeccion map(
            ResultSet rs,
            Map<Integer, Empleado> empleadosMap,
            Map<Integer, EstacionSismologica> estacionesMap,
            Map<String, Estado> estadosMap
    ) throws SQLException {
        String numeroOrden = rs.getString("numero_orden");
        int empleadoId = rs.getInt("empleado_id");
        int estacionId = rs.getInt("estacion_id");
        String estadoNombre = rs.getString("estado_nombre");
        String fechaFinalizacionStr = rs.getString("fecha_hora_finalizacion_estimada");
        String fechaRealizacionStr = rs.getString("fecha_hora_realizacion");

        Empleado empleado = empleadosMap.get(empleadoId);
        EstacionSismologica estacion = estacionesMap.get(estacionId);
        Estado estado = estadosMap.get(estadoNombre);

        LocalDateTime fechaFinalizacion = fechaFinalizacionStr != null ? LocalDateTime.parse(fechaFinalizacionStr) : null;
        LocalDateTime fechaRealizacion = fechaRealizacionStr != null ? LocalDateTime.parse(fechaRealizacionStr) : null;

        OrdenDeInspeccion orden = new OrdenDeInspeccion(numeroOrden, empleado, estacion, estado, fechaFinalizacion);
        if (fechaRealizacion != null) {
            orden.setFechaHoraRealizacion(fechaRealizacion);
        }
        return orden;
    }
}