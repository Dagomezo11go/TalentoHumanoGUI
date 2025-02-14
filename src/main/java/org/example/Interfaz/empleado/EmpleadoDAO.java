package org.example.Interfaz.empleado;

import org.example.Interfaz.BSD.DatabaseConnection;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmpleadoDAO {
    //Método para agregar un empleado con los nuevos campos
    public void agregarEmpleado(String idEmpleado, String nombre1, String nombre2, String apellido1, String apellido2,
                                String cedula, double sueldo, String email, String estado, String sexo, String idDepartamento) throws SQLException {
        String sql = "INSERT INTO Empleados (id_Empleado, emp_Nombre1, emp_Nombre2, emp_Apellido1, emp_Apellido2, emp_Cedula, " +
                "emp_Sueldo, emp_Mail, estado_emp, emp_sexo, id_Departamento, fecha_alta) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_DATE)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, idEmpleado);
            stmt.setString(2, nombre1);
            stmt.setString(3, nombre2);
            stmt.setString(4, apellido1);
            stmt.setString(5, apellido2);
            stmt.setString(6, cedula);
            stmt.setDouble(7, sueldo);
            stmt.setString(8, email);
            stmt.setString(9, estado);
            stmt.setString(10, sexo);
            stmt.setString(11, idDepartamento);

            stmt.executeUpdate(); // Ejecutar inserción
        }
    }


    // Método para generar dinámicamente el ID del empleado
    public String generarIdEmpleado() throws SQLException {
        int count = contarEmpleados(); // Contar los empleados actuales
        return String.format("E-%04d", count + 1); // Formato E-0001, E-0002, etc.
    }

    // Método privado para contar los empleados en la tabla
    private int contarEmpleados() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Empleados";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1); // Retornar el número total de empleados
            }
        }
        return 0;
    }

    // Método para dar de baja a un empleado
    public void eliminarEmpleado(String idEmpleado) throws SQLException {
        String sqlEmpleado = "UPDATE Empleados SET estado_emp = 'INC', fecha_baja = CURRENT_DATE WHERE id_Empleado = ?";
        String sqlRol = "UPDATE RolesDePago SET estado = 'INC' WHERE id_empleado = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmtEmpleado = conn.prepareStatement(sqlEmpleado);
             PreparedStatement stmtRol = conn.prepareStatement(sqlRol)) {

            // Cambiar estado del empleado a "INC"
            stmtEmpleado.setString(1, idEmpleado);
            stmtEmpleado.executeUpdate();

            // Cambiar estado de los roles del empleado a "INC"
            stmtRol.setString(1, idEmpleado);
            stmtRol.executeUpdate();

            JOptionPane.showMessageDialog(null, "Empleado eliminado correctamente y roles asociados inactivados.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
    }


    public boolean existeCedula(String cedula) {
        String sql = "SELECT COUNT(*) FROM empleados WHERE emp_cedula = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, cedula);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0; // Devuelve true si encuentra al menos una coincidencia
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al verificar la cédula: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false; // Devuelve false si ocurre un error o no se encuentra la cédula
    }
}

