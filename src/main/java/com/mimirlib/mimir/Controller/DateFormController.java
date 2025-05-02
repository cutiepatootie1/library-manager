package com.mimirlib.mimir.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.time.LocalDate;

public class DateFormController {

    @FXML
    DatePicker datePicker;

    private LocalDate selectedDate;

    private TransactionController transactionController; // This will hold the reference

    public void setTransactionController(TransactionController transactionController) {
        this.transactionController = transactionController; // Receive the reference
        System.out.println("TransactionController set: " + (this.transactionController != null));
    }

    @FXML
    private void handleConfirm(ActionEvent event) {
        try {
            if (datePicker != null) {
                selectedDate = datePicker.getValue();
            }
            if (selectedDate == null) {
                throw new IllegalStateException("No date selected.");
            }
            System.out.println("Selected date saved: " + selectedDate);
            handleClose(event); // Close the window after confirming
        } catch (IllegalStateException e) {
            System.err.println("Error getting selected date: " + e.getMessage());
            // Optionally, show an alert to the user
        }
    }

    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    public LocalDate getBorrowDate() {
        return LocalDate.now();
    }

    @FXML
    private void handleClose(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}