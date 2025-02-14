package org.example.Interfaz.interfaz.boton.botones;

import org.example.Interfaz.BSD.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RolDetallesDialog extends JDialog {

    public RolDetallesDialog(JFrame parent, int idRol) {
        super(parent, "Detalles del Rol de Pago", true);
        setSize(700, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT r.id_rolpago, r.id_empleado, r.sueldo_base, r.aporte_iees, r.total, " +
                             "r.fecha_generacion, r.estado, r.tipo_bonificacion, r.horas_extras, " +
                             "e.emp_Nombre1, e.emp_Apellido1, e.emp_Cedula, COALESCE(b.bon_valor, 0) AS bon_valor " +
                             "FROM RolesDePago r " +
                             "JOIN Empleados e ON r.id_empleado = e.id_Empleado " +
                             "LEFT JOIN Bonificaciones b ON r.id_bonificacion = b.id_bonificacion " +
                             "WHERE r.id_rolpago = ?")) {

            statement.setInt(1, idRol);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Encabezado
                addHeader(contentPanel, gbc, resultSet);

                // Calcular datos
                double sueldoBase = resultSet.getDouble("sueldo_base");
                double horasExtras = resultSet.getDouble("horas_extras");
                double pagoHorasExtras = horasExtras * (sueldoBase / 240) * 1.25; // Valor calculado
                double bonificacion = resultSet.getDouble("bon_valor");
                double totalIngresos = sueldoBase + pagoHorasExtras + bonificacion;
                double totalDescuentos = resultSet.getDouble("aporte_iees");
                double neto = totalIngresos - totalDescuentos;

                // Ingresos
                addSectionHeader(contentPanel, gbc, "Ingresos", 4);
                addDetailRow(contentPanel, gbc, "Sueldo básico:", String.format("$ %.2f", sueldoBase), 5);
                addDetailRow(contentPanel, gbc, "Horas extras:", String.format("$ %.2f", pagoHorasExtras), 6);
                addDetailRow(contentPanel, gbc, "Bonificación:", String.format("$ %.2f", bonificacion), 7);

                // Descuentos
                addSectionHeader(contentPanel, gbc, "Descuentos", 9);
                addDetailRow(contentPanel, gbc, "Aportes IESS:", String.format("$ %.2f", totalDescuentos), 10);

                // Totales
                addSectionHeader(contentPanel, gbc, "Totales", 12);
                addDetailRow(contentPanel, gbc, "Total Ingresos:", String.format("$ %.2f", totalIngresos), 13);
                addDetailRow(contentPanel, gbc, "Total Descuentos:", String.format("$ %.2f", totalDescuentos), 14);
                addDetailRow(contentPanel, gbc, "Neto a Recibir:", String.format("$ %.2f", neto), 15);
            } else {
                JOptionPane.showMessageDialog(this, "No se encontraron detalles para este rol.", "Información", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                return;
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar detalles del rol: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        add(contentPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        buttonPanel.add(btnCerrar);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addHeader(JPanel panel, GridBagConstraints gbc, ResultSet resultSet) throws SQLException {
        JLabel lblTitle = new JLabel("ROL DE PAGOS INDIVIDUAL", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(lblTitle, gbc);

        JLabel lblMes = new JLabel("Mes: Enero", JLabel.LEFT); // Cambiar dinámicamente si es necesario
        gbc.gridy = 1;
        panel.add(lblMes, gbc);

        JLabel lblEmpleado = new JLabel("Empleado: " + resultSet.getString("emp_Nombre1") +
                " " + resultSet.getString("emp_Apellido1"), JLabel.LEFT);
        gbc.gridy = 2;
        panel.add(lblEmpleado, gbc);

        JLabel lblCedula = new JLabel("Cédula: " + resultSet.getString("emp_Cedula"), JLabel.LEFT);
        gbc.gridy = 3;
        panel.add(lblCedula, gbc);
    }

    private void addSectionHeader(JPanel panel, GridBagConstraints gbc, String title, int row) {
        JLabel lblSection = new JLabel(title, JLabel.LEFT);
        lblSection.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(lblSection, gbc);
    }

    private void addDetailRow(JPanel panel, GridBagConstraints gbc, String label, String value, int row) {
        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(lblLabel, gbc);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        panel.add(lblValue, gbc);
    }
}


