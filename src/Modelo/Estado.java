package Modelo;

public class Estado {

    private String nombre;
    private String descripcion;
    private String ambito;

    private boolean esCompletamenteRealizado;
    private boolean esCerrada;
    private boolean esFueraDeServicio;

    public Estado(String nombre, String descripcion, String ambito, boolean esCompletamenteRealizado, boolean esCerrada, boolean esFueraDeServicio) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.ambito = ambito;
        this.esCompletamenteRealizado = esCompletamenteRealizado;
        this.esCerrada = esCerrada;
        this.esFueraDeServicio = esFueraDeServicio;
    }

    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getAmbito() { return ambito; }

    public boolean esCompletamenteRealizado() { return esCompletamenteRealizado; }
    public boolean esCerrada() { return esCerrada; }
    public boolean esFueraDeServicio() { return esFueraDeServicio; }

    @Override
    public String toString() {
            return nombre;
        }

}
