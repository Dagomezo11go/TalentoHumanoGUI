package org.example.Interfaz.interfaz.boton;

import org.example.Interfaz.BSD.DatabaseConnection;
import org.example.Interfaz.empleado.EmpleadoDAO;
import org.example.Interfaz.interfaz.boton.botones.EditEmployeeForm;
import org.example.Interfaz.interfaz.boton.botones.PayrollForm;
import org.example.Interfaz.interfaz.boton.botones.RegisterEmployeeForm;
import org.example.Interfaz.interfaz.boton.botones.RolDetallesDialog;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class MainApp extends JFrame {
    private JPanel employeeListPanel;
    private JPanel mainPanel;
    private EmpleadoDAO empleadoDAO;

    public MainApp() {
        empleadoDAO = new EmpleadoDAO();

        // Configuración de la ventana principal
        setUndecorated(true); // Eliminar la barra superior nativa
        setSize(1543, 768); // Ajustar al tamaño solicitado
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Barra Superior Personalizada
        JPanel topBar = new JPanel();
        topBar.setBackground(new Color(30, 30, 30));
        topBar.setPreferredSize(new Dimension(getWidth(), 50));
        topBar.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Gestión de Talento Humano", JLabel.LEFT);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        topBar.add(titleLabel, BorderLayout.WEST);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        controlPanel.setOpaque(false);
        JButton minimizeButton = createControlButton("-");
        JButton closeButton = createControlButton("X");
        closeButton.addActionListener(e -> System.exit(0));
        minimizeButton.addActionListener(e -> setState(JFrame.ICONIFIED));
        controlPanel.add(minimizeButton);
        controlPanel.add(closeButton);
        topBar.add(controlPanel, BorderLayout.EAST);

        // Hacer que la ventana sea arrastrable
        addDragFunctionality(this, topBar);

        add(topBar, BorderLayout.NORTH);

        // Barra Lateral
        JPanel sideBar = new JPanel();
        sideBar.setBackground(new Color(30, 30, 30));
        sideBar.setPreferredSize(new Dimension(200, getHeight()));
        sideBar.setLayout(new BoxLayout(sideBar, BoxLayout.Y_AXIS));

        // Orden de los botones en la barra lateral
        JButton registerButton = createSidebarButton("Registrar Empleado");
        registerButton.addActionListener(e -> openRegisterEmployeeForm());
        sideBar.add(Box.createVerticalStrut(20));
        sideBar.add(registerButton);

        JButton viewEmployeesButton = createSidebarButton("Ver Empleados Registrados");
        viewEmployeesButton.addActionListener(e -> loadEmployeesPanel());
        sideBar.add(Box.createVerticalStrut(20));
        sideBar.add(viewEmployeesButton);

        JButton viewRolesButton = createSidebarButton("Ver Roles de Pago Generados");
        viewRolesButton.addActionListener(e -> showRolesPanel());
        sideBar.add(Box.createVerticalStrut(20));
        sideBar.add(viewRolesButton);

        sideBar.add(Box.createVerticalGlue()); // Espaciado dinámico
        add(sideBar, BorderLayout.WEST);

        // Panel Principal
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(40, 40, 40));

        // Inicialmente mostrar la lista de empleados
        loadEmployeesPanel();

        add(mainPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void loadEmployeesPanel() {
        mainPanel.removeAll();

        // Título Central
        JLabel mainTitle = new JLabel("Empleados Registrados", JLabel.CENTER);
        mainTitle.setFont(new Font("Arial", Font.BOLD, 24));
        mainTitle.setForeground(Color.WHITE);
        mainTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        mainPanel.add(mainTitle, BorderLayout.NORTH);

        // Lista de Empleados
        employeeListPanel = new JPanel();
        employeeListPanel.setLayout(new BoxLayout(employeeListPanel, BoxLayout.Y_AXIS));
        employeeListPanel.setBackground(new Color(40, 40, 40));
        JScrollPane scrollPane = new JScrollPane(employeeListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        loadEmployees(); // Cargar empleados

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void showRolesPanel() {
        mainPanel.removeAll(); // Eliminar contenido existente

        JPanel rolesPanel = new JPanel(new BorderLayout());
        rolesPanel.setBackground(new Color(40, 40, 40));

        JLabel rolesTitle = new JLabel("Roles de Pago Registrados", JLabel.CENTER);
        rolesTitle.setFont(new Font("Arial", Font.BOLD, 24));
        rolesTitle.setForeground(Color.WHITE);
        rolesTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        rolesPanel.add(rolesTitle, BorderLayout.NORTH);

        JPanel rolesListPanel = new JPanel();
        rolesListPanel.setLayout(new BoxLayout(rolesListPanel, BoxLayout.Y_AXIS));
        rolesListPanel.setBackground(new Color(40, 40, 40));
        JScrollPane scrollPane = new JScrollPane(rolesListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        rolesPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(rolesPanel, BorderLayout.CENTER);

        loadRoles(rolesListPanel);

        mainPanel.revalidate();
        mainPanel.repaint();
    }


    private JPanel createRolePanel(int idRol, String idEmpleado, double sueldoBase, double descuento, double otrosIngresos, double total) {
        JPanel rolePanel = new JPanel(new BorderLayout());
        rolePanel.setBackground(new Color(50, 50, 50));
        rolePanel.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60), 2));
        rolePanel.setPreferredSize(new Dimension(900, 50));

        // Texto del rol
        JLabel roleLabel = new JLabel("Rol: " + idRol + " | Empleado: " + idEmpleado + " | Total: " + total, JLabel.LEFT);
        roleLabel.setForeground(Color.WHITE);
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        rolePanel.add(roleLabel, BorderLayout.CENTER);

        // Botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setOpaque(false);

        JButton editButton = new JButton("Editar");
        editButton.setForeground(Color.WHITE);
        editButton.setBackground(new Color(60, 120, 200));
        editButton.addActionListener(e -> editRole(String.valueOf(idRol)));

        JButton deleteButton = new JButton("Eliminar");
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setBackground(new Color(200, 60, 60));
        deleteButton.addActionListener(e -> deleteRole(String.valueOf(idRol)));

        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        rolePanel.add(buttonPanel, BorderLayout.EAST);

        return rolePanel;
    }

    private void editRole(String idRol) {
        JOptionPane.showMessageDialog(this, "Función de edición de rol no implementada aún.", "Editar Rol", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteRole(String idRol) {
        int confirm = JOptionPane.showConfirmDialog(this, "¿Estás seguro de eliminar este rol?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement("DELETE FROM RolesDePago WHERE id_rolpago = ?")) {

                statement.setString(1, idRol);
                statement.executeUpdate();
                JOptionPane.showMessageDialog(this, "Rol eliminado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar rol: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, 50));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(50, 50, 50));
        button.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.PLAIN, 16));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(200, 60, 60));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(50, 50, 50));
            }
        });

        return button;
    }

    private JButton createControlButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(40, 30));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(200, 60, 60));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder());
        return button;
    }

    private void openRegisterEmployeeForm() {
        new RegisterEmployeeForm(this).setVisible(true);
        loadEmployeesPanel(); // Recargar empleados después de registrar
    }

    private void loadRoles(JPanel rolesListPanel) {
        rolesListPanel.removeAll();

        String sql = "SELECT id_rolpago, id_empleado, sueldo_base, aporte_iees, total, estado, tipo_bonificacion FROM RolesDePago";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int idRol = resultSet.getInt("id_rolpago");
                String idEmpleado = resultSet.getString("id_empleado");
                double sueldoBase = resultSet.getDouble("sueldo_base");
                double aporteIEES = resultSet.getDouble("aporte_iees");
                double total = resultSet.getDouble("total");
                String estado = resultSet.getString("estado");
                String tipoBonificacion = resultSet.getString("tipo_bonificacion");

                // Crear el panel del rol
                JPanel rolePanel = createRolePanel(idRol, idEmpleado, sueldoBase, aporteIEES, total, estado, tipoBonificacion);
                rolesListPanel.add(rolePanel);
            }

            rolesListPanel.revalidate();
            rolesListPanel.repaint();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar roles: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createRolePanel(int idRol, String idEmpleado, double sueldoBase, double aporteIEES, double total, String estado, String tipoBonificacion) {
        JPanel rolePanel = new JPanel(new BorderLayout());
        rolePanel.setBackground(new Color(50, 50, 50));
        rolePanel.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60), 2));
        rolePanel.setPreferredSize(new Dimension(900, 50));

        JLabel roleLabel = new JLabel("Rol: " + idRol + " | Empleado: " + idEmpleado + " | Total: $" + String.format("%.2f", total) + " | Estado: " + estado + " | Bonificación: " + tipoBonificacion, JLabel.LEFT);
        roleLabel.setForeground(Color.WHITE);
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        rolePanel.add(roleLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setOpaque(false);

        // Botón "Ver Rol"
        JButton viewButton = new JButton("Ver Rol");
        viewButton.setForeground(Color.WHITE);
        viewButton.setBackground(new Color(60, 120, 200));
        viewButton.addActionListener(e -> viewRoleDetails(idRol));

        buttonPanel.add(viewButton);
        rolePanel.add(buttonPanel, BorderLayout.EAST);

        return rolePanel;
    }

    private void viewRoleDetails(int idRol) {
        RolDetallesDialog detallesDialog = new RolDetallesDialog(this, idRol);
        detallesDialog.setVisible(true);
    }

    private void loadEmployees() {
        employeeListPanel.removeAll();

        String sql = "SELECT id_Empleado, emp_Nombre1, emp_Apellido1, emp_Cedula FROM Empleados WHERE estado_emp = 'ACT'";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String idEmpleado = resultSet.getString("id_Empleado");
                String nombre = resultSet.getString("emp_Nombre1");
                String apellido = resultSet.getString("emp_Apellido1");
                String cedula = resultSet.getString("emp_Cedula");

                JPanel employeePanel = createEmployeePanel(idEmpleado, nombre, apellido, cedula);
                employeeListPanel.add(employeePanel);
            }

            employeeListPanel.revalidate();
            employeeListPanel.repaint();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar empleados: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createEmployeePanel(String idEmpleado, String nombre, String apellido, String cedula) {
        JPanel employeePanel = new JPanel(new BorderLayout());
        employeePanel.setBackground(new Color(50, 50, 50));
        employeePanel.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60), 2));
        employeePanel.setPreferredSize(new Dimension(1200, 50));

        JPanel textPanel = new JPanel(new GridLayout(1, 3));
        textPanel.setBackground(new Color(50, 50, 50));

        JLabel nameLabel = new JLabel(nombre, JLabel.LEFT);
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        JLabel lastNameLabel = new JLabel(apellido, JLabel.LEFT);
        lastNameLabel.setForeground(Color.WHITE);
        lastNameLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        JLabel cedulaLabel = new JLabel("Cédula: " + cedula, JLabel.LEFT);
        cedulaLabel.setForeground(Color.WHITE);
        cedulaLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        textPanel.add(nameLabel);
        textPanel.add(lastNameLabel);
        textPanel.add(cedulaLabel);

        employeePanel.add(textPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setOpaque(false);

        JButton editButton = new JButton("Editar");
        editButton.setForeground(Color.WHITE);
        editButton.setBackground(new Color(60, 120, 200));
        editButton.addActionListener(e -> abrirFormularioEdicion(idEmpleado));

        JButton deleteButton = new JButton("Eliminar");
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setBackground(new Color(200, 60, 60));
        deleteButton.addActionListener(e -> eliminarEmpleado(idEmpleado));

        JButton payrollButton = new JButton("Generar Rol");
        payrollButton.setForeground(Color.WHITE);
        payrollButton.setBackground(new Color(100, 200, 100));
        payrollButton.addActionListener(e -> openPayrollForm(idEmpleado));

        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(payrollButton);
        employeePanel.add(buttonPanel, BorderLayout.EAST);

        return employeePanel;
    }

    private void openPayrollForm(String idEmpleado) {
        try {
            // Verificar si ya existe un rol para este empleado
            String checkRoleSql = "SELECT id_rolpago FROM RolesDePago WHERE id_empleado = ?";
            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement checkStatement = connection.prepareStatement(checkRoleSql)) {

                checkStatement.setString(1, idEmpleado);
                ResultSet checkResultSet = checkStatement.executeQuery();

                if (checkResultSet.next()) {
                    int idRol = checkResultSet.getInt("id_rolpago");

                    // Mostrar mensaje con opción para ver el rol existente
                    int option = JOptionPane.showOptionDialog(
                            this,
                            "El rol de pagos para este empleado ya ha sido generado. ¿Desea verlo?",
                            "Rol ya generado",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.INFORMATION_MESSAGE,
                            null,
                            new Object[]{"Ver Rol", "Cancelar"},
                            "Ver Rol"
                    );

                    if (option == JOptionPane.YES_OPTION) {
                        // Mostrar los detalles del rol existente
                        RolDetallesDialog detallesDialog = new RolDetallesDialog(this, idRol);
                        detallesDialog.setVisible(true);
                    }
                    return; // Salir del método si ya existe un rol
                }
            }

            // Si no existe un rol, generar uno nuevo
            String sql = "SELECT emp_Sueldo, emp_Nombre1, emp_Apellido1, emp_Cedula FROM Empleados WHERE id_Empleado = ?";
            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {

                statement.setString(1, idEmpleado);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    // Obtener datos del empleado
                    double sueldoBase = resultSet.getDouble("emp_Sueldo");
                    String nombre = resultSet.getString("emp_Nombre1");
                    String apellido = resultSet.getString("emp_Apellido1");
                    String cedula = resultSet.getString("emp_Cedula");

                    // Abrir el formulario de rol para configuraciones adicionales
                    PayrollForm payrollForm = new PayrollForm(this, idEmpleado, nombre, apellido, cedula, sueldoBase);
                    payrollForm.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Empleado no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al verificar/generar rol: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewExistingRole(int idRol) {
        // Mostrar detalles del rol en un diálogo
        try {
            String sql = "SELECT id_empleado, sueldo_base, aporte_iees, total FROM RolesDePago WHERE id_rolpago = ?";
            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {

                statement.setInt(1, idRol);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    String idEmpleado = resultSet.getString("id_empleado");
                    double sueldoBase = resultSet.getDouble("sueldo_base");
                    double aporteIEES = resultSet.getDouble("aporte_iees");
                    double total = resultSet.getDouble("total");

                    // Crear el contenido del diálogo
                    JPanel detailsPanel = new JPanel(new GridLayout(5, 2, 10, 10));
                    detailsPanel.add(new JLabel("ID Rol:"));
                    detailsPanel.add(new JLabel(String.valueOf(idRol)));
                    detailsPanel.add(new JLabel("Empleado ID:"));
                    detailsPanel.add(new JLabel(idEmpleado));
                    detailsPanel.add(new JLabel("Sueldo Base:"));
                    detailsPanel.add(new JLabel(String.format("%.2f", sueldoBase)));
                    detailsPanel.add(new JLabel("Aporte IEES:"));
                    detailsPanel.add(new JLabel(String.format("%.2f", aporteIEES)));
                    detailsPanel.add(new JLabel("Total:"));
                    detailsPanel.add(new JLabel(String.format("%.2f", total)));

                    // Mostrar el diálogo
                    JOptionPane.showMessageDialog(this, detailsPanel, "Detalles del Rol", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar detalles del rol: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirFormularioEdicion(String idEmpleado) {
        EditEmployeeForm editForm = new EditEmployeeForm(this, idEmpleado);
        editForm.setVisible(true);

        loadEmployeesPanel();
    }

    private void eliminarEmpleado(String idEmpleado) {
        int confirm = JOptionPane.showConfirmDialog(this, "¿Estás seguro de eliminar este empleado?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                empleadoDAO.eliminarEmpleado(idEmpleado);
                JOptionPane.showMessageDialog(this, "Empleado eliminado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                loadEmployeesPanel();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar empleado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addDragFunctionality(JFrame frame, JPanel titleBar) {
        final Point[] initialClick = {null};
        titleBar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                initialClick[0] = e.getPoint();
            }
        });
        titleBar.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseDragged(java.awt.event.MouseEvent e) {
                int thisX = frame.getLocation().x;
                int thisY = frame.getLocation().y;

                int xMoved = e.getX() - initialClick[0].x;
                int yMoved = e.getY() - initialClick[0].y;

                frame.setLocation(thisX + xMoved, thisY + yMoved);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainApp::new);
    }
}

