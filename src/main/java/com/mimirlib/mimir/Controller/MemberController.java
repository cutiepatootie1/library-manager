package com.mimirlib.mimir.Controller;


import com.mimirlib.mimir.Data.DatabaseConnection;
import com.mimirlib.mimir.Data.Member;
import com.mimirlib.mimir.Data.MemberRole;
import com.mimirlib.mimir.Data.MemberStatus;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.mimirlib.mimir.Controller.AddMemController.containsSpecialCharacter;


public class MemberController {


    private static final Logger logger = Logger.getLogger(MemberController.class.getName());
    DatabaseConnection dbasecon = new DatabaseConnection();

    private Map<String, Integer> roleMap = new HashMap<>();
    private Map<String, Integer> statusMap = new HashMap<>();

    private Member selectedMember;
    private int selectedMemberId;

    public MemberController() {
    }

    // INITIALIZE FXML IDs
    @FXML
    private TableView<Member> mainTable;
    @FXML
    private TableColumn<Member, Number> idColumn;
    @FXML
    private TableColumn<Member, String> nameColumn;
    @FXML
    private TableColumn<Member, String> roleColumn;
    @FXML
    private TableView<Member> extMemberTable;
    @FXML
    private TableColumn<Member, Number> extIdCol;
    @FXML
    private TableColumn<Member, String> extNameCol;
    @FXML
    private TableColumn<Member, String> extEmailCol;
    @FXML
    private TableColumn<Member, String> extContCol;
    @FXML
    private TableColumn<Member, String> extRoleCol;
    @FXML
    private TableColumn<Member, String> extStatus;
    @FXML
    private TextField searchFld;
    @FXML
    private ChoiceBox<String> roleBox;
    @FXML
    private ChoiceBox<String> statusBox;
    // Button IDs
    @FXML
    private Button refreshButton;
    @FXML
    private Button deleteBtn;
    @FXML
    private Button addBtn;
    @FXML
    private Button resetBtn;
    @FXML
    private Button selectBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private Text borrowLabel;

    private boolean isBorrow;

    // INITIALIZE Methods
    public void initialize() throws SQLException {
        System.out.println("Initializing...");

        initializeTableColumns();
        loadMembersData();
        setupTableSelectionListener();
        membersPanel(false); // Default to non-borrow mode

        editInitialize();
        initializeRoles();
        initializeStatus();
        refreshTables();

        // Attach listeners for filtering when a selection is made
        roleBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> handleFilterSelection());
        statusBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> handleFilterSelection());

        // Initially disable buttons except add and cancel
        deleteBtn.setDisable(true);
        resetBtn.setDisable(true);
        selectBtn.setDisable(true);
    }

    private void initializeTableColumns() {
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        roleColumn.setCellValueFactory(cellData -> cellData.getValue().roleProperty());


        extIdCol.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        extNameCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        extEmailCol.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        extContCol.setCellValueFactory(cellData -> cellData.getValue().contactNumProperty());
        extRoleCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                dbasecon.getAllRoles().stream()
                        .filter(role -> role.getRoleId() == cellData.getValue().getRoleId()) // Match ID
                        .map(MemberRole::getRole) // Get role name
                        .findFirst()
                        .orElse("Unknown")
        ));

        extStatus.setCellValueFactory(cellData -> new SimpleStringProperty(
                dbasecon.getMemberStatuses().stream()
                        .filter(stat -> stat.getStatusId() == cellData.getValue().getStatusId()) // Match ID
                        .map(MemberStatus::getStatus) // Get status name
                        .findFirst()
                        .orElse("Unknown")
        ));

        System.out.println("Table columns initialized");
    }

    private void setupTableSelectionListener() {
        mainTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                extMemberTable.setItems(FXCollections.observableArrayList(newSel));
                deleteBtn.setDisable(false);
                resetBtn.setDisable(false);
                selectBtn.setDisable(!isBorrow);
            } else {
                extMemberTable.getItems().clear();
                deleteBtn.setDisable(true);
                resetBtn.setDisable(true);
                selectBtn.setDisable(true);
            }
            System.out.println("Loaded Selection Listener");

            // Dynamically update refresh button state
            updateRefreshButtonState();
        });
    }

    @FXML
    private void editInitialize() throws SQLException {
        extMemberTable.setEditable(true);
        List<MemberRole> roleList = dbasecon.getAllRoles();
        List<MemberStatus> statusList = dbasecon.getMemberStatuses();

        // Convert role & status lists to display names in dropdown
        ObservableList<String> roles = FXCollections.observableArrayList(
                roleList.stream().map(MemberRole::getRole).collect(Collectors.toList())
        );

        ObservableList<String> statuses = FXCollections.observableArrayList(
                statusList.stream().map(MemberStatus::getStatus).collect(Collectors.toList())
        );


        extNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        extEmailCol.setCellFactory(TextFieldTableCell.forTableColumn());
        extContCol.setCellFactory(TextFieldTableCell.forTableColumn());
        extRoleCol.setCellFactory(ChoiceBoxTableCell.forTableColumn(roles));
        extStatus.setCellFactory(ChoiceBoxTableCell.forTableColumn(statuses));

        setupEditCommitHandlers();
        System.out.println("Loaded edit initialize");
    }

    public static void showErrorModal(String title, String error, String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(error);
        alert.setContentText(message);
        alert.showAndWait(); // This makes it modal
    }

    public void initializeRoles() {
        List<MemberRole> roleList = dbasecon.getAllRoles();

        if (roleList.isEmpty()) {
            System.out.println("No roles retrieved from database.");
            return;
        }

        List<String> formattedRoles = new ArrayList<>();
        for (MemberRole role : roleList) {
            formattedRoles.add(role.getRole());
            roleMap.put(role.getRole(), role.getRoleId()); // Store name → ID mapping
        }

        ObservableList<String> roles = FXCollections.observableArrayList(formattedRoles);

        if (roleBox != null) {
            roleBox.setItems(roles);
        } else {
            System.out.println("Genre box items are still null. Check your FXML.");
        }

        //roleBox.setItems(FXCollections.observableArrayList(formattedRoles));

//        if (roleMap.isEmpty()) {
//            System.out.println("roleMap is empty, roles might not be loaded correctly.");
//        }
        System.out.println("Roles initialized");
    }

    public void initializeStatus() {
        List<MemberStatus> statusList = dbasecon.getMemberStatuses();

        if (statusList.isEmpty()) {
            System.out.println("No statuses retrieved from database.");
            return;
        }

        List<String> formattedStatuses = new ArrayList<>();
        for (MemberStatus stat : statusList) {
            formattedStatuses.add(stat.getStatus());
            statusMap.put(stat.getStatus(), stat.getStatusId()); // Store name → ID mapping
        }

        statusBox.setItems(FXCollections.observableArrayList(formattedStatuses));

        if (statusMap.isEmpty()) {
            System.out.println("statusMap is empty, statuses might not be loaded correctly.");
        }
        System.out.println("Status initialized");
    }


    // DATA LOADING and REFRESHING
    private void loadMembersData() {
        List<Member> members = dbasecon.getAllMembers();
        mainTable.setItems(FXCollections.observableArrayList(members));
        System.out.println("Loaded Member Data");
    }

//    public static boolean containsDigit(String input) {
//        for (char c : input.toCharArray()) {
//            if (Character.isDigit(c)) {
//                showErrorModal("Error","Invalid input","Contains digit");
//                return true; // exits immediately on first digit
//            }
//        }
//        return false; // no digit found
//    }


    private void setupEditCommitHandlers() {
        // Standard text edits
        extNameCol.setOnEditCommit(event -> {
            //
            String newValue = event.getNewValue();
            //containsDigit(newValue);
            if(!containsSpecialCharacter(newValue)){
                event.getRowValue().nameProperty().setValue(newValue);
                Member member = event.getRowValue();
                dbasecon.updateMember(member);
            }else{
                showErrorModal("Error", "Invalid input","contains digit or special characters");
                extMemberTable.refresh();
            }

        });
        extEmailCol.setOnEditCommit(event -> {
            String newValue = event.getNewValue();
            if (newValue != null && newValue.contains("@") && newValue.contains(".")) {
                event.getRowValue().emailProperty().setValue(newValue);
                Member member = event.getRowValue();
                dbasecon.updateMember(member);
            } else {
                showErrorModal("Error!","Invalid Email", "Email must contain '@' and '.'");
                // Revert to old value
                extMemberTable.refresh();
            }

        });
        extContCol.setOnEditCommit(event ->
                {
                    String newValue = event.getNewValue();
                    if (newValue != null && newValue.length() == 11) {
                        event.getRowValue().contactNumProperty().setValue(newValue);
                        Member member = event.getRowValue();
                        dbasecon.updateMember(member);
                    } else {
                        showErrorModal("Error", "Invalid Contact", "Contact must be exactly 11 characters.");
                        // Revert to old value
                        extMemberTable.refresh();
                    }
                });


        // **Fix Role selection to store ID instead of name**
        extRoleCol.setOnEditCommit(event -> {
            Member member = event.getRowValue();
            String selectedRoleName = event.getNewValue();

            int roleId = dbasecon.getAllRoles().stream()
                    .filter(role -> role.getRole().equalsIgnoreCase(selectedRoleName))
                    .map(MemberRole::getRoleId)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Invalid role selected"));

            // Store ID instead of Name
            member.roleIdProperty().set(roleId);
            member.roleProperty().set(selectedRoleName); // Still show name in UI

            dbasecon.updateMember(member);
        });

        // **Fix Status selection to store ID instead of name**
        extStatus.setOnEditCommit(event -> {
            Member member = event.getRowValue();
            String selectedStatusName = event.getNewValue();

            int statusId = dbasecon.getMemberStatuses().stream()
                    .filter(stat -> stat.getStatus().equalsIgnoreCase(selectedStatusName))
                    .map(MemberStatus::getStatusId)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Invalid status selected"));

            // Store ID instead of Name
            member.statusIdProperty().set(statusId);
            member.statusProperty().set(selectedStatusName); // Still show name in UI

            dbasecon.updateMember(member);
        });
    }

    @FXML
    public void refreshTables() throws SQLException {
        System.out.println("Refreshing member data...");

        // Store the currently selected member
        Member selectedMember = mainTable.getSelectionModel().getSelectedItem();

        // Reload member list from the database
        List<Member> members = dbasecon.getAllMembers();
        ObservableList<Member> memberList = FXCollections.observableArrayList(members);
        mainTable.setItems(memberList);

        // **Clear ChoiceBox selections to avoid retaining unwanted filters**
        roleBox.getSelectionModel().clearSelection();
        statusBox.getSelectionModel().clearSelection();

        // **Clear stored selections in model classes**
        MemberRole.setSelectedRole(null);
        MemberStatus.setSelectedStatus(null);

        // Restore selection if it still exists
        if (selectedMember != null && memberList.contains(selectedMember)) {
            mainTable.getSelectionModel().select(selectedMember);
            extMemberTable.setItems(FXCollections.observableArrayList(selectedMember));
        } else {
            extMemberTable.getItems().clear(); // Clear extended table if no selection
        }

        // Update button states dynamically
        updateRefreshButtonState();
    }

    private void updateRefreshButtonState() {
        boolean hasSelection = mainTable.getSelectionModel().getSelectedItem() != null;
        //refreshButton.setDisable(!hasSelection); // Disable if no selection exists
    }


    // CORE FUNCTIONALITIES
    @FXML
    public void add(MouseEvent event) {
        System.out.println("clicked add");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mimirlib/mimir/addMember.fxml"));
            Parent root = loader.load();


            AddMemController modalController = loader.getController();
            modalController.initializeRoles();


            Stage stage = new Stage();
            stage.setTitle("New Member");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            // Block input to parent window
            stage.initOwner(((Node) event.getSource()).getScene().getWindow());
            // set parent
            stage.showAndWait();


            refreshTables();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error opening add window", e);
        }
    }

    @FXML
    public void deleteMember() {
        try {
            Member selectedItem = mainTable.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                int selectedId = selectedItem.getId();
                System.out.println("Selected ID: " + selectedId);

                // Execute deletion procedure
                dbasecon.deleteMember(selectedId);

                // Refresh table after deletion
                refreshTables();
            } else {
                System.out.println("No member selected for deletion.");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting member", e);
        }
    }

    @FXML
    public void filterMembers() {
        handleFilterSelection();

        String searchName = searchFld.getText();
        String searchEmail = searchFld.getText();
        String searchPhoneNum = searchFld.getText();

        // Get selected Role and Status
        String selectedRole = roleBox.getValue();
        String selectedStatus = statusBox.getValue();

        // Convert selected role/status names to their respective IDs
        Integer roleId = selectedRole != null ? dbasecon.getAllRoles().stream()
                .filter(role -> role.getRole().equalsIgnoreCase(selectedRole))
                .map(MemberRole::getRoleId)
                .findFirst()
                .orElse(null) : null;

        Integer statusId = selectedStatus != null ? dbasecon.getMemberStatuses().stream()
                .filter(stat -> stat.getStatus().equalsIgnoreCase(selectedStatus))
                .map(MemberStatus::getStatusId)
                .findFirst()
                .orElse(null) : null;

        // Properly format search fields with wildcard for `LIKE`
        String formattedName = (searchName != null && !searchName.isEmpty()) ? "%" + searchName + "%" : null;
        String formattedEmail = (searchEmail != null && !searchEmail.isEmpty()) ? "%" + searchEmail + "%" : null;
        String formattedPhone = (searchPhoneNum != null && !searchPhoneNum.isEmpty()) ? "%" + searchPhoneNum + "%" : null;

        // Execute filtering with updated values
        List<Member> filteredMembers = dbasecon.viewMembersWithFilters(formattedName, formattedEmail, formattedPhone, roleId, statusId);

        // Refresh table with filtered results
        mainTable.setItems(FXCollections.observableArrayList(filteredMembers));

        System.out.println("Filtered Members: " + filteredMembers);
    }


    // SELECTION HANDLING
    public Member getSelectedMember() {
        return selectedMember;
    }

    public int getSelectedMemberId() {
        return selectedMemberId;
    }

    @FXML
    public void handleSelectMemberButtonAction() {
        Member selectedMember = mainTable.getSelectionModel().getSelectedItem();
        if (selectedMember != null) {
            this.selectedMemberId = selectedMember.getId();
            Stage stage = (Stage) selectBtn.getScene().getWindow();
            stage.close();
        } else {
            System.out.println("Please select a member.");
        }
    }

    public void showMemberSelection(ActionEvent event) {
        try {
            // Load the FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mimirlib/mimir/member.fxml"));
            Parent root = loader.load();
            MemberController selectionController = loader.getController();

            // Retrieve only active members
            List<Member> activeMembers = dbasecon.getAllMembers().stream()
                    .filter(member -> member.getStatus().equalsIgnoreCase("Active")) // Filter active members
                    .collect(Collectors.toList());

            // Load only active members into the table
            selectionController.loadFilteredMembers(activeMembers);

            // Configure borrow mode (if needed)
            selectionController.membersPanel(this.isBorrow);

            // Set up the stage
            Stage stage = new Stage();
            stage.setTitle("Select Active Member");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(((Node) event.getSource()).getScene().getWindow());

            // Show and wait
            stage.showAndWait();

            // Get the selected member AFTER the window closes
            this.selectedMemberId = selectionController.getSelectedMemberId();
            this.selectedMember = selectionController.getSelectedMember();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error opening member selection window", e);
        }
    }

    public void selectMember(int memberId) {
        for (Member member : mainTable.getItems()) {
            if (member.getId() == memberId) {
                mainTable.getSelectionModel().select(member);
                mainTable.scrollTo(member); // Optional: Scrolls the selected member into view
                // Optional: If you have a detail pane, populate it here
                break;
            }
        }
    }


    // BORROWING and TRANSACTIONS
    public void membersPanel(boolean isBorrow) {
        this.isBorrow = isBorrow;
        addBtn.setVisible(!isBorrow);
        addBtn.setManaged(!isBorrow);
        // Important for layout
        deleteBtn.setVisible(!isBorrow);
        deleteBtn.setManaged(!isBorrow);
        resetBtn.setVisible(!isBorrow);
        resetBtn.setManaged(!isBorrow);


        selectBtn.setVisible(isBorrow);
        selectBtn.setManaged(isBorrow);
        cancelBtn.setVisible(isBorrow);
        cancelBtn.setManaged(isBorrow);
        borrowLabel.setVisible(isBorrow);
        borrowLabel.setManaged(isBorrow);


        // Always enable addBtn and cancelBtn
        addBtn.setDisable(false);
        cancelBtn.setDisable(false);
        System.out.println("Loaded Members Panel");
    }

    @FXML
    public void cancelBorrowing(ActionEvent event) {
        //  Logic to handle canceling the borrowing process
        System.out.println("Borrowing process canceled.");
        //  Get the current stage
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        //  Close the stage
        stage.close();
    }


    // UTILITY
    private void initializeChoiceBox(ChoiceBox<String> choiceBox, List<String> dataList, String debugMessage) {
        ObservableList<String> items = FXCollections.observableArrayList(dataList);
        System.out.println(debugMessage + (choiceBox == null));


        if (choiceBox != null) {
            choiceBox.setItems(items);
        } else {
            System.out.println(debugMessage + "Check your FXML");
        }
    }

    private interface MemberPropertyUpdater {
        void update(Member member, String newValue);
    }

    @FXML
    private void handleFilterSelection() {
        String selectedRole = roleBox.getValue();
        String selectedStatus = statusBox.getValue();

        // Ensure role selection is valid
        Integer roleId = (selectedRole != null && roleMap.containsKey(selectedRole)) ? roleMap.get(selectedRole) : null;
        if (roleId != null) {
            MemberRole.setSelectedRole(new MemberRole(selectedRole, roleId));
        } else {
            System.out.println("Role selection failed: " + selectedRole);
        }

        // Ensure status selection is valid
        Integer statusId = (selectedStatus != null && statusMap.containsKey(selectedStatus)) ? statusMap.get(selectedStatus) : null;
        if (statusId != null) {
            MemberStatus.setSelectedStatus(new MemberStatus(selectedStatus, statusId));
        } else {
            System.out.println("Status selection failed: " + selectedStatus);
        }
    }

    public void loadFilteredMembers(List<Member> filteredMembers) {
        mainTable.setItems(FXCollections.observableArrayList(filteredMembers));
    }

    private void updateMemberProperty(TableColumn.CellEditEvent<Member, String> event, MemberPropertyUpdater updater) {
        Member member = event.getRowValue();
        updater.update(member, event.getNewValue());
        dbasecon.updateMember(member);
    }

}