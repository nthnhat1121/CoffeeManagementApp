/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nthn.coffeemanagementapp;

import com.nthn.configs.Utils;
import com.nthn.pojo.*;
import com.nthn.services.*;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;

public class MainController implements Initializable {
    @FXML
    private TextField tfProduct;
    @FXML
    private ComboBox cbProduct;
    @FXML
    private TableView<Product> tbvProduct;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtPrice;
    @FXML
    private ChoiceBox<Category> chbCategory;
    @FXML
    private Button btnAdd;
    @FXML
    private MenuButton btnEdit;
    @FXML
    private Button btnDelete;

    @FXML
    private TextField tfTable;
    @FXML
    private ComboBox<Integer> cbCapacity;
    @FXML
    private ComboBox cbStatus;
    @FXML
    private TableView<Table> tbvTable;
    @FXML
    private TextField txtTableName;
    @FXML
    private TextField txtCapacity;
    @FXML
    private Button btnAddTable;
    @FXML
    private Button btnEditTable;
    @FXML
    private Button btnDeleteTable;

    @FXML
    private TableView<Employee> tbvEmployee;
    @FXML
    private TextField txtUsername;
    @FXML
    private ComboBox<String> cbOptional;

    @FXML
    private TableView<Order> tbvOrder;
    @FXML
    private ComboBox<String> cbPayment;
    @FXML
    private DatePicker dpOrderDate;
    @FXML
    private Button btnPayment;
    @FXML
    private TextField txtTable;
    @FXML
    private Button btnFilterOrderDate;
    @FXML
    private Button btnOrder;

    private final ProductController pc = new ProductController();
    private final TableController tc = new TableController();
    private final EmployeeController ec = new EmployeeController();
    private final StatisticController sc = new StatisticController();

    private final ProductService ps = new ProductService();
    private final TableService ts = new TableService();
    private final EmployeeService es = new EmployeeService();
    private final AccountService as = new AccountService();
    private final OrderService os = new OrderService();
    private final OrderDetailService ods = new OrderDetailService();

    private Account account;
    @FXML
    private Text txtHome;

    public void setAccount(Account account) {
        this.account=account;
    }

    public Account getAccount() {
        return account;
    }


    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (account != null) {

            this.txtHome.setText(this.account.getUsername());
        }

        this.dpOrderDate.setConverter(Utils.converter);
        this.dpOrderDate.setValue(LocalDate.now());

        this.pc.loadTableViewProduct(tbvProduct);
        this.tc.loadTableViewTable(tbvTable);
        this.ec.loadTableViewEmployee(tbvEmployee);
        this.sc.loadTableViewOrder(tbvOrder);

        try {
            this.pc.loadComboBoxDataProduct(cbProduct);
            this.pc.loadChoiceBoxCategory(chbCategory);

            this.tc.loadComboBoxDataCapacity(cbCapacity);
            this.tc.loadComboBoxDataStatus(cbStatus);

            this.pc.loadTableDataProduct(null, tbvProduct, cbProduct);

            //Load danh s??ch b??n c?? tr???ng th??i c??n tr???ng
            this.tc.loadTableDateTable1(tbvTable, cbCapacity, cbStatus);

            this.ec.loadComboBoxOptional(cbOptional);
            this.ec.loadTableDataEmployee(tbvEmployee);

            this.sc.loadTableDataOrder(tbvOrder);
        } catch (SQLException ex) {
            Logger.getLogger(ProductController.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.tfProduct.textProperty().addListener((evt) -> {
            try {
                this.pc.loadTableDataProduct(this.tfProduct.getText(), tbvProduct, cbProduct);
            } catch (SQLException ex) {
                Logger.getLogger(ProductController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        this.tfTable.textProperty().addListener((evt) -> {
            try {
                this.tc.loadTableDataTable(this.tfTable.getText(), tbvTable);
            } catch (SQLException ex) {
                Logger.getLogger(ProductController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        this.cbCapacity.getSelectionModel().selectedItemProperty().addListener((ov, t, t1) -> {
            this.cbCapacity.setValue(t1);
            try {
                this.tc.loadTableDateTable1(tbvTable, cbCapacity, cbStatus);
            } catch (SQLException ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        this.cbStatus.getSelectionModel().selectedItemProperty().addListener((evt) -> {
            try {
                this.tc.loadTableDateTable1(tbvTable, cbCapacity, cbStatus);
            } catch (SQLException ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        this.cbOptional.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            this.cbOptional.valueProperty().set(newValue);

        });
        this.txtUsername.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isBlank())
                try {
                    this.ec.loadTableDataEmployee(tbvEmployee, cbOptional, newValue);
                } catch (SQLException ex) {
                    Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
                }
            else this.ec.loadTableDataEmployee(tbvEmployee);
        });

        this.txtTable.textProperty().addListener((observable, oldValue, newValue) -> {
            this.sc.loadTableDataOrder(tbvOrder, newValue);
        });

    }

    @FXML
    public void ResetComboBox(ActionEvent event) throws SQLException {
        this.cbCapacity.valueProperty().set(null);
        this.cbStatus.valueProperty().set(null);
        this.tc.loadComboBoxDataCapacity(cbCapacity);
        this.tc.loadComboBoxDataStatus(cbStatus);
        this.tc.loadTableDataTable(null, tbvTable);
    }

    //Tab product
    public void btnAddProductHandler(ActionEvent event) throws SQLException {
        if (txtName.getText().isBlank() && txtPrice.getText().isBlank()) {
            Utils.showAlert(Alert.AlertType.ERROR, "Add Product Error!",
                    "Vui l??ng nh???p t??n s???n ph???m v?? gi?? ti???n!");
            return;
        }
        if (txtName.getText().isBlank()) {
            Utils.showAlert(Alert.AlertType.ERROR, "Add Product Error!",
                    "Vui l??ng nh???p t??n s???n ph???m!");
            return;
        }
        if (txtName.getText().matches(".*[!@#$%^&*()].*")) {
            Utils.showAlert(Alert.AlertType.ERROR, "Add Product Error!",
                    "T??n s???n ph???m c?? ch???a k?? t??? ?????c bi???t. Nh???p l???i!");
            return;
        }
        if (txtName.getText().matches(".*[0-9].*")) {
            Utils.showAlert(Alert.AlertType.ERROR, "Add Product Error!",
                    "T??n s???n ph???m c?? ch???a ch??? s???. Nh???p l???i!");
            return;
        }
        if (txtPrice.getText().isBlank()) {
            Utils.showAlert(Alert.AlertType.ERROR, "Add Product Error!",
                    "Vui l??ng nh???p gi?? ti???n!");
        } else if (txtPrice.getText().matches(".*\\W.*")) {
            Utils.showAlert(Alert.AlertType.ERROR, "Add Product Error!",
                    "Gi?? ti???n ch???a k?? t??? ?????c bi???t!");
        } else if (txtPrice.getText().matches(".*[a-zA-Z].*")) {
            Utils.showAlert(Alert.AlertType.ERROR, "Add Product Error!",
                    "Gi?? ti???n ch???a k?? t??? ch??? c??i!");
        }

        String productName = txtName.getText();
        int unitPrice = Integer.parseInt(txtPrice.getText());
        Category category = chbCategory.getValue();
        Product product = new Product(Utils.randomID(), productName, unitPrice, category);

        ProductService ps = new ProductService();

        //N???u t??n s???n ph???m ch??a c?? th?? th??m s???n ph???m, t???i l???i danh s??ch s???n ph???m, ng?????c l???i b??o l???i
        if (ps.getProductsByName(product.getProductName()) == null) {
            ps.addProduct(product);
            Utils.showAlert(Alert.AlertType.INFORMATION, "Add Product Success", "Th??m s???n ph???m th??nh c??ng.");
            this.pc.loadTableDataProduct("", tbvProduct, cbProduct);

            this.txtName.clear();
            this.txtPrice.clear();
        } else {
            Utils.showAlert(Alert.AlertType.ERROR, "Add Product Failed", "S???n ph???m ???? t???n t???i. Nh???p l???i!");
            this.txtName.clear();
        }
    }

    public void btnEditNameHandler(ActionEvent event) throws SQLException {
        Product product = this.tbvProduct.getSelectionModel().getSelectedItem();
        if (product == null) {
            Utils.showAlert(Alert.AlertType.ERROR, "Edit Name Error!", "Vui l??ng ch???n s???n ph???m!");
            return;
        }
        Product product1 = ps.getProduct(product.getProductID());
        product.setCategory(product1.getCategory());

        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Nh???p t??n s???n ph???m: ");
        Optional<String> optional = dialog.showAndWait();
        if (optional.isPresent()) {
            product.setProductName(optional.get());
            ps.updateProduct(product);
            Utils.showAlert(Alert.AlertType.INFORMATION, "Edit Product Success", "Ch???nh s???a th??nh c??ng.");
            this.pc.loadTableDataProduct("", tbvProduct, cbProduct);
        } else {
            Utils.showAlert(Alert.AlertType.INFORMATION, "Edit Product Failed", "Ch???nh s???a th??nh c??ng.");
        }
    }

    public void btnEditPriceHandler(ActionEvent event) throws SQLException {
        Product product = this.tbvProduct.getSelectionModel().getSelectedItem();
        if (product == null) {
            Utils.showAlert(Alert.AlertType.ERROR, "Edit Name Error!", "Vui l??ng ch???n s???n ph???m!");
            return;
        }
        Product product1 = ps.getProduct(product.getProductID());
        product.setCategory(product1.getCategory());


        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Nh???p gi?? ti???n (VN??): ");
        Optional<String> optional = dialog.showAndWait();
        if (optional.isPresent()) {
            if (optional.get().isBlank()) {
                Utils.showAlert(Alert.AlertType.ERROR, "Edit Product Error", "Ch??a nh???p gi??. Th??? l???i!");
                return;
            }
            product.setUnitPrice(Integer.parseInt(optional.get()));
            ps.updateProduct(product);

            Utils.showAlert(Alert.AlertType.INFORMATION, "Edit Product Success", "Gi?? m???i ???? ???????c c???p nh???t.");
            this.pc.loadTableDataProduct("", tbvProduct, cbProduct);
        }
    }

    public void btnDeleteProductHandler(ActionEvent event) throws SQLException {
        Product product = this.tbvProduct.getSelectionModel().getSelectedItem();
        if (product == null) {
            Utils.showAlert(Alert.AlertType.ERROR, "Delete Product Error!", "Vui l??ng ch???n s???n ph???m c???n x??a!");
            return;
        }

        ps.deleteProduct(product.getProductID());
        if (ps.getProduct(product.getProductID()) == null) {
            Utils.showAlert(Alert.AlertType.INFORMATION, "Delete Product Success", "S???n ph???m " + product.getProductName() + " ???? x??a.");
            this.pc.loadTableDataProduct("", tbvProduct, cbProduct);
        } else {
            Utils.showAlert(Alert.AlertType.ERROR, "Delete Product Failed", "S???n ph???m " + product.getProductName() + " ch??a x??a.");
        }
    }

    //Tab table
    public void btnAddTableHandler(ActionEvent event) throws SQLException {
        if (txtTableName.getText().isBlank() && txtCapacity.getText().isBlank()) {
            Utils.showAlert(Alert.AlertType.ERROR, "Add Table Error!",
                    "Vui l??ng nh???p t??n b??n v?? s???c ch???a!");
            return;
        }
        if (txtTableName.getText().isBlank()) {
            Utils.showAlert(Alert.AlertType.ERROR, "Add Table Error!",
                    "Vui l??ng nh???p t??n b??n!");
            return;
        }
        if (txtCapacity.getText().isBlank()) {
            Utils.showAlert(Alert.AlertType.ERROR, "Add Table Error!",
                    "Vui l??ng nh???p s???c ch???a!");
        } else if (txtCapacity.getText().matches(".*\\W.*")) {
            Utils.showAlert(Alert.AlertType.ERROR, "Add Table Error!!",
                    "S???c ch???a ch???a k?? t??? ?????c bi???t!");
        } else if (txtCapacity.getText().matches(".*[a-zA-Z].*")) {
            Utils.showAlert(Alert.AlertType.ERROR, "Add Table Error!",
                    "S???c ch???a ch???a k?? t??? ch??? c??i!");
        }

        String tableName = txtTableName.getText();
        int capacity = Integer.parseInt(txtCapacity.getText());
        Table table = new Table(Utils.randomID(), tableName, capacity, Status.EMPTY);

        ts.addTable(table);

        if (ts.getTable(table.getTableID()) != null) {
            Utils.showAlert(Alert.AlertType.INFORMATION, "Add Table Success", "Th??m b??n th??nh c??ng.");

            try {
                this.tc.loadTableDataTable("", tbvTable);
            } catch (SQLException ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }

            this.txtTableName.clear();
            this.txtCapacity.clear();
        } else {
            Utils.showAlert(Alert.AlertType.ERROR, "Add Table Failed", "Th??m b??n th???t b???i.");
        }
    }

    public void btnEditNameTable(ActionEvent event) throws SQLException {
        Table table = this.tbvTable.getSelectionModel().getSelectedItem();
        if (table == null) {
            Utils.showAlert(Alert.AlertType.ERROR, "Edit Table Error!", "Vui l??ng ch???n b??n c???n ch???nh s???a!");
            return;
        }

        Table table1 = ts.getTable(table.getTableID());

        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Nh???p t??n b??n: ");
        Optional<String> optional = dialog.showAndWait();
        String newName;
        if (optional.isPresent()) {
            newName = optional.get();

            if (newName.isBlank()) {
                Utils.showAlert(Alert.AlertType.ERROR, "Edit Table Error", "Ch??a nh???p t??n b??n. Th??? l???i!");
                return;
            }
            if (ts.getTablesByName(newName).isEmpty()) {
                table.setTableName(newName);
                ts.updateTable(table);
                Utils.showAlert(Alert.AlertType.INFORMATION, "Edit Table Success", "Ch???nh s???a th??nh c??ng.");
                this.tc.loadTableDataTable("", tbvTable);
            } else {
                Utils.showAlert(Alert.AlertType.ERROR, "Edit Table Failed", "T??n b??n b??? tr??ng. Th??? l???i!");
            }
        }
    }

    public void btnEditCapacityTable(ActionEvent event) throws SQLException {
        Table table = this.tbvTable.getSelectionModel().getSelectedItem();
        if (table == null) {
            Utils.showAlert(Alert.AlertType.ERROR, "Edit Table Error!", "Vui l??ng ch???n b??n c???n ch???nh s???a!");
            return;
        }

        Table table1 = ts.getTable(table.getTableID());

        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Nh???p s???c ch???a: ");
        Optional<String> optional = dialog.showAndWait();

        if (optional.isPresent()) {
            if (optional.get().isBlank()) {
                Utils.showAlert(Alert.AlertType.ERROR, "Edit Table Error", "Ch??a nh???p s???a ch???a. Th??? l???i!");
                return;
            }

            int newCapacity = Integer.parseInt(optional.get());
            if (newCapacity == 0) {
                Utils.showAlert(Alert.AlertType.ERROR, "Edit Table Error", "S???c ch???a ph???i l???n h??n 0. Th??? l???i!");
                return;
            }

            table.setCapacity(newCapacity);
            ts.updateTable(table);
            Utils.showAlert(Alert.AlertType.INFORMATION, "Edit Table Success", "Ch???nh s???a th??nh c??ng.");
            this.tc.loadTableDataTable("", tbvTable);
        }
    }

    public void btnDeleteTableHandler(ActionEvent event) {
        Table table = this.tbvTable.getSelectionModel().getSelectedItem();
        if (table == null || table.getStatus() == Status.FULL) {
            Utils.showAlert(Alert.AlertType.ERROR, "Delete Table Error!", "Vui l??ng ch???n b??n c???n x??a!");
            return;
        }
        try {
            ts.deleteTable(table.getTableID());
            if (ts.getTable(table.getTableID()) == null) {
                Utils.showAlert(Alert.AlertType.INFORMATION, "Delete Table Success", table.getTableName() + " ???? x??a.");
                this.tc.loadTableDataTable("", tbvTable);
            } else {
                Utils.showAlert(Alert.AlertType.ERROR, "Delete Table Error", table.getTableName() + " kh??ng th??? x??a.");
            }
        } catch (SQLException ex) {
            Utils.showAlert(Alert.AlertType.ERROR, "Delete Table Error", table.getTableName() + " kh??ng th??? x??a. B??n ???? ?????t");
        }


    }

    //Tab employee
    public void btnAddEmployeeHandler(ActionEvent event) {
        App app = new App();
        app.loaderController("Register.fxml", "Coffee Management App - Register");
        this.ec.loadTableDataEmployee(tbvEmployee);
    }

    //S???a ?????a ch???
    public void btnEditAddressHandler(ActionEvent event) throws SQLException {
        Employee employee = this.tbvEmployee.getSelectionModel().getSelectedItem();
        if (employee == null) {
            Utils.showAlert(Alert.AlertType.ERROR, "Edit Address Error!", "Vui l??ng ch???n nh??n vi??n c???n ch???nh s???a!");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Nh???p ?????a ch???: ");
        Optional<String> optional = dialog.showAndWait();
        if (optional.isPresent()) {
            if (optional.get().isBlank()) {
                Utils.showAlert(Alert.AlertType.ERROR, "Edit Address Failed!", "Ch??a nh???p ?????a ch???. Th??? l???i!");
                return;
            }

            employee.setAddress(optional.get());
            es.updateEmployee(employee, employee.getAccount());
            this.ec.loadTableDataEmployee(tbvEmployee);
            Utils.showAlert(Alert.AlertType.INFORMATION, "Edit Address Success!", "Ch???nh s???a th??nh c??ng.");
        }
    }

    //S???a t??n nh??n vi??n
    public void btnEditFullNameHandler(ActionEvent event) throws SQLException {
        Employee employee = this.tbvEmployee.getSelectionModel().getSelectedItem();
        if (employee == null) {
            Utils.showAlert(Alert.AlertType.ERROR, "Edit Full Name Error!", "Vui l??ng ch???n nh??n vi??n c???n ch???nh s???a!");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Nh???p h??? t??n: ");
        Optional<String> optional = dialog.showAndWait();
        if (optional.isPresent()) {
            if (optional.get().isBlank()) {
                Utils.showAlert(Alert.AlertType.ERROR, "Edit Full Name Error!", "Ch??a nh???p h??? t??n. Th??? l???i!");
                return;
            }

            String fullname = optional.get();
            if (fullname.matches(".*[0-9].*")) {
                Utils.showAlert(Alert.AlertType.ERROR, "Edit Full Name Error!", "H??? t??n c?? ch???a ch??? s???. Th??? l???i!");
                return;
            } else {
                employee.setFullName(fullname);
                es.updateEmployee(employee, employee.getAccount());
                this.ec.loadTableDataEmployee(tbvEmployee);
                Utils.showAlert(Alert.AlertType.INFORMATION, "Edit Full Name Success!", "Ch???nh s???a th??nh c??ng.");
            }
        }
    }

    //X??a nh??n vi??n
    public void btnDeleteEmployeeHandler(ActionEvent event) {
        Employee employee = this.tbvEmployee.getSelectionModel().getSelectedItem();
        if (employee == null) {
            Utils.showAlert(Alert.AlertType.ERROR, "Delete Employee Error!", "Vui l??ng ch???n nh??n vi??n c???n x??a!");
            return;
        }
        es.deleteEmployee(employee.getEmployeeID(), employee.getAccount().getAccountID());
        if (es.getEmployeeByID(employee.getEmployeeID()) == null) {
            Utils.showAlert(Alert.AlertType.INFORMATION, "Delete Employee Success", "Nh??n vi??n " + employee.getFullName()
                    + " ???? x??a.");
            this.ec.loadTableDataEmployee(tbvEmployee);
        } else {
            Utils.showAlert(Alert.AlertType.INFORMATION, "Delete Employee Failed", "Nh??n vi??n " + employee.getFullName()
                    + " ch??a x??a.");
        }
    }


    //Tab order
    //Thanh to??n h??a ????n
    public void btnPaymentHandler(ActionEvent event) throws SQLException {
        Order order = tbvOrder.getSelectionModel().getSelectedItem();
        OrderController orderController = new OrderController();
        orderController.showOrderDetail(order.getOrderID());
        this.os.updateOrder(order);
        Table table = ts.getTable(order.getTableID());
        table.setStatus(Status.EMPTY);

        ts.updateTable(table);

        this.sc.loadTableDataOrder(tbvOrder);
    }

    //L???y date t??? datepicker
    private LocalDate getLocalDate(DatePicker datePicker) {
        TextField textField = datePicker.getEditor();
        String date = textField.getText();
        LocalDate result = Utils.converter.fromString(date);
        return result;
    }

    public void btnFilterOrderDateHandler(ActionEvent event) {
        LocalDate localDate = this.getLocalDate(this.dpOrderDate);
        this.sc.loadTableDataOrder(tbvOrder, localDate);
    }

    public void btnOrderHandler(ActionEvent event) {
        App app = new App();
        app.loaderController("Order.fxml", "Order");
    }

}
