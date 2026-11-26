package Modelo;

import java.time.LocalDateTime; // Fecha y Hora sin zona horaria
import java.util.ArrayList; // Almacena elementos, puede ser duplicados
import java.util.List;  // Define las operaciones basicas de una lista como el get()

public class CambioEstado {

    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private Estado estado;
    private List<MotivoFueraServicio> motivosFueraServicio; // Renombrado en respuestas anteriores

    private boolean esElEstadoActualDelSismografo; // Más descriptivo
    private boolean fueInhabilitadoPorInspeccionDirecta; // Más descriptivo

    public CambioEstado(LocalDateTime fechaHoraInicio, Estado estado) {
        this.fechaHoraInicio = fechaHoraInicio;
        this.estado = estado;
        this.motivosFueraServicio = new ArrayList<>();
        this.esElEstadoActualDelSismografo = true; // Un nuevo cambio de estado ES el actual por defecto
        this.fueInhabilitadoPorInspeccionDirecta = false; // Por defecto no, se setea explícitamente si es el caso
    }

    public LocalDateTime getFechaHoraInicio() { return fechaHoraInicio; }
    public void setFechaHoraInicio(LocalDateTime fechaHoraInicio) { this.fechaHoraInicio = fechaHoraInicio; }

    public LocalDateTime getFechaHoraFin() { return fechaHoraFin; }
    public void setFechaHoraFin(LocalDateTime fechaHoraFin) {
        this.fechaHoraFin = fechaHoraFin;
        this.esElEstadoActualDelSismografo = false; // Cuando se finaliza, ya no es el actual
    }

    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }
    public Estado conocerEstado() { return estado; }

    public List<MotivoFueraServicio> getMotivosFueraServicio() { return motivosFueraServicio; }
    public void setMotivosFueraServicio(List<MotivoFueraServicio> motivos) { this.motivosFueraServicio = motivos; }
    public void addMotivoFueraServicio(MotivoFueraServicio motivo) { this.motivosFueraServicio.add(motivo); }
    public List<MotivoFueraServicio> conocerMotivosFueraServicio() { return motivosFueraServicio; }


    // Método para saber si este CambioEstado representa el estado vigente del sismógrafo
    public boolean esEstadoActual() { // Este es el que el diagrama llama
            return esElEstadoActualDelSismografo;
    }
    // No necesitamos un setter para esElEstadoActualDelSismografo externamente, se maneja con setFechaHoraFin.

    // Método para saber si este CambioEstado fue originado por una inhabilitación directa de inspección
    public boolean esInhabilitadoPorInspeccion() { // Este es el que el diagrama llama
        return fueInhabilitadoPorInspeccionDirecta;
    }
    public void setEsInhabilitadoPorInspeccion(boolean esInhabilitado) { // Setter para Sismografo.crearCambioEstado
        this.fueInhabilitadoPorInspeccionDirecta = esInhabilitado;
    }

}
