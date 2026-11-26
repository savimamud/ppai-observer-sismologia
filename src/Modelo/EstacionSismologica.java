package Modelo;

import java.time.LocalDate;
import java.time.LocalDateTime; // Para los parámetros del método que llama a modificarSituacion
import java.util.List;         // Para los parámetros del método que llama a modificarSituacion

public class EstacionSismologica {

    // ... (atributos existentes) ...
    private String codigoEstacion;
    private String documentoCertificacionAdq;
    private LocalDate fechaSolicitudCertificacion;
    private double latitud;
    private double longitud;
    private String nombre;
    private LocalDate fechaInstalacion;
    private LocalDate fechaFinFuncionamiento;
    private Sismografo sismografo;

    public EstacionSismologica(String codigoEstacion, String nombre, double latitud, double longitud) {
        this.codigoEstacion = codigoEstacion;
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    // ... (getters y setters existentes) ...
    public String getCodigoEstacion() { return codigoEstacion; }
    public void setCodigoEstacion(String codigoEstacion) { this.codigoEstacion = codigoEstacion; }
    public String getDocumentoCertificacionAdq() { return documentoCertificacionAdq; }
    public void setDocumentoCertificacionAdq(String d) { this.documentoCertificacionAdq = d; }
    public LocalDate getFechaSolicitudCertificacion() { return fechaSolicitudCertificacion; }
    public void setFechaSolicitudCertificacion(LocalDate d) { this.fechaSolicitudCertificacion = d; }
    public double getLatitud() { return latitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }
    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public LocalDate getFechaInstalacion() { return fechaInstalacion; }
    public void setFechaInstalacion(LocalDate d) { this.fechaInstalacion = d; }
    public LocalDate getFechaFinFuncionamiento() { return fechaFinFuncionamiento; }
    public void setFechaFinFuncionamiento(LocalDate d) { this.fechaFinFuncionamiento = d; }
    public Sismografo getSismografo() { return sismografo; }
    public void setSismografo(Sismografo sismografo) { this.sismografo = sismografo; }

    public Sismografo obtenerSismografo() {
        // Esta llamada es para obtener la referencia al sismógrafo,
        // pero la modificación de situación la hará la estación misma.
        System.out.println("ESTACION (" + this.nombre + "): obtenerSismografo() llamado (posiblemente para obtener referencia).");
        return sismografo;
    }

    public Estado consultarEstadoActualSismografo() {
        System.out.println("ESTACION (" + this.nombre + "): consultando estado actual del sismógrafo.");
        if (this.sismografo != null) {
            // La llamada a Sismografo.obtenerEstadoActual() se hace desde Sismografo.modificarSituacion() internamente
            // para conocer el estado previo, así que no es necesario llamarla explícitamente aquí antes de modificar.
            return this.sismografo.obtenerEstadoActual();
        }
        System.out.println("  ESTACION (" + this.nombre + "): No tiene sismógrafo para consultar estado.");
        return null;
    }

    public String getIdentificadorDelSismografoAsociado() {
        if (this.sismografo != null) {
            return this.sismografo.getIdentificadorSismografo();
        }
        return "N/A";
    }

    /**
     * Método para que la Estación actualice la situación de su sismógrafo.
     * ESTA ES LA LLAMADA QUE CUMPLE EL DIAGRAMA: EstacionSismologica -> Sismografo.modificarSituacion()
     */
    public void modificarSituacionDelSismografoAsociado(Estado nuevoEstadoSismografo, List<MotivoFueraServicio> motivos,
                                                        boolean esInhabilitadoPorInspeccion) {
        // El diagrama muestra EstacionSismologica llamando a Sismografo.modificarSituacion()
        // Y Sismografo.modificarSituacion() internamente llama a Sismografo.obtenerEstadoActual() (o conocerCambioEstadoActual)
        // para finalizar el estado previo.
        System.out.println("ESTACION (" + this.nombre + "): modificarSituacionDelSismografoAsociado() llamado.");
        if (this.sismografo != null) {
            System.out.println("  ESTACION (" + this.nombre + "): Llamando a Sismografo.modificarSituacion() para Sismografo ID: "
                    + this.sismografo.getIdentificadorSismografo());
            // AQUÍ SE LLAMA A Sismografo.modificarSituacion() DESDE EstacionSismologica
            this.sismografo.modificarSituacion(nuevoEstadoSismografo, motivos, esInhabilitadoPorInspeccion);
        } else {
            System.out.println("  ESTACION (" + this.nombre + "): No hay sismógrafo asociado para actualizar su situación.");
        }
    }

    @Override
    public String toString() { return nombre + " (Cod: " + codigoEstacion + ")"; }
}