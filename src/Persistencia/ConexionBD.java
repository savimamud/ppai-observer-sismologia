package Persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionBD {

    private static ConexionBD instancia;
    private Connection connection;
    private static final String DB_URL = "jdbc:sqlite:sismologia.db";

    private ConexionBD() {
        try {
            this.connection = DriverManager.getConnection(DB_URL);
            System.out.println("Conexión a SQLite establecida.");
        } catch (SQLException e) {
            System.err.println("Error al conectar con la base de datos: " + e.getMessage());
            this.connection = null;
        }
    }

    public static synchronized ConexionBD getInstancia() {
        if (instancia == null) {
            instancia = new ConexionBD();
        }
        return instancia;
    }

    public Connection getConnection() {
        return connection;
    }

    public void inicializarBaseDeDatos() {
        if (connection == null) return;

        try (Statement stmt = connection.createStatement()) {
            // Crear Tablas
            stmt.execute("CREATE TABLE IF NOT EXISTS empleados (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nombre TEXT NOT NULL, " +
                    "apellido TEXT NOT NULL, " +
                    "mail TEXT, " +
                    "telefono TEXT)");

            stmt.execute("CREATE TABLE IF NOT EXISTS usuarios (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nombre TEXT NOT NULL UNIQUE, " +
                    "contrasena TEXT NOT NULL, " +
                    "empleado_id INTEGER, " +
                    "FOREIGN KEY (empleado_id) REFERENCES empleados(id))");

            stmt.execute("CREATE TABLE IF NOT EXISTS estaciones (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "codigo TEXT NOT NULL UNIQUE, " +
                    "nombre TEXT NOT NULL, " +
                    "latitud REAL, " +
                    "longitud REAL)");

            stmt.execute("CREATE TABLE IF NOT EXISTS sismografos (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "identificador TEXT NOT NULL UNIQUE, " +
                    "descripcion TEXT, " +
                    "marca TEXT, " +
                    "modelo TEXT, " +
                    "estacion_id INTEGER, " +
                    "FOREIGN KEY (estacion_id) REFERENCES estaciones(id))");

            stmt.execute("CREATE TABLE IF NOT EXISTS ordenes_inspeccion (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "numero_orden TEXT NOT NULL UNIQUE, " +
                    "fecha_hora_realizacion TEXT, " +
                    "fecha_hora_finalizacion_estimada TEXT, " +
                    "empleado_id INTEGER, " +
                    "estacion_id INTEGER, " +
                    "estado_nombre TEXT, " +
                    "fecha_hora_cierre TEXT, " +
                    "observacion_cierre TEXT, " +
                    "FOREIGN KEY (empleado_id) REFERENCES empleados(id), " +
                    "FOREIGN KEY (estacion_id) REFERENCES estaciones(id))");

            // Verificar si la base de datos ya tiene datos
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM empleados");
            if (rs.next() && rs.getInt(1) == 0) {
                System.out.println("Base de datos vacía. Poblando con datos iniciales...");
                poblarDatosIniciales(stmt);
            }

        } catch (SQLException e) {
            System.err.println("Error al inicializar la base de datos: " + e.getMessage());
        }
    }

    private void poblarDatosIniciales(Statement stmt) throws SQLException {
        // Insertar Empleados
        stmt.execute("INSERT INTO empleados (id, nombre, apellido, mail, telefono) VALUES (1, 'Lara', 'Sasaki', 'lsasaki@example.com', '123456789')");
        stmt.execute("INSERT INTO empleados (id, nombre, apellido, mail, telefono) VALUES (2, 'Ana', 'Gomez', 'agomez@example.com', '654321')");

        // Insertar Usuario
        stmt.execute("INSERT INTO usuarios (nombre, contrasena, empleado_id) VALUES ('cperez_ri', 'pass123', 1)");

        // Insertar Estaciones
        stmt.execute("INSERT INTO estaciones (id, codigo, nombre, latitud, longitud) VALUES (1, 'EST-N01', 'Estación Central Norte', 10.0, 20.0)");
        stmt.execute("INSERT INTO estaciones (id, codigo, nombre, latitud, longitud) VALUES (2, 'EST-S05', 'Estación Remota Sur', -15.5, 25.3)");

        // Insertar Sismografos
        stmt.execute("INSERT INTO sismografos (identificador, descripcion, marca, modelo, estacion_id) VALUES ('SISM-A01', 'Sismógrafo Principal Est. Norte', 'GeoSIG', 'VE-323', 1)");
        stmt.execute("INSERT INTO sismografos (identificador, descripcion, marca, modelo, estacion_id) VALUES ('SISM-B02', 'Sismógrafo Auxiliar Est. Sur', 'Kinemetrics', 'Episensor', 2)");

        // --- ÓRDENES DE INSPECCIÓN ---
        stmt.execute("INSERT INTO ordenes_inspeccion (numero_orden, empleado_id, estacion_id, estado_nombre, fecha_hora_finalizacion_estimada, fecha_hora_realizacion) VALUES ('OI-2024-001', 1, 1, 'Completamente Realizada', '2024-08-15T17:00:00', '2024-08-14T10:00:00')");
        stmt.execute("INSERT INTO ordenes_inspeccion (numero_orden, empleado_id, estacion_id, estado_nombre, fecha_hora_finalizacion_estimada, fecha_hora_realizacion) VALUES ('OI-2024-002', 1, 2, 'Completamente Realizada', '2024-09-01T17:00:00', '2024-08-28T11:30:00')");
        stmt.execute("INSERT INTO ordenes_inspeccion (numero_orden, empleado_id, estacion_id, estado_nombre, fecha_hora_finalizacion_estimada, fecha_hora_realizacion) VALUES ('OI-2024-003', 2, 1, 'Completamente Realizada', '2024-08-20T17:00:00', '2024-08-15T11:00:00') ");
        stmt.execute("INSERT INTO ordenes_inspeccion (numero_orden, empleado_id, estacion_id, estado_nombre, fecha_hora_finalizacion_estimada, fecha_hora_realizacion) VALUES ('OI-2024-004', 1, 2, 'Completamente Realizada', '2024-10-10T17:00:00', '2024-10-05T15:00:00')");
        stmt.execute("INSERT INTO ordenes_inspeccion (numero_orden, empleado_id, estacion_id, estado_nombre, fecha_hora_finalizacion_estimada, fecha_hora_realizacion) VALUES ('OI-2024-005', 1, 1, 'Completamente Realizada', '2024-11-20T12:00:00', '2024-11-15T09:00:00')");
        // Orden pendiente para Lara Sasaki que NO aparecerá en la lista
        stmt.execute("INSERT INTO ordenes_inspeccion (numero_orden, empleado_id, estacion_id, estado_nombre, fecha_hora_finalizacion_estimada, fecha_hora_realizacion) VALUES ('OI-2024-006', 1, 2, 'Pendiente de Inspección', '2024-12-01T18:00:00', '2024-11-29T10:00:00')");
        // Otra orden completada para Ana Gomez que NO aparecerá para Lara
        stmt.execute("INSERT INTO ordenes_inspeccion (numero_orden, empleado_id, estacion_id, estado_nombre, fecha_hora_finalizacion_estimada, fecha_hora_realizacion) VALUES ('OI-2024-007', 2, 2, 'Completamente Realizada', '2024-11-25T10:00:00', '2024-11-22T14:00:00')");

        System.out.println("Datos iniciales insertados correctamente.");
    }
}