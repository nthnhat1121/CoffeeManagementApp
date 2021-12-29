/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nthn.services;

import com.nthn.configs.JdbcUtils;
import com.nthn.pojo.Employee;
import com.nthn.pojo.Gender;
import com.nthn.pojo.Role;
import com.nthn.pojo.State;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author HONGNHAT
 */
public class EmployeeService {

    public List<Employee> getEmployees() throws SQLException {
        List<Employee> employees = new ArrayList<>();
        try (Connection c = JdbcUtils.getConnection()) {
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery("SELECT * FROM employees");
            while (rs.next()) {
                int id = rs.getInt("EmployeeID");
                String lastName = rs.getString("LastName");
                String firstName = rs.getString("FirstName");
                String address = rs.getString("Address");
                String phone = rs.getString("Phone");
                Date hireDate = rs.getDate("HireDate");
                int accountID = rs.getInt("AccountID");
                Employee e = new Employee(accountID, hireDate, id, accountID, lastName, firstName, id, address, phone);
                employees.add(e);
            }
        }
        return employees;
    }

    public void addEmployee(Employee e) {

        try {
            Connection connection = JdbcUtils.getConnection();

            connection.setAutoCommit(false);

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO employees(EmployeeID, LastName, FirstName, "
                    + "HireDate, StateID, AccountID, genderID, Address, Phone) "
                    + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)");
            preparedStatement.setInt(1, e.getEmployeeID());
            preparedStatement.setString(2, e.getLastName());
            preparedStatement.setString(3, e.getFirstName());
            preparedStatement.setDate(4, e.getHireDate());
            preparedStatement.setInt(5, e.getStateID());
            preparedStatement.setInt(6, e.getAccountID());
            preparedStatement.setInt(7, e.getGenderID());
            preparedStatement.setString(8, e.getAddress());
            preparedStatement.setString(9, e.getPhone());

            preparedStatement.executeUpdate();

            connection.commit();
        } catch (SQLException ex) {
            Logger.getLogger(RoleService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Employee getEmployee(int id) throws SQLException {

        try (Connection c = JdbcUtils.getConnection()) {
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery("SELECT * FROM employees WHERE EmployeeID=" + id);
            while (rs.next()) {
                String lastName = rs.getString("LastName");
                String firstName = rs.getString("FirstName");
                String address = rs.getString("Address");
                String phone = rs.getString("Phone");
                Date hireDate = rs.getDate("HireDate");
                int accountID = rs.getInt("AccountID");
                Employee e = new Employee(accountID, hireDate, id, accountID, lastName, firstName, id, address, phone);
                return e;
            }
        }
        return null;
    }
}