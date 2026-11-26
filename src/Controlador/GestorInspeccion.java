package Controlador;

import Modelo.*;
import Interfaz.PantallaInspeccion;
import Persistencia.Mappers.OrdenDeInspeccionMapper; // -> 1. IMPORTAR EL MAPPER

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// El nombre de la interfaz aquí es ISujetoCierreOrden
public class GestorInspeccion implements ISujetoCierreOrden {

    private PantallaInspeccion pantalla;
    private Sesion sesionActiva;
    private List<OrdenDeInspeccion> ordenesEmpleado;
    private Estado estadoCerradaParaOrden;
    private Estado estadoFueraDeServicioParaSismografo;
    private List<MotivoTipo> todosLosMotivosTipoRepositorio;
    private LocalDateTime fechaHoraActual;
    private Empleado empleadoLogueado;
    private OrdenDeInspeccion ordenInspeccionSeleccionada;
    private String observacionIngresada;
    private List<MotivoFueraServicio> motivosSeleccionadosParaSismografoFS;
    private List<OrdenDeInspeccion> todasLasOrdenesRepositorio;
    private List<Estado> todosLosEstadosRepositorio;
    private boolean operacionEnCurso = false;

    private List<IObservadorCierreOrden> observadores = new ArrayList<>();

    // -> 2. AÑADIR ATRIBUTO PARA EL MAPPER
    private OrdenDeInspeccionMapper ordenMapper;

    // Atributos para almacenar temporalmente los datos para la notificación
    private Sismografo sismografoParaNotificar;
    private Estado estadoParaNotificar;
    private Empleado empleadoParaNotificar;
    private List<MotivoFueraServicio> motivosParaNotificar;

    // -> 3. MODIFICAR EL CONSTRUCTOR PARA RECIBIR EL MAPPER
    public GestorInspeccion(PantallaInspeccion pantallaLogica, Sesion sesionActiva,
                            List<OrdenDeInspeccion> repoOrdenes, List<MotivoTipo> repoMotivosTipo,
                            List<Estado> repoEstados, OrdenDeInspeccionMapper ordenMapper) {
        this.pantalla = pantallaLogica;
        this.sesionActiva = sesionActiva;
        this.todasLasOrdenesRepositorio = repoOrdenes;
        this.todosLosMotivosTipoRepositorio = new ArrayList<>(repoMotivosTipo);
        this.todosLosEstadosRepositorio = repoEstados;
        this.ordenesEmpleado = new ArrayList<>();
        this.motivosSeleccionadosParaSismografoFS = new ArrayList<>();
        this.ordenMapper = ordenMapper; // <- Guardar la referencia

        this.inicializarObservadores();
    }

    /**
     * Orquesta la creación y suscripción de los observadores,.
     */
    private void inicializarObservadores() {
        System.out.println("GESTOR: Iniciando la suscripción de observadores...");

        IObservadorCierreOrden observadorMail = new InterfazMail();
        this.suscribir(observadorMail);

        IObservadorCierreOrden observadorMonitor = new InterfazMonitor();
        this.suscribir(observadorMonitor);
    }

    @Override
    public void suscribir(IObservadorCierreOrden observador) {
        System.out.println("GESTOR: [AUTO-LLAMADA] suscribir(observador:" + observador.getClass().getSimpleName() + ")");
        this.observadores.add(observador);
    }

    @Override
    public void quitar(IObservadorCierreOrden observador) {
        this.observadores.remove(observador);
    }

    /**
     * Inicia la notificación.
     */
    @Override
    public void notificarCierreOrden() {
        System.out.println("GESTOR: [AUTO-LLAMADA] notificarCierreOrden()");

        String identificadorSismografo = sismografoParaNotificar.getIdentificadorSismografo();
        String estado = estadoParaNotificar.getNombre();
        LocalDateTime fechaHoraRegistro = fechaHoraActual;

        List<String> motivosLista = new ArrayList<>();
        List<String> comentariosLista = new ArrayList<>();
        for (MotivoFueraServicio mfs : this.motivosParaNotificar) {
            motivosLista.add(mfs.getMotivoTipo().obtenerDescripcion());
            comentariosLista.add(mfs.getComentario());
        }
        String[] motivos = motivosLista.toArray(new String[0]);
        String[] comentarios = comentariosLista.toArray(new String[0]);

        System.out.println("GESTOR: loop [mientras hayan pantallas mail]");
        for (IObservadorCierreOrden obs : this.observadores) {
            if (obs instanceof InterfazMail) {
                obs.notificarCierre(identificadorSismografo, estado, fechaHoraRegistro, motivos, comentarios);
            }
        }

        System.out.println("GESTOR: loop [mientras hayan pantallas monitor]");
        for (IObservadorCierreOrden obs : this.observadores) {
            if (obs instanceof InterfazMonitor) {
                obs.notificarCierre(identificadorSismografo, estado, fechaHoraRegistro, motivos, comentarios);
            }
        }
    }

    public void tomarConfirmacionCerrarOrden(boolean confirmacionUsuario) {
        if (!confirmacionUsuario) {
            this.pantalla.mostrarMensaje("Operación cancelada por el usuario.");
            this.finCU();
            return;
        }

        boolean sismografoSePoneFueraDeServicioEfectivamente = false;
        if (this.estadoFueraDeServicioParaSismografo != null &&
                !this.motivosSeleccionadosParaSismografoFS.isEmpty() &&
                this.estadoFueraDeServicioParaSismografo.esFueraDeServicio()) {
            sismografoSePoneFueraDeServicioEfectivamente = true;
        }

        this.obtenerFechaHoraActual();

        // --- Cambios en la lógica de negocio (en memoria) ---
        this.ordenInspeccionSeleccionada.setEstado(this.estadoCerradaParaOrden);
        this.ordenInspeccionSeleccionada.setFechaHoraCierre(this.fechaHoraActual);
        this.ordenInspeccionSeleccionada.setObservacionCierre(this.observacionIngresada);

        // --- 4. AÑADIR LLAMADA PARA PERSISTIR LOS CAMBIOS ---
        if (this.ordenMapper != null) {
            this.ordenMapper.actualizar(this.ordenInspeccionSeleccionada);
        }

        Sismografo sismografoAfectado = null;
        if (this.ordenInspeccionSeleccionada.conocerEstacionSismologica() != null) {
            sismografoAfectado = this.ordenInspeccionSeleccionada.conocerEstacionSismologica().obtenerSismografo();
        }

        if (sismografoSePoneFueraDeServicioEfectivamente && sismografoAfectado != null) {
            this.actualizarSituacionSismografo(
                    this.ordenInspeccionSeleccionada,
                    this.estadoFueraDeServicioParaSismografo,
                    this.motivosSeleccionadosParaSismografoFS,
                    true
            );

            this.sismografoParaNotificar = sismografoAfectado;
            this.estadoParaNotificar = this.estadoFueraDeServicioParaSismografo;
            this.empleadoParaNotificar = this.empleadoLogueado;
            this.motivosParaNotificar = this.motivosSeleccionadosParaSismografoFS;

            this.notificarCierreOrden();
        }

        Estado estadoFinalSismografoParaReporte = (sismografoAfectado != null) ? sismografoAfectado.obtenerEstadoActual() : null;
        this.pantalla.informarCierreExitoso(this.ordenInspeccionSeleccionada, sismografoAfectado,
                estadoFinalSismografoParaReporte, sismografoSePoneFueraDeServicioEfectivamente);
        this.finCU();
    }

    public void nuevoCierreOrden() {
        if (operacionEnCurso) {
            return;
        }
        operacionEnCurso = true;

        this.obtenerEmpleadoLogueado();

        if (this.empleadoLogueado == null) {
            this.pantalla.mostrarMensaje(
                    "Error: Empleado no logueado o no se pudo obtener la información."
            );
            this.finCU();
            return;
        }

        this.obtenerOrdenesPorEmpleado();
        this.ordenarPorFechaFinalizacion();

        this.pantalla.mostrarOrdenesInspeccion(this.ordenesEmpleado);
        this.pantalla.solicitarSeleccionOrdenInspeccion();
    }

    public void obtenerEmpleadoLogueado() {
        if (this.sesionActiva != null) {
            Usuario usuarioDeSesion = this.sesionActiva.obtenerUsuario();
            if (usuarioDeSesion != null) {
                this.empleadoLogueado = usuarioDeSesion.getRILLogueado();
                if (this.empleadoLogueado != null) {
                    this.pantalla.mostrarEmpleadoLogueado(this.empleadoLogueado);
                    return;
                }
            }
        }

        this.empleadoLogueado = null;
        this.pantalla.mostrarEmpleadoLogueado(null);
    }

    public void obtenerOrdenesPorEmpleado() {
        this.ordenesEmpleado.clear();

        if (this.empleadoLogueado != null && this.todasLasOrdenesRepositorio != null) {
            this.ordenesEmpleado.addAll(
                    this.todasLasOrdenesRepositorio.stream()
                            .filter(o -> o.esTuEmpleado(this.empleadoLogueado))
                            .filter(OrdenDeInspeccion::esCompletamenteRealizado)
                            .filter(o -> {
                                Estado estadoActualOrden = o.getEstado();
                                return estadoActualOrden != null && !estadoActualOrden.esCerrada();
                            })
                            .toList()
            );
        }
    }

    public void ordenarPorFechaFinalizacion() {
        if (this.ordenesEmpleado != null) {
            this.ordenesEmpleado.sort(
                    Comparator.comparing(
                            OrdenDeInspeccion::getFechaHoraFinalizacion,
                            Comparator.nullsLast(Comparator.naturalOrder())
                    )
            );
        }
    }

    public void tomarSeleccionOrdenInspeccion(OrdenDeInspeccion ordenSeleccionadaIU) {
        this.ordenInspeccionSeleccionada = ordenSeleccionadaIU;
        this.observacionIngresada = null;
        this.motivosSeleccionadosParaSismografoFS.clear();

        if (this.ordenInspeccionSeleccionada != null) {
            this.pantalla.solicitarIngresoObservacion();
            this.habilitarActualizarSituacionSismografos();

            List<MotivoTipo> tiposMotivo = this.buscarMotivosFueraServicio();
            this.pantalla.mostrarTiposDeMotivo(tiposMotivo);
            this.pantalla.solicitarSeleccionMotivos();
        }
    }

    public void tomarObservacion(String obs) {
        this.observacionIngresada = obs;
    }

    public void habilitarActualizarSituacionSismografos() {
        if (this.pantalla != null) {
            this.pantalla.habilitarActualizarSituacionSismografos();
        }
    }

    public List<MotivoTipo> buscarMotivosFueraServicio() {
        return new ArrayList<>(this.todosLosMotivosTipoRepositorio);
    }

    public void tomarMotivoTipo(MotivoTipo motivoTipoSeleccionado) {
        if (motivoTipoSeleccionado != null) {
            this.pantalla.solicitarComentarioMotivo(motivoTipoSeleccionado);
        }
    }

    public void tomarComentarioMotivo(MotivoTipo motivoAsociado, String comentario) {
        // Implementación pendiente
    }

    public void tomarMotivosFueraServicio(List<MotivoFueraServicio> motivosCompletos) {
        this.motivosSeleccionadosParaSismografoFS.clear();
        this.motivosSeleccionadosParaSismografoFS.addAll(motivosCompletos);
    }

    public void procesarIntentoDeConfirmacion() {
        if (!this.validarObservacionYCierreDeOrden()) {
            this.finCU();
            return;
        }

        this.estadoCerradaParaOrden = this.buscarEstadoAsignar("Cerrada");
        if (this.estadoCerradaParaOrden == null) {
            this.finCU();
            return;
        }

        boolean checkboxFSmarcadoEnUI =
                (this.pantalla.getGui() != null && this.pantalla.getGui().isChkFueraDeServicioSelected());

        if (checkboxFSmarcadoEnUI && !this.motivosSeleccionadosParaSismografoFS.isEmpty()) {
            this.estadoFueraDeServicioParaSismografo = this.buscarEstadoAsignar("Fuera de Servicio");
            if (this.estadoFueraDeServicioParaSismografo == null) {
                this.finCU();
                return;
            }

            if (!this.estadoFueraDeServicioParaSismografo.esFueraDeServicio()) {
                this.pantalla.informarErrorValidacion(
                        "Error de configuración: El estado definido como 'Fuera de Servicio' no es reconocido como tal."
                );
                this.finCU();
                return;
            }
        } else {
            this.estadoFueraDeServicioParaSismografo = null;
        }

        this.pantalla.solicitarConfirmacion();
    }

    private void actualizarSituacionSismografo(
            OrdenDeInspeccion o,
            Estado e,
            List<MotivoFueraServicio> m,
            boolean b
    ) {
        o.actualizarSismografo(e, m, b);
    }

    private boolean validarObservacionYCierreDeOrden() {
        if (this.ordenInspeccionSeleccionada == null) {
            this.pantalla.informarErrorValidacion("No se ha seleccionado ninguna orden de inspección.");
            return false;
        }

        if (this.observacionIngresada == null || this.observacionIngresada.trim().isEmpty()) {
            this.pantalla.informarErrorValidacion("La observación de cierre no puede estar vacía.");
            return false;
        }

        boolean checkboxFSmarcadoEnUI =
                (this.pantalla.getGui() != null && this.pantalla.getGui().isChkFueraDeServicioSelected());

        if (checkboxFSmarcadoEnUI) {
            if (this.motivosSeleccionadosParaSismografoFS.isEmpty()) {
                this.pantalla.informarErrorValidacion(
                        "Si el sismógrafo se marca como 'Fuera de Servicio', debe agregar al menos un motivo con comentario."
                );
                return false;
            }

            for (MotivoFueraServicio mfs : this.motivosSeleccionadosParaSismografoFS) {
                if (mfs.getComentario() == null || mfs.getComentario().trim().isEmpty()) {
                    this.pantalla.informarErrorValidacion(
                            "El motivo '" + mfs.getMotivoTipo().obtenerDescripcion() + "' no tiene un comentario válido."
                    );
                    return false;
                }
            }
        }

        return true;
    }

    public Estado buscarEstadoAsignar(String nombre) {
        for (Estado e : this.todosLosEstadosRepositorio) {
            if (e.getNombre().equalsIgnoreCase(nombre)) {
                return e;
            }
        }

        this.pantalla.informarErrorValidacion(
                "Error crítico: Estado '" + nombre + "' no definido en el sistema."
        );
        return null;
    }

    public void obtenerFechaHoraActual() {
        this.fechaHoraActual = LocalDateTime.now();
    }

    private void finCU() {
        this.ordenInspeccionSeleccionada = null;
        this.observacionIngresada = null;
        this.motivosSeleccionadosParaSismografoFS.clear();
        this.estadoCerradaParaOrden = null;
        this.estadoFueraDeServicioParaSismografo = null;
        this.fechaHoraActual = null;
        this.operacionEnCurso = false;
        this.sismografoParaNotificar = null;
        this.estadoParaNotificar = null;
        this.empleadoParaNotificar = null;
        this.motivosParaNotificar = null;
    }

    public List<OrdenDeInspeccion> getOrdenesEmpleado() {
        return this.ordenesEmpleado;
    }
}