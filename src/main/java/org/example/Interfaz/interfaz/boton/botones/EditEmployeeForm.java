package org.example.Interfaz.interfaz.boton.botones;

import org.example.Interfaz.BSD.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EditEmployeeForm extends JFrame {
    private String employeeId;
    private JTextField txtPrimerNombre, txtSegundoNombre, txtPrimerApellido, txtSegundoApellido;
    private JTextField txtCedula, txtSueldo, txtCorreoElectronico;
    private JComboBox<String> cmbSexo, cmbEstado, cmbDepartamento;

    public EditEmployeeForm(JFrame parent, String employeeId) {
        super();
        setTitle("Editar Empleado");
        this.employeeId = employeeId;

        setSize(800, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // Panel principal
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(40, 40, 40));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(mainPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);

        // Primera fila
        addLabel(mainPanel, "Primer Nombre:", gbc, 0, 0);
        txtPrimerNombre = addValidatedTextField(mainPanel, gbc, 1, 0);

        addLabel(mainPanel, "Cédula:", gbc, 2, 0);
        txtCedula = addCedulaTextField(mainPanel, gbc, 3, 0);

        // Segunda fila
        addLabel(mainPanel, "Segundo Nombre:", gbc, 0, 1);
        txtSegundoNombre = addValidatedTextField(mainPanel, gbc, 1, 1);

        addLabel(mainPanel, "Sueldo:", gbc, 2, 1);
        txtSueldo = addValidatedSueldoTextField(mainPanel, gbc, 3, 1);

        // Tercera fila
        addLabel(mainPanel, "Primer Apellido:", gbc, 0, 2);
        txtPrimerApellido = addValidatedTextField(mainPanel, gbc, 1, 2);

        addLabel(mainPanel, "Correo Electrónico:", gbc, 2, 2);
        txtCorreoElectronico = addTextField(mainPanel, gbc, 3, 2);

        // Cuarta fila
        addLabel(mainPanel, "Segundo Apellido:", gbc, 0, 3);
        txtSegundoApellido = addValidatedTextField(mainPanel, gbc, 1, 3);

        addLabel(mainPanel, "Sexo:", gbc, 2, 3);
        cmbSexo = new JComboBox<>(new String[]{"M", "F"});
        gbc.gridx = 3;
        gbc.gridy = 3;
        mainPanel.add(cmbSexo, gbc);

        // Quinta fila
        addLabel(mainPanel, "Estado:", gbc, 0, 4);
        cmbEstado = new JComboBox<>(new String[]{"ACT", "INC"});
        gbc.gridx = 1;
        gbc.gridy = 4;
        mainPanel.add(cmbEstado, gbc);

        addLabel(mainPanel, "Departamento:", gbc, 2, 4);
        cmbDepartamento = new JComboBox<>(loadDepartamentos());
        gbc.gridx = 3;
        gbc.gridy = 4;
        mainPanel.add(cmbDepartamento, gbc);

        // Botones
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(40, 40, 40));
        JButton btnGuardar = createButton("Guardar", new Color(60, 120, 200));
        JButton btnCancelar = createButton("Cancelar", new Color(200, 60, 60));

        btnGuardar.addActionListener(e -> actualizarEmpleado());
        btnCancelar.addActionListener(e -> dispose());

        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnCancelar);

        add(buttonPanel, BorderLayout.SOUTH);

        cargarDatosEmpleado(); // Cargar los datos del empleado seleccionado
    }

    private void addLabel(JPanel panel, String text, GridBagConstraints gbc, int x, int y) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        gbc.gridx = x;
        gbc.gridy = y;
        panel.add(label, gbc);
    }

    private JTextField addValidatedTextField(JPanel panel, GridBagConstraints gbc, int x, int y) {
        JTextField textField = new JTextField(15);
        textField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                String text = textField.getText();
                if (!text.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]*")) {
                    textField.setText("");
                    JOptionPane.showMessageDialog(null, "Solo se permiten letras, espacios y tildes.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        gbc.gridx = x;
        gbc.gridy = y;
        panel.add(textField, gbc);
        return textField;
    }

    private JTextField addCedulaTextField(JPanel panel, GridBagConstraints gbc, int x, int y) {
        JTextField textField = new JTextField(15);
        textField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                String text = textField.getText();
                if (!text.matches("\\d{0,10}") || text.length() > 10) {
                    textField.setText("");
                    JOptionPane.showMessageDialog(null, "La cédula debe contener exactamente 10 dígitos.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        gbc.gridx = x;
        gbc.gridy = y;
        panel.add(textField, gbc);
        return textField;
    }

    private JTextField addValidatedSueldoTextField(JPanel panel, GridBagConstraints gbc, int x, int y) {
        JTextField textField = new JTextField(15);
        textField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                String text = textField.getText();
                if (!text.matches("\\d+(\\.\\d{0,2})?") || text.equals("0")) {
                    textField.setText("");
                    JOptionPane.showMessageDialog(null, "El sueldo debe ser un número positivo mayor a cero.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        gbc.gridx = x;
        gbc.gridy = y;
        panel.add(textField, gbc);
        return textField;
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

    private String[] loadDepartamentos() {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT id_departamento FROM Departamentos")) {
            ResultSet resultSet = statement.executeQuery();
            java.util.List<String> departamentos = new java.util.ArrayList<>();
            while (resultSet.next()) {
                departamentos.add(resultSet.getString("id_departamento"));
            }
            return departamentos.toArray(new String[0]);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar departamentos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return new String[0];
        }
    }

    private void cargarDatosEmpleado() {
        String sql = "SELECT emp_Nombre1, emp_Nombre2, emp_Apellido1, emp_Apellido2, emp_Cedula, emp_Sueldo, emp_Mail, emp_Sexo, estado_emp, id_departamento " +
                "FROM Empleados WHERE id_empleado = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, employeeId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                txtPrimerNombre.setText(resultSet.getString("emp_Nombre1"));
                txtSegundoNombre.setText(resultSet.getString("emp_Nombre2"));
                txtPrimerApellido.setText(resultSet.getString("emp_Apellido1"));
                txtSegundoApellido.setText(resultSet.getString("emp_Apellido2"));
                txtCedula.setText(resultSet.getString("emp_Cedula"));
                txtSueldo.setText(String.valueOf(resultSet.getDouble("emp_Sueldo")));
                txtCorreoElectronico.setText(resultSet.getString("emp_Mail"));
                cmbSexo.setSelectedItem(resultSet.getString("emp_Sexo"));
                cmbEstado.setSelectedItem(resultSet.getString("estado_emp"));
                cmbDepartamento.setSelectedItem(resultSet.getString("id_departamento"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar datos del empleado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarEmpleado() {
        if (!validarCamposObligatorios()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "UPDATE Empleados SET emp_Nombre1 = ?, emp_Nombre2 = ?, emp_Apellido1 = ?, emp_Apellido2 = ?, " +
                "emp_Cedula = ?, emp_Sueldo = ?, emp_Mail = ?, emp_Sexo = ?, estado_emp = ?, id_departamento = ?, fecha_modif = CURRENT_DATE WHERE id_empleado = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, txtPrimerNombre.getText());
            statement.setString(2, txtSegundoNombre.getText());
            statement.setString(3, txtPrimerApellido.getText());
            statement.setString(4, txtSegundoApellido.getText());
            statement.setString(5, txtCedula.getText());
            statement.setDouble(6, Double.parseDouble(txtSueldo.getText()));
            statement.setString(7, txtCorreoElectronico.getText());
            statement.setString(8, (String) cmbSexo.getSelectedItem());
            statement.setString(9, (String) cmbEstado.getSelectedItem());
            statement.setString(10, (String) cmbDepartamento.getSelectedItem());
            statement.setString(11, employeeId);

            statement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Empleado actualizado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al actualizar empleado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validarCamposObligatorios() {
        return !txtPrimerNombre.getText().isEmpty() &&
                !txtSegundoNombre.getText().isEmpty() &&
                !txtPrimerApellido.getText().isEmpty() &&
                !txtSegundoApellido.getText().isEmpty() &&
                !txtCedula.getText().isEmpty() &&
                !txtSueldo.getText().isEmpty() &&
                cmbSexo.getSelectedItem() != null &&
                cmbEstado.getSelectedItem() != null &&
                cmbDepartamento.getSelectedItem() != null;
    }
}

