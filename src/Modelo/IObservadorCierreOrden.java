package Modelo;

import java.time.LocalDateTime;

/**
 * Interfaz para los observadores del cierre de orden..
 */
public interface IObservadorCierreOrden {
    void notificarCierre(String identificadorSismografo,
                         String estado,
                         LocalDateTime fechaHoraRegistro,
                         String[] motivos,
                         String[] comentarios);
}