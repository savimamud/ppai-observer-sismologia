package Interfaz;

import Modelo.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PantallaInspeccionGUI extends JFrame {

    private final PantallaInspeccion pantallaLogica;

    // Componentes para CardLayout
    private CardLayout cardLayout;
    private JPanel mainCardPanel;
    private JButton btnIniciarCierreOrden;

    // Componentes de la vista de trabajo
    private JTable tblOrdenesInspeccion;
    private JLabel ordenInspeccionLbl;
    private JTextArea observacionTxt;
    private JList<MotivoTipo> lstMotivos;
    private JLabel motivosLbl;
    private JTextArea comentarioTxt;
    private JButton btnConfirmar;
    private JLabel lblEmpleadoNombreGUI;
    private DefaultTableModel tableModelOrdenesGUI;
    private JCheckBox chkSismografoFSGUI;
    private JPanel pnlMotivosGUI;
    private JButton btnAgregarMotivoGUI;
    private JTextArea txtMotivosAgregadosDisplayGUI;
    private DefaultListModel<MotivoTipo> listModelTiposMotivoGUI;
    private MotivoTipo motivoTipoSeleccionadoEnLista;

    // Paneles estructurales de la vista de trabajo
    private JPanel panelSuperior;
    private JPanel panelCentral;
    private JPanel panelInferior;


    public PantallaInspeccionGUI() {
        this.pantallaLogica = new PantallaInspeccion(this);
        initComponents();
    }

    public PantallaInspeccion getPantallaLogica() {
        return pantallaLogica;
    }

    private void initComponents() {
        setTitle("Sistema de Inspecciones"); // Título inicial
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 200); // Tamaño inicial para el botón
        setLocationRelativeTo(null);
        // setIconImage(new ImageIcon(getClass().getResource("/path/to/your/icon.png")).getImage()); // Opcional: poner ícono

        cardLayout = new CardLayout();
        mainCardPanel = new JPanel(cardLayout);

        // Panel 1: Vista inicial con el botón
        JPanel inicioPanel = new JPanel(new GridBagLayout()); // Usar GridBagLayout para centrar
        inicioPanel.setBackground(Color.WHITE); // Fondo blanco como en la imagen de referencia
        btnIniciarCierreOrden = new JButton("Cerrar Orden de Inspección");
        btnIniciarCierreOrden.setFont(new Font("Arial", Font.PLAIN, 14));
        btnIniciarCierreOrden.addActionListener(e -> {
            pantallaLogica.opcionCerrarOrdenInspeccion();
        });
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        inicioPanel.add(btnIniciarCierreOrden, gbc);
        mainCardPanel.add(inicioPanel, "INICIO");

        // Panel 2: Vista de trabajo principal (creada por un método auxiliar)
        JPanel trabajoPanel = crearPanelDeTrabajo();
        mainCardPanel.add(trabajoPanel, "TRABAJO");

        add(mainCardPanel, BorderLayout.CENTER);
        cardLayout.show(mainCardPanel, "INICIO"); // Mostrar el panel inicial
    }

    private JPanel crearPanelDeTrabajo() {
        JPanel panelTrabajoContenedor = new JPanel(new BorderLayout(10, 10));

        panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(5,5,0,5));
        panelSuperior.add(new JLabel("Empleado: "));
        lblEmpleadoNombreGUI = new JLabel("N/A");
        panelSuperior.add(lblEmpleadoNombreGUI);
        panelTrabajoContenedor.add(panelSuperior, BorderLayout.NORTH);

        panelCentral = new JPanel();
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
        panelCentral.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        ordenInspeccionLbl = new JLabel(); // Se seteará texto en prepararComponentesIniciales
        panelCentral.add(ordenInspeccionLbl);

        String[] columnasOrdenes = {"N° Orden", "Nombre de la Estación", "Sismografo ID", "Fecha Finalización"};
        tableModelOrdenesGUI = new DefaultTableModel(columnasOrdenes, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblOrdenesInspeccion = new JTable(tableModelOrdenesGUI);
        tblOrdenesInspeccion.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblOrdenesInspeccion.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                int selectedRow = tblOrdenesInspeccion.getSelectedRow();
                if (selectedRow != -1) {
                    List<OrdenDeInspeccion> ordenesActualesEnLogica = pantallaLogica.getOrdenesMostradas();
                    if (ordenesActualesEnLogica != null && selectedRow < ordenesActualesEnLogica.size()) {
                        OrdenDeInspeccion orden = ordenesActualesEnLogica.get(selectedRow);
                        pantallaLogica.tomarSeleccionOrdenInspeccion(orden);
                    }
                } else {
                    pantallaLogica.tomarSeleccionOrdenInspeccion(null);
                }
            }
        });
        JScrollPane scrollTabla = new JScrollPane(tblOrdenesInspeccion);
        scrollTabla.setPreferredSize(new Dimension(750, 150));
        panelCentral.add(scrollTabla);
        panelCentral.add(Box.createRigidArea(new Dimension(0,10)));

        panelCentral.add(new JLabel("Observación de Cierre (General):"));
        observacionTxt = new JTextArea(3,40);
        observacionTxt.setLineWrap(true);
        observacionTxt.setWrapStyleWord(true);
        observacionTxt.setEnabled(false); // Inicialmente deshabilitado
        observacionTxt.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { pantallaLogica.tomarObservacionDeGUI(observacionTxt.getText()); }
            public void removeUpdate(DocumentEvent e) { pantallaLogica.tomarObservacionDeGUI(observacionTxt.getText()); }
            public void insertUpdate(DocumentEvent e) { pantallaLogica.tomarObservacionDeGUI(observacionTxt.getText()); }
        });
        panelCentral.add(new JScrollPane(observacionTxt));
        panelCentral.add(Box.createRigidArea(new Dimension(0,10)));

        chkSismografoFSGUI = new JCheckBox("¿El Sismógrafo quedará 'Fuera de Servicio'?");
        chkSismografoFSGUI.setEnabled(false); // Inicialmente deshabilitado
        chkSismografoFSGUI.addItemListener(e ->
                pantallaLogica.setSismografoFueraDeServicioSeleccionado(e.getStateChange() == ItemEvent.SELECTED)
        );
        panelCentral.add(chkSismografoFSGUI);
        panelCentral.add(Box.createRigidArea(new Dimension(0,5)));

        pnlMotivosGUI = new JPanel();
        pnlMotivosGUI.setLayout(new BoxLayout(pnlMotivosGUI, BoxLayout.Y_AXIS));
        pnlMotivosGUI.setBorder(BorderFactory.createTitledBorder("Detalle de Motivo 'Fuera de Servicio'"));

        motivosLbl = new JLabel(); // Se seteará texto en prepararComponentesIniciales
        pnlMotivosGUI.add(motivosLbl);
        listModelTiposMotivoGUI = new DefaultListModel<>();
        lstMotivos = new JList<>(listModelTiposMotivoGUI);
        lstMotivos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstMotivos.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                motivoTipoSeleccionadoEnLista = lstMotivos.getSelectedValue();
                pantallaLogica.tomarMotivoTipoSeleccionado(motivoTipoSeleccionadoEnLista);
            }
        });
        JScrollPane scrollMotivos = new JScrollPane(lstMotivos);
        scrollMotivos.setPreferredSize(new Dimension(350,100));
        pnlMotivosGUI.add(scrollMotivos);
        pnlMotivosGUI.add(Box.createRigidArea(new Dimension(0,5)));

        pnlMotivosGUI.add(new JLabel("Comentario para el motivo seleccionado:"));
        comentarioTxt = new JTextArea(2,30);
        comentarioTxt.setLineWrap(true);
        comentarioTxt.setWrapStyleWord(true);
        pnlMotivosGUI.add(new JScrollPane(comentarioTxt));
        pnlMotivosGUI.add(Box.createRigidArea(new Dimension(0,5)));

        btnAgregarMotivoGUI = new JButton("Agregar Motivo Seleccionado con Comentario");
        btnAgregarMotivoGUI.addActionListener(_ -> {
            if (motivoTipoSeleccionadoEnLista != null) {
                pantallaLogica.tomarComentarioMotivoP(motivoTipoSeleccionadoEnLista, comentarioTxt.getText());
            } else {
                mostrarError("Atención", "Por favor, seleccione un tipo de motivo de la lista primero.");
            }
        });
        pnlMotivosGUI.add(btnAgregarMotivoGUI);
        pnlMotivosGUI.add(Box.createRigidArea(new Dimension(0,5)));

        pnlMotivosGUI.add(new JLabel("Motivos Agregados para 'Fuera de Servicio':"));
        txtMotivosAgregadosDisplayGUI = new JTextArea(4, 30);
        txtMotivosAgregadosDisplayGUI.setEditable(false);
        txtMotivosAgregadosDisplayGUI.setLineWrap(true);
        txtMotivosAgregadosDisplayGUI.setWrapStyleWord(true);
        pnlMotivosGUI.add(new JScrollPane(txtMotivosAgregadosDisplayGUI));

        pnlMotivosGUI.setVisible(false); // El panel de motivos es inicialmente invisible
        habilitarComponentesPanelMotivos(false); // Y sus componentes deshabilitados
        panelCentral.add(pnlMotivosGUI);
        panelTrabajoContenedor.add(panelCentral, BorderLayout.CENTER);

        panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelInferior.setBorder(BorderFactory.createEmptyBorder(0,5,5,5));
        btnConfirmar = new JButton("Confirmar Cierre de Orden Seleccionada");
        btnConfirmar.setEnabled(false); // Inicialmente deshabilitado
        btnConfirmar.addActionListener(_ -> {
            pantallaLogica.botonConfirmarPresionado();
        });
        panelInferior.add(btnConfirmar);
        panelTrabajoContenedor.add(panelInferior, BorderLayout.SOUTH);

        return panelTrabajoContenedor;
    }

    public void mostrarVistaDeTrabajo() {
        setTitle("Cierre de Órdenes de Inspección"); // Cambiar título
        setSize(850, 780); // Ajustar tamaño para la vista completa
        setLocationRelativeTo(null); // Re-centrar
        cardLayout.show(mainCardPanel, "TRABAJO");
    }

    public void actualizarTablaOrdenes(List<OrdenDeInspeccion> ordenes) {
        tableModelOrdenesGUI.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        if (ordenes != null) {
            for (OrdenDeInspeccion orden : ordenes) {
                tableModelOrdenesGUI.addRow(new Object[]{
                        orden.getNumeroOrden(),
                        orden.getNombreDeEstacionSismologica(),
                        orden.getIdentificadorSismografoDeEstacion(),
                        orden.getFechaHoraFinalizacion() != null ? orden.getFechaHoraFinalizacion().format(formatter) : "N/A"
                });
            }
        }
    }

    public void actualizarListaTiposMotivo(List<MotivoTipo> motivosTipo) {
        listModelTiposMotivoGUI.clear();
        if (motivosTipo != null) {
            for (MotivoTipo tipo : motivosTipo) {
                listModelTiposMotivoGUI.addElement(tipo);
            }
        }
    }

    public void actualizarEmpleadoLogueado(Empleado empleado) {
        if (empleado != null) {
            lblEmpleadoNombreGUI.setText(empleado.getNombre() + " " + empleado.getApellido());
        } else {
            lblEmpleadoNombreGUI.setText("N/A");
        }
    }

    public void actualizarMotivosAgregadosDisplay(List<MotivoFueraServicio> motivosAgregados) {
        StringBuilder sb = new StringBuilder();
        if (motivosAgregados != null) {
            for (MotivoFueraServicio mfs : motivosAgregados) {
                sb.append("• ").append(mfs.getMotivoTipo().obtenerDescripcion()).append(": ")
                        .append(mfs.getComentario()).append("\n");
            }
        }
        txtMotivosAgregadosDisplayGUI.setText(sb.toString());
    }

    public void limpiarCamposDeMotivoIndividual() {
        lstMotivos.clearSelection();
        comentarioTxt.setText("");
        motivoTipoSeleccionadoEnLista = null;
    }

    public void limpiarParaNuevaOperacionCompleta() {
        tblOrdenesInspeccion.clearSelection();
        observacionTxt.setText("");
        chkSismografoFSGUI.setSelected(false);
        limpiarCamposDeMotivoIndividual();
        txtMotivosAgregadosDisplayGUI.setText("");
    }

    public void prepararComponentesIniciales(String textoLblOrden, String textoLblMotivo) {
        if (this.ordenInspeccionLbl != null) this.ordenInspeccionLbl.setText(textoLblOrden);
        if (this.motivosLbl != null) this.motivosLbl.setText(textoLblMotivo);
    }

    public void habilitarBotonConfirmar(boolean habilitar) {
        btnConfirmar.setEnabled(habilitar);
    }

    public void habilitarCamposDeEntradaOrden(boolean habilitar) {
        observacionTxt.setEnabled(habilitar);
        chkSismografoFSGUI.setEnabled(habilitar);
    }

    public void setPanelMotivosVisible(boolean visible) {
        System.out.println("GUI: setPanelMotivosVisible(" + visible + ")");
        pnlMotivosGUI.setVisible(visible);
    }

    public void habilitarComponentesPanelMotivos(boolean habilitar) {
        System.out.println("GUI: habilitarComponentesPanelMotivos(" + habilitar + ")");
        lstMotivos.setEnabled(habilitar);
        comentarioTxt.setEnabled(habilitar);
        btnAgregarMotivoGUI.setEnabled(habilitar);
    }

    public boolean isChkFueraDeServicioSelected() {
        return chkSismografoFSGUI.isSelected();
    }

    public void setChkFueraDeServicioSelected(boolean selected) {
        chkSismografoFSGUI.setSelected(selected);
    }

    public String getContenidoObservacionActual() {
        return observacionTxt.getText();
    }

    public void actualizarObservacionDisplay(String texto) {
        observacionTxt.setText(texto);
    }

    public void mostrarDialogoCierreExitoso(OrdenDeInspeccion ordenCerrada, Sismografo sismografoAfectado,
                                            Estado nuevoEstadoSismografo, boolean fuePuestoFueraServicio) {
        StringBuilder mensajeExito = new StringBuilder("<html><body>");
        mensajeExito.append("<h3>--- Proceso de Cierre Completado Exitosamente ---</h3>");
        mensajeExito.append("<b>Orden N° ").append(ordenCerrada.getNumeroOrden()).append("</b>");
        if (ordenCerrada.getEstado() != null) {
            mensajeExito.append(" - Nuevo Estado: <b>").append(ordenCerrada.getEstado().getNombre()).append("</b>");
        }
        if (ordenCerrada.getFechaHoraCierre() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            mensajeExito.append("<br><b>Fecha y Hora de Cierre:</b> ").append(ordenCerrada.getFechaHoraCierre()
                    .format(formatter));
        }
        mensajeExito.append("<br>");

        if (sismografoAfectado != null) {
            mensajeExito.append("Sismógrafo <b>").append(sismografoAfectado.getIdentificadorSismografo()).append("</b>");
            if (fuePuestoFueraServicio && nuevoEstadoSismografo != null) {
                mensajeExito.append(" - Nuevo Estado: <b>").append(nuevoEstadoSismografo.getNombre()).append("</b>");
                if (ordenCerrada.getEstacionSismologica() != null && ordenCerrada.getEstacionSismologica().getNombre() != null) {
                    mensajeExito.append(" (Estación: ").append(ordenCerrada.getEstacionSismologica().getNombre()).append(")");
                }
            } else {
                CambioEstado ceActualSismo = sismografoAfectado.conocerCambioEstadoActual();
                if (ceActualSismo != null && ceActualSismo.getEstado() != null) {
                    mensajeExito.append(" - Estado Actual: <b>").append(ceActualSismo.getEstado().getNombre()).append("</b>");
                } else {
                    mensajeExito.append(" - (Estado del sismógrafo no definido)");
                }
            }
        } else {
            mensajeExito.append("Sismógrafo no afectado o no disponible en esta orden.<br>");
        }
        mensajeExito.append("</body></html>");
        JOptionPane.showMessageDialog(this, mensajeExito.toString(), "Cierre Exitoso",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrarError(String titulo, String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, JOptionPane.ERROR_MESSAGE);
    }

    public void mostrarDialogoInformativo(String titulo, String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrarDialogoDeConfirmacionGeneral(OrdenDeInspeccion orden, String observacion,
                                                    List<MotivoFueraServicio> motivos, boolean esFS) {
        System.out.println("GUI: Mostrando diálogo de confirmación general.");
        StringBuilder msg = new StringBuilder("<html><body><h3>--- Resumen de Cierre ---</h3>");
        msg.append("<b>Orden a cerrar:</b> ").append(orden.getNumeroOrden()).append("<br>");
        if (orden.getFechaHoraFinalizacion() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            msg.append("<b>Fecha Finalización Prevista:</b> ").append(orden.getFechaHoraFinalizacion()
                    .format(formatter)).append("<br>");
        } else {
            msg.append("<b>Fecha Finalización Prevista:</b> N/A<br>");
        }
        msg.append("<b>Observación:</b> ").append(observacion.isEmpty() ? "<i>(ninguna)</i>" :
                observacion).append("<br><br>");
        if (esFS && motivos != null && !motivos.isEmpty()) {
            msg.append("<b>Sismógrafo a 'Fuera de Servicio' con motivos:</b><br>");
            for (MotivoFueraServicio mfs : motivos) {
                msg.append(" • ").append(mfs.getMotivoTipo().obtenerDescripcion()).append(": ").append(
                        mfs.getComentario()).append("<br>");
            }
        } else if (esFS){
            msg.append("<b>Sismógrafo marcado como 'Fuera de Servicio' pero SIN motivos especificados.</b><br>");
        }
        else {
            msg.append("El sismógrafo NO cambiará a 'Fuera de Servicio'.<br>");
        }
        msg.append("<br><b>¿Desea confirmar el cierre de la orden con estos datos?</b>");
        msg.append("</body></html>");

        Object[] opciones = {"Confirmar", "Cancelar"};
        int respuesta = JOptionPane.showOptionDialog(
                this,
                msg.toString(),
                "Confirmar Cierre",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]
        );
        pantallaLogica.procesarRespuestaConfirmacionUsuario(respuesta == 0);
    }

    public void indicarAlUsuario(String mensaje) {
        System.out.println("GUI INFO PARA USUARIO: " + mensaje);
    }

    public void enfocarCampoObservacion() {
        if(observacionTxt != null) observacionTxt.requestFocusInWindow();
    }
    public void enfocarCampoComentarioMotivo() {
        if(comentarioTxt != null) comentarioTxt.requestFocusInWindow();
    }

    public void hacerVisible() {
        setVisible(true);
    }

    public void prepararParaComentarioDeMotivo(MotivoTipo motivo) {
        this.motivoTipoSeleccionadoEnLista = motivo;
        comentarioTxt.setText(""); // Limpiar comentario anterior
    }
}