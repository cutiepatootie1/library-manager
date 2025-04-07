package com.mimirlib.mimir.Controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mimirlib.mimir.Data.Book;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;


import com.mimirlib.mimir.Data.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class BookController {
    private static final Logger logger = Logger.getLogger(BookController.class.getName());
    private Stage stage;
    private Scene scene;
    private Parent root;
//from add book modal
    @FXML
    private TextField titlefld;
    @FXML
    private TextField authorfld;
    @FXML
    private ChoiceBox<String> categoryBox;
    @FXML
    private ChoiceBox<String> genreBox;

    //FROM edit book modal
    @FXML
    private TextField editTitle;
    @FXML
    private TextField editAuthor;
    @FXML
    private ChoiceBox<String> editCat;
    @FXML
    private ChoiceBox<String> editGenre;

    DatabaseConnection dbasecon = new DatabaseConnection();

    public BookController() throws SQLException {
    }

    //INITIALIZE SHIT HERE
    @FXML
    private TableView<Book> mainTable;
    @FXML
    private TableColumn<Book, Number> idColumn;
    @FXML
    private TableColumn<Book, String> titleColumn;
    @FXML
    private TableColumn<Book, String> authColumn;
    @FXML
    private TableView<Book> extBookTable;
    @FXML
    private TableColumn<Book, Number> extIdCol;
    @FXML
    private TableColumn<Book, String> extTitleCol;
    @FXML
    private TableColumn<Book, String> extAuthCol;
    @FXML
    private TableColumn<Book, String> extCatCol;
    @FXML
    private TableColumn<Book, String> extGenCol;
    @FXML
    private TableColumn<Book, String> extStatus;


    @FXML
    public void initialize() throws SQLException {
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        titleColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        authColumn.setCellValueFactory(cellData -> cellData.getValue().authProperty());

        extIdCol.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        extTitleCol.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        extAuthCol.setCellValueFactory(cellData -> cellData.getValue().authProperty());
        extCatCol.setCellValueFactory(cellData -> cellData.getValue().catProperty());
        extGenCol.setCellValueFactory(cellData -> cellData.getValue().genreProperty());
        extStatus.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

        List<Book> books = dbasecon.getAllBooks();
        mainTable.setItems(FXCollections.observableArrayList(books));

        mainTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
                    if (newSel != null) {
                        extBookTable.setItems(FXCollections.observableArrayList(newSel));
                    }
        });
        initializeCats();
        initializeGenre();
        editInitialize();

    }
//FOR EXTENDED BOOK INFO

//OTHER THINGS TO INITIALIZE
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

/**
 * CORE FUNCTIONALITIES
 * HERE!!
 * */

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
    private void editInitialize() throws SQLException{
        extBookTable.setEditable(true);
        List<String> catList = dbasecon.getAllCategories();
        List<String> genList = dbasecon.getAllGenre();
        List<String> statusList = dbasecon.getAllStatus();


        ObservableList<String> categories = FXCollections.observableArrayList(catList);
        ObservableList<String> genres = FXCollections.observableArrayList(genList);
        ObservableList<String> status = FXCollections.observableArrayList(statusList);


        extTitleCol.setCellFactory(TextFieldTableCell.forTableColumn());
        extAuthCol.setCellFactory(TextFieldTableCell.forTableColumn());
        extCatCol.setCellFactory(ChoiceBoxTableCell.forTableColumn(categories));
        extGenCol.setCellFactory(ChoiceBoxTableCell.forTableColumn(genres));
        extStatus.setCellFactory(ChoiceBoxTableCell.forTableColumn(status));

        extTitleCol.setOnEditCommit(event -> {
            Book book = event.getRowValue();
            book.idProperty().getValue();
            book.titleProperty().set(event.getNewValue());
            dbasecon.updateBook(book);
        });

        extAuthCol.setOnEditCommit(event -> {
            Book book = event.getRowValue();
            book.idProperty().getValue();
            book.authProperty().set(event.getNewValue());
            dbasecon.updateBook(book);
        });

        extCatCol.setOnEditCommit(event -> {
            Book book = event.getRowValue();
            book.idProperty().getValue();
            book.catProperty().set(event.getNewValue().split(" ")[0]);
            dbasecon.updateBook(book);
        });

        extGenCol.setOnEditCommit(event -> {
            Book book = event.getRowValue();
            book.idProperty().getValue();
            book.genreProperty().set(event.getNewValue().split(" ")[0]);
            dbasecon.updateBook(book);
        });

        extStatus.setOnEditCommit(event -> {
            Book book = event.getRowValue();
            book.idProperty().getValue();
            book.statusProperty().set(event.getNewValue());
            dbasecon.updateBook(book);
        });
    }


    /**
     * EVENT HANDLERS
    */

    @FXML
    private void handleClose(ActionEvent event){
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

}
