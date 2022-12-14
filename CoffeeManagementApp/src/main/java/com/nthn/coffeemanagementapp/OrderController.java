/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.nthn.coffeemanagementapp;

import com.nthn.configs.Utils;
import com.nthn.pojo.Category;
import com.nthn.pojo.Employee;
import com.nthn.pojo.Order;
import com.nthn.pojo.OrderDetail;
import com.nthn.pojo.Product;
import com.nthn.pojo.Status;
import com.nthn.pojo.Table;
import com.nthn.services.OrderDetailService;
import com.nthn.services.OrderService;
import com.nthn.services.ProductService;
import com.nthn.services.TableService;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author HONGNHAT
 */
public class OrderController implements Initializable {

    @FXML
    private Label lblOrderDate;
    @FXML
    private Label lblTable;
    @FXML
    private Label lblEmployee;

    @FXML
    private ComboBox<Category> cbCategory;
    @FXML
    private TextField txtCapacity;
    @FXML
    private TextField txtName;
    @FXML
    private TableView<OrderDetail> tbvOrder;
    @FXML
    private TableView<Table> tbvTable;
    @FXML
    private TableView<Product> tbvProduct;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnOK;
    @FXML
    private Label lblTotal;
    @FXML
    private TableColumn<OrderDetail, String> productNameCol;
    @FXML
    private TableColumn<OrderDetail, String> quantityCol;
    @FXML
    private TableColumn<OrderDetail, Long> unitPriceCol;

    private Order order;
    private List<OrderDetail> orderDetails;
    private Table table;
    private Employee employee;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        init();
        initTableViewOrder();
        initComboBoxCategory();
        initTableViewTable();
        initTableViewProduct();

        loadTableViewTable(getTableList());
        loadTableViewProduct(getProductList(cbCategory.getValue().getContent()));

        this.txtCapacity.textProperty().addListener((ov, t, t1) -> {
            loadTableViewTable(getTableList(t1));
        });

        this.cbCategory.getSelectionModel().selectedItemProperty().addListener((ov, t, t1) -> {
            loadTableViewProduct(getProductList(t1.getContent()));
        });

        this.tbvTable.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends Table> ov, Table t, Table t1) -> {
                    lblTable.setText("B??n ?????t: " + t1.getTableName());
                    table = t1;
                });

        this.txtName.textProperty().addListener((ov, t, t1) -> {
            loadTableViewProduct(getProductListByName(this.cbCategory.getValue().getContent(), t1));
        });

        this.tbvProduct.getSelectionModel().selectedItemProperty().addListener((ov, t, t1) -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setHeaderText("Nh???p s??? l?????ng: ");
            Optional<String> optional = dialog.showAndWait();
            if (optional.isPresent()) {
                int amount;
                if (optional.get().isBlank() || Integer.parseInt(optional.get()) == 0) {
                    amount = 1;
                } else amount = Integer.parseInt(optional.get());

                OrderDetail detail = new OrderDetail(order.getOrderID(),
                        t1.getProductID(), t1.getProductName(), amount,
                        t1.getUnitPrice(), null);
                orderDetails.add(detail);
                loadTableViewOrder();
            }
        });
    }

    public void orderHandler(ActionEvent event) throws SQLException {
        if (this.table == null && this.tbvOrder.getItems().isEmpty()) {
            Utils.showAlert(Alert.AlertType.ERROR, "Order Error!", "Ch??a c?? th??ng tin b??n v?? m??n!");
        } else if (this.tbvOrder.getItems().isEmpty()) {
            Utils.showAlert(Alert.AlertType.ERROR, "Order Error!", "Ch??a ch???n m??n!");
        } else if (this.table == null) {
            Utils.showAlert(Alert.AlertType.ERROR, "Order Error!", "Ch??a ch???n b??n");
        } else {
            order = new Order(order.getOrderID(), LocalDate.now(), BigDecimal.valueOf(calculate()),
                    order.getEmployeeID(), table.getTableID(), 0);

            OrderService orderService = new OrderService();
            if (orderService.addOrder(order)) {
                orderDetails.forEach((t) -> {
                    OrderDetailService detailService = new OrderDetailService();
                    detailService.addOrderDetail(t);
                });

                //C???p nh???t tr???ng th??i b??n ?????t
                TableService service = new TableService();
                this.table.setStatus(Status.FULL);
                service.updateTable(table);
                loadTableViewTable(getTableList());

                showOrderDetail(order.getOrderID());
            }
            init();
        }
    }

    public void cancelHandler(ActionEvent event) {
        App app = new App();
        app.loaderController("Main.fxml", "Coffee Management App");
    }

    private void init() {
        this.orderDetails = new ArrayList<>();
        this.table = null;
        this.order = new Order();
        this.order.setOrderID(Utils.randomID());
        this.order.setEmployeeID("54cf6d95-fdff-4477-8237-805d07e90217");

        this.btnCancel.getStyleClass().setAll("btn", "btn-danger");
        this.btnOK.getStyleClass().setAll("btn", "btn-success");
        this.lblOrderDate.setText("Ng??y ?????t: " + Utils.converter.toString(LocalDate.now()));
        this.tbvOrder.getItems().clear();

        this.lblTotal.setText("T???ng:...");
        this.lblTable.setText("B??n ?????t: ...");
        this.lblEmployee.setText("Nh??n vi??n:...");
    }

    private long calculate() {
        long sum = 0;
        for (int i = 0; i < orderDetails.size(); i++) {
            sum += orderDetails.get(i).getQuantity() * orderDetails.get(i).getUnitPrice();
        }
        return sum;
    }

    private void initComboBoxCategory() {
        ObservableList<Category> list = FXCollections.observableArrayList(Category.DRINK, Category.FOOD);
        this.cbCategory.setItems(list);
        this.cbCategory.setValue(Category.DRINK);
        this.cbCategory.setCursor(Cursor.HAND);
    }

    private void initTableViewTable() {
        TableColumn<Table, String> tableNameCol = new TableColumn<>("T??n b??n");
        TableColumn<Table, Integer> capacityCol = new TableColumn<>("S???c ch???a");

        tableNameCol.setCellValueFactory(new PropertyValueFactory<>("tableName"));
        capacityCol.setCellValueFactory(new PropertyValueFactory<>("capacity"));

        tableNameCol.setSortType(TableColumn.SortType.ASCENDING);

        this.tbvTable.getColumns().addAll(tableNameCol, capacityCol);

        //Ch??? cho ph??p ch???n 1 d??ng tr??n danh s??ch
        this.tbvTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    private void initTableViewProduct() {
        TableColumn<Product, String> productNameCol = new TableColumn<>("T??n s???n ph???m");
        TableColumn<Product, Long> unitPriceCol = new TableColumn<>("????n gi?? (VN??)");

        productNameCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
        unitPriceCol.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));

        productNameCol.setSortType(TableColumn.SortType.ASCENDING);

        this.tbvProduct.getColumns().addAll(productNameCol, unitPriceCol);

        //Ch??? cho ph??p ch???n 1 d??ng tr??n danh s??ch
        this.tbvProduct.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    private void initTableViewOrder() {
        productNameCol = new TableColumn<>("T??n s???n ph???m");
        quantityCol = new TableColumn<>("S??? l?????ng");
        unitPriceCol = new TableColumn<>("????n gi?? (VN??)");

        productNameCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        unitPriceCol.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));

        this.tbvOrder.getColumns().addAll(productNameCol, quantityCol, unitPriceCol);

        //Ch??? cho ph??p ch???n 1 d??ng tr??n danh s??ch
        this.tbvOrder.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    private void loadTableViewTable(ObservableList<Table> list) {
        this.tbvTable.setItems(list);
    }

    public void showOrderDetail(String orderID) throws SQLException {
        OrderService orderService = new OrderService();
        TableService tableService = new TableService();
        OrderDetailService detailService = new OrderDetailService();
        List<OrderDetail> orderDetails = detailService.getOrderDetailsByOrderID(orderID);
        Order order = orderService.getOrderByID(orderID);
        Table table = tableService.getTable(order.getTableID());
        String result = String.format("- M?? h??a ????n: %s\n- Ng??y ?????t: %s\n- B??n ?????t: %s\n- Nh??n vi??n: %s\nTH??NG TIN S???N PH???M\n",
                order.getOrderID(), Utils.converter.toString(order.getOrderDate()), table.getTableName(), null);

        for (int i = 0; i < orderDetails.size(); i++) {
            result += orderDetails.get(i).toString() + "\n";
        }
        Utils.showAlert(Alert.AlertType.INFORMATION, "TH??NG TIN H??A ????N", result);
    }

    private void loadTableViewProduct(ObservableList<Product> list) {
        this.tbvProduct.setItems(list);
    }

    private void loadTableViewOrder() {
        ObservableList<OrderDetail> list = FXCollections.observableArrayList(orderDetails);
        this.tbvOrder.setItems(list);
        this.lblTotal.setText("T???ng: " + calculate());
    }

    //L???y danh s??ch b??n tr???ng
    private ObservableList<Table> getTableList() {
        try {
            TableService service = new TableService();
            List<Table> tables = service.getTablesByStatus(Status.EMPTY.getContent());
            return FXCollections.observableArrayList(tables);
        } catch (SQLException ex) {
            Logger.getLogger(OrderController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    //L???y danh s??ch b??n tr???ng theo s???c ch???a
    private ObservableList<Table> getTableList(String capacity) {
        try {
            TableService service = new TableService();
            List<Table> tables = service.getTablesByAll(capacity, Status.EMPTY.getContent());
            return FXCollections.observableArrayList(tables);
        } catch (SQLException ex) {
            Logger.getLogger(OrderController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    //L???y danh s??ch s???n ph???m theo lo???i
    private ObservableList<Product> getProductList(String category) {
        ProductService service = new ProductService();
        List<Product> products = service.getProductsByCategory(category);
        return FXCollections.observableArrayList(products);
    }

    //L???y danh s??ch s???n ph???m theo t??n
    private ObservableList<Product> getProductListByName(String category, String name) {
        ProductService service = new ProductService();
        List<Product> products = service.getProducts(category, name);
        return FXCollections.observableArrayList(products);
    }

}
