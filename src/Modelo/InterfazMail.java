package Modelo;

import java.time.LocalDateTime;

public class InterfazMail implements IObservadorCierreOrden {

    @Override
    public void notificarCierre(String identificadorSismografo, String estado, LocalDateTime fechaHoraRegistro,
                                String[] motivos, String[] comentarios) {

        System.out.println("INTERFAZ MAIL: notificarCierre() llamado por el Gestor.");

        // Llama al método privado para simular el envío, como en el diagrama
        this.enviarMail(identificadorSismografo, estado, fechaHoraRegistro, motivos, comentarios);
    }

    /**
     * Este método privado simula el envío del correo.
     * Corresponde al auto-mensaje 'enviarMail()' en el diagrama de secuencia.
     */
    private void enviarMail(String identificadorSismografo, String estado, LocalDateTime fechaHoraRegistro,
                            String[] motivos, String[] comentarios) {
        System.out.println("\n--- [InterfazMail] Simulación de Envío de Mail ---");
        System.out.println("Sismógrafo ID: " + identificadorSismografo);
        System.out.println("Nuevo Estado Sismógrafo: " + estado);
        System.out.println("Fecha y Hora del cambio: " + fechaHoraRegistro);

        if (motivos != null && motivos.length > 0) {
            System.out.println("Motivos y Comentarios del Sismógrafo 'Fuera de Servicio':");
            for (int i = 0; i < motivos.length; i++) {
                String comentario = (i < comentarios.length) ? comentarios[i] : "";
                System.out.println("  - " + motivos[i] + ": " + comentario);
            }
        } else {
            System.out.println("  (El sismógrafo no fue puesto Fuera de Servicio o no se especificaron motivos)");
        }
        System.out.println("Destinatarios: (Configurados para responsables de reparaciones y CCRS)");
        System.out.println("--- [InterfazMail] Simulación de Envío Completada ---\n");
    }
}