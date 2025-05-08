package com.mimirlib.mimir.Controller;

import com.mimirlib.mimir.Data.DatabaseConnection;
import com.mimirlib.mimir.Data.MemberRole;
import com.mimirlib.mimir.Data.MemberStatus;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
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

//    public static boolean containsDigit(String input) {
//        for (char c : input.toCharArray()) {
//            if (Character.isDigit(c)) {
//               // showErrorModal("Error","Invalid input","Contains digit");
//                return true; // exits immediately on first digit
//            }
//        }
//        return false; // no digit found
//    }

    public static boolean containsSpecialCharacter(String input) {
        for (char c : input.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) {
                //showErrorModal("Error", "Invalid input", "Contains special character");
                return true; // exits immediately on first special character
            }
        }
        return false; // no special characters found
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
            if(containsSpecialCharacter(name)){
                throw new IllegalArgumentException("Contains digit and/or special character");
            }

            if(contactNum.length() != 11){
                throw new IllegalArgumentException("Phone number must be 11 digits");
            }
            if(!(email.contains("@")) || !(email.contains(".")) ){
                throw new IllegalArgumentException("Contains illegal characters");
            }

            // Pass IDs instead of names to the database procedure
            dbasecon.memberInput(name, email, contactNum, roleId, statusId);

            // Close the current stage
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error adding member: " + e.getMessage(), e);
            showErrorModal("Error!!", "Error message", e.getMessage());
        }
    }

    public static void showErrorModal(String title, String error, String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(error);
        alert.setContentText(message);
        alert.showAndWait(); // This makes it modal
    }

    @FXML
    private void handleClose(ActionEvent event){
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
