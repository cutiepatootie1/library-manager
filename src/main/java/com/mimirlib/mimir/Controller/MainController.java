package com.mimirlib.mimir.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainController {

    @FXML
    private BorderPane contentArea;

    private Parent adminView;
    private Parent bookView;
    private Parent memberView;
    private Parent transactionView;

    private static final Logger logger = Logger.getLogger(MainController.class.getName());

    @FXML
    public void initialize() {

        try {
            URL fxmlResource = getClass().getResource("/com/mimirlib/mimir/admin2.fxml");

            if (fxmlResource != null) {
                FXMLLoader adminLoader = new FXMLLoader(fxmlResource);
                adminView = adminLoader.load();
                AdminController adminController = adminLoader.getController();
            } else {
                System.err.println("Error: admin.fxml not found!");
            }

//            FXMLLoader adminLoader = new FXMLLoader(getClass().getResource("com/mimirlib/mimir/admin.fxml"));
//            adminView = adminLoader.load();
//            AdminController adminController = adminLoader.getController();

            FXMLLoader bookLoader = new FXMLLoader(getClass().getResource("/com/mimirlib/mimir/book.fxml"));
            bookView = bookLoader.load();
            BookController bookController = bookLoader.getController();

            FXMLLoader memberLoader = new FXMLLoader(getClass().getResource("/com/mimirlib/mimir/member.fxml"));
            memberView = memberLoader.load();
            MemberController memberController = memberLoader.getController();
            memberController.membersPanel(false);

            FXMLLoader transactionLoader = new FXMLLoader(getClass().getResource("/com/mimirlib/mimir/transaction.fxml")); // Assuming your FXML is named transaction.fxml
            transactionView = transactionLoader.load();
            // Declare it as a class member
            TransactionController transactionController = transactionLoader.getController();
            transactionController.setControllers(memberController, bookController, this);  // Set dependencies ONCE

            bookController.setMemberController(memberController);
            bookController.setTransactionController(transactionController); // Pass the instance to BookController

            //If you have DateFormController initialized here, pass the instance
            //dateFormController.setTransactionController(transactionController);

            showBooks();

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error loading FXML", e);
        }
    }

    @FXML
    public void showAdmin() {
        contentArea.setCenter(adminView);
    }

    @FXML
    public void showBooks() {
        contentArea.setCenter(bookView);
    }

    @FXML
    public void showMembers() {
        contentArea.setCenter(memberView);
    }

    @FXML
    public void showTransactions() {
        contentArea.setCenter(transactionView);
    }

}