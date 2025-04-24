package com.mimirlib.mimir.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane; // Import BorderPane

import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainController {

    @FXML
    private BorderPane contentArea; // fx:id is now a BorderPane

    private Parent bookView;
    private Parent memberView;

    private static final Logger logger = Logger.getLogger(MainController.class.getName());

    @FXML
    public void initialize() {
        try {
            bookView = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/mimirlib/mimir/book.fxml")));
            memberView = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/mimirlib/mimir/member.fxml")));
            showBooks(); // Show books by default
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error loading FXML", e);
        }
    }

    @FXML
    public void showBooks() {
        contentArea.setCenter(bookView); // Set in the Center
    }

    @FXML
    public void showMembers() {
        contentArea.setCenter(memberView); // Set in the Center
    }

}