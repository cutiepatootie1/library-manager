package com.mimirlib.mimir.Controller;

import com.mimirlib.mimir.Data.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

public class AddMemController {
    //private static final Logger logger = Logger.getLogger(AddMemController.class.getName());
    DatabaseConnection dbasecon = new DatabaseConnection();
    @FXML
    private TextField namefld;
    @FXML
    private TextField emailfld;
    @FXML
    private TextField contactNumfld;
    @FXML
    private ChoiceBox<String> roleBox;

    public void initializeRoles() throws SQLException {
        List<String> roleList = dbasecon.getAllRoles();


        ObservableList<String> roles = FXCollections.observableArrayList(roleList);
        System.out.println("Category List is null: " + (roleBox == null));  // Debugging line

        if (roleBox != null) {
            roleBox.setItems(roles);
        }else{
            System.out.println("category box items is still null. Check your FXML");
        }

    }

    @FXML
    private void addProcess(ActionEvent event)  {

        String name = namefld.getText();
        String email = emailfld.getText();
        String contactNum = contactNumfld.getText();
        String role = roleBox.getValue();
        String status = "Active";

        dbasecon.memberInput(false, name, email, contactNum, role, status);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();

    }

    @FXML
    private void handleClose(ActionEvent event){
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
