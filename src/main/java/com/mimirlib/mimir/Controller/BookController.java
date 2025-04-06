package com.mimirlib.mimir.Controller;

import java.io.IOException;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;


import com.mimirlib.mimir.Data.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class BookController {

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

    DatabaseConnection dbasecon = new DatabaseConnection();

    //INITIALIZE SHIT HERE
    @FXML
    public void initialize() throws SQLException {
        List<String> books = dbasecon.getAllBooks();
       ObservableList<String> bookList = FXCollections.observableArrayList(books);
       booksList.setItems(bookList);
    }



    @FXML
    public void add(MouseEvent event)throws IOException {
        System.out.println("clicked add");
        URL url = getClass().getResource("/com/mimirlib/mimir/addBook.fxml");
        System.out.println("FXML URL: " + url);

        Parent root = FXMLLoader.load(getClass().getResource("/com/mimirlib/mimir/addBook.fxml"));

        stage = new Stage();
        stage.setTitle("New book");
        stage.setScene(new Scene(root));
        stage.initModality(Modality.WINDOW_MODAL); // Block input to parent window
        stage.initOwner(((Node) event.getSource()).getScene().getWindow()); // set parent
        stage.showAndWait();

        System.out.println("Successfully added a new book!");
    }

    @FXML
    private void addProcess(ActionEvent event){

        String title = titlefld.getText();
        String author = authorfld.getText();
        String category = catfld.getText();
        String genre = genrefld.getText();
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
