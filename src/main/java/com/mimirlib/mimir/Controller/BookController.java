package com.mimirlib.mimir.Controller;

import java.io.IOException;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;


import com.mimirlib.mimir.Data.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class BookController {
    private static final Logger logger = Logger.getLogger(BookController.class.getName());
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private TextField titlefld;
    @FXML
    private TextField authorfld;
    @FXML
    private TextField catfld;
    @FXML
    private TextField genrefld;
    @FXML
    private ListView<String> booksList;
    @FXML
    private ChoiceBox<String> categoryBox;
    @FXML
    private ChoiceBox<String> genreBox;

    DatabaseConnection dbasecon = new DatabaseConnection();

    public BookController() throws SQLException {
    }

    //INITIALIZE SHIT HERE
    @FXML
    public void initialize() throws SQLException {
        List<String> books = dbasecon.getAllBooks();
        ObservableList<String> bookList = FXCollections.observableArrayList(books);
        System.out.println("booksList is null: " + (booksList == null));  // Debugging line

        if (booksList != null) {
            booksList.setItems(bookList);
        } else {
            System.out.println("ListView is still null. Check your FXML.");
        }
        initializeCats();
        initializeGenre();

    }

    @FXML
    public void initializeCats() throws SQLException{
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
    public void add(MouseEvent event) throws IOException, SQLException {
        System.out.println("clicked add");
//        URL url = getClass().getResource("/com/mimirlib/mimir/addBook.fxml");
//        System.out.println("FXML URL: " + url);

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/mimirlib/mimir/addBook.fxml"));

            stage = new Stage();
            stage.setTitle("New book");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL); // Block input to parent window
            stage.initOwner(((Node) event.getSource()).getScene().getWindow()); // set parent
            stage.showAndWait();
            initialize();
        }catch (Exception e){
            logger.log(Level.SEVERE,"Error fetching books", e);
        }

    }


    @FXML
    private void addProcess(ActionEvent event){

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
