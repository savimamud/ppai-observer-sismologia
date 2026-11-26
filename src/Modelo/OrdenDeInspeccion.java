package Modelo;

import java.time.LocalDateTime;
import java.util.List;

public class OrdenDeInspeccion {

    // ... (atributos y constructor) ...
    private LocalDateTime fechaHoraCreacion;
    private LocalDateTime fechaHoraAsignacionRI;
    private LocalDateTime fechaHoraFinalizacion;
    private LocalDateTime fechaHoraRealizacion;
    private String numeroOrden;
    private String observacionCierre;
    private Empleado empleado;
    private Estado estado;
    private EstacionSismologica estacionSismologica;
    private LocalDateTime fechaCierre;

    public OrdenDeInspeccion(String numeroOrden, Empleado empleado, EstacionSismologica estacionSismologica,
                             Estado estadoInicial, LocalDateTime fechaHoraFinalizacionEstimada) {
        this.numeroOrden = numeroOrden;
        this.empleado = empleado;
        this.estacionSismologica = estacionSismologica;
        this.estado = estadoInicial;
        this.fechaHoraFinalizacion = fechaHoraFinalizacionEstimada;
        this.fechaHoraCreacion = LocalDateTime.now();
    }


    // ... (getters y setters sin cambios) ...
    public LocalDateTime getFechaHoraCreacion() { return fechaHoraCreacion; }
    public void setFechaHoraCreacion(LocalDateTime d) { this.fechaHoraCreacion = d; }
    public LocalDateTime getFechaHoraAsignacionRI() { return fechaHoraAsignacionRI; }
    public void setFechaHoraAsignacionRI(LocalDateTime d) { this.fechaHoraAsignacionRI = d; }
    public LocalDateTime getFechaHoraFinalizacion() { return fechaHoraFinalizacion; }
    public void setFechaHoraFinalizacion(LocalDateTime d) { this.fechaHoraFinalizacion = d; }
    public LocalDateTime getFechaHoraRealizacion() { return fechaHoraRealizacion; }
    public void setFechaHoraRealizacion(LocalDateTime d) { this.fechaHoraRealizacion = d; }
    public String getNumeroOrden() { return numeroOrden; }
    public String getObservacionCierre() { return observacionCierre; }
    public void setObservacionCierre(String observacionCierre) { this.observacionCierre = observacionCierre; }
    public Empleado getEmpleado() { return empleado; }
    public void setEmpleado(Empleado empleado) { this.empleado = empleado; }
    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }
    public EstacionSismologica getEstacionSismologica() { return estacionSismologica; }
    public void setEstacionSismologica(EstacionSismologica estacion) { this.estacionSismologica = estacion; }
    public LocalDateTime getFechaCierre() { return fechaCierre; }
    public void setFechaCierre(LocalDateTime fechaCierre) { this.fechaCierre = fechaCierre; }
    public void setFechaHoraCierre(LocalDateTime fechaCierre) { this.fechaCierre = fechaCierre; }
    public LocalDateTime getFechaHoraCierre() { return this.fechaCierre; }
    public Empleado conocerEmpleado() { return empleado; }
    public Estado conocerEstado() { return estado; }
    public EstacionSismologica conocerEstacionSismologica() { return estacionSismologica; }
    public boolean esTuEmpleado(Empleado e) {
        if(this.empleado==null||e==null)return false; return this.empleado.equals(e); }
    public boolean esCompletamenteRealizado() {
        if(this.estado!=null)return this.estado.esCompletamenteRealizado(); return false; }
    public String getNombreDeEstacionSismologica() {
        if(this.estacionSismologica!=null)return this.estacionSismologica.getNombre(); return "N/A"; }
    public String getIdentificadorSismografoDeEstacion() {
        if(this.estacionSismologica!=null && this.estacionSismologica.getSismografo() != null) {
            return this.estacionSismologica.getSismografo().getIdentificadorSismografo();
        }
        return "N/A";
    }

    /**
     * Método llamado por el GestorInspeccion.
     * La Orden delega a la EstacionSismologica la tarea de actualizar su sismógrafo.
     * Esto se alinea con el diagrama de secuencia.
     */
    public void actualizarSismografo(Estado nuevoEstadoSismografo, List<MotivoFueraServicio> motivos,
                                     boolean esInhabilitadoPorInspeccion) {
        System.out.println("ORDEN (" + this.numeroOrden + "): actualizarSismografo() llamado.");
        if (this.estacionSismologica != null) {
            // La Orden delega a la EstacionSismologica la tarea de actualizar su sismógrafo.
            // Corresponde a: selecc:OrdenDeInspeccion -> orden:EstacionSismologica.modificarSituacion() en el diagrama.
            // (Nota: el diagrama muestra que EstacionSismologica luego llama a Sismografo.modificarSituacion)
            System.out.println("  ORDEN (" + this.numeroOrden + "): Delegando a EstacionSismologica.modificarSituacionDelSismografoAsociado()");
            this.estacionSismologica.modificarSituacionDelSismografoAsociado(nuevoEstadoSismografo, motivos, esInhabilitadoPorInspeccion);
        } else {
            System.out.println("  ORDEN (" + this.numeroOrden + "): No tiene estación sismológica asignada.");
        }
    }

    @Override
    public String toString() { return "Orden N°: " + numeroOrden; }
}