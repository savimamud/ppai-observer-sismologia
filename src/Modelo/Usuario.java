package Modelo;

public class Usuario {

    private String nombre;
    private String contraseña;
    private Empleado empleadoLogueado;

    public Usuario(String nombre, String contraseña, Empleado empleadoLogueado) {
        this.nombre = nombre;
        this.contraseña = contraseña;
        this.empleadoLogueado = empleadoLogueado;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getContraseña() { return contraseña; }
    public void setContraseña(String contraseña) { this.contraseña = contraseña; }

    // Devuelve el objeto Empleado
    public Empleado getRILLogueado() {
            return empleadoLogueado;
        }
    public void setEmpleadoLogueado(Empleado empleadoLogueado) { this.empleadoLogueado = empleadoLogueado; }
    public Empleado conocerEmpleado() { return empleadoLogueado; }

    // Obtiene el nombre del Empleado (RI) asociado a este Usuario.

    public String getNombreDelEmpleadoRI() {
        if (this.empleadoLogueado != null) {
            return this.empleadoLogueado.getNombre(); // Usuario llama a Empleado.getNombre()
        }
        return null;
    }
}

