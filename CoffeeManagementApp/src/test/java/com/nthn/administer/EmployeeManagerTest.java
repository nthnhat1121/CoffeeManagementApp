package com.nthn.administer;

import com.nthn.configs.Utils;
import com.nthn.pojo.Account;
import com.nthn.pojo.Active;
import com.nthn.pojo.Employee;
import com.nthn.pojo.Gender;
import com.nthn.pojo.Role;
import com.nthn.services.AccountService;
import com.nthn.services.EmployeeService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * @author HONGNHAT
 */
public class EmployeeManagerTest {

    private EmployeeService service = new EmployeeService();

    @ParameterizedTest(name = "{index} => employee={0}, account={1}")
    @CsvSource({"54cf6d95-fdff-4477-8237-805d07e90217, 0fbbabeb-331a-4a03-8536-c3a9fbcd3381"})
    public void testDeleteEmployeeSuccess(String employee, String account) {
        try {
            service.deleteEmployee(employee, account);
            Employee employee1 = service.getEmployeeByID(employee);

            AccountService as = new AccountService();
            Account a = as.getAccountByID(account);

            Assertions.assertNull(employee1);
            Assertions.assertNull(a);
        } catch (SQLException ex) {
            Logger.getLogger(TableManagerTester.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void testAddEmployeeSuccess() {
        Account account = new Account(Utils.randomID(), "user77", "PASSword55",
                Active.AVAILABLE, Role.USER);
        Employee employee = new Employee(Utils.randomID(), "Nguyễn Văn A", Gender.MALE,
                LocalDate.of(2000, Month.MARCH, 2), "048585453403", "0696845856",
                null, LocalDate.now(), account);

        service.addEmployee(employee, account);
        employee = service.getEmployeeByID(employee.getEmployeeID());

        Assertions.assertNotNull(employee);
    }

    @Test
    public void testAddEmployeeFailed() {

        Account account = new Account(Utils.randomID(), "username", "password",
                Active.AVAILABLE, Role.USER);
        Employee employee = new Employee(Utils.randomID(), "Nguyễn Văn A", Gender.MALE,
                LocalDate.of(2000, Month.MARCH, 2), "049585453433", "0696845856",
                null, LocalDate.now(), account);

        service.addEmployee(employee, account);
        employee = service.getEmployeeByID(employee.getEmployeeID());

        Assertions.assertNull(employee);

    }

    @Test
    public void testUpdateEmployeeSuccess() {
        Account account = new Account(Utils.randomID(), "username", "password",
                Active.AVAILABLE, Role.USER);
        Employee employee = new Employee(Utils.randomID(), "Nguyễn Văn A", Gender.MALE,
                LocalDate.of(2000, Month.MARCH, 2), "049585453433", "0696845856",
                null, LocalDate.now(), account);
        try {
            service.updateEmployee(employee, account);

            Employee employee1 = service.getEmployeeByID(employee.getEmployeeID());

            Assertions.assertNotEquals(employee, employee1);
        } catch (SQLException ex) {
            Logger.getLogger(TableManagerTester.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
