package Modelo;

public class MotivoFueraServicio {

    private String comentario;
    private MotivoTipo motivoTipo;

    public MotivoFueraServicio(MotivoTipo motivoTipo, String comentario) {
        this.motivoTipo = motivoTipo;
        this.comentario = comentario;
    }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public MotivoTipo getMotivoTipo() { return motivoTipo; }
    public void setMotivoTipo(MotivoTipo motivoTipo) { this.motivoTipo = motivoTipo; }

    public MotivoTipo conocerMotivoTipo() {
            return motivoTipo;
        }

    @Override
    public String toString() {
        return "Motivo: " + (motivoTipo != null ? motivoTipo.getDescripcion() : "N/A") + ", Comentario: " + comentario;
    }
}

