package com.mimirlib.mimir.Controller;

import com.mimirlib.mimir.Data.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.util.List;

public class AdminController {

    DatabaseConnection dbasecon = new DatabaseConnection();

    public AdminController() {
    }

    // Initialize FXML
    @FXML
    private TableView<BookStatus> BookStatusTable;
    @FXML
    private TableView<BookCategory> BookCategoriesTable;
    @FXML
    private TableView<BookGenre> BookGenresTable;
    @FXML
    private TableView<MemberRole> MemberRolesTable;
    @FXML
    private TableView<MemberStatus> MemberStatusTable;

    @FXML
    private TableColumn<BookStatus, String> bookStatusIdCol;
    @FXML
    private TableColumn<BookStatus, String> bookStatusCol;
    @FXML
    private TableColumn<BookCategory, String> bookCategoryIdCol;
    @FXML
    private TableColumn<BookCategory, String> bookCategoryCol;
    @FXML
    private TableColumn<BookGenre, String> bookGenreIdCol;
    @FXML
    private TableColumn<BookGenre, String> bookGenreCol;
    @FXML
    private TableColumn<MemberRole, String> memberRoleIdCol;
    @FXML
    private TableColumn<MemberRole, String> memberRoleCol;
    @FXML
    private TableColumn<MemberStatus, String> memberStatusIdCol;
    @FXML
    private TableColumn<MemberStatus, String> memberStatusCol;

    @FXML
    private Button refreshBtn;
    @FXML
    private Button toggleEditBtn;

    private boolean isEditable = false;

    @FXML
    private TextField newCategoryCodeField;
    @FXML
    private TextField newCategoryNameField;

    @FXML
    private TextField newGenreCodeField;
    @FXML
    private TextField newGenreNameField;

    @FXML
    private TextField newBookStatusField;
    @FXML
    private TextField newMemberStatusField;
    @FXML
    private TextField newMemberRoleField;

    @FXML
    public void initialize() {
        initBookStatusTable();
        initBookCategoriesTable();
        initBookGenresTable();
        initMemberRolesTable();
        initMemberStatusTable();
        loadAllData();
        toggleEditBtn.setText("Enable Editing");
        toggleEditBtn.setOnAction(event -> toggleTableEditMode());
        setTableEditable(isEditable);
    }

    private void initBookStatusTable() {
        bookStatusIdCol.setCellValueFactory(new PropertyValueFactory<>("statusId"));
        bookStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void initBookCategoriesTable() {
        bookCategoryIdCol.setCellValueFactory(new PropertyValueFactory<>("categoryCode"));
        bookCategoryCol.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
    }

    private void initBookGenresTable() {
        bookGenreIdCol.setCellValueFactory(new PropertyValueFactory<>("genreCode"));
        bookGenreCol.setCellValueFactory(new PropertyValueFactory<>("genreName"));
    }

    private void initMemberRolesTable() {
        memberRoleIdCol.setCellValueFactory(new PropertyValueFactory<>("roleId"));
        memberRoleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
    }

    private void initMemberStatusTable() {
        memberStatusIdCol.setCellValueFactory(new PropertyValueFactory<>("statusId"));
        memberStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    @FXML
    private void handleRefresh() {
        loadAllData();
        refreshTables();
    }

    private void refreshTables() {
        BookStatusTable.refresh();
        BookCategoriesTable.refresh();
        BookGenresTable.refresh();
        MemberRolesTable.refresh();
        MemberStatusTable.refresh();
    }

    private void loadAllData() {
        loadBookStatusData();
        loadBookCategoriesData();
        loadBookGenresData();
        loadMemberRolesData();
        loadMemberStatusData();
    }

    private void loadBookStatusData() {
        List<BookStatus> statuses = dbasecon.getBookStatuses();
        BookStatusTable.setItems(FXCollections.observableArrayList(statuses));
    }

    private void loadBookCategoriesData() {
        List<BookCategory> categories = dbasecon.getAllCategories();
        BookCategoriesTable.setItems(FXCollections.observableArrayList(categories));
    }

    private void loadBookGenresData() {
        List<BookGenre> genres = dbasecon.getAllGenres();
        BookGenresTable.setItems(FXCollections.observableArrayList(genres));
    }

    private void loadMemberRolesData() {
        List<MemberRole> roles = dbasecon.getAllRoles();
        MemberRolesTable.setItems(FXCollections.observableArrayList(roles));
    }

    private void loadMemberStatusData() {
        List<MemberStatus> statuses = dbasecon.getMemberStatuses();
        MemberStatusTable.setItems(FXCollections.observableArrayList(statuses));
    }

    private void toggleTableEditMode() {
        toggleEditBtn.setText("Enable Editing");
        isEditable = !isEditable; // Switch state
        setTableEditable(isEditable); // Apply state change

        // Update button text accordingly
        toggleEditBtn.setText(isEditable ? "Disable Editing" : "Enable Editing");
    }

    private void setTableEditable(boolean editable) {
        BookStatusTable.setEditable(editable);
        BookCategoriesTable.setEditable(editable);
        BookGenresTable.setEditable(editable);
        MemberRolesTable.setEditable(editable);
        MemberStatusTable.setEditable(editable);

        // Apply editability to columns
        if (editable) {
            bookCategoryCol.setCellFactory(TextFieldTableCell.forTableColumn());
            bookCategoryCol.setOnEditCommit(event -> {
                BookCategory updatedCategory = event.getRowValue();
                updatedCategory.setCategoryName(event.getNewValue());
                dbasecon.updateBookCategory(updatedCategory);
                refreshTables();
            });

            bookGenreCol.setCellFactory(TextFieldTableCell.forTableColumn());
            bookGenreCol.setOnEditCommit(event -> {
                BookGenre updatedGenre = event.getRowValue();
                updatedGenre.setGenreName(event.getNewValue());
                dbasecon.updateBookGenre(updatedGenre);
                refreshTables();
            });

            bookStatusCol.setCellFactory(TextFieldTableCell.forTableColumn());
            bookStatusCol.setOnEditCommit(event -> {
                BookStatus updatedStatus = event.getRowValue();
                updatedStatus.statusProperty().set(event.getNewValue());
                dbasecon.updateBookStatus(updatedStatus, "BOOK");
                refreshTables();
            });

            memberRoleCol.setCellFactory(TextFieldTableCell.forTableColumn());
            memberRoleCol.setOnEditCommit(event -> {
                MemberRole updatedRole = event.getRowValue();
                updatedRole.setRole(event.getNewValue());
                dbasecon.updateMemberRole(updatedRole);
                refreshTables();
            });

            memberStatusCol.setCellFactory(TextFieldTableCell.forTableColumn());
            memberStatusCol.setOnEditCommit(event -> {
                MemberStatus updatedStatus = event.getRowValue();
                updatedStatus.setStatus(event.getNewValue());
                dbasecon.updateMemberStatus(updatedStatus, "MEMBER");
                refreshTables();
            });

        } else {
            // Disable edit mode properly
            bookCategoryCol.setCellFactory(TextFieldTableCell.forTableColumn());
            bookGenreCol.setCellFactory(TextFieldTableCell.forTableColumn());
            bookStatusCol.setCellFactory(TextFieldTableCell.forTableColumn());
            memberRoleCol.setCellFactory(TextFieldTableCell.forTableColumn());
            memberStatusCol.setCellFactory(TextFieldTableCell.forTableColumn());
        }
    }

    @FXML
    private void handleAddCategory() {
        String code = newCategoryCodeField.getText();
        String name = newCategoryNameField.getText();
        if (code != null && !code.isEmpty() && name != null && !name.isEmpty()) {
            dbasecon.bookCategoryInput(code, name);
            loadBookCategoriesData(); // Refresh table data
            refreshTables();
            newCategoryCodeField.clear();
            newCategoryNameField.clear();
        } else {
            System.out.println("Category code and name cannot be empty.");
        }
    }

    @FXML
    private void handleAddGenre() {
        String code = newGenreCodeField.getText();
        String name = newGenreNameField.getText();
        if (code != null && !code.isEmpty() && name != null && !name.isEmpty()) {
            dbasecon.bookGenreInput(code, name);
            loadBookGenresData();
            refreshTables();
            newGenreCodeField.clear();
            newGenreNameField.clear();
        } else {
            System.out.println("Genre code and name cannot be empty.");
        }
    }

    @FXML
    private void handleAddBookStatus() {
        String status = newBookStatusField.getText();
        if (status != null && !status.isEmpty()) {
            dbasecon.bookStatusInput(status, "BOOK");
            loadBookStatusData();
            refreshTables();
            newBookStatusField.clear();
        } else {
            System.out.println("Book status cannot be empty.");
        }
    }

    @FXML
    private void handleAddMemberStatus() {
        String status = newMemberStatusField.getText();
        if (status != null && !status.isEmpty()) {
            dbasecon.memberStatusInput(status, "MEMBER");
            loadMemberStatusData();
            refreshTables();
            newMemberStatusField.clear();
        } else {
            System.out.println("Member status cannot be empty.");
        }
    }

    @FXML
        private void handleAddMemberRole() {
        String role = newMemberRoleField.getText();
        if (role != null && !role.isEmpty()) {
            dbasecon.memberRoleInput(role);
            loadMemberRolesData();
            refreshTables();
            newMemberRoleField.clear();
        } else {
            System.out.println("Member role cannot be empty.");
        }
    }

}