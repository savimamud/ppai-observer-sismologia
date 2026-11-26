package Modelo;

public class MotivoTipo {


    private String descripcion;
    private int id;

    public MotivoTipo(int id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
    }

    public int getId() {
        return id;
    }

    public String getDescripcion() { // `getDescripcion` en diagrama
        return descripcion;
    }

    public String obtenerDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}

