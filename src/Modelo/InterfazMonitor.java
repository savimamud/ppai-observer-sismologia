package Modelo;

import java.time.LocalDateTime;

public class InterfazMonitor implements IObservadorCierreOrden {

    @Override
    public void notificarCierre(String identificadorSismografo, String estado, LocalDateTime fechaHoraRegistro,
                                String[] motivos, String[] comentarios) {

        System.out.println("INTERFAZ MONITOR: notificarCierre() llamado por el Gestor.");

        // Llamada al método privado que realiza la acción, como en el diagrama
        this.actualizarMonitor(identificadorSismografo, estado, fechaHoraRegistro, motivos, comentarios);
    }

    /**
     * Este método privado simula la actualización del monitor.
     * Corresponde al auto-mensaje 'actualizarMonitor()' en el diagrama de secuencia.
     */
    private void actualizarMonitor(String identificadorSismografo, String estado, LocalDateTime fechaHoraRegistro,
                                   String[] motivos, String[] comentarios) {
        System.out.println("\n--- [InterfazMonitor] Notificación a Monitor CCRS ---");
        System.out.println("Sismógrafo: " + identificadorSismografo);
        System.out.println("Nuevo Estado Sismógrafo: " + estado);
        System.out.println("Fecha y Hora del cambio: " + fechaHoraRegistro);
        System.out.println("Motivos y Comentarios del Sismógrafo:");

        if (motivos != null && motivos.length > 0) {
            for (int i = 0; i < motivos.length; i++) {
                String comentario = (i < comentarios.length) ? comentarios[i] : "";
                System.out.println("  - " + motivos[i] + ": " + comentario);
            }
        } else {
            System.out.println("  (El sismógrafo no fue puesto Fuera de Servicio o no se especificaron motivos)");
        }
        System.out.println("--- Fin Notificación a Monitor CCRS ---\n");
    }
}