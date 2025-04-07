package com.mimirlib.mimir.Controller;

import com.mimirlib.mimir.Data.Book;
import com.mimirlib.mimir.Data.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class AddModalController {
    private static final Logger logger = Logger.getLogger(AddModalController.class.getName());
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
    public void initializeCats() throws SQLException {
        List<String> catList = dbasecon.getAllCategories();


        ObservableList<String> categories = FXCollections.observableArrayList(catList);
        System.out.println("Category List is null: " + (categoryBox == null));  // Debugging line

        if (categoryBox != null) {
            categoryBox.setItems(categories);
        }else{
            System.out.println("category box items is still null. Check your FXML");
        }

    }

    @FXML
    public void initializeGenre() throws SQLException{
        List<String> genList = dbasecon.getAllGenre();

        ObservableList<String> genres = FXCollections.observableArrayList(genList);
        System.out.println("Genre List is null: " + (genreBox == null));  // Debugging line

        if (genreBox != null) {
            genreBox.setItems(genres);
        }else{
            System.out.println("genre box items is still null. Check your FXML");
        }

    }

    @FXML
    private void addProcess(ActionEvent event) throws SQLException {

        String title = titlefld.getText();
        String author = authorfld.getText();
        String category = categoryBox.getValue().split(" ")[0];
        String genre = genreBox.getValue().split(" ")[0];
        String status = "Available";

        dbasecon.bookInput(false, title, author, category, genre,status);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();

    }

    @FXML
    private void handleClose(ActionEvent event){
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
