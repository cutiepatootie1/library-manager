package com.mimirlib.mimir.Controller;

import com.mimirlib.mimir.Data.DatabaseConnection;
import com.mimirlib.mimir.Data.MemberRole;
import com.mimirlib.mimir.Data.MemberStatus;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class AddMemController {

    private static final Logger logger = Logger.getLogger(AddMemController.class.getName());
    DatabaseConnection dbasecon = new DatabaseConnection();

    @FXML
    private TextField namefld;
    @FXML
    private TextField emailfld;
    @FXML
    private TextField contactNumfld;
    @FXML
    private ChoiceBox<String> roleBox;

    public void initializeRoles() {
        List<MemberRole> roleList = dbasecon.getAllRoles();

        List<String> formattedRoles = roleList.stream()
                .map(rol -> rol.getRole())
                .collect(Collectors.toList());

        ObservableList<String> roles = FXCollections.observableArrayList(formattedRoles);
        System.out.println("Category List is null: " + (roleBox == null));  // Debugging line

        if (roleBox != null) {
            roleBox.setItems(roles);
        }else{
            System.out.println("category box items is still null. Check your FXML");
        }

    }

    @FXML
    private void addProcess(ActionEvent event) {
        String name = namefld.getText();
        String email = emailfld.getText();
        String contactNum = contactNumfld.getText();
        String selectedRole = roleBox.getValue();
        String selectedStatus = "Active";  // Status should be mapped to its ID

        try {
            // Retrieve the corresponding RoleID
            int roleId = dbasecon.getAllRoles().stream()
                    .filter(role -> role.getRole().equals(selectedRole))
                    .map(MemberRole::getRoleId)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Invalid role selected"));

            // Retrieve the corresponding StatusID
            int statusId = dbasecon.getMemberStatuses().stream()
                    .filter(status -> status.getStatus().equals(selectedStatus))
                    .map(MemberStatus::getStatusId)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Invalid status selected"));

            // Pass IDs instead of names to the database procedure
            dbasecon.memberInput(name, email, contactNum, roleId, statusId);

            // Close the current stage
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error adding member: " + e.getMessage(), e);
        }
    }


    @FXML
    private void handleClose(ActionEvent event){
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
