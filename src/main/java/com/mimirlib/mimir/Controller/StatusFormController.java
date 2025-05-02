package com.mimirlib.mimir.Controller;

import com.mimirlib.mimir.Data.BookStatus;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.mimirlib.mimir.Data.TransactionViewModel;

public class StatusFormController {

    private static final Logger logger = Logger.getLogger(StatusFormController.class.getName());

    @FXML
    private ChoiceBox<String> statusBox;
    @FXML
    private Button confirmStatusBtn; // Corrected ID

    private TransactionController transactionController;
    private TransactionViewModel selectedTransaction;
    private String currentStatus;

    public void setTransactionController(TransactionController transactionController) {
        this.transactionController = transactionController;
    }

    public void setSelectedTransaction(TransactionViewModel selectedTransaction) {
        this.selectedTransaction = selectedTransaction;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
        statusBox.setValue(currentStatus);
    }

    public String getUpdatedStatus() {
        return currentStatus;
    }

    public void initialize() {
        statusBox.getItems().addAll("Borrowed", "Overdue", "Available");
    }

    @FXML
    private void handleConfirm() {
        String selectedStatus = statusBox.getValue(); // Get the selected status name

        if (selectedStatus != null && !selectedStatus.isEmpty()) {
            try {
                // Retrieve corresponding StatusID for selected status name
                int statusId = transactionController.dbasecon.getBookStatuses().stream()
                        .filter(status -> status.getStatus().equals(selectedStatus))
                        .map(BookStatus::getStatusId)
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Invalid book status selected"));

                selectedTransaction.setStatusId(statusId);  // Set the StatusID
                selectedTransaction.setBookStatus(selectedStatus); // Maintain readable name for UI

                // Update book status in the database using BookID and StatusID
                transactionController.dbasecon.updateBookStatus(selectedTransaction.getBookId(), statusId);
                System.out.println("Transaction and book status updated to: " + selectedStatus);

                // Close the status update window
                Stage stage = (Stage) confirmStatusBtn.getScene().getWindow();
                stage.close();

            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error updating transaction and book status", e);
                System.err.println("Database error: " + e.getMessage());
            }
        } else {
            System.out.println("Please select a valid status.");
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) confirmStatusBtn.getScene().getWindow(); // Corrected ID
        stage.close();
    }
}