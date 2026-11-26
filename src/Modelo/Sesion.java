package Modelo;

import java.time.LocalDateTime;

public class Sesion {

    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private Usuario usuario;

    public Sesion(Usuario usuario) {
        this.usuario = usuario;
        this.fechaHoraInicio = LocalDateTime.now();
    }

    public LocalDateTime getFechaHoraInicio() { return fechaHoraInicio; }
    public void setFechaHoraInicio(LocalDateTime fh) { this.fechaHoraInicio = fh; }
    public LocalDateTime getFechaHoraFin() { return fechaHoraFin; }
    public void setFechaHoraFin(LocalDateTime fh) { this.fechaHoraFin = fh; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario u) { this.usuario = u; }

    public Usuario obtenerUsuario() {
            return usuario;
        }
    public Usuario conocerUsuario() { return usuario; }

    /**
     * Obtiene el Empleado (RI) asociado al Usuario de esta Sesión.
     * El Gestor lo usa para obtener el objeto Empleado completo.
     */
    public Empleado obtenerEmpleadoRI() {
        if (this.usuario != null) {
            return this.usuario.getRILLogueado(); // Sesion -> Usuario.getRILLogueado() -> Empleado
        }
        return null;
    }

    /**
     * Obtiene el NOMBRE del Empleado (RI) asociado al Usuario de esta Sesión.
     */
    public String obtenerNombreDelEmpleadoRI_SegunDS() {
        if (this.usuario != null) {
            // Sesion llama a un método en Usuario que devuelve el nombre del empleado de ese usuario.
            return this.usuario.getNombreDelEmpleadoRI();
        }
        return null;
    }
}

