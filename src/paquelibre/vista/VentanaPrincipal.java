package paquelibre.vista;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import paquelibre.excepciones.DimensionInvalidaException;
import paquelibre.excepciones.GuiaInvalidaException;
import paquelibre.excepciones.PesoInvalidoException;
import paquelibre.excepciones.TransicionInvalidaException;
import paquelibre.excepciones.ValorDeclaradoInvalidoException;
import paquelibre.modelo.Envio;
import paquelibre.modelo.EnvioEstandar;
import paquelibre.modelo.EnvioExpress;
import paquelibre.modelo.EnvioInternacional;
import paquelibre.modelo.RegistroHistorial;
import paquelibre.modelo.ResumenEstado;
import paquelibre.patron.EstadoRegistrado;
import paquelibre.persistencia.EnvioDAO;
import paquelibre.persistencia.EnvioDAOImpl;

public class VentanaPrincipal extends JFrame {

    private final EnvioDAO envioDAO;

    private JComboBox<String> cmbTipo;
    private JTextField txtGuia;
    private JTextField txtRemitente;
    private JTextField txtDestinatario;
    private JLabel lblDestino;
    private JTextField txtDestino;
    private JTextField txtPesoReal;
    private JTextField txtLargo;
    private JTextField txtAncho;
    private JTextField txtAlto;
    private JTextField txtValorDeclarado;
    private JTextField txtEstadoActual;

    private JTable tablaEnvios;
    private DefaultTableModel modeloTabla;

    public VentanaPrincipal() {
        this.envioDAO = new EnvioDAOImpl();
        inicializarComponentes();
        cargarEnviosEnTabla();
    }

    private void inicializarComponentes() {
        setTitle("Paqueteria PaqueLibre - Variante 5");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1080, 680);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(crearPanelFormulario(), BorderLayout.CENTER);
        panelSuperior.add(crearPanelAcciones(), BorderLayout.SOUTH);

        add(panelSuperior, BorderLayout.NORTH);
        add(crearPanelTabla(), BorderLayout.CENTER);
    }

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Datos del Envio"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cmbTipo = new JComboBox<>(new String[]{"Estandar", "Express", "Internacional"});
        txtGuia = new JTextField(10);
        txtRemitente = new JTextField(12);
        txtDestinatario = new JTextField(12);
        lblDestino = new JLabel("Ciudad destino:");
        txtDestino = new JTextField(12);
        txtPesoReal = new JTextField(6);
        txtLargo = new JTextField(5);
        txtAncho = new JTextField(5);
        txtAlto = new JTextField(5);
        txtValorDeclarado = new JTextField(8);
        txtEstadoActual = new JTextField(10);
        txtEstadoActual.setEditable(false);

        cmbTipo.addActionListener(e -> {
            String tipo = (String) cmbTipo.getSelectedItem();
            if ("Internacional".equals(tipo)) {
                lblDestino.setText("Pais destino:");
            } else {
                lblDestino.setText("Ciudad destino:");
            }
        });

        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Tipo de servicio:"), gbc);
        gbc.gridx = 1; panel.add(cmbTipo, gbc);
        gbc.gridx = 2; panel.add(new JLabel("Numero de guia:"), gbc);
        gbc.gridx = 3; panel.add(txtGuia, gbc);
        gbc.gridx = 4; panel.add(new JLabel("Estado actual:"), gbc);
        gbc.gridx = 5; panel.add(txtEstadoActual, gbc);

        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Remitente:"), gbc);
        gbc.gridx = 1; panel.add(txtRemitente, gbc);
        gbc.gridx = 2; panel.add(new JLabel("Destinatario:"), gbc);
        gbc.gridx = 3; panel.add(txtDestinatario, gbc);
        gbc.gridx = 4; panel.add(lblDestino, gbc);
        gbc.gridx = 5; panel.add(txtDestino, gbc);

        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Peso real (kg):"), gbc);
        gbc.gridx = 1; panel.add(txtPesoReal, gbc);
        gbc.gridx = 2; panel.add(new JLabel("Largo x Ancho x Alto (cm):"), gbc);
        JPanel panelDim = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        panelDim.add(txtLargo);
        panelDim.add(new JLabel("x"));
        panelDim.add(txtAncho);
        panelDim.add(new JLabel("x"));
        panelDim.add(txtAlto);
        gbc.gridx = 3; panel.add(panelDim, gbc);
        gbc.gridx = 4; panel.add(new JLabel("Valor declarado ($):"), gbc);
        gbc.gridx = 5; panel.add(txtValorDeclarado, gbc);

        return panel;
    }

    private JPanel crearPanelAcciones() {
        JPanel panelContenedor = new JPanel(new BorderLayout());

        JPanel panelCrud = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));
        JButton btnGuardar = new JButton("Guardar Envio");
        JButton btnCorregir = new JButton("Corregir Datos");
        JButton btnEliminar = new JButton("Eliminar Envio");
        JButton btnLimpiar = new JButton("Limpiar Campos");

        btnGuardar.addActionListener(e -> guardarEnvio());
        btnCorregir.addActionListener(e -> corregirEnvio());
        btnEliminar.addActionListener(e -> eliminarEnvio());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        panelCrud.add(btnGuardar);
        panelCrud.add(btnCorregir);
        panelCrud.add(btnEliminar);
        panelCrud.add(btnLimpiar);

        JPanel panelTransiciones = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));
        panelTransiciones.setBorder(BorderFactory.createTitledBorder("Transiciones de Estado (Patron State) y Consultas"));

        JButton btnEnRuta = new JButton("Pasar a EnRuta");
        JButton btnEntregado = new JButton("Pasar a Entregado");
        JButton btnDevuelto = new JButton("Pasar a Devuelto");
        JButton btnResumen = new JButton("Resumen por Estado");
        JButton btnHistorial = new JButton("Historial de la Guia");

        btnEnRuta.addActionListener(e -> cambiarEstadoEnvio("EN_RUTA"));
        btnEntregado.addActionListener(e -> cambiarEstadoEnvio("ENTREGADO"));
        btnDevuelto.addActionListener(e -> cambiarEstadoEnvio("DEVUELTO"));
        btnResumen.addActionListener(e -> mostrarConsultaResumenPorEstado());
        btnHistorial.addActionListener(e -> mostrarConsultaHistorial());

        panelTransiciones.add(btnEnRuta);
        panelTransiciones.add(btnEntregado);
        panelTransiciones.add(btnDevuelto);
        panelTransiciones.add(new JLabel(" | "));
        panelTransiciones.add(btnResumen);
        panelTransiciones.add(btnHistorial);

        panelContenedor.add(panelCrud, BorderLayout.NORTH);
        panelContenedor.add(panelTransiciones, BorderLayout.SOUTH);
        return panelContenedor;
    }

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Envios Registrados"));

        String[] columnas = {
                "Guia", "Tipo", "Remitente", "Destinatario", "Destino",
                "Peso Real (kg)", "Facturable (kg)", "Flete ($)", "Seguro ($)", "Total ($)", "Estado"
        };

        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaEnvios = new JTable(modeloTabla);
        tablaEnvios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaEnvios.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarDatosFilaSeleccionada();
            }
        });

        JScrollPane scroll = new JScrollPane(tablaEnvios);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void cargarEnviosEnTabla() {
        try {
            modeloTabla.setRowCount(0);
            List<Envio> lista = envioDAO.listarTodos();
            for (Envio e : lista) {
                modeloTabla.addRow(new Object[]{
                        e.getGuia(),
                        e.getTipo(),
                        e.getRemitente(),
                        e.getDestinatario(),
                        e.getDestino(),
                        String.format("%.2f", e.getPesoReal()),
                        String.format("%.0f", e.calcularPesoFacturable()),
                        String.format("$%,.0f", e.calcularFlete()),
                        String.format("$%,.0f", e.calcularSeguro()),
                        String.format("$%,.0f", e.calcularTotal()),
                        e.getEstado().getNombre()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar envios: " + ex.getMessage(), "Error BD", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarDatosFilaSeleccionada() {
        int fila = tablaEnvios.getSelectedRow();
        if (fila >= 0) {
            String guia = (String) modeloTabla.getValueAt(fila, 0);
            try {
                Envio e = envioDAO.buscarPorGuia(guia);
                if (e != null) {
                    cmbTipo.setSelectedItem(e.getTipo());
                    txtGuia.setText(e.getGuia());
                    txtGuia.setEditable(false);
                    txtRemitente.setText(e.getRemitente());
                    txtDestinatario.setText(e.getDestinatario());
                    txtDestino.setText(e.getDestino());
                    txtPesoReal.setText(String.valueOf(e.getPesoReal()));
                    txtLargo.setText(String.valueOf(e.getLargo()));
                    txtAncho.setText(String.valueOf(e.getAncho()));
                    txtAlto.setText(String.valueOf(e.getAlto()));
                    txtValorDeclarado.setText(String.valueOf(e.getValorDeclarado()));
                    txtEstadoActual.setText(e.getEstado().getNombre());
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al consultar envio: " + ex.getMessage(), "Error BD", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void guardarEnvio() {
        try {
            String guia = txtGuia.getText().trim();
            String tipo = (String) cmbTipo.getSelectedItem();
            String remitente = txtRemitente.getText().trim();
            String destinatario = txtDestinatario.getText().trim();
            String destino = txtDestino.getText().trim();

            if (guia.isEmpty() || remitente.isEmpty() || destinatario.isEmpty() || destino.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos los campos de texto son obligatorios.", "Validacion", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double pesoReal = Double.parseDouble(txtPesoReal.getText().trim());
            double largo = Double.parseDouble(txtLargo.getText().trim());
            double ancho = Double.parseDouble(txtAncho.getText().trim());
            double alto = Double.parseDouble(txtAlto.getText().trim());
            double valorDeclarado = Double.parseDouble(txtValorDeclarado.getText().trim());

            Envio nuevoEnvio;
            if ("Express".equalsIgnoreCase(tipo)) {
                nuevoEnvio = new EnvioExpress(guia, remitente, destinatario, destino, pesoReal, largo, ancho, alto, valorDeclarado, new EstadoRegistrado());
            } else if ("Internacional".equalsIgnoreCase(tipo)) {
                nuevoEnvio = new EnvioInternacional(guia, remitente, destinatario, destino, pesoReal, largo, ancho, alto, valorDeclarado, new EstadoRegistrado());
            } else {
                nuevoEnvio = new EnvioEstandar(guia, remitente, destinatario, destino, pesoReal, largo, ancho, alto, valorDeclarado, new EstadoRegistrado());
            }

            envioDAO.guardar(nuevoEnvio);
            cargarEnviosEnTabla();
            limpiarFormulario();
            JOptionPane.showMessageDialog(this, "Envio guardado exitosamente.", "Exito", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Los campos numericos (peso, dimensiones, valor) deben tener formato valido.", "Error Numerico", JOptionPane.WARNING_MESSAGE);
        } catch (GuiaInvalidaException | PesoInvalidoException | DimensionInvalidaException | ValorDeclaradoInvalidoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Validacion", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error de base de datos (posible guia duplicada): " + ex.getMessage(), "Error BD", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void corregirEnvio() {
        String guia = txtGuia.getText().trim();
        if (guia.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un envio de la tabla para corregir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Envio envio = envioDAO.buscarPorGuia(guia);
            if (envio == null) {
                JOptionPane.showMessageDialog(this, "No se encontro el envio con guia: " + guia, "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double nuevoPeso = Double.parseDouble(txtPesoReal.getText().trim());
            double nuevoLargo = Double.parseDouble(txtLargo.getText().trim());
            double nuevoAncho = Double.parseDouble(txtAncho.getText().trim());
            double nuevoAlto = Double.parseDouble(txtAlto.getText().trim());
            double nuevoValor = Double.parseDouble(txtValorDeclarado.getText().trim());

            envio.setRemitente(txtRemitente.getText().trim());
            envio.setDestinatario(txtDestinatario.getText().trim());
            envio.setDestino(txtDestino.getText().trim());
            envio.setValorDeclarado(nuevoValor);
            envio.setDimensiones(nuevoLargo, nuevoAncho, nuevoAlto);
            envio.setPesoReal(nuevoPeso);

            envioDAO.actualizar(envio);
            cargarEnviosEnTabla();
            JOptionPane.showMessageDialog(this, "Datos corregidos correctamente.", "Exito", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Los campos numericos deben contener valores validos.", "Error Numerico", JOptionPane.WARNING_MESSAGE);
        } catch (TransicionInvalidaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Operacion no permitida en este estado", JOptionPane.WARNING_MESSAGE);
        } catch (PesoInvalidoException | DimensionInvalidaException | ValorDeclaradoInvalidoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Validacion", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al actualizar en base de datos: " + ex.getMessage(), "Error BD", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cambiarEstadoEnvio(String tipoTransicion) {
        String guia = txtGuia.getText().trim();
        if (guia.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un envio de la tabla para cambiar su estado.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Envio envio = envioDAO.buscarPorGuia(guia);
            if (envio == null) {
                JOptionPane.showMessageDialog(this, "Envio no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String estadoAnterior = envio.getEstado().getNombre();

            if ("EN_RUTA".equals(tipoTransicion)) {
                envio.pasarAEnRuta();
            } else if ("ENTREGADO".equals(tipoTransicion)) {
                envio.pasarAEntregado();
            } else if ("DEVUELTO".equals(tipoTransicion)) {
                envio.pasarADevuelto();
            }

            String estadoNuevo = envio.getEstado().getNombre();
            envioDAO.registrarCambioEstado(envio.getGuia(), estadoAnterior, estadoNuevo);
            txtEstadoActual.setText(estadoNuevo);
            cargarEnviosEnTabla();
            JOptionPane.showMessageDialog(this, "Estado actualizado de " + estadoAnterior + " a " + estadoNuevo, "Exito", JOptionPane.INFORMATION_MESSAGE);

        } catch (TransicionInvalidaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Transicion Invalida", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error de base de datos al cambiar estado: " + ex.getMessage(), "Error BD", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarEnvio() {
        String guia = txtGuia.getText().trim();
        if (guia.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un envio de la tabla para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Esta seguro de que desea eliminar el envio " + guia + " y su historial?",
                "Confirmar Eliminacion", JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                envioDAO.eliminar(guia);
                cargarEnviosEnTabla();
                limpiarFormulario();
                JOptionPane.showMessageDialog(this, "Envio eliminado exitosamente.", "Exito", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar envio: " + ex.getMessage(), "Error BD", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void mostrarConsultaResumenPorEstado() {
        try {
            List<ResumenEstado> lista = envioDAO.consultarResumenPorEstado();

            String[] columnas = {"Estado", "Cantidad de Envios", "Total Facturado ($)"};
            DefaultTableModel modelo = new DefaultTableModel(columnas, 0);

            for (ResumenEstado r : lista) {
                modelo.addRow(new Object[]{
                        r.getEstado(),
                        r.getCantidadEnvios(),
                        String.format("$%,.0f", r.getTotalFacturado())
                });
            }

            JTable tabla = new JTable(modelo);
            JDialog dialogo = new JDialog(this, "Consulta R4.a: Envíos y Total Facturado por Estado", true);
            dialogo.setSize(500, 260);
            dialogo.setLocationRelativeTo(this);
            dialogo.setLayout(new BorderLayout(8, 8));
            dialogo.add(new JScrollPane(tabla), BorderLayout.CENTER);

            JButton btnCerrar = new JButton("Cerrar");
            btnCerrar.addActionListener(e -> dialogo.dispose());
            JPanel pnlBoton = new JPanel();
            pnlBoton.add(btnCerrar);
            dialogo.add(pnlBoton, BorderLayout.SOUTH);

            dialogo.setVisible(true);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al consultar resumen por estado: " + ex.getMessage(), "Error BD", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarConsultaHistorial() {
        String guiaInicial = txtGuia.getText().trim();
        String guia = JOptionPane.showInputDialog(this, "Ingrese el numero de guia a consultar:", guiaInicial);

        if (guia == null || guia.trim().isEmpty()) {
            return;
        }

        guia = guia.trim().toUpperCase();
        try {
            List<RegistroHistorial> historial = envioDAO.consultarHistorialGuia(guia);

            if (historial.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se registraron cambios de estado para la guia: " + guia, "Historial Vacio", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String[] columnas = {"ID", "Guia", "Estado Anterior", "Estado Nuevo", "Fecha y Hora"};
            DefaultTableModel modelo = new DefaultTableModel(columnas, 0);

            for (RegistroHistorial reg : historial) {
                modelo.addRow(new Object[]{
                        reg.getId(),
                        reg.getGuia(),
                        reg.getEstadoAnterior() != null ? reg.getEstadoAnterior() : "(Inicio)",
                        reg.getEstadoNuevo(),
                        reg.getFechaHora()
                });
            }

            JTable tabla = new JTable(modelo);
            JDialog dialogo = new JDialog(this, "Consulta R4.b: Historial de Estados de Guia " + guia, true);
            dialogo.setSize(620, 300);
            dialogo.setLocationRelativeTo(this);
            dialogo.setLayout(new BorderLayout(8, 8));
            dialogo.add(new JScrollPane(tabla), BorderLayout.CENTER);

            JButton btnCerrar = new JButton("Cerrar");
            btnCerrar.addActionListener(e -> dialogo.dispose());
            JPanel pnlBoton = new JPanel();
            pnlBoton.add(btnCerrar);
            dialogo.add(pnlBoton, BorderLayout.SOUTH);

            dialogo.setVisible(true);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al consultar historial: " + ex.getMessage(), "Error BD", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarFormulario() {
        cmbTipo.setSelectedIndex(0);
        txtGuia.setText("");
        txtGuia.setEditable(true);
        txtRemitente.setText("");
        txtDestinatario.setText("");
        txtDestino.setText("");
        txtPesoReal.setText("");
        txtLargo.setText("");
        txtAncho.setText("");
        txtAlto.setText("");
        txtValorDeclarado.setText("");
        txtEstadoActual.setText("");
        tablaEnvios.clearSelection();
    }
}
