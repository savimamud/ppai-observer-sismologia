package Modelo;

/**
 * Interfaz para el Sujeto del patrón Observer.
 * Permite a los observadores suscribirse y ser notificados.
 */
public interface ISujetoCierreOrden {
    void suscribir(IObservadorCierreOrden observador);
    void quitar(IObservadorCierreOrden observador);
    void notificarCierreOrden();
}
