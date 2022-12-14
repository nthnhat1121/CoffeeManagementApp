/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.nthn.coffeemanagementapp;

import com.nthn.check.RegisterChecker;
import com.nthn.configs.Utils;
import com.nthn.pojo.Account;
import com.nthn.pojo.Active;
import com.nthn.pojo.Employee;
import com.nthn.pojo.Gender;
import com.nthn.pojo.Role;
import com.nthn.services.AccountService;
import com.nthn.services.EmployeeService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;

/**
 * FXML Controller class
 *
 * @author HONGNHAT
 */
public class RegisterController implements Initializable {

    @FXML
    private TextField txtFullname;
    @FXML
    private RadioButton rbMale;
    @FXML
    private RadioButton rbFemale;
    @FXML
    private RadioButton rbOther;
    @FXML
    private DatePicker dpBirthDate;
    @FXML
    private TextField txtIdentityCard;
    @FXML
    private TextField txtPhone;
    @FXML
    private TextField txtAddress;
    @FXML
    private DatePicker dpHireDate;

    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private PasswordField txtConfirm;

    @FXML
    private Button btnRegister;
    @FXML
    private Button btnSwitchLogin;

    @FXML
    private Label lbError;

    private final AccountService as = new AccountService();
    private final EmployeeService es = new EmployeeService();

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.btnRegister.getStyleClass().setAll("btn", "btn-success");

        this.dpBirthDate.setConverter(Utils.converter);
        this.dpBirthDate.setValue(LocalDate.now());

        this.dpHireDate.setConverter(Utils.converter);
        this.dpHireDate.setValue(LocalDate.now());

        this.txtFullname.textProperty().addListener((ov, t, t1) -> {
            if (t1.isBlank()) {
                this.lbError.setText("H??? t??n kh??ng ???????c tr???ng.");
            } else if (t1.matches(".*\\d.*")) {
                this.lbError.setText("H??? t??n ch???a s???.");
            } else if (t1.matches(".*\\W.*")) {
                this.lbError.setText("H??? t??n ch???a k?? t??? ?????c bi???t.");
            } else {
                this.lbError.setText("");
            }
        });

        this.txtIdentityCard.textProperty().addListener((ov, t, t1) -> {
            if (t1.isBlank()) {
                this.lbError.setText("CMND/CCCD kh??ng ???????c b??? tr???ng.");
            } else if (t1.matches(".*[a-zA-Z].*")) {
                this.lbError.setText("CMND/CCCD ch???a k?? t??? ch??? c??i.");
            } else if (t1.matches(".*\\W.*")) {
                this.lbError.setText("CMND/CCCD ch???a k?? t??? ?????c bi???t.");
            } else if (!t1.matches("\\d{9}") && !t1.matches("\\d{12}")) {
                this.lbError.setText("CMND/CCCD ph???i ch???a 9 ho???c 12 ch??? s???.");
            } else {
                this.lbError.setText("");
            }
        });

        this.txtPhone.textProperty().addListener((ov, t, t1) -> {
                    if (t1.isBlank()) {
                        this.lbError.setText("S??? ??i???n tho???i kh??ng ???????c b??? tr???ng.");
                    } else if (!t1.matches("\\d{9}")) {
                        this.lbError.setText("S??? ??i???n tho???i ph???i ch???a 9 ch??? s???.");
                    } else if (t1.matches(".*[a-zA-Z].*")) {
                        this.lbError.setText("S??? ??i???n tho???i ch???a k?? t??? ch??? c??i.");
                    } else if (t1.matches(".*\\W.*")) {
                        this.lbError.setText("S??? ??i???n tho???i ch???a k?? t??? ?????c bi???t.");
                    } else {
                        this.lbError.setText("");
                    }
                }
        );

        this.txtUsername.textProperty().addListener((ov, t, t1) -> {
            if (t1.isBlank()) {
                this.lbError.setText("T??n ????ng nh???p kh??ng ???????c b??? tr???ng.");
            } else if (t1.length() > 20) {
                this.lbError.setText("T??n ????ng nh???p v?????t qu?? 20 k?? t???.");
            } else if (t1.contains(" ")) {
                this.lbError.setText("T??n ????ng nh???p ch???a kho???ng tr???ng.");
            } else if (t1.matches(".*\\W.*")) {
                this.lbError.setText("T??n ????ng nh???p ch???a k?? t??? ?????c bi???t.");
            } else {
                this.lbError.setText("");
            }
        });

        this.txtPassword.textProperty().addListener((ov, t, t1) -> {
            if (t1.isBlank()) {
                this.lbError.setText("M???t kh???u kh??ng ???????c b??? tr???ng.");
            } else if (t1.length() < 6) {
                this.lbError.setText("M???t kh???u ??t h??n 6 k?? t???.");
            } else if (t1.contains(" ")) {
                this.lbError.setText("M???t kh???u ch???a kho???ng tr???ng.");
            } else if (t1.matches(".*\\W.*")) {
                this.lbError.setText("M???t kh???u ch???a k?? t??? ?????c bi???t.");
            } else if (!t1.matches(".*[a-z].*")) {
                this.lbError.setText("M???t kh???u ph???i c?? ??t nh???t 1 k?? t??? ch??? th?????ng.");
            } else if (!t1.matches(".*[A-Z].*")) {
                this.lbError.setText("M???t kh???u ph???i c?? ??t nh???t 1 k?? t??? ch??? hoa.");
            } else if (!t1.matches(".*[0-9].*")) {
                this.lbError.setText("M???t kh???u ph???i c?? ??t nh???t 1 ch??? s???.");
            } else {
                this.lbError.setText("");
            }
        });

        this.txtConfirm.textProperty().addListener((ov, t, t1) -> {
            if (t1.equals(this.txtPassword.getText())) {
                this.lbError.setText("M???t kh???u kh??ng kh???p.");
            }
        });

    }

    public void registerHandler(ActionEvent event) throws SQLException {
        if (txtFullname.getText().isBlank()) {
            Utils.showAlert(Alert.AlertType.ERROR, "Register Error!", "Vui l??ng nh???p h??? t??n.");
            return;
        }
        if (txtIdentityCard.getText().isBlank()) {
            Utils.showAlert(Alert.AlertType.ERROR, "Register Error!", "Vui l??ng nh???p CMND/CCCD.");
            return;
        }
        if (txtPhone.getText().isBlank()) {
            Utils.showAlert(Alert.AlertType.ERROR, "Register Error!", "Vui l??ng nh???p s??? ??i???n tho???i.");
            return;
        }
        if (txtUsername.getText().isBlank()) {
            Utils.showAlert(Alert.AlertType.ERROR, "Register Error!", "Vui l??ng nh???p t??n ????ng nh???p.");
            return;
        }
        if (txtPassword.getText().isBlank()) {
            Utils.showAlert(Alert.AlertType.ERROR, "Register Error!", "Vui l??ng nh???p m???t kh???u.");
            return;
        }
        if (txtConfirm.getText().isBlank()) {
            Utils.showAlert(Alert.AlertType.ERROR, "Register Error!", "Vui l??ng nh???p x??c nh???n m???t kh???u.");
            return;
        }

        if (as.getAccountByUsername(this.txtUsername.getText()) != null) {
            this.lbError.setText("T??n ????ng nh???p ???? t???n t???i.");
            return;
        }

        String fullname = this.txtFullname.getText();
        Gender gender = Gender.MALE;
        if (rbFemale.isSelected()) {
            gender = Gender.FEMALE;
        } else if (rbOther.isSelected()) {
            gender = Gender.OTHER;
        }
        LocalDate birthDate = this.getLocalDate(this.dpBirthDate);
        String identityCard = this.txtIdentityCard.getText();
        String phone = this.txtPhone.getText();
        String address = this.txtAddress.getText();
        LocalDate hireDate = this.getLocalDate(this.dpHireDate);

        String username = this.txtUsername.getText();
        String password = this.txtPassword.getText();
        String confirm = this.txtConfirm.getText();

        //L??u th??ng tin
        Account account = new Account(Utils.randomID(), username, password,
                Active.AVAILABLE, Role.USER);
        Employee employee = new Employee(Utils.randomID(), fullname, gender,
                birthDate, identityCard, phone, address, hireDate, account);

        es.addEmployee(employee, account);

        //Ki???m tra k???t qu??? l??u
        try {
            if (new RegisterChecker().isSuccessRegister(employee, account)) {
                App app = new App();
                app.loaderController("Main.fxml", "Coffee Management App - Main");
            }
        } catch (SQLException ex) {
            Logger.getLogger(RegisterController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void loginHandler(ActionEvent event) throws IOException {
        App app = new App();
        app.loaderController("Login.fxml", "Coffee Management App - Login");
    }

    private LocalDate getLocalDate(DatePicker datePicker) {
        TextField textField = datePicker.getEditor();
        String date = textField.getText();
        LocalDate result = Utils.converter.fromString(date);
        return result;
    }

}
