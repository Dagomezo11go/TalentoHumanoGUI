package org.example.Interfaz.interfaz.boton.botones;

import org.example.Interfaz.BSD.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PayrollForm extends JFrame {
    private JTextField txtSueldoBase, txtAporteIEES, txtTotal;
    private JComboBox<String> cmbBonificaciones, cmbHorasExtras;
    private JSpinner spnHorasExtras;
    private JButton btnCalcular, btnGuardar, btnCancelar, btnSalir, btnVerRol;
    private String empleadoId, empleadoNombre, empleadoApellido, empleadoCedula;
    private boolean isRolGuardado = false;
    private List<Bonificacion> bonificaciones;

    public PayrollForm(JFrame parent, String empleadoId, String nombre, String apellido, String cedula, double sueldoBase) {
        super("Rol de Pagos");
        this.empleadoId = empleadoId;
        this.empleadoNombre = nombre;
        this.empleadoApellido = apellido;
        this.empleadoCedula = cedula;

        setSize(700, 400);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(40, 40, 40));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(mainPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Sueldo Base
        addLabel(mainPanel, "Sueldo Base:", gbc, 0, 0);
        txtSueldoBase = addTextField(mainPanel, gbc, 1, 0);
        txtSueldoBase.setText(String.format("%.2f", sueldoBase));
        txtSueldoBase.setEditable(false);

        // Aporte IEES
        addLabel(mainPanel, "Aporte IEES:", gbc, 0, 1);
        txtAporteIEES = addTextField(mainPanel, gbc, 1, 1);
        txtAporteIEES.setEditable(false);
        calcularAporteIEES(sueldoBase);

        // Bonificaciones
        addLabel(mainPanel, "Bonificaciones:", gbc, 0, 2);
        cmbBonificaciones = new JComboBox<>();
        cargarBonificaciones(); // Cargar bonificaciones dinámicamente desde la base de datos
        gbc.gridx = 1;
        gbc.gridy = 2;
        mainPanel.add(cmbBonificaciones, gbc);

        // Horas Extras
        addLabel(mainPanel, "¿Horas Extras?", gbc, 0, 3);
        cmbHorasExtras = new JComboBox<>(new String[]{"No", "Sí"});
        cmbHorasExtras.addActionListener(e -> toggleHorasExtras());
        gbc.gridx = 1;
        gbc.gridy = 3;
        mainPanel.add(cmbHorasExtras, gbc);

        addLabel(mainPanel, "Cantidad de Horas Extras:", gbc, 0, 4);
        spnHorasExtras = new JSpinner(new SpinnerNumberModel(0, 0, 12, 1)); // Máximo 12 horas semanales
        spnHorasExtras.setEnabled(false);
        gbc.gridx = 1;
        gbc.gridy = 4;
        mainPanel.add(spnHorasExtras, gbc);

        // Total
        addLabel(mainPanel, "Total:", gbc, 0, 5);
        txtTotal = addTextField(mainPanel, gbc, 1, 5);
        txtTotal.setEditable(false);

        // Botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(40, 40, 40));

        btnCalcular = createButton("Calcular", new Color(60, 120, 200));
        btnGuardar = createButton("Guardar", new Color(60, 180, 80));
        btnCancelar = createButton("Cancelar", new Color(200, 60, 60));
        btnSalir = createButton("Salir", new Color(150, 60, 200));
        btnVerRol = createButton("Ver Rol", new Color(100, 200, 100));

        buttonPanel.add(btnCalcular);
        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnVerRol);
        buttonPanel.add(btnCancelar);
        buttonPanel.add(btnSalir);

        add(buttonPanel, BorderLayout.SOUTH);

        // Acciones de los botones
        btnCalcular.addActionListener(e -> calcularTotal());
        btnGuardar.addActionListener(e -> guardarRol());
        btnVerRol.addActionListener(e -> verRol());
        btnCancelar.addActionListener(e -> limpiarCampos());
        btnSalir.addActionListener(e -> dispose());
    }

    private void addLabel(JPanel panel, String text, GridBagConstraints gbc, int x, int y) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        gbc.gridx = x;
        gbc.gridy = y;
        panel.add(label, gbc);
    }

    private JTextField addTextField(JPanel panel, GridBagConstraints gbc, int x, int y) {
        JTextField textField = new JTextField(15);
        gbc.gridx = x;
        gbc.gridy = y;
        panel.add(textField, gbc);
        return textField;
    }

    private JButton createButton(String text, Color background) {
        JButton button = new JButton(text);
        button.setBackground(background);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }

    private void toggleHorasExtras() {
        boolean isEnabled = cmbHorasExtras.getSelectedItem().equals("Sí");
        spnHorasExtras.setEnabled(isEnabled);
    }

    private void calcularAporteIEES(double sueldoBase) {
        double aporteIEES = sueldoBase * 0.09;
        txtAporteIEES.setText(String.format("%.2f", aporteIEES));
    }

    private void cargarBonificaciones() {
        bonificaciones = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT id_bonificacion, bon_descripcion, bon_valor FROM Bonificaciones WHERE estado_bon = 'ACT'")) {

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String id = resultSet.getString("id_bonificacion");
                String descripcion = resultSet.getString("bon_descripcion");
                double valor = resultSet.getDouble("bon_valor");

                bonificaciones.add(new Bonificacion(id, descripcion, valor));
                cmbBonificaciones.addItem(descripcion);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar bonificaciones: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void calcularTotal() {
        try {
            double sueldoBase = Double.parseDouble(txtSueldoBase.getText());
            double aporteIEES = Double.parseDouble(txtAporteIEES.getText());
            double pagoHorasExtras = 0;
            double bonificacion = 0;

            // Obtener el valor de la bonificación seleccionada
            int selectedBonificacionIndex = cmbBonificaciones.getSelectedIndex();
            if (selectedBonificacionIndex >= 0) {
                bonificacion = bonificaciones.get(selectedBonificacionIndex).valor;
            }

            // Calcular horas extras
            if (cmbHorasExtras.getSelectedItem().equals("Sí")) {
                int cantidadHorasExtras = (int) spnHorasExtras.getValue();
                if (cantidadHorasExtras > 0) {
                    pagoHorasExtras = calcularPagoHorasExtras(sueldoBase, cantidadHorasExtras);
                } else {
                    JOptionPane.showMessageDialog(this, "La cantidad de horas extras debe ser mayor a 0.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            double totalIngresos = sueldoBase + pagoHorasExtras + bonificacion;
            double totalDescuentos = aporteIEES;
            double total = totalIngresos - totalDescuentos;

            txtTotal.setText(String.format("%.2f", total));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese valores numéricos válidos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private double calcularPagoHorasExtras(double sueldoBase, int horasExtras) {
        double salarioPorHora = sueldoBase / 240;
        return horasExtras * salarioPorHora * 1.25;
    }

    private void guardarRol() {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO RolesDePago (id_empleado, sueldo_base, aporte_iees, horas_extras, total, estado, id_bonificacion) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?)")) {

            double sueldoBase = Double.parseDouble(txtSueldoBase.getText());
            double aporteIEES = Double.parseDouble(txtAporteIEES.getText());
            double total = Double.parseDouble(txtTotal.getText());
            int horasExtras = cmbHorasExtras.getSelectedItem().equals("Sí") ? (int) spnHorasExtras.getValue() : 0;

            // Obtener el ID de la bonificación seleccionada
            int selectedBonificacionIndex = cmbBonificaciones.getSelectedIndex();
            String idBonificacion = bonificaciones.get(selectedBonificacionIndex).id;

            statement.setString(1, empleadoId);
            statement.setDouble(2, sueldoBase);
            statement.setDouble(3, aporteIEES);
            statement.setInt(4, horasExtras);
            statement.setDouble(5, total);
            statement.setString(6, "ACT");
            statement.setString(7, idBonificacion);

            statement.executeUpdate();

            isRolGuardado = true;
            JOptionPane.showMessageDialog(this, "Rol de pagos guardado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar el rol de pagos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void verRol() {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT id_rolpago FROM RolesDePago WHERE id_empleado = ? ORDER BY fecha_generacion DESC LIMIT 1")) {

            // Buscar el último rol asociado al empleado
            statement.setString(1, empleadoId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int idRol = resultSet.getInt("id_rolpago");

                // Mostrar el diálogo de detalles del rol
                RolDetallesDialog detallesDialog = new RolDetallesDialog(this, idRol);
                detallesDialog.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró un rol para este empleado.", "Información", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al buscar el rol de pagos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarCampos() {
        spnHorasExtras.setValue(0);
        txtTotal.setText("");
    }

    // Clase auxiliar para almacenar las bonificaciones
    private static class Bonificacion {
        String id;
        String descripcion;
        double valor;

        Bonificacion(String id, String descripcion, double valor) {
            this.id = id;
            this.descripcion = descripcion;
            this.valor = valor;
        }
    }
}
