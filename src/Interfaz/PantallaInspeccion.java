package Interfaz;

import Controlador.GestorInspeccion;
import Modelo.*;
import java.util.ArrayList;
import java.util.List;

public class PantallaInspeccion {

    private final PantallaInspeccionGUI gui;
    private GestorInspeccion gestorInspeccion;

    private OrdenDeInspeccion ordenInspeccionSeleccionadaEnLogica;
    private String contenidoObservacionTxtLogica;
    private List<MotivoFueraServicio> motivosFueraServicioAcumuladosParaOperacion;
    private List<OrdenDeInspeccion> ordenesMostradasEnLogica;

    private final String textoEtiquetaOrdenInspeccion = "Seleccione de la tabla la orden a cerrar:";
    private final String textoEtiquetaMotivos = "Si el sismógrafo queda 'Fuera de Servicio', " +
            "seleccione tipo(s) de motivo, agregue comentarios y pulse \"Agregar Motivo\":";

    private boolean esperandoConfirmacionUsuario = false;

    public PantallaInspeccion(PantallaInspeccionGUI gui) {
        this.gui = gui;
        this.motivosFueraServicioAcumuladosParaOperacion = new ArrayList<>();
        this.ordenesMostradasEnLogica = new ArrayList<>();
    }
    public void setGestorInspeccion(GestorInspeccion gestor) { this.gestorInspeccion = gestor; }
    public PantallaInspeccionGUI getGui() { return this.gui; }

    public void opcionCerrarOrdenInspeccion() {
        System.out.println("LOGICA Pantalla: opcionCerrarOrdenInspeccion() llamada.");
        if (gui != null) {
            gui.mostrarVistaDeTrabajo();
            gui.prepararComponentesIniciales(this.textoEtiquetaOrdenInspeccion, this.textoEtiquetaMotivos);
        }

        if (gestorInspeccion != null) {
            gestorInspeccion.nuevoCierreOrden();
        } else {
            if (gui != null) gui.mostrarError("Error Configuración", "El gestor de inspección no ha" +
                    " sido asignado.");
        }
    }

    public void habilitarVentana() {
        System.out.println("LOGICA Pantalla: [SELF] habilitarVentana()");
        if (gui != null) {
            gui.hacerVisible();
        }
    }

    public void mostrarEmpleadoLogueado(Empleado e) {
        System.out.println("LOGICA Pantalla: Gestor solicitó mostrarEmpleadoLogueado(): " +
                (e != null ? e.getNombre() + " " + e.getApellido() : "null"));
        if (gui != null) gui.actualizarEmpleadoLogueado(e);
    }

    public void mostrarOrdenesInspeccion(List<OrdenDeInspeccion> ordenes) {
        this.ordenesMostradasEnLogica = (ordenes != null) ? new ArrayList<>(ordenes) : new ArrayList<>();
        System.out.println("LOGICA Pantalla: Gestor solicitó mostrarOrdenesInspeccion() con " +
                this.ordenesMostradasEnLogica.size() + " órdenes.");
        if (gui != null) gui.actualizarTablaOrdenes(this.ordenesMostradasEnLogica);

        if (this.ordenInspeccionSeleccionadaEnLogica != null && !this.ordenesMostradasEnLogica
                .contains(this.ordenInspeccionSeleccionadaEnLogica)) {
            tomarSeleccionOrdenInspeccion(null);
        } else {
            habilitarAccionesDependientesDeSeleccionOrden();
        }
    }

    public void solicitarSeleccionOrdenInspeccion() {
        System.out.println("LOGICA Pantalla: Gestor solicitó seleccionar orden.");
        if (gui != null) {
            gui.indicarAlUsuario("Por favor, seleccione una orden de la tabla.");
            habilitarAccionesDependientesDeSeleccionOrden();
        }
    }

    public void solicitarIngresoObservacion() {
        System.out.println("LOGICA Pantalla: Gestor solicitó ingresar observación.");
        if (gui != null) {
            gui.indicarAlUsuario("Por favor, ingrese la observación de cierre para la orden seleccionada.");
            gui.enfocarCampoObservacion();
        }
    }

    public void habilitarActualizarSituacionSismografos() {
        System.out.println("LOGICA Pantalla: Gestor solicitó habilitar/configurar UI para sismógrafos.");
        if (gui != null) {
            boolean panelVisible = (this.ordenInspeccionSeleccionadaEnLogica != null &&
                    gui.isChkFueraDeServicioSelected());
            gui.setPanelMotivosVisible(panelVisible);
            gui.habilitarComponentesPanelMotivos(panelVisible);
        }
    }

    public void mostrarTiposDeMotivo(List<MotivoTipo> motivosTipo) {
        System.out.println("LOGICA Pantalla: Gestor solicitó mostrarTiposDeMotivo() con " +
                (motivosTipo != null ? motivosTipo.size() : 0) + " tipos.");
        if (gui != null) gui.actualizarListaTiposMotivo(motivosTipo);
    }

    public void solicitarSeleccionMotivos() {
        System.out.println("LOGICA Pantalla: Gestor solicitó seleccionar motivos.");
        if (gui != null) gui.indicarAlUsuario("Si el sismógrafo quedará 'Fuera de Servicio', " +
                "seleccione un tipo de motivo, ingrese un comentario y agréguelo.");
    }

    public void solicitarComentarioMotivo(MotivoTipo motivoParaComentar) {
        System.out.println("LOGICA Pantalla: Gestor solicitó comentario para el motivo: " +
                motivoParaComentar.obtenerDescripcion());
        if (gui != null) {
            gui.indicarAlUsuario("Por favor, ingrese un comentario para el motivo: " +
                    motivoParaComentar.obtenerDescripcion());
            gui.prepararParaComentarioDeMotivo(motivoParaComentar);
            gui.enfocarCampoComentarioMotivo();
        }
    }

    /**
     * Invocado por la GUI cuando el usuario presiona el botón de confirmar.
     * Recopila datos y le pide al gestor que procese el intento de confirmación.
     */
    public void botonConfirmarPresionado() {
        System.out.println("LOGICA Pantalla: botonConfirmarPresionado() llamado por la GUI.");
        if (esperandoConfirmacionUsuario) {
            System.out.println("LOGICA Pantalla: Ya se está esperando una confirmación. Se ignora.");
            return;
        }

        if (gui != null) {
            this.contenidoObservacionTxtLogica = gui.getContenidoObservacionActual();
        }

        // Se podría hacer una validación muy básica aquí en la pantalla (ej. campos no vacíos)
        // pero la validación principal (semántica) la hará el gestor.
        if (this.ordenInspeccionSeleccionadaEnLogica == null) {
            if (gui != null) gui.mostrarError("Error", "Debe seleccionar una orden de inspección.");
            return;
        }
        if (this.contenidoObservacionTxtLogica == null || this.contenidoObservacionTxtLogica.trim().isEmpty()) {
            if (gui != null) gui.mostrarError("Error de Validación", "La observación de cierre no " +
                    "puede estar vacía.");
            return;
        }


        if (gestorInspeccion != null) {
            // La pantalla le pasa al gestor la observación y los motivos recolectados.
            gestorInspeccion.tomarObservacion(this.contenidoObservacionTxtLogica);

            List<MotivoFueraServicio> motivosFinales = new ArrayList<>();
            if (gui != null && gui.isChkFueraDeServicioSelected()) {
                motivosFinales.addAll(this.motivosFueraServicioAcumuladosParaOperacion);
            }
            gestorInspeccion.tomarMotivosFueraServicio(motivosFinales);

            // Ahora le pide al gestor que procese este intento de confirmación
            gestorInspeccion.procesarIntentoDeConfirmacion();
        }
    }

    /**
     * Este método es llamado por el GestorInspeccion (después de sus validaciones)
     * para que la pantalla efectivamente muestre el diálogo de confirmación al usuario.
     * Este es el `solicitarConfirmacion()` del diagrama de secuencia.
     */
    public void solicitarConfirmacion() {
        System.out.println("LOGICA Pantalla: Gestor solicitó solicitarConfirmacion(). Mostrando diálogo a GUI.");
        if (gui != null && this.ordenInspeccionSeleccionadaEnLogica != null) {
            esperandoConfirmacionUsuario = true;
            gui.mostrarDialogoDeConfirmacionGeneral(
                    this.ordenInspeccionSeleccionadaEnLogica,
                    this.contenidoObservacionTxtLogica, // Ya fue tomado en botonConfirmarPresionado
                    this.motivosFueraServicioAcumuladosParaOperacion, // Ya fueron tomados
                    (gui.isChkFueraDeServicioSelected() && !this.motivosFueraServicioAcumuladosParaOperacion.isEmpty())
            );
        } else {
            System.err.println("LOGICA Pantalla: No se puede solicitar confirmación, GUI o orden no disponible.");
            // Podríamos informar un error si esto sucede inesperadamente.
        }
    }


    public void procesarRespuestaConfirmacionUsuario(boolean confirmacionDelUsuario) {
        System.out.println("LOGICA Pantalla: procesarRespuestaConfirmacionUsuario(): " + confirmacionDelUsuario);
        esperandoConfirmacionUsuario = false; // Ya no estamos esperando

        if (gestorInspeccion != null) {
            gestorInspeccion.tomarConfirmacionCerrarOrden(confirmacionDelUsuario);
        }
    }


    public void informarCierreExitoso(OrdenDeInspeccion ordenCerrada, Sismografo sismografoAfectado,
                                      Estado nuevoEstadoSismografo, boolean fuePuestoFueraServicio) {
        System.out.println("LOGICA Pantalla: Gestor informó cierre exitoso.");
        if (gui != null) {
            gui.mostrarDialogoCierreExitoso(ordenCerrada, sismografoAfectado, nuevoEstadoSismografo,
                    fuePuestoFueraServicio);
            limpiarEstadoParaNuevaOperacion();
            gui.limpiarParaNuevaOperacionCompleta();

            if(gestorInspeccion!=null) {
                gestorInspeccion.obtenerOrdenesPorEmpleado();
                gestorInspeccion.ordenarPorFechaFinalizacion();
                this.mostrarOrdenesInspeccion(gestorInspeccion.getOrdenesEmpleado());
            }
        }
    }

    public void informarErrorValidacion(String mensajeError) {
        System.out.println("LOGICA Pantalla: Gestor informó error de validación: " + mensajeError);
        if (gui != null) gui.mostrarError("Error de Validación", mensajeError);
    }

    public void mostrarMensaje(String mensaje) {
        System.out.println("LOGICA Pantalla: Gestor solicitó mostrar mensaje: " + mensaje);
        if (gui != null) gui.mostrarDialogoInformativo("Información", mensaje);
    }


    public void tomarSeleccionOrdenInspeccion(OrdenDeInspeccion orden) {
        System.out.println("LOGICA Pantalla: GUI informó tomarSeleccionOrdenInspeccion(): " + (orden != null ?
                orden.getNumeroOrden() : "null"));
        this.ordenInspeccionSeleccionadaEnLogica = orden;

        this.contenidoObservacionTxtLogica = "";
        this.motivosFueraServicioAcumuladosParaOperacion.clear();

        if (gui != null) {
            gui.actualizarObservacionDisplay(this.contenidoObservacionTxtLogica);
            gui.actualizarMotivosAgregadosDisplay(this.motivosFueraServicioAcumuladosParaOperacion);
            gui.setChkFueraDeServicioSelected(orden != null && gui.isChkFueraDeServicioSelected());
            if (orden == null) {
                gui.setChkFueraDeServicioSelected(false);
            }
        }

        if (gestorInspeccion != null) gestorInspeccion.tomarSeleccionOrdenInspeccion(orden);
        habilitarAccionesDependientesDeSeleccionOrden();
    }

    public void tomarObservacionDeGUI(String observacion) {
        // Este método es llamado por el DocumentListener de la GUI.
        // El gestor tomará la observación justo antes de procesar el intento de confirmación.
        // System.out.println("LOGICA Pantalla: GUI informó tomarObservacionDeGUI(): " + observacion);
        this.contenidoObservacionTxtLogica = observacion;
        // No se lo pasamos al gestor aquí, sino en botonConfirmarPresionado()
    }

    public void tomarMotivoTipoSeleccionado(MotivoTipo motivoTipo) {
        System.out.println("LOGICA Pantalla: GUI informó tomarMotivoTipoSeleccionado(): " + (motivoTipo != null ?
                motivoTipo.obtenerDescripcion() : "null"));
        if (motivoTipo != null && gestorInspeccion != null) {
            gestorInspeccion.tomarMotivoTipo(motivoTipo);
        }
    }

    public void tomarComentarioMotivoP(MotivoTipo motivoAsociado, String comentario) {
        System.out.println("LOGICA Pantalla: GUI informó tomarComentarioParaMotivoAgregado() para '" +
                (motivoAsociado != null ? motivoAsociado.obtenerDescripcion() : "null") + "': " + comentario);
        if (motivoAsociado == null) {
            if (gui != null) gui.mostrarError("Error", "Debe seleccionar un tipo de motivo de la lista.");
            return;
        }
        if (comentario == null || comentario.trim().isEmpty()) {
            if (gui != null) gui.mostrarError("Error", "El comentario para el motivo '" +
                    motivoAsociado.obtenerDescripcion() + "' no puede estar vacío.");
            return;
        }

        MotivoFueraServicio mfsExistente = null;
        for(MotivoFueraServicio mfs : motivosFueraServicioAcumuladosParaOperacion) {
            if(mfs.getMotivoTipo().equals(motivoAsociado)) {
                mfsExistente = mfs;
                break;
            }
        }
        if (mfsExistente != null) {
            mfsExistente.setComentario(comentario);
            System.out.println("LOGICA Pantalla: Comentario actualizado para motivo existente.");
        } else {
            motivosFueraServicioAcumuladosParaOperacion.add(new MotivoFueraServicio(motivoAsociado, comentario));
            System.out.println("LOGICA Pantalla: Nuevo motivo con comentario agregado.");
        }

        if (gui != null) {
            gui.actualizarMotivosAgregadosDisplay(motivosFueraServicioAcumuladosParaOperacion);
            gui.limpiarCamposDeMotivoIndividual();
        }

        if (gestorInspeccion != null) {
            gestorInspeccion.tomarComentarioMotivo(motivoAsociado, comentario);
        }
    }

    public void setSismografoFueraDeServicioSeleccionado(boolean seleccionado) {
        System.out.println("LOGICA Pantalla: GUI informó Sismógrafo FS Checkbox: " + seleccionado);
        if (!seleccionado) {
            this.motivosFueraServicioAcumuladosParaOperacion.clear();
            if (gui != null) {
                gui.actualizarMotivosAgregadosDisplay(this.motivosFueraServicioAcumuladosParaOperacion);
                gui.limpiarCamposDeMotivoIndividual();
            }
        }
        habilitarActualizarSituacionSismografos();
    }

    private void limpiarEstadoParaNuevaOperacion() {
        this.ordenInspeccionSeleccionadaEnLogica = null;
        this.contenidoObservacionTxtLogica = "";
        this.motivosFueraServicioAcumuladosParaOperacion.clear();
        this.esperandoConfirmacionUsuario = false;
    }

    private void habilitarAccionesDependientesDeSeleccionOrden(){
        boolean habilitar = (this.ordenInspeccionSeleccionadaEnLogica != null);
        if(gui != null) {
            gui.habilitarBotonConfirmar(habilitar);
            gui.habilitarCamposDeEntradaOrden(habilitar);
            if (!habilitar) {
                gui.setChkFueraDeServicioSelected(false);
            }
        }
        habilitarActualizarSituacionSismografos();
    }


    public List<OrdenDeInspeccion> getOrdenesMostradas() {
        return this.ordenesMostradasEnLogica;
    }
    public OrdenDeInspeccion getOrdenInspeccionSeleccionadaEnLogica() { return ordenInspeccionSeleccionadaEnLogica; }
    public String getContenidoObservacionTxtLogica() { return contenidoObservacionTxtLogica; }
    public List<MotivoFueraServicio> getMotivosFueraServicioAcumuladosParaOperacion() {
        return motivosFueraServicioAcumuladosParaOperacion; }
}