package App;

import Controlador.GestorInspeccion;
import Modelo.*;
import Interfaz.PantallaInspeccion;
import Interfaz.PantallaInspeccionGUI;
import Persistencia.ConexionBD;
import Persistencia.Mappers.*;

import javax.swing.SwingUtilities;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {

        // --- 1. Conexión e inicialización de la base de datos ---
        ConexionBD conexionBD = ConexionBD.getInstancia();
        conexionBD.inicializarBaseDeDatos();
        Connection connection = conexionBD.getConnection();

        // --- 2. Creación de los Mappers para acceder a la BD ---
        EmpleadoMapper empleadoMapper = new EmpleadoMapper(connection);
        UsuarioMapper usuarioMapper = new UsuarioMapper(connection);
        EstacionSismologicaMapper estacionMapper = new EstacionSismologicaMapper(connection);
        SismografoMapper sismografoMapper = new SismografoMapper(connection);
        OrdenDeInspeccionMapper ordenMapper = new OrdenDeInspeccionMapper(connection);

        // --- 3. Carga de datos desde la BD usando los Mappers ---
        Map<Integer, Empleado> empleadosMap = empleadoMapper.buscarTodosConId();
        Map<Integer, EstacionSismologica> estacionesMap = estacionMapper.buscarTodosConId();
        List<Usuario> todosLosUsuarios = usuarioMapper.buscarTodos(empleadosMap);
        List<Sismografo> todosLosSismografos = sismografoMapper.buscarTodos(estacionesMap);

        // --- 4. Creación de objetos que no se persisten (Estados y Motivos) ---
        Estado estadoPendiente = new Estado("Pendiente de Inspección", "...", "Orden", false, false, false);
        Estado estadoCompletamenteRealizada = new Estado("Completamente Realizada", "...", "Orden", true, false, false);
        Estado estadoCerrada = new Estado("Cerrada", "...", "Orden", true, true, false);
        Estado estadoSismografoOperativo = new Estado("Operativo", "...", "Sismografo", false, false, false);
        Estado estadoSismografoFueraServicio = new Estado("Fuera de Servicio", "...", "Sismografo", false, false, true);
        List<Estado> todosLosEstados = new ArrayList<>(List.of(estadoPendiente, estadoCompletamenteRealizada, estadoCerrada, estadoSismografoOperativo, estadoSismografoFueraServicio));
        Map<String, Estado> estadosMap = todosLosEstados.stream().collect(Collectors.toMap(Estado::getNombre, Function.identity()));

        MotivoTipo motivoFallaElectrica = new MotivoTipo(1, "Falla eléctrica general");
        MotivoTipo motivoDanioFisico = new MotivoTipo(2, "Daño físico por vandalismo");
        MotivoTipo motivoSensorDefectuoso = new MotivoTipo(3, "Sensor defectuoso o descalibrado");
        MotivoTipo motivoProblemaComunicaciones = new MotivoTipo(4, "Problema de comunicaciones");
        List<MotivoTipo> todosLosMotivosTipo = new ArrayList<>(List.of(
                motivoFallaElectrica,
                motivoDanioFisico,
                motivoSensorDefectuoso,
                motivoProblemaComunicaciones
        ));

        // --- 5. Carga de las órdenes, que dependen de los datos ya cargados ---
        List<OrdenDeInspeccion> todasLasOrdenes = ordenMapper.buscarTodos(empleadosMap, estacionesMap, estadosMap);

        // --- 6. Configuración de la sesión y estado inicial de objetos ---
        Usuario usuarioLogueado = todosLosUsuarios.stream().filter(u -> u.getNombre().equals("cperez_ri")).findFirst().orElse(null);
        Sesion sesionActiva = new Sesion(usuarioLogueado);

        for (Sismografo sismo : todosLosSismografos) {
            if (sismo.conocerCambioEstadoActual() == null) {
                sismo.modificarSituacion(estadoSismografoOperativo, null, false);
            }
        }

        // --- 7. Creación de la Interfaz Gráfica y el Gestor ---
        SwingUtilities.invokeLater(() -> {
            PantallaInspeccionGUI pantallaGUI = new PantallaInspeccionGUI();
            PantallaInspeccion pantallaLogica = pantallaGUI.getPantallaLogica();

            // Se le pasa la instancia de `ordenMapper` al constructor del Gestor
            GestorInspeccion gestor = new GestorInspeccion(
                    pantallaLogica,
                    sesionActiva,
                    todasLasOrdenes,
                    todosLosMotivosTipo,
                    todosLosEstados,
                    ordenMapper
            );

            pantallaLogica.setGestorInspeccion(gestor);
            pantallaLogica.habilitarVentana();
        });
    }
}