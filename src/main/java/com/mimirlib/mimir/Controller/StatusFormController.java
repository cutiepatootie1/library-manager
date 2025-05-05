package com.mimirlib.mimir.Controller;

import com.mimirlib.mimir.Data.BookStatus;
import com.mimirlib.mimir.Data.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.mimirlib.mimir.Data.TransactionViewModel;
import java.util.List;
import java.util.stream.Collectors;

public class StatusFormController {

    private static final Logger logger = Logger.getLogger(StatusFormController.class.getName());
    final DatabaseConnection dbasecon = new DatabaseConnection();

    @FXML
    private ChoiceBox<String> statusBox;
    @FXML
    private Button confirmStatusBtn;

    private TransactionController transactionController;
    private TransactionViewModel selectedTransaction;
    private String currentStatus;

//    public StatusFormController() {
//        setTransactionController();
//    }

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
        return statusBox.getValue();
    }

    public void initialize() {
        loadTransactionStatuses();
    }

    private void loadTransactionStatuses() {
        try {
            List<BookStatus> transactionStatusList = dbasecon.getTransactionStatuses(); // Use the provided method
            List<String> statusNames = transactionStatusList.stream()
                    .map(BookStatus::getStatus)
                    .collect(Collectors.toList());

            statusBox.getItems().addAll(statusNames);

            if (currentStatus != null) {
                statusBox.setValue(currentStatus);
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error loading transaction statuses", e);
            System.err.println("Error loading transaction statuses: " + e.getMessage());
        }
    }

    @FXML
    private void handleConfirm() {
        String selectedStatusName = statusBox.getValue();

        if (selectedStatusName != null && !selectedStatusName.isEmpty()) {
            try {
                int statusId = transactionController.dbasecon.getTransactionStatuses().stream() // Use the provided method
                        .filter(status -> status.getStatus().equalsIgnoreCase(selectedStatusName))
                        .map(BookStatus::getStatusId)
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Invalid transaction status selected"));

                selectedTransaction.setStatusId(statusId);
                selectedTransaction.setBookStatus(selectedStatusName);

                transactionController.dbasecon.updateBorrowStatus(selectedTransaction.getTransactionId(), statusId);
                System.out.println("Transaction status updated to: " + selectedStatusName);

                Stage stage = (Stage) confirmStatusBtn.getScene().getWindow();
                stage.close();

            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error updating transaction status", e);
                System.err.println("Database error: " + e.getMessage());
            }
        } else {
            System.out.println("Please select a valid status.");
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) confirmStatusBtn.getScene().getWindow();
        stage.close();
    }
}