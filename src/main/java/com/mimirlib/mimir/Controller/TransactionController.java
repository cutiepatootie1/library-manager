package com.mimirlib.mimir.Controller;

import com.mimirlib.mimir.Data.BookStatus;
import com.mimirlib.mimir.Data.DatabaseConnection;
import com.mimirlib.mimir.Data.TransactionViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TransactionController {

    private static final Logger logger = Logger.getLogger(TransactionController.class.getName());
    final DatabaseConnection dbasecon = new DatabaseConnection();
    private MemberController memberController;
    private BookController bookController;
    private MainController mainController;

    // Transaction function UI related stuff
    @FXML
    private TableView<TransactionViewModel> mainTable;
    @FXML
    private TableColumn<TransactionViewModel, Number> idColumn;
    @FXML
    private TableColumn<TransactionViewModel, String> titleColumn;
    @FXML
    private TableColumn<TransactionViewModel, String> nameColumn;
    @FXML
    private TableView<TransactionViewModel> extTransactionTable;
    @FXML
    private TableColumn<TransactionViewModel, Number> extIdCol;
    @FXML
    private TableColumn<TransactionViewModel, String> extTitleCol;
    @FXML
    private TableColumn<TransactionViewModel, String> extNameCol;
    @FXML
    private TableColumn<TransactionViewModel, LocalDate> extBorrowCol;
    @FXML
    private TableColumn<TransactionViewModel, LocalDate> extDueCol;
    @FXML
    private TableColumn<TransactionViewModel, LocalDate> extReturnCol;

    @FXML
    private ChoiceBox<String> statusBox;
    @FXML
    private TextField searchFld;
    @FXML
    private Button refreshButton;
    @FXML
    private Button bookBtn;
    @FXML
    private Button memBtn;
    @FXML
    private Button statusBtn;
    @FXML
    private Button returnBtn;

    public void initialize() throws SQLException {
        loadTransactionsData();
        setUpTableSelectionListener();
        transactionInitialize();
        initializeTableColumns();
        initializeStatus();
        autoUpdateBookStatus();
    }

    private void initializeTableColumns() {
        try {
            idColumn.setCellValueFactory(cellData -> cellData.getValue().transactionIdProperty());
            titleColumn.setCellValueFactory(cellData -> cellData.getValue().bookTitleProperty());
            nameColumn.setCellValueFactory(cellData -> cellData.getValue().borrowerNameProperty());

            extIdCol.setCellValueFactory(cellData -> cellData.getValue().transactionIdProperty());
            extTitleCol.setCellValueFactory(cellData -> cellData.getValue().bookTitleProperty());
            extNameCol.setCellValueFactory(cellData -> cellData.getValue().borrowerNameProperty());
            extBorrowCol.setCellValueFactory(cellData -> cellData.getValue().borrowDateProperty());
            extDueCol.setCellValueFactory(cellData -> cellData.getValue().dueDateProperty());
            extReturnCol.setCellValueFactory(cellData -> cellData.getValue().returnDateProperty());

            System.out.println("Table columns initialized.");
        } catch (Exception e) {
            System.out.println("Table columns initialization error: " + e);
        }
    }


    private void initializeStatus() {
        try {
            statusBox.setItems(FXCollections.observableArrayList("Borrowed", "Available", "Overdue"));
            statusBox.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
                System.out.println("Selected Status: " + newValue);
            });
            System.out.println("Status initialized.");
        } catch (Exception e) {
            System.out.println("Status initialization error: " + e);
        }
    }

    @FXML
    private void transactionInitialize() throws SQLException {
        extTransactionTable.setEditable(false); // Prevent user edits

        // Fetch transaction data from the database
        List<TransactionViewModel> transactionList = dbasecon.getAllTransactions();

        // Convert to observable list
        ObservableList<TransactionViewModel> transactions = FXCollections.observableArrayList(transactionList);

        // Set table column cell factories to display data (no editing)
        extIdCol.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : String.valueOf(item));
            }
        });

        extTitleCol.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item);
            }
        });

        extNameCol.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item);
            }
        });

        extBorrowCol.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.toString());
            }
        });

        extDueCol.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.toString());
            }
        });

        extReturnCol.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : (item != null ? item.toString() : "Not returned"));
            }
        });

        // Set table data
        extTransactionTable.setItems(transactions);

        System.out.println("Transaction table initialized successfully.");
    }

    private void loadTransactionsData() {
        List<TransactionViewModel> transactions = dbasecon.getAllTransactions();
        mainTable.setItems(FXCollections.observableArrayList(transactions));
        updateButtonStates(); // Initial button state
        System.out.println("Transactions data initialized.");
    }

    private void setUpTableSelectionListener() {
        try {
            mainTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
                if (newSel != null) {
                    extTransactionTable.setItems(FXCollections.observableArrayList(newSel));
                } else {
                    extTransactionTable.getItems().clear();
                }
                updateButtonStates(); // Update button states on selection change
            });
            System.out.println("Table Listener initialized.");
        } catch (Exception e) {
            System.out.println("Table listener error: " + e);
        }
    }

    private void updateButtonStates() {

        TransactionViewModel selectedTransaction = mainTable.getSelectionModel().getSelectedItem();
        boolean hasSelection = selectedTransaction != null;
        boolean hasReturnDate = hasSelection && selectedTransaction.getReturnDate() != null;

        refreshButton.setDisable(false);

        boolean hasSelection = mainTable.getSelectionModel().getSelectedItem() != null;
        refreshButton.setDisable(false); // Always enable refresh
      
        bookBtn.setDisable(!hasSelection);
        memBtn.setDisable(!hasSelection);
        statusBtn.setDisable(!hasSelection || hasReturnDate);
        returnBtn.setDisable(!hasSelection || hasReturnDate);
    }

    @FXML
    public void refreshTables() {
        loadTransactionsData();
    }

    @FXML
    public void handleSearchFilterSort() throws SQLException {
        String searchTitle = searchFld.getText();
        String searchName = searchFld.getText();
        String filterStatus = statusBox.getValue();

        List<TransactionViewModel> filteredTransactions = dbasecon.searchFilterSortTransactions(searchTitle, searchName, filterStatus);
        mainTable.setItems(FXCollections.observableArrayList(filteredTransactions));

        System.out.println(filteredTransactions);
        System.out.println("Clicked button");
    }

    @FXML
    private void handleStatusUpdate() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mimirlib/mimir/statusUpdateForm.fxml"));
            Parent root = loader.load();

            StatusFormController statusController = loader.getController();
            if (statusController == null) {
                throw new IllegalStateException("Failed to load StatusFormController");
            } else {
                statusController.setTransactionController(this);
            }


            TransactionViewModel selectedTransaction = mainTable.getSelectionModel().getSelectedItem();
            if (selectedTransaction == null) {
                System.out.println("No transaction selected.");
                return;
            }

            int bookId = selectedTransaction.getBookId();
            String currentStatus = selectedTransaction.getBookStatus();

            statusController.setSelectedTransaction(selectedTransaction);
            statusController.setCurrentStatus(currentStatus);

            Stage statusStage = new Stage();
            statusStage.setTitle("Update Status");
            statusStage.setScene(new Scene(root));
            statusStage.initModality(Modality.APPLICATION_MODAL);
            statusStage.showAndWait();

            // Get updated status from the form
            String updatedStatus = statusController.getUpdatedStatus();
            if (updatedStatus != null && !updatedStatus.equals(currentStatus)) {

                // Retrieve corresponding StatusID from the database
                int statusId = dbasecon.getBookStatuses().stream()
                        .filter(status -> status.getStatus().equals(updatedStatus))
                        .map(BookStatus::getStatusId)
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Invalid book status selected"));

                // Use `updateBookStatus()` instead of direct DB calls
                dbasecon.updateBookStatus(new BookStatus(updatedStatus, statusId), "Book");

                // Refresh transactions after status update
                loadTransactionsData();
                System.out.println("Book status updated successfully to: " + updatedStatus);
            } else {
                System.out.println("No status change detected.");
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error updating book status", e);
        }
    }

    @FXML
    public void handleReturnBook() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mimirlib/mimir/dateForm.fxml"));
            Parent root = loader.load();

            DateFormController dateFormController = loader.getController();
            dateFormController.setTransactionController(this);

            Stage returnStage = new Stage();
            returnStage.setTitle("Return Book");
            returnStage.setScene(new Scene(root));
            returnStage.initModality(Modality.APPLICATION_MODAL);
            returnStage.showAndWait();

            LocalDate returnDate = dateFormController.getSelectedDate();
            if (returnDate != null) {
                System.out.println("Return date selected: " + returnDate);
                TransactionViewModel selectedTransaction = mainTable.getSelectionModel().getSelectedItem(); // Get selected transaction again
                if (selectedTransaction != null) {

                    updateBookReturn(returnDate, selectedTransaction.getTransactionId(), selectedTransaction.getBookId());
                    updateBookReturn(returnDate, selectedTransaction.getTransactionId()); // Get borrowId from table

                } else {
                    System.err.println("Error: No transaction selected after date form.");
                }

            } else {
                System.out.println("No return date selected.");
            }

        } catch (IOException | SQLException e) {
            System.err.println("Error loading dateForm.fxml: " + e.getMessage());
        }
    }


    private void updateBookReturn(LocalDate returnDate, int transactionId, int bookId) throws SQLException {

    /**
     * FIX TRANSACTION SHIT!!!!
     * @param returnDate
     * @param transactionId
     * @throws SQLException
     */

    private void updateBookReturn(LocalDate returnDate, int transactionId) throws SQLException {

        TransactionViewModel selectedTransaction = mainTable.getSelectionModel().getSelectedItem();
        if (selectedTransaction == null) {
            System.err.println("Error: No transaction selected.");
            return;
        }


        int returnedStatusId = dbasecon.getTransactionStatuses().stream()  // Use getTransactionStatuses()
                .filter(status -> status.getStatus().equalsIgnoreCase("Returned"))

        int bookId = selectedTransaction.getBookId();

        // Update the borrow record with the return date
        dbasecon.updateBorrow(selectedTransaction);

        // Retrieve StatusID for "Available"
        int availableStatusId = dbasecon.getBookStatuses().stream()
                .filter(status -> status.getStatus().equals("Available"))

                .map(BookStatus::getStatusId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid status"));


        int availableStatusId = dbasecon.getBookStatuses().stream()  // Use getTransactionStatuses()
                .filter(status -> status.getStatus().equalsIgnoreCase("Available"))
                .map(BookStatus::getStatusId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid status"));

        dbasecon.updateBorrow(selectedTransaction, returnDate, returnedStatusId);

        // Update book status using StatusID
        dbasecon.updateBookStatus(bookId, availableStatusId);

        // Refresh transactions after update
        loadTransactionsData();
        System.out.println("Book return recorded successfully.");
    }

    @FXML
    public void handleViewBook() {
        TransactionViewModel selectedTransaction = mainTable.getSelectionModel().getSelectedItem();
        if (selectedTransaction != null) {
            int bookId = selectedTransaction.getBookId();
            if (mainController != null && bookId > 0) {
                mainController.showBooks(); // Call showBooks from MainController
                bookController.selectBook(bookId);  // NEW: Select the book
            } else {
                System.err.println("Error: MainController not set or invalid bookId.");
            }
        } else {
            System.err.println("Error: No transaction selected to view book.");
        }
    }

    @FXML
    public void handleViewMember() {
        TransactionViewModel selectedTransaction = mainTable.getSelectionModel().getSelectedItem();
        if (selectedTransaction != null) {
            int memberId = selectedTransaction.getMemberId();
            if (mainController != null && memberId > 0) {
                mainController.showMembers(); // Call showMembers from MainController
                memberController.selectMember(memberId); // NEW: Select the member
            } else {
                System.err.println("Error: MainController not set or invalid memberId.");
            }
        } else {
            System.err.println("Error: No transaction selected to view member.");
        }
    }

    // Unrelated to the UI stuff
    public void setControllers(MemberController memberController, BookController bookController, MainController mainController) {
        this.memberController = memberController;
        this.bookController = bookController;
        this.mainController = mainController;
        logger.info("MemberController set: " + (this.memberController != null));
        logger.info("BookController set: " + (this.bookController != null));
        logger.info("MainController set: " + (this.mainController != null));
        System.out.println("TransactionController.setControllers() called");
    }

    private void autoUpdateBookStatus() {
        List<TransactionViewModel> transactions = dbasecon.getAllTransactions();
        LocalDate currentDate = LocalDate.now();

        for (TransactionViewModel transaction : transactions) {
            LocalDate dueDate = transaction.getDueDate();
            LocalDate returnDate = transaction.getReturnDate(); // Added return date check
            int id = transaction.getTransactionId();
            int currentStatusId = transaction.getStatusId();


            int overdueStatusId = dbasecon.getTransactionStatuses().stream()
                    .filter(status -> status.getStatus().equalsIgnoreCase("Overdue"))

            // Retrieve StatusID for "Overdue"
            int overdueStatusId = dbasecon.getBookStatuses().stream()
                    .filter(status -> status.getStatus().equals("Overdue"))

                    .map(BookStatus::getStatusId)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Invalid overdue status"));


            // Ensure the book is overdue AND has not been returned
            if (dueDate != null && currentDate.isAfter(dueDate) && returnDate == null && currentStatusId != overdueStatusId) {
                dbasecon.updateBorrowStatus(id, overdueStatusId);
                System.out.println("Book ID " + id + " marked as Overdue.");

            // If past due and not already marked as Overdue, update status
            if (dueDate != null && currentDate.isAfter(dueDate) && currentStatusId != overdueStatusId) {
                dbasecon.updateBookStatus(bookId, overdueStatusId);
                System.out.println("Book ID " + bookId + " marked as Overdue.");

            }
        }

        // Refresh table data after updates
        loadTransactionsData();
    }

    public void saveBorrowTransaction(int bookId, int memberId, LocalDate borrowDate, LocalDate dueDate, DateFormController dateFormController) {
        System.out.println("TransactionController.saveBorrowTransaction() called");
        System.out.println(" bookId: " + bookId + ", memberId: " + memberId + ", borrowDate: " + borrowDate + ", dueDate: " + dueDate);

        try {
            if (isDataValid(bookId, memberId, borrowDate, dueDate)) {
                // Retrieve StatusID for "Borrowed"
                int borrowedStatusId = dbasecon.getBookStatuses().stream()
                        .filter(status -> status.getStatus().equals("Borrowed"))
                        .map(BookStatus::getStatusId)
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Invalid borrowed status"));

                persistBorrowTransaction(bookId, memberId, borrowDate, dueDate, borrowedStatusId);
                refreshTables();
            }
            logger.info("TransactionController - dateFormController: " + dateFormController);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error in saveBorrowTransaction", e);
            System.out.println("Transaction error: " + e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "General error in saveBorrowTransaction", e);
            System.out.println("Transaction error: " + e.getMessage());
        }
    }

    private boolean isDataValid(int bookId, int memberId, LocalDate borrowDate, LocalDate dueDate) throws SQLException {
        System.out.println("TransactionController.isDataValid() called");
        if (memberController == null || bookController == null) {
            logger.severe("Required controllers not set.");
            return false;
        }
        if (bookId == 0 || memberId == 0) {
            logger.severe("No member or book selected.");
            return false;
        }
        if (borrowDate == null || dueDate == null) {
            logger.severe("Borrow date or due date is not set.");
            return false;
        }
        System.out.println(" Data is valid");
        return true;
    }


    private boolean canBorrowAgain(int bookId, int memberId) throws SQLException {
        return dbasecon.getAllTransactions().stream()
                .filter(t -> t.getBookId() == bookId && t.getMemberId() == memberId)
                .noneMatch(t -> t.getReturnDate() == null); // Allows borrowing only if previous loan is still active
    }

    //private boolean

    private void persistBorrowTransaction(int bookId, int memberId, LocalDate borrowDate, LocalDate dueDate, int statusId) throws SQLException {
        System.out.println("Adding new borrow record");
        dbasecon.borrowInput(bookId, memberId, borrowDate, dueDate, null, statusId);
    }

}