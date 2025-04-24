package com.mimirlib.mimir.Controller;

import com.mimirlib.mimir.Data.DatabaseConnection;
import com.mimirlib.mimir.Data.Member;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MemberController {
    private static final Logger logger = Logger.getLogger(MemberController.class.getName());

    DatabaseConnection dbasecon = new DatabaseConnection();

    private final String member = "memberstatus"; // Table name

    public MemberController()  {}

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
    private javafx.scene.control.TableView<Member> extMemberTable;
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

    @FXML
    public void initialize() throws SQLException {
        System.out.println("Initializing...");
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        roleColumn.setCellValueFactory(cellData -> cellData.getValue().roleProperty());

        extIdCol.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        extNameCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        extEmailCol.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        extContCol.setCellValueFactory(cellData -> cellData.getValue().contactNumProperty());
        extRoleCol.setCellValueFactory(cellData -> cellData.getValue().roleProperty());
        extStatus.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

        List<Member> members = dbasecon.getAllMembers();
        mainTable.setItems(FXCollections.observableArrayList(members));

        mainTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                extMemberTable.setItems(FXCollections.observableArrayList(newSel));
            }
        });
        editInitialize();
        initializeRoles();
        initializeStatus();
    }

    // Revise this method after completing the add member fxml and controller
    @FXML
    public void add(MouseEvent event) throws SQLException {
        System.out.println("clicked add");
        initialize();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mimirlib/mimir/addMember.fxml"));
            Parent root = loader.load();

            AddMemController modalController = loader.getController();
            modalController.initializeRoles();

            Stage stage = new Stage();
            stage.setTitle("New Member");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL); // Block input to parent window
            stage.initOwner(((Node) event.getSource()).getScene().getWindow()); // set parent
            stage.showAndWait();

            refreshTables();

        }catch (Exception e){
            logger.log(Level.SEVERE,"Error opening add window", e);
        }
    }

    public void refreshTables() throws SQLException {
        System.out.println("refreshing...");
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        roleColumn.setCellValueFactory(cellData -> cellData.getValue().roleProperty());

        extIdCol.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        extNameCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        extEmailCol.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        extContCol.setCellValueFactory(cellData -> cellData.getValue().contactNumProperty());
        extRoleCol.setCellValueFactory(cellData -> cellData.getValue().roleProperty());
        extStatus.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

        List<Member> members = dbasecon.getAllMembers();
        mainTable.setItems(FXCollections.observableArrayList(members));

        mainTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                extMemberTable.setItems(FXCollections.observableArrayList(newSel));
            }
        });
        editInitialize();
    }


    @FXML
    private void editInitialize() throws SQLException{
        extMemberTable.setEditable(true);
        List<String> roleList = dbasecon.getAllRoles();
        List<String> statusList = dbasecon.getAllStatus(member);

        ObservableList<String> roles = FXCollections.observableArrayList(roleList);
        ObservableList<String> status = FXCollections.observableArrayList(statusList);


        extNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        extEmailCol.setCellFactory(TextFieldTableCell.forTableColumn());
        extContCol.setCellFactory(TextFieldTableCell.forTableColumn());
        extRoleCol.setCellFactory(ChoiceBoxTableCell.forTableColumn(roles));
        extStatus.setCellFactory(ChoiceBoxTableCell.forTableColumn(status));

        extNameCol.setOnEditCommit(event -> {
            Member member = event.getRowValue();
            member.idProperty().getValue();
            member.nameProperty().set(event.getNewValue());
            dbasecon.updateMember(member);
        });

        extEmailCol.setOnEditCommit(event -> {
            Member member = event.getRowValue();
            member.idProperty().getValue();
            member.emailProperty().set(event.getNewValue());
            dbasecon.updateMember(member);
        });

        extContCol.setOnEditCommit(event -> {
            Member member = event.getRowValue();
            member.idProperty().getValue();
            member.contactNumProperty().set(event.getNewValue());
            dbasecon.updateMember(member);
        });

        extRoleCol.setOnEditCommit(event -> {
            Member member = event.getRowValue();
            member.idProperty().getValue();
            member.roleProperty().set(event.getNewValue());
            dbasecon.updateMember(member);
        });

        extStatus.setOnEditCommit(event -> {
            Member member = event.getRowValue();
            member.idProperty().getValue();
            member.statusProperty().set(event.getNewValue());
            dbasecon.updateMember(member);
        });
    }

    @FXML
    public void deleteMember()throws SQLException{
        Member selectedItem = mainTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null){
            long selectedId = selectedItem.getId();
            System.out.println("selected id:" + selectedId);
            DatabaseConnection.deleteMemberById(selectedId);
            refreshTables();
        }
    }

        @FXML
        public void filterMembers(){
            String searchName = searchFld.getText();
            String searchEmail = searchFld.getText();
            String searchPhoneNum = searchFld.getText();
            String roleFilter = roleBox.getValue() != null ? roleBox.getValue() : "";
            String statusFilter = statusBox.getValue() != null ? statusBox.getValue(): "";

            List<Member> filteredMembers = dbasecon.viewMembersWithFilters(searchName, searchEmail, searchPhoneNum, roleFilter, statusFilter);
            mainTable.setItems(FXCollections.observableArrayList(filteredMembers));

            System.out.println(filteredMembers);

            System.out.println("CLicked button");

        }

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

    public void initializeStatus() throws SQLException{
        List<String> statList = dbasecon.getAllStatus(member);

        ObservableList<String> genres = FXCollections.observableArrayList(statList);
        System.out.println("Status List is null: " + (statusBox == null));  // Debugging line

        if (statusBox != null) {
            statusBox.setItems(genres);
        }else{
            System.out.println("status box items is still null. Check your FXML");
        }

    }
}
