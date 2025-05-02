package com.mimirlib.mimir.Controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.mimirlib.mimir.Data.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


import javafx.event.ActionEvent;
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

    DatabaseConnection dbasecon = new DatabaseConnection();

    private int selectedBookId;


    public BookController() {
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
    @FXML
    private Button refreshButton;
    @FXML
    private Button borrowBtn;
    @FXML
    private Button deleteBtn;
    @FXML
    private Button resetBtn;


    public void initialize() throws SQLException {
        System.out.println("Initializing...");
        initializeTableColumns();
        loadBooksData();
        setupTableSelectionListener();

        editInitialize();
        initializeGenre();
        initializeCats();
        initializeStatus();
        refreshTables();

        // Initially disable the buttons
        borrowBtn.setDisable(true);
        deleteBtn.setDisable(true);
        resetBtn.setDisable(true);
    }

    private void initializeTableColumns() {
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        titleColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        authColumn.setCellValueFactory(cellData -> cellData.getValue().authorProperty());

        extIdCol.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        extTitleCol.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        extAuthCol.setCellValueFactory(cellData -> cellData.getValue().authorProperty());
        extCatCol.setCellValueFactory(cellData -> cellData.getValue().categoryNameProperty());
        extGenCol.setCellValueFactory(cellData -> cellData.getValue().genreNameProperty());
        extStatus.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
    }

    private void setupTableSelectionListener() {
        mainTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                extBookTable.setItems(FXCollections.observableArrayList(newSel));
                borrowBtn.setDisable(false);
                deleteBtn.setDisable(false);
                resetBtn.setDisable(false);
            } else {
                extBookTable.getItems().clear();
                borrowBtn.setDisable(true);
                deleteBtn.setDisable(true);
                resetBtn.setDisable(true);
            }

            // Dynamically update refresh button state
            updateRefreshButtonState();
        });
    }

    @FXML
    private void editInitialize() throws SQLException{
        extBookTable.setEditable(true);
        // Fetch categories as BookCategory objects with ID, Code, and Name
        List<BookCategory> catList = dbasecon.getAllCategories();

        // Fetch genres as BookGenre objects with ID, Code, and Name
        List<BookGenre> genList = dbasecon.getAllGenres();

        // Fetch statuses as BookStatus objects with ID and Status Name
        List<BookStatus> statusList = dbasecon.getBookStatuses();


        // Convert lists to ObservableList for UI binding
        ObservableList<String> categories = FXCollections.observableArrayList(
                catList.stream()
                        .map(cat -> cat.getCategoryCode() + " - " + cat.getCategoryName())
                        .collect(Collectors.toList())
        );

        ObservableList<String> genres = FXCollections.observableArrayList(
                genList.stream()
                        .map(gen -> gen.getGenreCode() + " - " + gen.getGenreName())
                        .collect(Collectors.toList())
        );

        ObservableList<String> status = FXCollections.observableArrayList(
                statusList.stream()
                        .map(stat -> stat.getStatus()) // Only display status name
                        .collect(Collectors.toList())
        );

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
            book.authorProperty().set(event.getNewValue());
            dbasecon.updateBook(book);
        });

        extCatCol.setOnEditCommit(event -> {
            Book book = event.getRowValue();
            book.idProperty().getValue();
            book.categoryNameProperty().set(event.getNewValue().split(" ")[0]);
            dbasecon.updateBook(book);
        });

        extGenCol.setOnEditCommit(event -> {
            Book book = event.getRowValue();
            book.idProperty().getValue();
            book.genreNameProperty().set(event.getNewValue().split(" ")[0]);
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
                .map(gen -> gen.getGenreCode() + " - " + gen.getGenreName())
                .collect(Collectors.toList());

        ObservableList<String> genres = FXCollections.observableArrayList(formattedGenres);
        System.out.println("Genre List is null: " + (genreBox == null));  // Debugging line

        if (genreBox != null) {
            genreBox.setItems(genres);
        }else{
            System.out.println("genre box items is still null. Check your FXML");
        }

    }

    public void initializeStatus() {
        List<BookStatus> statList = dbasecon.getBookStatuses();

        List<String> formattedStatusses = statList.stream()
                .map(gen -> gen.getStatus())
                .collect(Collectors.toList());

        ObservableList<String> genres = FXCollections.observableArrayList(formattedStatusses);
        System.out.println("Genre List is null: " + (statusBox == null));  // Debugging line

        if (statusBox != null) {
            statusBox.setItems(genres);
        }else{
            System.out.println("status box items is still null. Check your FXML");
        }

    }


    private void loadBooksData() {
        List<Book> books = dbasecon.getAllBooks();
        mainTable.setItems(FXCollections.observableArrayList(books));
    }

    @FXML
    public void refreshTables() throws SQLException {
        System.out.println("Refreshing book data...");

        // Store the currently selected book
        Book selectedBook = mainTable.getSelectionModel().getSelectedItem();

        // Reload books from the database
        List<Book> books = dbasecon.getAllBooks();
        ObservableList<Book> bookList = FXCollections.observableArrayList(books);
        mainTable.setItems(bookList);

        // Restore selection if book still exists
        if (selectedBook != null && bookList.contains(selectedBook)) {
            mainTable.getSelectionModel().select(selectedBook);
            extBookTable.setItems(FXCollections.observableArrayList(selectedBook));
        } else {
            extBookTable.getItems().clear(); // Clear extended table if no selection
        }

        // Update button states dynamically
        updateRefreshButtonState();
    }

    private void updateRefreshButtonState() {
        boolean hasSelection = mainTable.getSelectionModel().getSelectedItem() != null;
        refreshButton.setDisable(!hasSelection); // Disable when no selection exists
    }


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

            Stage stage = new Stage();
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

    @FXML
    public void deleteBook()throws SQLException{
        Book selectedItem = mainTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null){
            int selectedId = selectedItem.getId();
            System.out.println("selected id:" + selectedId);
            dbasecon.deleteBook(selectedId);
            refreshTables();
        }
    }

    @FXML
    public void filterBooks(){
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


    /**
     * EVENT HANDLERS
     */

    Book getSelectedBook() {
        return mainTable.getSelectionModel().getSelectedItem();
    }

    private MemberController memberController;
    private TransactionController transactionController;

    public void setMemberController(MemberController memberController) {
        this.memberController = memberController;
    }

    public void setTransactionController(TransactionController transactionController) {
        this.transactionController = transactionController;
    }

    public void selectBook(int bookId) {
        if (bookId <= 0) return; // Assuming negative or zero is invalid

        for (Book book : mainTable.getItems()) {
            if (Objects.equals(book.getId(), bookId)) {
                mainTable.getSelectionModel().select(book);
                mainTable.scrollTo(book);
                break;
            }
        }
    }

    @FXML
    public void handleBorrowBookAction(ActionEvent event) {
        try {
            Book selectedBook = getSelectedBook();
            if (selectedBook == null) {
                System.out.println("Please select a book to borrow.");
                return;
            }
            selectedBookId = selectedBook.getId();
            memberController.membersPanel(true);
            memberController.showMemberSelection(event);
            int selectedMemberId = memberController.getSelectedMemberId();
            if (selectedMemberId != 0) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mimirlib/mimir/dateForm.fxml"));
                Parent root = loader.load();
                DateFormController dateFormController = loader.getController();
                dateFormController.setTransactionController(transactionController); // Pass the reference

                Stage dateStage = new Stage();
                dateStage.setTitle("Set Borrow Date");
                dateStage.setScene(new Scene(root));
                dateStage.initModality(Modality.WINDOW_MODAL);
                dateStage.initOwner(((Node) event.getSource()).getScene().getWindow());
                dateStage.showAndWait();
                transactionController.saveBorrowTransaction(selectedBookId, selectedMemberId, dateFormController.getBorrowDate(), dateFormController.getSelectedDate(), dateFormController);
                memberController.membersPanel(false);
            } else {
                System.out.println("No member selected.");
                memberController.membersPanel(false);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error showing member selection", e);
        }
    }

}
