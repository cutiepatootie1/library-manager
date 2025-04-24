package com.mimirlib.mimir.Data;

import com.mimirlib.mimir.HelloApplication;

import java.sql.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;

public class DatabaseConnection {

    static Scanner scanner = new Scanner(System.in);
    static Connection connection;
    static Statement statement;
    static PreparedStatement preparedStatement;
    static ResultSet resultSet;
    static HelloApplication m = new HelloApplication();
    private static final Logger logger = Logger.getLogger(DatabaseConnection.class.getName());

    private final String databaseName = "library_db";
    private final String URL = "jdbc:mysql://localhost:3306/";
    private final String USER = "library";
    private final String PASSWORD = "root";

    public void Connect() throws SQLException {
        createDatabase();

        String fullUrl = URL + databaseName;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(fullUrl, USER, PASSWORD);
            createDatabase();
            createTables();
            createStoredProcedure();
            System.out.println("Succesfully connected to library_db");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void createDatabase() throws SQLException {
        try (Connection tempConnection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = tempConnection.createStatement()) {
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + databaseName);
        }
    }

    private void createTables() throws SQLException {
        String[] tableQueries = {
                "CREATE TABLE IF NOT EXISTS BookCategories (CategoryCode VARCHAR(10) PRIMARY KEY, CategoryName VARCHAR(255) NOT NULL UNIQUE)",
                "CREATE TABLE IF NOT EXISTS BookGenres (GenreCode VARCHAR(10) PRIMARY KEY, GenreName VARCHAR(255) NOT NULL UNIQUE)",
                "CREATE TABLE IF NOT EXISTS BookStatus (Status VARCHAR(50) NOT NULL UNIQUE, StatusID INT PRIMARY KEY AUTO_INCREMENT)",
                "CREATE TABLE IF NOT EXISTS MemberRoles (Role VARCHAR(50) NOT NULL UNIQUE, RoleID INT PRIMARY KEY AUTO_INCREMENT)",
                "CREATE TABLE IF NOT EXISTS MemberStatus (Status VARCHAR(50) NOT NULL UNIQUE, StatusID INT PRIMARY KEY AUTO_INCREMENT)",
                "CREATE TABLE IF NOT EXISTS Books (BookID BIGINT PRIMARY KEY AUTO_INCREMENT, Title VARCHAR(255) NOT NULL, Author VARCHAR(255), Quantity INT, CategoryCode VARCHAR(10) NOT NULL, GenreCode VARCHAR(10) NOT NULL, Status VARCHAR(50) NOT NULL, FOREIGN KEY (CategoryCode) REFERENCES BookCategories(CategoryCode), FOREIGN KEY (GenreCode) REFERENCES BookGenres(GenreCode), FOREIGN KEY (Status) REFERENCES BookStatus(Status))",
                "CREATE TABLE IF NOT EXISTS Members (MemberID INT PRIMARY KEY AUTO_INCREMENT, Name VARCHAR(255) NOT NULL, Email VARCHAR(255), PhoneNumber VARCHAR(15), MemberType VARCHAR(50) NOT NULL, Status VARCHAR(50) NOT NULL, FOREIGN KEY (MemberType) REFERENCES MemberRoles(Role), FOREIGN KEY (Status) REFERENCES MemberStatus(Status))",
                "CREATE TABLE IF NOT EXISTS Borrowing (BorrowID INT PRIMARY KEY AUTO_INCREMENT, BookID BIGINT NOT NULL, MemberID INT NOT NULL, BorrowDate DATE NOT NULL, DueDate DATE NOT NULL, ReturnDate DATE, FOREIGN KEY (BookID) REFERENCES Books(BookID), FOREIGN KEY (MemberID) REFERENCES Members(MemberID))"
        };

        try (Statement statement = connection.createStatement()) {
            for (String query : tableQueries) {
                statement.executeUpdate(query);
            }
        }
    }

    // STORED PROCEDURES
    private void createStoredProcedure() throws SQLException {
        String[] procedureQueries = {
                "DROP PROCEDURE IF EXISTS InsertBook",
                "DROP PROCEDURE IF EXISTS GetAllBooks",
                "DROP PROCEDURE IF EXISTS GetAllMembers",
                "DROP PROCEDURE IF EXISTS GetAllCategories",
                "DROP PROCEDURE IF EXISTS GetAllGenres",
                "DROP PROCEDURE IF EXISTS GetAllStatus",
                "DROP PROCEDURE IF EXISTS GetAllRoles",
                "DROP PROCEDURE IF EXISTS UpdateBook",
                "DROP PROCEDURE IF EXISTS InsertMember",
                "DROP PROCEDURE IF EXISTS UpdateMember",
                "DROP PROCEDURE IF EXISTS DeleteBook",
                "DROP PROCEDURE IF EXISTS DeleteMember",
                "DROP PROCEDURE IF EXISTS SearchFilterSortBooks",
                "DROP PROCEDURE IF EXISTS SearchFilterSortMembers",
                "CREATE PROCEDURE GetAllBooks() BEGIN SELECT BookID, Title, Author, CategoryCode, GenreCode, Status FROM books; END",
                "CREATE PROCEDURE GetAllMembers() BEGIN SELECT MemberID, Name, Email, PhoneNumber, MemberType, Status FROM members; END",
                "CREATE PROCEDURE GetAllGenres() BEGIN SELECT GenreCode, GenreName FROM bookgenres; END",
                "CREATE PROCEDURE GetAllCategories() BEGIN SELECT CategoryCode, CategoryName FROM bookcategories; END",
                "CREATE PROCEDURE GetAllStatus(IN entityType VARCHAR(50)) BEGIN IF entityType = 'bookstatus' THEN SELECT Status FROM bookstatus; ELSEIF entityType = 'memberstatus' THEN SELECT Status FROM memberstatus; ELSE SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Invalid entity type'; END IF; END",
                "CREATE PROCEDURE GetAllRoles() BEGIN SELECT Role from memberroles; END",
                "CREATE PROCEDURE InsertBook(IN bookTitle VARCHAR(255), IN bookAuthor VARCHAR(255), IN categoryCode VARCHAR(50), IN genreCode VARCHAR(50), IN statusName VARCHAR(50), OUT p_error_message VARCHAR(255)) BEGIN IF NOT EXISTS (SELECT 1 FROM BookCategories WHERE CategoryCode = categoryCode) THEN SET p_error_message = 'Invalid category code.'; ELSEIF NOT EXISTS (SELECT 1 FROM BookGenres WHERE GenreCode = genreCode) THEN SET p_error_message = 'Invalid genre code.'; ELSEIF NOT EXISTS (SELECT 1 FROM BookStatus WHERE Status = statusName) THEN SET p_error_message = 'Invalid book status.'; ELSEIF EXISTS (SELECT 1 FROM Books WHERE Title = bookTitle AND Author = bookAuthor) THEN SET p_error_message = 'Book already exists.'; ELSE INSERT INTO Books (Title, Author, CategoryCode, GenreCode, Status) VALUES (bookTitle, bookAuthor, categoryCode, genreCode, statusName); SET p_error_message = NULL; END IF; END",
                "CREATE PROCEDURE UpdateBook(IN p_book_id BIGINT, IN p_title VARCHAR(255), IN p_author VARCHAR(255), IN p_category_code VARCHAR(50), IN p_genre_code VARCHAR(50), IN p_status VARCHAR(50), OUT p_error_message VARCHAR(255)) BEGIN IF NOT EXISTS (SELECT 1 FROM Books WHERE BookID = p_book_id LIMIT 1) THEN SET p_error_message = 'Book ID does not exist.'; ELSEIF NOT EXISTS (SELECT 1 FROM BookCategories WHERE CategoryCode = p_category_code LIMIT 1) THEN SET p_error_message = 'Category Code does not exist.'; ELSEIF NOT EXISTS (SELECT 1 FROM BookGenres WHERE GenreCode = p_genre_code LIMIT 1) THEN SET p_error_message = 'Genre Code does not exist.'; ELSEIF NOT EXISTS (SELECT 1 FROM BookStatus WHERE Status = p_status LIMIT 1) THEN SET p_error_message = 'Invalid book status.'; ELSE UPDATE Books SET Title = p_title, Author = p_author, CategoryCode = p_category_code, GenreCode = p_genre_code, Status = p_status WHERE BookID = p_book_id; SET p_error_message = NULL; END IF; END",
                "CREATE PROCEDURE InsertMember(IN memberName VARCHAR(255), IN memberEmail VARCHAR(255), IN memberPhone VARCHAR(15), IN memberType VARCHAR(50), IN status VARCHAR(50), OUT p_error_message VARCHAR(255)) BEGIN IF EXISTS (SELECT 1 FROM Members WHERE Name = memberName AND Email = memberEmail) THEN SET p_error_message = 'Member already exists.'; ELSE INSERT INTO Members (Name, Email, PhoneNumber, MemberType, Status) VALUES (memberName, memberEmail, memberPhone, memberType, status); SET p_error_message = NULL; END IF; END",
                "CREATE PROCEDURE UpdateMember(IN p_member_id INT, IN p_name VARCHAR(255), IN p_email VARCHAR(255), IN p_phone VARCHAR(15), IN p_member_type VARCHAR(50), OUT p_error_message VARCHAR(255)) BEGIN IF NOT EXISTS (SELECT 1 FROM Members WHERE MemberID = p_member_id LIMIT 1) THEN SET p_error_message = 'Member ID does not exist.'; ELSE UPDATE Members SET Name = p_name, Email = p_email, PhoneNumber = p_phone, MemberType = p_member_type WHERE MemberID = p_member_id; SET p_error_message = NULL; END IF; END",
                "CREATE PROCEDURE DeleteBook(IN p_book_id BIGINT, OUT p_error_message VARCHAR(255)) BEGIN IF NOT EXISTS (SELECT 1 FROM Books WHERE BookID = p_book_id) THEN SET p_error_message = 'Book ID does not exist.'; ELSE DELETE FROM Books WHERE BookID = p_book_id; SET p_error_message = NULL; END IF; END",
                "CREATE PROCEDURE DeleteMember(IN p_member_id INT, OUT p_error_message VARCHAR(255)) BEGIN IF NOT EXISTS (SELECT 1 FROM Members WHERE MemberID = p_member_id) THEN SET p_error_message = 'Member ID does not exist.'; ELSE DELETE FROM Members WHERE MemberID = p_member_id; SET p_error_message = NULL; END IF; END",
                "CREATE PROCEDURE `SearchFilterSortBooks`(IN searchTitle VARCHAR(255), IN searchAuthor VARCHAR(255), IN categoryFilter VARCHAR(255), IN genreFilter VARCHAR(255), IN statusFilter VARCHAR(255)) BEGIN DECLARE finalQuery TEXT; SET finalQuery = 'SELECT * FROM Books WHERE 1=1'; IF searchTitle IS NOT NULL AND searchTitle <> '' THEN SET finalQuery = CONCAT(finalQuery, ' AND (Title LIKE ''%', searchTitle, '%'' OR Author LIKE ''%', searchTitle, '%'')');END IF; IF categoryFilter IS NOT NULL AND categoryFilter <> '' THEN SET finalQuery = CONCAT(finalQuery, ' AND CategoryCode LIKE ''%', categoryFilter, '%'''); END IF; IF genreFilter IS NOT NULL AND genreFilter <> '' THEN SET finalQuery = CONCAT(finalQuery, ' AND GenreCode LIKE ''%', genreFilter, '%'''); END IF; IF statusFilter IS NOT NULL AND statusFilter <> '' THEN SET finalQuery = CONCAT(finalQuery, ' AND Status LIKE ''%', statusFilter, '%'''); END IF; SET @query = finalQuery; PREPARE stmt FROM @query; EXECUTE stmt; DEALLOCATE PREPARE stmt; END",
                "CREATE PROCEDURE SearchFilterSortMembers( IN searchName VARCHAR(255), IN searchEmail VARCHAR(255), IN searchPhone VARCHAR(15), IN memberTypeFilter VARCHAR(50), IN statusFilter VARCHAR(50)) BEGIN DECLARE finalQuery TEXT; SET finalQuery = 'SELECT * FROM Members WHERE 1=1'; IF searchName IS NOT NULL AND LENGTH(searchName) > 0 THEN SET finalQuery = CONCAT(finalQuery, ' AND (Name LIKE ''%', searchName, '%'' OR Email LIKE ''%', searchName, '%'' OR PhoneNumber LIKE ''%', searchName, '%'' )'); END IF; IF memberTypeFilter IS NOT NULL AND LENGTH(memberTypeFilter) > 0 THEN SET finalQuery = CONCAT(finalQuery, ' AND MemberType = ''', memberTypeFilter, ''''); END IF; IF statusFilter IS NOT NULL AND LENGTH(statusFilter) > 0 THEN SET finalQuery = CONCAT(finalQuery, ' AND Status = ''', statusFilter, ''''); END IF; SET @query = finalQuery; PREPARE stmt FROM @query; EXECUTE stmt; DEALLOCATE PREPARE stmt; END"
        };
        try (Statement statement = connection.createStatement()) {
            for (String query : procedureQueries) {
                statement.executeUpdate(query);
            }
        }
    }

    public List<Book> getAllBooks(){
        List<Book> books = new ArrayList<>();

        try ( CallableStatement stmt = connection.prepareCall("{CALL GetAllBooks()}");
              ResultSet rs = stmt.executeQuery()
            ) {

            while (rs.next()) {
                int id = rs.getInt("BookID");
                String title = rs.getString("Title");
                String author = rs.getString("Author");
                String category = rs.getString("CategoryCode");
                String genre = rs.getString("GenreCode");
                String status = rs.getString("Status");
                books.add(new Book(id, title, author, category, genre, status));
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Error fetching books", e);
        }

        return books;
    }

    public List<Member> getAllMembers(){
        List<Member> members = new ArrayList<>();

        try ( CallableStatement stmt = connection.prepareCall("{CALL GetAllMembers()}");
              ResultSet rs = stmt.executeQuery()
        ) {

            while (rs.next()) {
                int id = rs.getInt("MemberID");
                String name = rs.getString("Name");
                String email = rs.getString("Email");
                String contactNum = rs.getString("PhoneNumber");
                String role = rs.getString("MemberType");
                String status = rs.getString("Status");
                members.add(new Member(id, name, email, contactNum, role, status));
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Error fetching books", e);
        }

        return members;
    }

    public List<String> getAllCategories() throws SQLException{
        List<String> categories = new ArrayList<>();

        try(CallableStatement stmt = connection.prepareCall("{CALL GetAllCategories()}");
        ResultSet rs = stmt.executeQuery()
        ){
            while (rs.next()) {
                String code = rs.getString("CategoryCode");
                String name = rs.getString("CategoryName");
                String combined = code + " - " + name;
                categories.add(combined); // Add each category code

            }
        }catch (SQLException e){
            logger.log(Level.SEVERE,"Error fetching categories");
        }

        return categories;
    }

    public List<String> getAllGenre() throws SQLException{
        List<String> genre = new ArrayList<>();

        try(CallableStatement stmt = connection.prepareCall("{CALL GetAllGenres()}");
            ResultSet rs = stmt.executeQuery()
        ){
            while (rs.next()) {
                String genrecode = rs.getString("GenreCode");
                String genrename = rs.getString("GenreName");
                String combined = genrecode + " - " + genrename;
                genre.add(combined); // Add each category code

            }
        }catch (SQLException e){
            logger.log(Level.SEVERE,"Error fetching genre codes");
        }

        return genre;
    }

    public List<String> getAllStatus(String tableName) throws SQLException {
        List<String> statusList = new ArrayList<>();

        // Validate table name before executing the query to prevent SQL injection
        if (!tableName.equalsIgnoreCase("bookstatus") && !tableName.equalsIgnoreCase("memberstatus")) {
            throw new IllegalArgumentException("Invalid table name: " + tableName);
        }

        String procedureCall = "{CALL GetAllStatus(?)}";

        try (CallableStatement stmt = connection.prepareCall(procedureCall)) {
            stmt.setString(1, tableName);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    statusList.add(rs.getString("Status"));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error fetching status from " + tableName, e);
            throw e; // Rethrow to propagate error
        }

        return statusList;
    }

    public List<String> getAllRoles() throws SQLException{
        List<String> roleList = new ArrayList<>();

        try(CallableStatement stmt = connection.prepareCall("{CALL GetAllRoles()}");
            ResultSet rs = stmt.executeQuery()
        ){
            while (rs.next()) {
                String role = rs.getString("Role");
                roleList.add(role);
            }
        }catch (SQLException e){
            logger.log(Level.SEVERE,"Error fetching member roles");
        }

        return roleList;
    }

    public List<Book> viewBooksWithFilters(String searchTitle, String searchAuthor,
                                           String categoryFilter, String genreFilter,
                                           String statusFilter) {
        List<Book> filteredbooks = new ArrayList<>();
        try {
            System.out.println("Params: " + searchTitle + ", " + searchAuthor + ", " + categoryFilter + ", " + genreFilter + ", " + statusFilter);

            // Prepare stored procedure call
            String sql = "{CALL SearchFilterSortBooks(?, ?, ?, ?, ?)}";
            try (CallableStatement stmt = connection.prepareCall(sql)) {
                stmt.setString(1, searchTitle);
                stmt.setString(2, searchAuthor);
                stmt.setString(3, categoryFilter);
                stmt.setString(4, genreFilter);
                stmt.setString(5, statusFilter);


                // Execute and display results
                try (ResultSet rs = stmt.executeQuery()) {

                    while (rs.next()) {
                        int id = rs.getInt("BookID");
                        String title = rs.getString("Title");
                        String author = rs.getString("Author");
                        String category = rs.getString("CategoryCode");
                        String genre = rs.getString("GenreCode");
                        String status = rs.getString("Status");
                        filteredbooks.add(new Book(id, title, author, category, genre, status));
                    }
                    displayResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("View books error: " + e.getMessage());
        }

        System.out.println(filteredbooks);
        return filteredbooks;
    }

    public List<Member> viewMembersWithFilters(String searchName, String searchEmail, String searchPhoneNum, String roleFilter, String statusFilter) {
        List<Member> filteredmembers = new ArrayList<>();
        try {
            System.out.println("Params: " + searchName + ", " + searchEmail + ", " + searchPhoneNum + ", "  + roleFilter + ", " + statusFilter);

            // Prepare stored procedure call
            String sql = "{CALL SearchFilterSortMembers(?, ?, ?, ?, ?)}";
            try (CallableStatement stmt = connection.prepareCall(sql)) {
                stmt.setString(1, searchName);
                stmt.setString(2, searchEmail);
                stmt.setString(3, searchPhoneNum);
                stmt.setString(4, roleFilter);
                stmt.setString(5, statusFilter);


                // Execute and display results
                try (ResultSet rs = stmt.executeQuery()) {

                    while (rs.next()) {
                        int id = rs.getInt("MemberID");
                        String name = rs.getString("Name");
                        String email = rs.getString("Email");
                        String contactNum = rs.getString("PhoneNumber");
                        String role = rs.getString("MemberType");
                        String status = rs.getString("Status");
                        filteredmembers.add(new Member(id, name, email, contactNum, role, status));
                    }
                    displayResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("View books error: " + e.getMessage());
        }

        System.out.println(filteredmembers);
        return filteredmembers;
    }

    public void displayResultSet(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        while (rs.next()) {
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(metaData.getColumnLabel(i) + ": " + rs.getString(i) + ", ");
            }
            System.out.println();
        }
    }

    // EXTRAS. NOT RLLY NEEDED
    public void bookInput(boolean isUpdate, String title, String author, String categoryCode, String genreCode,String status) {
        long bookID = isUpdate ? inputID("Books") : 0;

        executeBookProcedure(isUpdate, bookID, title, author, categoryCode, genreCode, status);

//        if (confirmAction(isUpdate, "book")) {
//            executeBookProcedure(isUpdate, bookID, title, author, categoryCode, genreCode, status);
//        }
    }

    public void memberInput(boolean isUpdate, String name, String email, String contactNum, String role, String status) {
        long memberID = isUpdate ? inputID("Books") : 0;

        executeMemberProcedure(isUpdate, memberID, name, email, contactNum, role, status);

//        if (confirmAction(isUpdate, "book")) {
//            executeBookProcedure(isUpdate, bookID, title, author, categoryCode, genreCode, status);
//        }
    }

    public void updateBook(Book book){
        executeBookProcedure(true,
                book.idProperty().get(),
                book.titleProperty().get(),
                book.authProperty().get(),
                book.catProperty().get(),
                book.genreProperty().get(),
                book.statusProperty().get()
        );
    }

    public void updateMember(Member member){
        executeMemberProcedure(true,
                member.idProperty().get(),
                member.nameProperty().get(),
                member.emailProperty().get(),
                member.contactNumProperty().get(),
                member.roleProperty().get(),
                member.statusProperty().get()
        );
    }

    public static void deleteBookById(long bookId) throws SQLException {
        String sp = "{CALL DeleteBook(?,?)}";

        try(CallableStatement stmt = connection.prepareCall("{CALL DeleteBook(?,?)}")) {
            stmt.setLong(1, bookId);
            stmt.registerOutParameter(2,Types.VARCHAR);
            stmt.execute();

            String errorMessage = stmt.getString(2);
            if (errorMessage != null) {
                System.out.println("Error: " + errorMessage);
                // Optionally show an alert here
            } else {
                System.out.println("Book deleted successfully.");
            }
        }catch (SQLException e){
            logger.log(Level.SEVERE, "Error deleting book");
        }
    }

    public static void deleteMemberById(long memberId) throws SQLException {
        String sp = "{CALL DeleteMember(?,?)}";

        try(CallableStatement stmt = connection.prepareCall("{CALL DeleteMember(?,?)}")) {
            stmt.setLong(1, memberId);
            stmt.registerOutParameter(2,Types.VARCHAR);
            stmt.execute();

            String errorMessage = stmt.getString(2);
            if (errorMessage != null) {
                System.out.println("Error: " + errorMessage);
                // Optionally show an alert here
            } else {
                System.out.println("Member deleted successfully.");
            }
        }catch (SQLException e){
            logger.log(Level.SEVERE, "Error deleting member");
        }
    }

    /**
     * STUFF U NEED TO GET SHIT WORKING
     * **/


    private void executeBookProcedure(boolean isUpdate, long bookID, String title, String author, String categoryCode, String genreCode, String status) {
        if (isUpdate) {
            executeProcedure("UpdateBook", new Object[]{bookID, title, author, categoryCode, genreCode, status});
        } else {
            executeProcedure("InsertBook", new Object[]{title, author, categoryCode, genreCode, status});
        }
    }

    private void executeMemberProcedure(boolean isUpdate, long memberID, String name, String email, String phone, String role, String status) {
        if (isUpdate) {
            executeProcedure("UpdateMember", new Object[]{memberID, name, email, phone, role, status});
        } else {
            executeProcedure("InsertMember", new Object[]{name, email, phone, role, status});
        }
    }

    private void executeProcedure(String procedureName, Object[] parameters) {
        try (CallableStatement callableStatement = connection.prepareCall("{CALL " + procedureName + "(?" + ", ?".repeat(parameters.length) + ")}")) {
            for (int i = 0; i < parameters.length; i++) {
                callableStatement.setObject(i + 1, parameters[i]);
            }
            callableStatement.registerOutParameter(parameters.length + 1, Types.VARCHAR);
            callableStatement.execute();
            String errorMessage = callableStatement.getString(parameters.length + 1);
            if (errorMessage != null) {
                System.out.println("Error: " + errorMessage);
            } else {
                System.out.println("Success.");
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    // Below are not used methods
    private int inputID(String tableName) {
        int id = 0;
        boolean validID = false;
        while (!validID) {
            System.out.print("Enter " + tableName + " ID: ");
            id = scanner.nextInt();
            scanner.nextLine();
            if (idExists(tableName, id)) {
                validID = true;
            } else {
                System.out.println("Invalid ID.");
            }
        }
        return id;
    }

    private boolean idExists(String tableName, int id) {
        String query = "SELECT COUNT(*) FROM " + tableName + " WHERE " + (tableName.equals("Books") ? "BookID" : "MemberID") + " = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("ID check error: " + e.getMessage());
        }
        return false;
    }
}
