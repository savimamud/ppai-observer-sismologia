package Modelo;

public class Empleado {

    private String nombre;
    private String apellido;
    private String mail;
    private String telefono;

    public Empleado(String nombre, String apellido, String mail, String telefono) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.mail = mail;
        this.telefono = telefono;
    }

    public String getNombre() { // Coincide con `getNombre()` del diagrama de clases
            return nombre;
        }
        public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getMail() { return mail; }
    public void setMail(String mail) { this.mail = mail; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    @Override // Para verificiar que el metodo exista, es una sobreescritura
    public String toString() {
            return nombre + " " + apellido;
        }
}

