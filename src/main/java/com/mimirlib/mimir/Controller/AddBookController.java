package com.mimirlib.mimir.Controller;

import com.mimirlib.mimir.Data.BookCategory;
import com.mimirlib.mimir.Data.BookGenre;
import com.mimirlib.mimir.Data.BookStatus;
import com.mimirlib.mimir.Data.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class AddBookController {

    private static final Logger logger = Logger.getLogger(AddBookController.class.getName());

    DatabaseConnection dbasecon = new DatabaseConnection();
    @FXML
    private TextField titlefld;
    @FXML
    private TextField authorfld;
    @FXML
    private ChoiceBox<String> categoryBox;
    @FXML
    private ChoiceBox<String> genreBox;

    @FXML
    public void initializeCats() {
        List<BookCategory> catList = dbasecon.getAllCategories();

        // Convert to a list containing only the formatted code-name pair
        List<String> formattedCategories = catList.stream()
                .map(cat -> cat.getCategoryCode() + " - " + cat.getCategoryName())
                .collect(Collectors.toList());

        ObservableList<String> categories = FXCollections.observableArrayList(formattedCategories);
        System.out.println("Category List is null: " + (categoryBox == null)); // Debugging line

        if (categoryBox != null) {
            categoryBox.setItems(categories);
        } else {
            System.out.println("category box items is still null. Check your FXML");
        }
    }

    @FXML
    public void initializeGenre() {
        List<BookGenre> genList = dbasecon.getAllGenres();

        // Convert to a list containing only the formatted code-name pair
        List<String> formattedGenres = genList.stream()
                .map(cat -> cat.getGenreCode() + " - " + cat.getGenreName())
                .collect(Collectors.toList());

        ObservableList<String> genres = FXCollections.observableArrayList(formattedGenres);
        System.out.println("Genre List is null: " + (genreBox == null));  // Debugging line

        if (genreBox != null) {
            genreBox.setItems(genres);
        }else{
            System.out.println("genre box items is still null. Check your FXML");
        }

    }

    @FXML
    private void addProcess(ActionEvent event) {
        String title = titlefld.getText();
        String author = authorfld.getText();
        String selectedCategory = categoryBox.getValue();
        String selectedGenre = genreBox.getValue();
        String selectedStatus = "Available";  // Default status

        try {
            // Retrieve the corresponding CategoryID
            int categoryId = dbasecon.getAllCategories().stream()
                    .filter(cat -> (cat.getCategoryCode() + " - " + cat.getCategoryName()).equals(selectedCategory))
                    .map(BookCategory::getCategoryId)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Invalid category selected"));

            // Retrieve the corresponding GenreID
            int genreId = dbasecon.getAllGenres().stream()
                    .filter(genre -> (genre.getGenreCode() + " - " + genre.getGenreName()).equals(selectedGenre))
                    .map(BookGenre::getGenreId)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Invalid genre selected"));

            // Retrieve the corresponding StatusID
            int statusId = dbasecon.getBookStatuses().stream()
                    .filter(status -> status.getStatus().equals(selectedStatus))
                    .map(BookStatus::getStatusId)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Invalid status selected"));

            // Execute book input procedure with IDs instead of names
            dbasecon.bookInput(title, author, categoryId, genreId, statusId);

            // Close the stage
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error adding book: " + e.getMessage(), e);
        }
    }

    @FXML
    private void handleClose(ActionEvent event){
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
