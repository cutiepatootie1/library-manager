package com.mimirlib.mimir.Controller;

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
    private TextField searchFld;
    @FXML
    private ChoiceBox<String> categoryBox;
    @FXML
    private ChoiceBox<String> genreBox;
    @FXML
    private ChoiceBox<String> statusBox;

    // Table names
    private String book = "bookstatus";

    @FXML
    public void initialize() throws SQLException {
        System.out.println("Initializing...");
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
        editInitialize();

        initializeGenre();
        initializeCats();
        initializeStatus();

    }
//FOR EXTENDED BOOK INFO

//OTHER THINGS TO INITIALIZE


/**
 * CORE FUNCTIONALITIES
 * HERE!!
 * */

    @FXML
    public void add(MouseEvent event) throws SQLException {
        System.out.println("clicked add");
        initialize();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mimirlib/mimir/addBook.fxml"));
            Parent root = loader.load();

            AddBookController modalController = loader.getController();
            modalController.initializeGenre();
            modalController.initializeCats();

            stage = new Stage();
            stage.setTitle("New book");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL); // Block input to parent window
            stage.initOwner(((Node) event.getSource()).getScene().getWindow()); // set parent
            stage.showAndWait();

            refreshTables();

        }catch (Exception e){
            logger.log(Level.SEVERE,"Error opening add window", e);
        }
    }

    public void refreshTables() throws SQLException {
        System.out.println("refreshing...");
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
        editInitialize();
    }


    @FXML
    private void editInitialize() throws SQLException{
        extBookTable.setEditable(true);
        List<String> catList = dbasecon.getAllCategories();
        List<String> genList = dbasecon.getAllGenre();
        List<String> statusList = dbasecon.getAllStatus(book);


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

    @FXML
    public void deleteBook(ActionEvent event)throws SQLException{
        Book selectedItem = mainTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null){
            long selectedId = selectedItem.getId();
            System.out.println("selected id:" + selectedId);
            dbasecon.deleteBookById(selectedId);
            refreshTables();
        }
    }

    @FXML
    public void filterBooks(ActionEvent event){
        String searchTitle = searchFld.getText();
        String searchAuthor = searchFld.getText();
        String categoryFilter = categoryBox.getValue() != null ? categoryBox.getValue().split(" ")[0] : "";
        String genreFilter = genreBox.getValue() != null ? genreBox.getValue().split(" ")[0] : "";
        String statusFilter = statusBox.getValue() != null ? statusBox.getValue(): "";

        List<Book> filteredBooks = dbasecon.viewBooksWithFilters(searchTitle, searchAuthor, categoryFilter, genreFilter, statusFilter);
        mainTable.setItems(FXCollections.observableArrayList(filteredBooks));

        System.out.println(filteredBooks);

        System.out.println("CLicked button");

    }

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

    public void initializeStatus() throws SQLException{
        List<String> statList = dbasecon.getAllStatus(book);

        ObservableList<String> genres = FXCollections.observableArrayList(statList);
        System.out.println("Genre List is null: " + (statusBox == null));  // Debugging line

        if (statusBox != null) {
            statusBox.setItems(genres);
        }else{
            System.out.println("status box items is still null. Check your FXML");
        }

    }


    /**
     * EVENT HANDLERS
    */

}
