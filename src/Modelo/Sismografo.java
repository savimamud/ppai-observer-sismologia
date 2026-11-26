package Modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Sismografo {

    // ... (atributos y constructor sin cambios) ...
    private LocalDate fechaAdquisicion;
    private String identificadorSismografo;
    private String marca;
    private String modelo;
    private String descripcion;
    private List<CambioEstado> historialCambioEstado;
    private EstacionSismologica estacionSismologica;

    public Sismografo(String identificadorSismografo, String descripcion, EstacionSismologica estacionSismologica, String marca, String modelo) {
        this.identificadorSismografo = identificadorSismografo;
        this.descripcion = descripcion;
        this.estacionSismologica = estacionSismologica;
        this.marca = marca;
        this.modelo = modelo;
        this.historialCambioEstado = new ArrayList<>();
        this.fechaAdquisicion = LocalDate.now();
    }

    // ... (getters y setters sin cambios) ...
    public LocalDate getFechaAdquisicion() { return fechaAdquisicion; }
    public void setFechaAdquisicion(LocalDate fechaAdquisicion) { this.fechaAdquisicion = fechaAdquisicion; }
    public String getIdentificadorSismografo() { return identificadorSismografo; }
    public void setIdentificadorSismografo(String id) { this.identificadorSismografo = id; }
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public EstacionSismologica getEstacionSismologica() { return estacionSismologica; }
    public void setEstacionSismologica(EstacionSismologica estacion) { this.estacionSismologica = estacion; }
    public EstacionSismologica conocerEstacionSismologica() { return estacionSismologica; }
    public List<CambioEstado> getHistorialCambioEstado() { return historialCambioEstado; }
    public void setHistorialCambioEstado(List<CambioEstado> cambios) { this.historialCambioEstado = cambios; }


    public CambioEstado conocerCambioEstadoActual() {
        if (this.historialCambioEstado.isEmpty()) {
            return null;
        }
        return this.historialCambioEstado.get(this.historialCambioEstado.size() - 1);
    }

    public Estado obtenerEstadoActual() {
        CambioEstado ultimoCambio = conocerCambioEstadoActual();
        if (ultimoCambio != null) {
            return ultimoCambio.getEstado();
        }
        return null;
    }

    public void modificarSituacion(Estado nuevoEstadoEntidad, List<MotivoFueraServicio> motivos,
                                   boolean esCausadoPorInspeccionDirecta) {
        System.out.println("SISMOGRAFO (" + identificadorSismografo + "): [AUTO-LLAMADA] modificarSituacion() " +
                "para nuevo estado: " + nuevoEstadoEntidad.getNombre());

        CambioEstado cambioEstadoPrevio = conocerCambioEstadoActual();

        if (cambioEstadoPrevio != null && cambioEstadoPrevio.getEstado() != null) {
            System.out.println("  SISMOGRAFO: Estado previo era: " + cambioEstadoPrevio.getEstado().getNombre());

            // --- INICIO DE MODIFICACIÓN PARA REFLEJAR EL DIAGRAMA ---
            // Consultando propiedades del estado previo, como se muestra en el diagrama
            boolean previoEraActual = cambioEstadoPrevio.esEstadoActual(); // Llamada explícita
            boolean previoFuePorInspeccion = cambioEstadoPrevio.esInhabilitadoPorInspeccion(); // Llamada explícita

            System.out.println("    SISMOGRAFO: Info estado previo - ¿Era actual?: " + previoEraActual +
                    ", ¿Fue inhabilitado por inspección?: " + previoFuePorInspeccion);

            // Lógica adicional basada en estos flags (opcional, según la necesidad del negocio)
            // Por ejemplo, si se necesitara hacer algo diferente si el estado previo NO era el actual
            // (lo cual indicaría un problema de consistencia de datos, ya que conocerCambioEstadoActual()
            // debería devolver el que sí es actual).
            if (!previoEraActual) {
                System.err.println("    SISMOGRAFO: ADVERTENCIA - El estado previo obtenido (" +
                        cambioEstadoPrevio.getEstado().getNombre() +
                        ") no está marcado como 'actual'. Esto podría indicar un problema.");
            }
            // --- FIN DE MODIFICACIÓN ---

            System.out.println("  SISMOGRAFO: Finalizando estado previo: " + cambioEstadoPrevio.getEstado().getNombre());
            cambioEstadoPrevio.setFechaHoraFin(LocalDateTime.now());
        } else if (cambioEstadoPrevio != null) {
            System.out.println("  SISMOGRAFO: Estado previo encontrado pero sin nombre (estado nulo). Finalizando igualmente.");
            cambioEstadoPrevio.setFechaHoraFin(LocalDateTime.now());
        } else {
            System.out.println("  SISMOGRAFO: No se encontró estado previo para finalizar.");
        }

        CambioEstado nuevoCambioDeEstado = this.crearCambioEstado(nuevoEstadoEntidad, motivos,
                esCausadoPorInspeccionDirecta);
        this.historialCambioEstado.add(nuevoCambioDeEstado);
        System.out.println("  SISMOGRAFO: Nuevo cambio de estado agregado: " + nuevoCambioDeEstado.getEstado().getNombre() +
                ", Es actual: " + nuevoCambioDeEstado.esEstadoActual() +
                ", Inhabilitado por Insp.: " + nuevoCambioDeEstado.esInhabilitadoPorInspeccion());
    }

    private CambioEstado crearCambioEstado(Estado estadoEntidad, List<MotivoFueraServicio> motivos,
                                           boolean esInhabilitadoPorInspParam) {
        System.out.println("SISMOGRAFO (" + identificadorSismografo + "): [AUTO-LLAMADA] crearCambioEstado()");
        CambioEstado nuevoCambio = new CambioEstado(LocalDateTime.now(), estadoEntidad);
        nuevoCambio.setEsInhabilitadoPorInspeccion(esInhabilitadoPorInspParam);
        if (motivos != null) {
            for (MotivoFueraServicio motivo : motivos) {
                nuevoCambio.addMotivoFueraServicio(motivo);
            }
        }
        return nuevoCambio;
    }

    @Override
    public String toString() { return "Sismografo ID: " + identificadorSismografo + " (" + descripcion + ")"; }
}