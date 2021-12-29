/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nthn.coffeemanagementapp;

import com.nthn.pojo.Product;
import com.nthn.services.ProductService;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * FXML Controller class
 *
 * @author PC
 */
public class ProductController implements Initializable {
    @FXML private TextField txtProduct;
    @FXML private Button btnSubmit;
    @FXML private TableView<Product> tbProduct;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
        this.loadTableViewProduct();
        
        ProductService ps = new ProductService();
        List<Product> products = new ArrayList<>();
        try {
            products = ps.getProducts();
            this.loadTableDataProduct(null);
        } catch (SQLException ex) {
            Logger.getLogger(ProductController.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        this.txtProduct.textProperty().addListener((evt) -> {
            try {
                this.loadTableDataProduct(this.txtProduct.getText());
            } catch (SQLException ex) {
                Logger.getLogger(ProductController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }    
    
    private void loadTableViewProduct() {
        TableColumn colProductName = new TableColumn("Product name");
        colProductName.setCellValueFactory(new PropertyValueFactory("ProductName"));
        colProductName.setPrefWidth(400);
        
        TableColumn colUnitPrice = new TableColumn("Unit price");
        colProductName.setCellValueFactory(new PropertyValueFactory("UnitPrice"));
        colProductName.setPrefWidth(400);
        
        this.tbProduct.getColumns().addAll(colProductName, colUnitPrice);            
    }
    
    private void loadTableDataProduct(String kw) throws SQLException {      
        ProductService ps = new ProductService();
        this.tbProduct.setItems(FXCollections.observableList(ps.getProductsByName(kw)));        
    }
}