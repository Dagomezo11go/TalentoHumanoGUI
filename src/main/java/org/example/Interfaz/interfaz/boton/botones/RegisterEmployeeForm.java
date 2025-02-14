package org.example.Interfaz.interfaz.boton.botones;

import org.example.Interfaz.BSD.DatabaseConnection;
import org.example.Interfaz.empleado.EmpleadoDAO;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class RegisterEmployeeForm extends JDialog {
    private JTextField txtPrimerNombre, txtSegundoNombre, txtPrimerApellido, txtSegundoApellido;
    private JTextField txtCedula, txtSueldo, txtCorreoElectronico;
    private JComboBox<String> cmbSexo, cmbEstado, cmbDepartamento;
    private String idEmpleado;

    public RegisterEmployeeForm(JFrame parent) {
        this(parent, null);
    }

    public RegisterEmployeeForm(JFrame parent, String idEmpleado) {
        super(parent, true);
        this.idEmpleado = idEmpleado;

        setTitle(idEmpleado == null ? "Registrar Empleado" : "Editar Empleado");
        setSize(800, 400);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

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
        txtCorreoElectronico = addEmailValidatedTextField(mainPanel, gbc, 3, 2);

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
        JButton btnCancelar = createButton("Cancelar", new Color(200, 150, 60));
        JButton btnSalir = createButton("Salir", new Color(200, 60, 60));

        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnCancelar);
        buttonPanel.add(btnSalir);

        add(buttonPanel, BorderLayout.SOUTH);

        btnGuardar.addActionListener(e -> guardarEmpleado());
        btnCancelar.addActionListener(e -> limpiarCampos());
        btnSalir.addActionListener(e -> dispose());

        if (idEmpleado != null) {
            cargarDatosEmpleado(idEmpleado);
        }
    }

    private JTextField addValidatedSueldoTextField(JPanel panel, GridBagConstraints gbc, int x, int y) {
        JTextField textField = new JTextField(15);
        textField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                String text = textField.getText();
                if (!text.matches("\\d*(\\.\\d{0,2})?") || text.startsWith(".") || Double.parseDouble(text.isEmpty() ? "0" : text) <= 0) {
                    JOptionPane.showMessageDialog(null, "El sueldo debe ser un número positivo mayor que cero con hasta dos decimales.", "Error", JOptionPane.ERROR_MESSAGE);
                    textField.setText("");
                }
            }
        });
        gbc.gridx = x;
        gbc.gridy = y;
        panel.add(textField, gbc);
        return textField;
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

    private JTextField addEmailValidatedTextField(JPanel panel, GridBagConstraints gbc, int x, int y) {
        JTextField textField = new JTextField(15);
        textField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                String text = textField.getText();
                if (text.chars().filter(ch -> ch == '@').count() > 1) {
                    textField.setText("");
                    JOptionPane.showMessageDialog(null, "El correo no puede contener más de un '@'.", "Error", JOptionPane.ERROR_MESSAGE);
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

    private JButton createButton(String text, Color background) {
        JButton button = new JButton(text);
        button.setBackground(background);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }

    private String[] loadDepartamentos() {
        String sql = "SELECT id_Departamento FROM Departamentos";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            java.util.List<String> departamentos = new java.util.ArrayList<>();
            while (resultSet.next()) {
                departamentos.add(resultSet.getString("id_Departamento"));
            }
            return departamentos.toArray(new String[0]);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los departamentos: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return new String[0];
        }
    }

    private void limpiarCampos() {
        txtPrimerNombre.setText("");
        txtSegundoNombre.setText("");
        txtPrimerApellido.setText("");
        txtSegundoApellido.setText("");
        txtCedula.setText("");
        txtSueldo.setText("");
        txtCorreoElectronico.setText("");
        cmbEstado.setSelectedIndex(0);
        cmbSexo.setSelectedIndex(0);
        cmbDepartamento.setSelectedIndex(0);
    }

    private void cargarDatosEmpleado(String idEmpleado) {
        String sql = "SELECT emp_Nombre1, emp_Nombre2, emp_Apellido1, emp_Apellido2, emp_Cedula, " +
                "emp_Sueldo, emp_Mail, estado_emp, emp_sexo, id_Departamento " +
                "FROM Empleados WHERE id_Empleado = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, idEmpleado);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                txtPrimerNombre.setText(resultSet.getString("emp_Nombre1"));
                txtSegundoNombre.setText(resultSet.getString("emp_Nombre2"));
                txtPrimerApellido.setText(resultSet.getString("emp_Apellido1"));
                txtSegundoApellido.setText(resultSet.getString("emp_Apellido2"));
                txtCedula.setText(resultSet.getString("emp_Cedula"));
                txtSueldo.setText(String.valueOf(resultSet.getDouble("emp_Sueldo")));
                txtCorreoElectronico.setText(resultSet.getString("emp_Mail"));
                cmbEstado.setSelectedItem(resultSet.getString("estado_emp"));
                cmbSexo.setSelectedItem(resultSet.getString("emp_sexo"));
                cmbDepartamento.setSelectedItem(resultSet.getString("id_Departamento"));
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró un empleado con el ID proporcionado.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los datos del empleado: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardarEmpleado() {
        try {
            EmpleadoDAO empleadoDAO = new EmpleadoDAO();
            String cedula = txtCedula.getText();

            if (empleadoDAO.existeCedula(cedula)) {
                JOptionPane.showMessageDialog(this, "Error: La cédula ya existe en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String idEmpleado = empleadoDAO.generarIdEmpleado();
            String nombre1 = txtPrimerNombre.getText();
            String nombre2 = txtSegundoNombre.getText();
            String apellido1 = txtPrimerApellido.getText();
            String apellido2 = txtSegundoApellido.getText();
            double sueldo = Double.parseDouble(txtSueldo.getText());
            String email = txtCorreoElectronico.getText();
            String estado = (String) cmbEstado.getSelectedItem();
            String sexo = (String) cmbSexo.getSelectedItem();
            String idDepartamento = (String) cmbDepartamento.getSelectedItem();

            empleadoDAO.agregarEmpleado(idEmpleado, nombre1, nombre2, apellido1, apellido2, cedula, sueldo, email, estado, sexo, idDepartamento);

            JOptionPane.showMessageDialog(this, "Empleado registrado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarCampos();
            dispose();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al registrar empleado: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese datos válidos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
