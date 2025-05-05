package com.mimirlib.mimir.Data;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;

public class DatabaseConnection {

    // CONNECTION MANAGEMENT
    static Connection connection;
    private static final Logger logger = Logger.getLogger(DatabaseConnection.class.getName());

    private final String databaseName = "librarydb";
    private final String URL = "jdbc:mysql://localhost:3306/";
    private final String USER = "library";
    private final String PASSWORD = "root";

    private static IDValidator validator = new IDValidator(connection);

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

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    // DATABASE and TABLES SETUP
    private void createDatabase() throws SQLException {
        try (Connection tempConnection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = tempConnection.createStatement()) {
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + databaseName);
        }
    }
    // MemberType
    private void createTables() throws SQLException {
        String[] tableQueries = {
                "CREATE TABLE IF NOT EXISTS BookCategories (CategoryID INT PRIMARY KEY AUTO_INCREMENT, CategoryCode VARCHAR(10) NOT NULL, CategoryName VARCHAR(255) NOT NULL, Deleted BOOLEAN DEFAULT FALSE);",
                "CREATE TABLE IF NOT EXISTS BookGenres (GenreID INT PRIMARY KEY AUTO_INCREMENT, GenreCode VARCHAR(10) NOT NULL, GenreName VARCHAR(255) NOT NULL, Deleted BOOLEAN DEFAULT FALSE);",
                "CREATE TABLE IF NOT EXISTS BookStatus (Status VARCHAR(50) NOT NULL, StatusID INT PRIMARY KEY AUTO_INCREMENT, Deleted BOOLEAN DEFAULT FALSE, Transaction BOOLEAN DEFAULT FALSE); ",
                "CREATE TABLE IF NOT EXISTS MemberRoles (Role VARCHAR(50) NOT NULL, RoleID INT PRIMARY KEY AUTO_INCREMENT, Deleted BOOLEAN DEFAULT FALSE); ",
                "CREATE TABLE IF NOT EXISTS MemberStatus (Status VARCHAR(50) NOT NULL, StatusID INT PRIMARY KEY AUTO_INCREMENT, Deleted BOOLEAN DEFAULT FALSE);",
                "CREATE TABLE IF NOT EXISTS Books (BookID INT PRIMARY KEY AUTO_INCREMENT, Title VARCHAR(255) NOT NULL, Author VARCHAR(255), Quantity INT, CategoryID INT NOT NULL, GenreID INT NOT NULL, StatusID INT NOT NULL, Deleted BOOLEAN DEFAULT FALSE, FOREIGN KEY (CategoryID) REFERENCES BookCategories(CategoryID) ON UPDATE CASCADE, FOREIGN KEY (GenreID) REFERENCES BookGenres(GenreID) ON UPDATE CASCADE, FOREIGN KEY (StatusID) REFERENCES BookStatus(StatusID) ON UPDATE CASCADE);",
                "CREATE TABLE IF NOT EXISTS Members (MemberID INT PRIMARY KEY AUTO_INCREMENT, Name VARCHAR(255) NOT NULL, Email VARCHAR(255), PhoneNumber VARCHAR(15), RoleID INT NOT NULL, StatusID INT NOT NULL, Deleted BOOLEAN DEFAULT FALSE, FOREIGN KEY (RoleID) REFERENCES MemberRoles(RoleID) ON UPDATE CASCADE, FOREIGN KEY (StatusID) REFERENCES MemberStatus(StatusID) ON UPDATE CASCADE);",
                "CREATE TABLE IF NOT EXISTS Borrowing (BorrowID INT PRIMARY KEY AUTO_INCREMENT, BookID INT NOT NULL, MemberID INT NOT NULL, BorrowDate DATE NOT NULL, DueDate DATE NOT NULL, ReturnDate DATE, StatusID INT NOT NULL, FOREIGN KEY (BookID) REFERENCES Books(BookID), FOREIGN KEY (MemberID) REFERENCES Members(MemberID), FOREIGN KEY (StatusID) REFERENCES BookStatus(StatusID))"
        };

        try (Statement statement = connection.createStatement()) {
            for (String query : tableQueries) {
                statement.executeUpdate(query);
            }
        }
    }

    private void createStoredProcedure() throws SQLException {
        String[] procedureQueries = {

                "DROP PROCEDURE IF EXISTS GetAllBooks",
                "DROP PROCEDURE IF EXISTS GetAllMembers",
                "DROP PROCEDURE IF EXISTS GetAllCategories",
                "DROP PROCEDURE IF EXISTS GetAllGenres",
                "DROP PROCEDURE IF EXISTS GetAllStatus",
                "DROP PROCEDURE IF EXISTS GetAllRoles",
                "DROP PROCEDURE IF EXISTS GetAllTransaction",
                "DROP PROCEDURE IF EXISTS InsertBook",
                "DROP PROCEDURE IF EXISTS InsertMember",
                "DROP PROCEDURE IF EXISTS InsertBorrow",
                "DROP PROCEDURE IF EXISTS InsertBookCategory",
                "DROP PROCEDURE IF EXISTS InsertBookGenre",
                "DROP PROCEDURE IF EXISTS InsertMemberRole",
                "DROP PROCEDURE IF EXISTS InsertStatus",
                "DROP PROCEDURE IF EXISTS UpdateBook",
                "DROP PROCEDURE IF EXISTS UpdateMember",
                "DROP PROCEDURE IF EXISTS UpdateBorrowReturnDate",
                "DROP PROCEDURE IF EXISTS UpdateBookStatus",
                "DROP PROCEDURE IF EXISTS UpdateTransactionStatus",
                "DROP PROCEDURE IF EXISTS UpdateBookCategory",
                "DROP PROCEDURE IF EXISTS UpdateBookGenre",
                "DROP PROCEDURE IF EXISTS UpdateMemberRole",
                "DROP PROCEDURE IF EXISTS UpdateStatus",
                "DROP PROCEDURE IF EXISTS DeleteRecord",
                "DROP PROCEDURE IF EXISTS SearchFilterSortBooks",
                "DROP PROCEDURE IF EXISTS SearchFilterSortMembers",
                "DROP PROCEDURE IF EXISTS SearchFilterSortTransactions",

                "CREATE PROCEDURE GetAllBooks() BEGIN SELECT b.BookID, b.Title, b.Author, b.CategoryID, c.CategoryCode, c.CategoryName, b.GenreID, g.GenreCode, g.GenreName, b.StatusID, s.Status FROM Books b JOIN BookCategories c ON b.CategoryID = c.CategoryID JOIN BookGenres g ON b.GenreID = g.GenreID JOIN BookStatus s ON b.StatusID = s.StatusID WHERE b.Deleted = FALSE; END",
                "CREATE PROCEDURE GetAllMembers() BEGIN SELECT m.MemberID, m.Name, m.Email, m.PhoneNumber, m.RoleID, r.Role, s.StatusID, s.Status FROM Members m JOIN MemberRoles r ON m.RoleID = r.RoleID JOIN MemberStatus s ON m.StatusID = s.StatusID WHERE m.Deleted = FALSE; END",
                "CREATE PROCEDURE GetAllGenres() BEGIN SELECT GenreID, GenreCode, GenreName FROM BookGenres WHERE Deleted = FALSE; END",
                "CREATE PROCEDURE GetAllCategories() BEGIN SELECT CategoryID, CategoryCode, CategoryName FROM BookCategories WHERE Deleted = FALSE; END",
                //"CREATE PROCEDURE GetAllStatus(IN entityType VARCHAR(50)) BEGIN IF entityType = 'bookstatus' THEN SELECT StatusID, Status FROM BookStatus WHERE Deleted = FALSE; ELSEIF entityType = 'memberstatus' THEN SELECT StatusID, Status FROM MemberStatus WHERE Deleted = FALSE; ELSE SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Invalid entity type'; END IF; END",
                "CREATE PROCEDURE GetAllStatus(IN entityType VARCHAR(50)) BEGIN IF entityType = 'bookstatus' THEN SELECT StatusID, Status FROM BookStatus WHERE Deleted = FALSE AND Transaction = FALSE; ELSEIF entityType = 'transactionstatus' THEN SELECT StatusID, Status FROM BookStatus WHERE Deleted = FALSE AND Transaction = TRUE; ELSEIF entityType = 'booktransactstatus' THEN SELECT StatusID, Status FROM BookStatus WHERE Deleted = FALSE; ELSEIF entityType = 'memberstatus' THEN SELECT StatusID, Status FROM MemberStatus WHERE Deleted = FALSE; ELSE SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Invalid entity type. Use \"bookstatus\", \"transactionstatus\", or \"memberstatus\".'; END IF; END",
                "CREATE PROCEDURE GetAllRoles() BEGIN SELECT RoleID, Role FROM MemberRoles WHERE Deleted = FALSE; END",
                "CREATE PROCEDURE GetAllTransaction() BEGIN SELECT t.BorrowID, t.BookID, b.Title, t.MemberID, m.Name, t.BorrowDate, t.DueDate, t.ReturnDate, t.StatusID, s.Status FROM Borrowing t JOIN Books b ON t.BookID = b.BookID JOIN Members m ON t.MemberID = m.MemberID JOIN BookStatus s ON t.StatusID = s.StatusID WHERE s.Transaction = TRUE; END",

                "CREATE PROCEDURE SearchFilterSortBooks(IN searchTitle VARCHAR(255), IN searchAuthor VARCHAR(255), IN categoryID INT, IN genreID INT, IN statusID INT) BEGIN DECLARE finalQuery TEXT; SET finalQuery = 'SELECT * FROM Books WHERE Deleted = FALSE'; IF searchTitle IS NOT NULL AND searchTitle <> '' THEN SET finalQuery = CONCAT(finalQuery, ' AND (Title LIKE ''%', searchTitle, '%'' OR Author LIKE ''%', searchTitle, '%'' )'); END IF; IF categoryID IS NOT NULL THEN SET finalQuery = CONCAT(finalQuery, ' AND CategoryID = ', categoryID); END IF; IF genreID IS NOT NULL THEN SET finalQuery = CONCAT(finalQuery, ' AND GenreID = ', genreID); END IF; IF statusID IS NOT NULL THEN SET finalQuery = CONCAT(finalQuery, ' AND StatusID = ', statusID); END IF; SET @query = finalQuery; PREPARE stmt FROM @query; EXECUTE stmt; DEALLOCATE PREPARE stmt; END",
                //"CREATE PROCEDURE SearchFilterSortBooks(IN searchTitle VARCHAR(255),  IN searchAuthor VARCHAR(255),  IN categoryID INT,  IN genreID INT,  IN statusID INT) BEGIN  SET @query = 'SELECT * FROM Books WHERE Deleted = FALSE'; IF searchTitle IS NOT NULL AND searchTitle <> '' THEN  SET @query = CONCAT(@query, ' AND (Title LIKE CONCAT(\"%\", ?, \"%\") OR Author LIKE CONCAT(\"%\", ?, \"%\"))'); END IF;  IF categoryID IS NOT NULL THEN  SET @query = CONCAT(@query, ' AND CategoryID = ?'); END IF;  IF genreID IS NOT NULL THEN  SET @query = CONCAT(@query, ' AND GenreID = ?'); END IF;  IF statusID IS NOT NULL THEN  SET @query = CONCAT(@query, ' AND StatusID = ?'); END IF;  PREPARE stmt FROM @query;  EXECUTE stmt USING searchTitle, searchAuthor, categoryID, genreID, statusID;  DEALLOCATE PREPARE stmt; END",          "CREATE PROCEDURE SearchFilterSortMembers(IN searchName VARCHAR(255), IN searchEmail VARCHAR(255), IN searchPhone VARCHAR(15), IN roleID INT, IN statusID INT) BEGIN DECLARE finalQuery TEXT; SET finalQuery = 'SELECT * FROM Members WHERE Deleted = FALSE'; IF searchName IS NOT NULL AND LENGTH(searchName) > 0 THEN SET finalQuery = CONCAT(finalQuery, ' AND (Name LIKE ''%', searchName, '%'' OR Email LIKE ''%', searchName, '%'' OR PhoneNumber LIKE ''%', searchName, '%'' )'); END IF; IF roleID IS NOT NULL THEN SET finalQuery = CONCAT(finalQuery, ' AND RoleID = ', roleID); END IF; IF statusID IS NOT NULL THEN SET finalQuery = CONCAT(finalQuery, ' AND StatusID = ', statusID); END IF; SET @query = finalQuery; PREPARE stmt FROM @query; EXECUTE stmt; DEALLOCATE PREPARE stmt; END",
                "CREATE PROCEDURE SearchFilterSortMembers(IN searchName VARCHAR(255), IN searchEmail VARCHAR(255), IN searchPhone VARCHAR(15), IN roleID INT, IN statusID INT) BEGIN DECLARE finalQuery TEXT; SET finalQuery = 'SELECT * FROM Members WHERE Deleted = FALSE'; IF searchName IS NOT NULL AND LENGTH(searchName) > 0 THEN SET finalQuery = CONCAT(finalQuery, ' AND (Name LIKE ''%', searchName, '%'' OR Email LIKE ''%', searchName, '%'' OR PhoneNumber LIKE ''%', searchName, '%'' )'); END IF; IF roleID IS NOT NULL THEN SET finalQuery = CONCAT(finalQuery, ' AND RoleID = ', roleID); END IF; IF statusID IS NOT NULL THEN SET finalQuery = CONCAT(finalQuery, ' AND StatusID = ', statusID); END IF; SET @query = finalQuery; PREPARE stmt FROM @query; EXECUTE stmt; DEALLOCATE PREPARE stmt; END",
                "CREATE PROCEDURE SearchFilterSortTransactions(IN searchTitle VARCHAR(255), IN searchName VARCHAR(255), IN filterStatusID INT) BEGIN DECLARE finalQuery TEXT; SET finalQuery = 'SELECT t.BorrowID, t.BookID, bks.Title, t.MemberID, m.Name, t.BorrowDate, t.DueDate, t.ReturnDate, bks.StatusID FROM Borrowing t JOIN Books bks ON t.BookID = bks.BookID JOIN Members m ON t.MemberID = m.MemberID WHERE bks.Deleted = FALSE AND m.Deleted = FALSE'; IF (searchTitle IS NOT NULL AND LENGTH(searchTitle) > 0) OR (searchName IS NOT NULL AND LENGTH(searchName) > 0) THEN SET finalQuery = CONCAT(finalQuery, ' AND (bks.Title LIKE ''%', searchTitle, '%'' OR m.Name LIKE ''%', searchName, '%'' )'); END IF; IF filterStatusID IS NOT NULL THEN SET finalQuery = CONCAT(finalQuery, ' AND bks.StatusID = ', filterStatusID); END IF; SET @query = finalQuery; PREPARE stmt FROM @query; EXECUTE stmt; DEALLOCATE PREPARE stmt; END",
                //"CREATE PROCEDURE SearchFilterSortTransactions( IN searchTitle VARCHAR(255),  IN searchName VARCHAR(255),  IN filterStatusID INT) BEGIN  SET @query = 'SELECT t.BorrowID, t.BookID, b.Title, t.MemberID, m.Name, t.BorrowDate, t.DueDate, t.ReturnDate, b.StatusID                   FROM Borrowing t                  JOIN Books b ON t.BookID = b.BookID                  JOIN Members m ON t.MemberID = m.MemberID                  WHERE b.Deleted = FALSE AND m.Deleted = FALSE'; IF searchTitle IS NOT NULL AND searchTitle <> '' THEN  SET @query = CONCAT(@query, ' AND b.Title LIKE CONCAT(\"%\", ?, \"%\")'); END IF;  IF searchName IS NOT NULL AND searchName <> '' THEN  SET @query = CONCAT(@query, ' AND m.Name LIKE CONCAT(\"%\", ?, \"%\")'); END IF;  IF filterStatusID IS NOT NULL THEN  SET @query = CONCAT(@query, ' AND b.StatusID = ?'); END IF;  PREPARE stmt FROM @query;  EXECUTE stmt USING searchTitle, searchName, filterStatusID;  DEALLOCATE PREPARE stmt; END",

                "CREATE PROCEDURE InsertBook(IN bookTitle VARCHAR(255), IN bookAuthor VARCHAR(255), IN categoryId INT, IN genreId INT, IN statusId INT, OUT p_error_message VARCHAR(255)) BEGIN IF NOT EXISTS (SELECT 1 FROM BookCategories WHERE CategoryID = categoryId) THEN SET p_error_message = 'Invalid category ID.'; ELSEIF NOT EXISTS (SELECT 1 FROM BookGenres WHERE GenreID = genreId) THEN SET p_error_message = 'Invalid genre ID.'; ELSEIF NOT EXISTS (SELECT 1 FROM BookStatus WHERE StatusID = statusId) THEN SET p_error_message = 'Invalid book status ID.'; ELSEIF EXISTS (SELECT 1 FROM Books WHERE Title = bookTitle AND Author = bookAuthor) THEN SET p_error_message = 'Book already exists.'; ELSE INSERT INTO Books (Title, Author, CategoryID, GenreID, StatusID) VALUES (bookTitle, bookAuthor, categoryId, genreId, statusId); SET p_error_message = NULL; END IF; END",
                "CREATE PROCEDURE InsertMember(IN memberName VARCHAR(255), IN memberEmail VARCHAR(255), IN memberPhone VARCHAR(15), IN roleId INT, IN statusId INT, OUT p_error_message VARCHAR(255)) BEGIN IF EXISTS (SELECT 1 FROM Members WHERE Name = memberName AND Email = memberEmail) THEN SET p_error_message = 'Member already exists.'; ELSE INSERT INTO Members (Name, Email, PhoneNumber, RoleID, StatusID) VALUES (memberName, memberEmail, memberPhone, roleId, statusId); SET p_error_message = NULL; END IF; END",
                "CREATE PROCEDURE InsertBorrow(IN bookId BIGINT, IN memberId INT, IN borrowDate DATE, IN dueDate DATE, IN statusId INT, OUT p_error_message VARCHAR(255)) BEGIN IF NOT EXISTS (SELECT 1 FROM Books WHERE BookID = bookId) THEN SET p_error_message = 'Invalid book ID.'; ELSEIF NOT EXISTS (SELECT 1 FROM Members WHERE MemberID = memberId) THEN SET p_error_message = 'Invalid member ID.'; ELSEIF NOT EXISTS (SELECT 1 FROM BookStatus WHERE StatusID = statusId) THEN SET p_error_message = 'Invalid status ID.'; ELSEIF dueDate <= borrowDate THEN SET p_error_message = 'Due date must be later than borrow date.'; ELSE INSERT INTO Borrowing (BookID, MemberID, BorrowDate, DueDate, StatusID) VALUES (bookId, memberId, borrowDate, dueDate, statusId); SET p_error_message = NULL; END IF; END;",
                "CREATE PROCEDURE InsertBookCategory(IN categoryCode VARCHAR(10), IN categoryName VARCHAR(255), OUT errorMessage VARCHAR(255)) BEGIN DECLARE EXIT HANDLER FOR SQLEXCEPTION BEGIN GET DIAGNOSTICS CONDITION 1 errorMessage = MESSAGE_TEXT; END; INSERT INTO BookCategories (CategoryCode, CategoryName) VALUES (categoryCode, categoryName); SET errorMessage = NULL; END",
                "CREATE PROCEDURE InsertBookGenre(IN genreCode VARCHAR(10), IN genreName VARCHAR(255), OUT errorMessage VARCHAR(255)) BEGIN DECLARE EXIT HANDLER FOR SQLEXCEPTION BEGIN GET DIAGNOSTICS CONDITION 1 errorMessage = MESSAGE_TEXT; END; INSERT INTO BookGenres (GenreCode, GenreName) VALUES (genreCode, genreName); SET errorMessage = NULL; END ;",
                "CREATE PROCEDURE InsertMemberRole(IN role VARCHAR(50), OUT errorMessage VARCHAR(255)) BEGIN DECLARE EXIT HANDLER FOR SQLEXCEPTION BEGIN GET DIAGNOSTICS CONDITION 1 errorMessage = MESSAGE_TEXT; END; INSERT INTO MemberRoles (Role) VALUES (role); SET errorMessage = NULL; END",                "CREATE PROCEDURE InsertStatus(IN status VARCHAR(50), IN entityType VARCHAR(50), OUT errorMessage VARCHAR(255)) BEGIN DECLARE EXIT HANDLER FOR SQLEXCEPTION BEGIN GET DIAGNOSTICS CONDITION 1 errorMessage = MESSAGE_TEXT; END; IF entityType = 'BOOK' THEN INSERT INTO BookStatus (Status) VALUES (status); SET errorMessage = NULL; ELSEIF entityType = 'MEMBER' THEN INSERT INTO MemberStatus (Status) VALUES (status); SET errorMessage = NULL; ELSE SET errorMessage = 'Invalid entity type. Use ''BOOK'' or ''MEMBER''.'; END IF; END",

                "CREATE PROCEDURE UpdateBook(IN p_book_id BIGINT, IN p_title VARCHAR(255), IN p_author VARCHAR(255), IN p_category_id INT, IN p_genre_id INT, IN p_status_id INT, OUT p_error_message VARCHAR(255)) BEGIN IF NOT EXISTS (SELECT 1 FROM Books WHERE BookID = p_book_id LIMIT 1) THEN SET p_error_message = 'Book ID does not exist.'; ELSEIF NOT EXISTS (SELECT 1 FROM BookCategories WHERE CategoryID = p_category_id LIMIT 1) THEN SET p_error_message = 'Category ID does not exist.'; ELSEIF NOT EXISTS (SELECT 1 FROM BookGenres WHERE GenreID = p_genre_id LIMIT 1) THEN SET p_error_message = 'Genre ID does not exist.'; ELSEIF NOT EXISTS (SELECT 1 FROM BookStatus WHERE StatusID = p_status_id LIMIT 1) THEN SET p_error_message = 'Invalid book status.'; ELSE UPDATE Books SET Title = p_title, Author = p_author, CategoryID = p_category_id, GenreID = p_genre_id, StatusID = p_status_id WHERE BookID = p_book_id; SET p_error_message = NULL; END IF; END;",
                "CREATE PROCEDURE UpdateMember(IN p_member_id INT, IN p_name VARCHAR(255), IN p_email VARCHAR(255), IN p_phone VARCHAR(15), IN p_role_id INT, IN p_status_id INT, OUT p_error_message VARCHAR(255)) BEGIN IF NOT EXISTS (SELECT 1 FROM Members WHERE MemberID = p_member_id LIMIT 1) THEN SET p_error_message = 'Member ID does not exist.'; ELSEIF NOT EXISTS (SELECT 1 FROM MemberRoles WHERE RoleID = p_role_id LIMIT 1) THEN SET p_error_message = 'Role ID does not exist.'; ELSEIF NOT EXISTS (SELECT 1 FROM MemberStatus WHERE StatusID = p_status_id LIMIT 1) THEN SET p_error_message = 'Invalid member status.'; ELSE UPDATE Members SET Name = p_name, Email = p_email, PhoneNumber = p_phone, RoleID = p_role_id, StatusID = p_status_id WHERE MemberID = p_member_id; SET p_error_message = NULL; END IF; END;",
                "CREATE PROCEDURE UpdateBorrowReturnDate(IN p_borrow_id INT, IN p_return_date DATE, IN p_status_id INT, OUT p_error_message VARCHAR(255)) BEGIN IF NOT EXISTS (SELECT 1 FROM Borrowing WHERE BorrowID = p_borrow_id LIMIT 1) THEN SET p_error_message = 'Borrow ID does not exist.'; ELSE UPDATE Borrowing SET ReturnDate = p_return_date, StatusID = p_status_id WHERE BorrowID = p_borrow_id; SET p_error_message = NULL; END IF; END;",
                "CREATE PROCEDURE UpdateBookStatus(IN p_book_id BIGINT, IN p_status_id INT, OUT p_error_message VARCHAR(255)) BEGIN IF NOT EXISTS (SELECT 1 FROM Books WHERE BookID = p_book_id LIMIT 1) THEN SET p_error_message = 'Book ID does not exist.'; ELSEIF NOT EXISTS (SELECT 1 FROM BookStatus WHERE StatusID = p_status_id LIMIT 1) THEN SET p_error_message = 'Invalid book status.'; ELSE UPDATE Books SET StatusID = p_status_id WHERE BookID = p_book_id; SET p_error_message = NULL; END IF; END;",
                "CREATE PROCEDURE UpdateTransactionStatus(IN p_borrow_id BIGINT, IN p_status_id INT, OUT p_error_message VARCHAR(255)) BEGIN IF NOT EXISTS (SELECT 1 FROM Borrowing WHERE BorrowID = p_borrow_id LIMIT 1) THEN SET p_error_message = 'Borrow ID does not exist.'; ELSEIF NOT EXISTS (SELECT 1 FROM BookStatus WHERE StatusID = p_status_id LIMIT 1) THEN SET p_error_message = 'Invalid book status.'; ELSE UPDATE Borrowing SET StatusID = p_status_id WHERE BorrowID = p_borrow_id; SET p_error_message = NULL; END IF; END;",

                "CREATE PROCEDURE UpdateBookCategory(IN p_category_id INT, IN p_category_code VARCHAR(10), IN p_category_name VARCHAR(255), OUT resultMessage VARCHAR(255)) BEGIN IF EXISTS (SELECT 1 FROM BookCategories WHERE CategoryID = p_category_id LIMIT 1) THEN UPDATE BookCategories SET CategoryCode = p_category_code, CategoryName = p_category_name WHERE CategoryID = p_category_id; SET resultMessage = 'Update completed'; ELSE SET resultMessage = 'Category ID does not exist'; END IF; END;",
                "CREATE PROCEDURE UpdateBookGenre(IN p_genre_id INT, IN p_genre_code VARCHAR(10), IN p_genre_name VARCHAR(255), OUT errorMessage VARCHAR(255)) BEGIN DECLARE rowsAffected INT DEFAULT 0; DECLARE EXIT HANDLER FOR SQLEXCEPTION BEGIN GET DIAGNOSTICS CONDITION 1 errorMessage = MESSAGE_TEXT; END; IF EXISTS (SELECT 1 FROM BookGenres WHERE GenreID = p_genre_id LIMIT 1) THEN UPDATE BookGenres SET GenreCode = p_genre_code, GenreName = p_genre_name WHERE GenreID = p_genre_id; SET rowsAffected = ROW_COUNT(); IF rowsAffected = 0 THEN SET errorMessage = 'No changes were made.'; ELSE SET errorMessage = NULL; END IF; ELSE SET errorMessage = 'Genre ID does not exist.'; END IF; END;",
                "CREATE PROCEDURE UpdateMemberRole(IN p_role_id INT, IN p_role VARCHAR(50), OUT errorMessage VARCHAR(255)) BEGIN DECLARE rowsAffected INT DEFAULT 0; DECLARE EXIT HANDLER FOR SQLEXCEPTION BEGIN GET DIAGNOSTICS CONDITION 1 errorMessage = MESSAGE_TEXT; END; IF EXISTS (SELECT 1 FROM MemberRoles WHERE RoleID = p_role_id LIMIT 1) THEN UPDATE MemberRoles SET Role = p_role WHERE RoleID = p_role_id; SET rowsAffected = ROW_COUNT(); IF rowsAffected = 0 THEN SET errorMessage = 'No changes were made.'; ELSE SET errorMessage = NULL; END IF; ELSE SET errorMessage = 'Role ID does not exist.'; END IF; END;",
                "CREATE PROCEDURE UpdateStatus(IN p_status_id INT, IN p_status VARCHAR(50), IN p_entity_type VARCHAR(50), OUT errorMessage VARCHAR(255)) BEGIN DECLARE rowsAffected INT DEFAULT 0; DECLARE EXIT HANDLER FOR SQLEXCEPTION BEGIN GET DIAGNOSTICS CONDITION 1 errorMessage = MESSAGE_TEXT; END; CASE WHEN p_entity_type = 'BOOK' THEN IF EXISTS (SELECT 1 FROM BookStatus WHERE StatusID = p_status_id LIMIT 1) THEN UPDATE BookStatus SET Status = p_status WHERE StatusID = p_status_id; SET rowsAffected = ROW_COUNT(); SET errorMessage = IF(rowsAffected = 0, 'No changes were made.', NULL); ELSE SET errorMessage = 'Book Status ID does not exist.'; END IF; WHEN p_entity_type = 'MEMBER' THEN IF EXISTS (SELECT 1 FROM MemberStatus WHERE StatusID = p_status_id LIMIT 1) THEN UPDATE MemberStatus SET Status = p_status WHERE StatusID = p_status_id; SET rowsAffected = ROW_COUNT(); SET errorMessage = IF(rowsAffected = 0, 'No changes were made.', NULL); ELSE SET errorMessage = 'Member Status ID does not exist.'; END IF; ELSE SET errorMessage = 'Invalid entity type. Use ''BOOK'' or ''MEMBER''.'; END CASE; END;",

                "CREATE PROCEDURE DeleteRecord(IN entityType VARCHAR(50), IN recordId INT, OUT errorMessage VARCHAR(255)) BEGIN DECLARE tableName VARCHAR(255); DECLARE idColumnName VARCHAR(255); DECLARE deleteColumn VARCHAR(50) DEFAULT 'Deleted'; CASE entityType WHEN 'Book' THEN SET tableName = 'Books'; SET idColumnName = 'BookID'; WHEN 'Member' THEN SET tableName = 'Members'; SET idColumnName = 'MemberID'; WHEN 'Category' THEN SET tableName = 'BookCategories'; SET idColumnName = 'CategoryID'; WHEN 'Genre' THEN SET tableName = 'BookGenres'; SET idColumnName = 'GenreID'; WHEN 'BookStatus' THEN SET tableName = 'BookStatus'; SET idColumnName = 'StatusID'; WHEN 'MemberRole' THEN SET tableName = 'MemberRoles'; SET idColumnName = 'RoleID'; WHEN 'MemberStatus' THEN SET tableName = 'MemberStatus'; SET idColumnName = 'StatusID'; ELSE SET errorMessage = 'Invalid entity type.'; END CASE; IF errorMessage IS NULL THEN SET @sql = CONCAT('UPDATE ', tableName, ' SET ', deleteColumn, ' = TRUE WHERE ', idColumnName, ' = ?'); PREPARE stmt FROM @sql; SET @id = recordId; EXECUTE stmt USING @id; DEALLOCATE PREPARE stmt; IF ROW_COUNT() = 0 THEN SET errorMessage = CONCAT(entityType, ' with ID ', recordId, ' does not exist.'); ELSE SET errorMessage = NULL; END IF; END IF; END",
        };
        try (Statement statement = connection.createStatement()) {
            for (String query : procedureQueries) {
                statement.executeUpdate(query);
            }
        }
    }

    // DATA RETRIEVAL METHODS
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();

        try (CallableStatement stmt = connection.prepareCall("{CALL GetAllBooks()}");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("BookID");
                String title = rs.getString("Title");
                String author = rs.getString("Author");
                int categoryId = rs.getInt("CategoryID");  // Fetch CategoryID
                String categoryCode = rs.getString("CategoryCode"); // Fetch readable Code
                String categoryName = rs.getString("CategoryName"); // Fetch readable Name
                int genreId = rs.getInt("GenreID"); // Fetch GenreID
                String genreCode = rs.getString("GenreCode"); // Fetch readable Code
                String genreName = rs.getString("GenreName"); // Fetch readable Name
                int statusId = rs.getInt("StatusID"); // Fetch StatusID
                String status = rs.getString("Status"); // Fetch readable Status name

                books.add(new Book(id, title, author, categoryId, categoryCode, categoryName, genreId, genreCode, genreName, statusId, status));
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error fetching books", e);
        }

        return books;
    }

    public List<Member> getAllMembers() {
        List<Member> members = new ArrayList<>();

        try (CallableStatement stmt = connection.prepareCall("{CALL GetAllMembers()}");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("MemberID");
                String name = rs.getString("Name");
                String email = rs.getString("Email");
                String contactNum = rs.getString("PhoneNumber");
                int roleId = rs.getInt("RoleID");  // Now fetching RoleID
                String role = rs.getString("Role"); // Fetching role name
                int statusId = rs.getInt("StatusID"); // Now fetching StatusID
                String status = rs.getString("Status"); // Fetching status name

                members.add(new Member(id, name, email, contactNum, roleId, role, statusId, status));
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error fetching members", e);
        }

        return members;
    }

    public List<BookCategory> getAllCategories() {
        List<BookCategory> categories = new ArrayList<>();

        try (CallableStatement stmt = connection.prepareCall("{CALL GetAllCategories()}");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("CategoryID");  // Fetch CategoryID but don't display it
                String code = rs.getString("CategoryCode");
                String name = rs.getString("CategoryName");

                // Store ID for internal use, but UI will only use code and name
                categories.add(new BookCategory(id, code, name));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error fetching categories", e);
        }

        return categories;
    }

    public List<BookGenre> getAllGenres() {
        List<BookGenre> genre = new ArrayList<>();

        try(CallableStatement stmt = connection.prepareCall("{CALL GetAllGenres()}");
            ResultSet rs = stmt.executeQuery()
        ){
            while (rs.next()) {
                int genreid = rs.getInt("GenreID");
                String genrecode = rs.getString("GenreCode");
                String genrename = rs.getString("GenreName");

                genre.add(new BookGenre(genreid, genrecode, genrename)); // Add each category code

            }
        }catch (SQLException e){
            logger.log(Level.SEVERE,"Error fetching genre codes");
        }

        return genre;
    }

    public List<BookStatus> getBookStatuses() {
        List<BookStatus> bookStatusList = new ArrayList<>();

        try (CallableStatement stmt = connection.prepareCall("{CALL GetAllStatus(?)}")) {
            stmt.setString(1, "bookstatus"); // Corrected parameter value
            try(ResultSet rs = stmt.executeQuery()) { //Ensuring ResultSet is closed
                while (rs.next()) {
                    int statusID = rs.getInt("StatusID");
                    String status = rs.getString("Status");
                    bookStatusList.add(new BookStatus(status, statusID));
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error fetching book statuses", e);
        }
        return bookStatusList;
    }

    public List<BookStatus> getTransactionStatuses() {
        List<BookStatus> bookStatusList = new ArrayList<>();

        try (CallableStatement stmt = connection.prepareCall("{CALL GetAllStatus(?)}")) {
            stmt.setString(1, "transactionstatus"); // Corrected parameter value
            try(ResultSet rs = stmt.executeQuery()) { //Ensuring ResultSet is closed
                while (rs.next()) {
                    int statusID = rs.getInt("StatusID");
                    String status = rs.getString("Status");
                    bookStatusList.add(new BookStatus(status, statusID));
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error fetching transaction statuses", e);
        }
        return bookStatusList;
    }

    public List<BookStatus> getBookAndTransactStatuses() {
        List<BookStatus> bookStatusList = new ArrayList<>();

        try (CallableStatement stmt = connection.prepareCall("{CALL GetAllStatus(?)}")) {
            stmt.setString(1, "booktransactstatus"); // Corrected parameter value
            try(ResultSet rs = stmt.executeQuery()) { //Ensuring ResultSet is closed
                while (rs.next()) {
                    int statusID = rs.getInt("StatusID");
                    String status = rs.getString("Status");
                    bookStatusList.add(new BookStatus(status, statusID));
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error fetching transaction statuses", e);
        }
        return bookStatusList;
    }

    public List<MemberRole> getAllRoles() {
        List<MemberRole> roleList = new ArrayList<>();

        try (CallableStatement stmt = connection.prepareCall("{CALL GetAllRoles()}");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int roleId = rs.getInt("RoleID");  // Fetch RoleID for reference
                String role = rs.getString("Role"); // Fetch readable Role name
                roleList.add(new MemberRole(role, roleId));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error fetching member roles", e);
        }

        return roleList;
    }

    public List<MemberStatus> getMemberStatuses() {
        List<MemberStatus> memberStatusList = new ArrayList<>();

        try (CallableStatement stmt = connection.prepareCall("{CALL GetAllStatus(?)}")) {
            stmt.setString(1, "memberstatus"); // Corrected parameter value
            try(ResultSet rs = stmt.executeQuery()) { //Ensuring ResultSet is closed
                while (rs.next()) {
                    int statusID = rs.getInt("StatusID");
                    String status = rs.getString("Status");
                    memberStatusList.add(new MemberStatus(status, statusID));
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error fetching member statuses", e);
        }

        return memberStatusList;
    }

    public List<TransactionViewModel> getAllTransactions() {
        List<TransactionViewModel> transactions = new ArrayList<>();

        try (CallableStatement stmt = connection.prepareCall("{CALL GetAllTransaction()}");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                transactions.add(new TransactionViewModel(
                        rs.getInt("BorrowID"),
                        rs.getInt("BookID"),
                        rs.getString("Title"),
                        rs.getInt("MemberID"),
                        rs.getString("Name"),
                        rs.getDate("BorrowDate").toLocalDate(),
                        rs.getDate("DueDate").toLocalDate(),
                        rs.getDate("ReturnDate") != null ? rs.getDate("ReturnDate").toLocalDate() : null,
                        rs.getInt("StatusID"),  // Retrieve StatusID
                        rs.getString("Status")  // Retrieve readable status name
                ));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Transaction fetching error", e);
        }
        return transactions;
    }

    /** // old version
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

    // Other methods relating to their model class
    public List<BookCategory> getCategories() {
        List<BookCategory> categories = new ArrayList<>();

        try (CallableStatement stmt = connection.prepareCall("{CALL GetAllCategories()}");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String code = rs.getString("CategoryCode");
                String name = rs.getString("CategoryName");
                categories.add(new  BookCategory(code, name));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error fetching categories", e);
        }

        return categories;
    }

    public List<BookGenre> getGenres() {
        List<BookGenre> genres = new ArrayList<>();

        try (CallableStatement stmt = connection.prepareCall("{CALL GetAllGenres()}");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String genreCode = rs.getString("GenreCode");
                String genreName = rs.getString("GenreName");
                genres.add(new BookGenre(genreCode, genreName));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error fetching genres", e);
        }

        return genres;
    }


    public List<BookStatus> getBookStatuses() {
        List<BookStatus> bookStatusList = new ArrayList<>();

        String sql = "{CALL GetAllStatus(?)}";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setString(1, "bookstatus");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String status = rs.getString("Status");
                    int statusID = rs.getInt("StatusID");
                    bookStatusList.add(new BookStatus(status, statusID));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error fetching book statuses", e);
        }

        return bookStatusList;
    }

    public List<MemberStatus> getMemberStatuses() {
        List<MemberStatus> memberStatusList = new ArrayList<>();

        String sql = "{CALL GetAllStatus(?)}";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setString(1, "memberstatus");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String status = rs.getString("Status");
                    int statusID = rs.getInt("StatusID");
                    memberStatusList.add(new MemberStatus(status, statusID));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error fetching book statuses", e);
        }

        return memberStatusList;
    }
     */


    // FILTERED QUERIES
    public List<Book> viewBooksWithFilters(String searchTitle, String searchAuthor, String categoryFilter, String genreFilter, String statusFilter) {
        List<Book> filteredBooks = new ArrayList<>();

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

                // Execute query
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        int id = rs.getInt("BookID");
                        String title = rs.getString("Title");
                        String author = rs.getString("Author");
                        int categoryId = rs.getInt("CategoryID");  // Fetch CategoryID
                        String categoryCode = rs.getString("CategoryCode");
                        String categoryName = rs.getString("CategoryName");
                        int genreId = rs.getInt("GenreID");
                        String genreCode = rs.getString("GenreCode");
                        String genreName = rs.getString("GenreName");
                        int statusId = rs.getInt("StatusID");
                        String status = rs.getString("Status");

                        filteredBooks.add(new Book(id, title, author, categoryId, categoryCode, categoryName, genreId, genreCode, genreName, statusId, status));
                    }
                    displayResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("View books error: " + e.getMessage());
        }

        System.out.println(filteredBooks);
        return filteredBooks;
    }

    public List<Member> viewMembersWithFilters(String searchName, String searchEmail, String searchPhoneNum, String roleFilter, String statusFilter) {
        List<Member> filteredMembers = new ArrayList<>();

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

                // Execute query and retrieve results
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        int id = rs.getInt("MemberID");
                        String name = rs.getString("Name");
                        String email = rs.getString("Email");
                        String contactNum = rs.getString("PhoneNumber");
                        int roleId = rs.getInt("RoleID");  // Fetch RoleID
                        String role = rs.getString("Role"); // Fetch readable Role
                        int statusId = rs.getInt("StatusID"); // Fetch StatusID
                        String status = rs.getString("Status"); // Fetch readable Status

                        filteredMembers.add(new Member(id, name, email, contactNum, roleId, role, statusId, status));
                    }
                    displayResultSet(rs); // Debugging output
                }
            }
        } catch (SQLException e) {
            System.err.println("View members error: " + e.getMessage());
        }

        System.out.println(filteredMembers);
        return filteredMembers;
    }

    public List<TransactionViewModel> searchFilterSortTransactions(String searchTitle, String searchName, int filterStatusId) throws SQLException {
        List<TransactionViewModel> filteredTransactions = new ArrayList<>();

        String sql = "{CALL SearchFilterSortTransactions(?, ?, ?)}";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setString(1, searchTitle);
            stmt.setString(2, searchName);
            stmt.setInt(3, filterStatusId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    filteredTransactions.add(new TransactionViewModel(
                            rs.getInt("BorrowID"),
                            rs.getInt("BookID"),
                            rs.getString("Title"),
                            rs.getInt("MemberID"),
                            rs.getString("Name"),
                            rs.getDate("BorrowDate").toLocalDate(),
                            rs.getDate("DueDate").toLocalDate(),
                            rs.getDate("ReturnDate") != null ? rs.getDate("ReturnDate").toLocalDate() : null,
                            rs.getInt("StatusID"),  // Retrieve StatusID
                            rs.getString("BookStatus")  // Retrieve readable status name
                    ));
                }

                // Debugging output
                displayResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("View transactions error: " + e.getMessage());
        }

        System.out.println("Filtered Transactions: " + filteredTransactions);
        return filteredTransactions;
    }


    // DATA ENTRY
    public void bookInput(String title, String author, int categoryId, int genreId, int statusId) {
        try {
            boolean isUpdate = false;
            int bookID = isUpdate ? inputID("book") : 0;

            executeBookProcedure(isUpdate, bookID, title, author, categoryId, genreId, statusId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error inserting/updating book", e);
        }
    }

    public void memberInput(String name, String email, String contactNum, int roleId, int statusId) {
        try {
            boolean isUpdate = false;
            int memberID = isUpdate ? inputID("member") : 0;

            executeMemberProcedure(isUpdate, memberID, name, email, contactNum, roleId, statusId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error inserting/updating member", e);
        }
    }

    public void borrowInput(int bookId, int memberId, LocalDate borrowDate, LocalDate dueDate, LocalDate returnDate, int statusId) {
        try {
            System.out.println("Borrow input is commencing...");
            boolean isUpdate = false;
            int borrowID = isUpdate ? inputID("borrow") : 0;

            executeBorrowProcedure(isUpdate, borrowID, bookId, memberId, borrowDate, dueDate, returnDate, statusId);
            System.out.println("Borrow input is finished.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error processing borrow input", e);
        }
    }

    public void bookCategoryInput(String categoryCode, String categoryName) {
        boolean isUpdate = false;
        int id = isUpdate ? inputID("category") : 0;

        logger.info("categoryCode being passed: " + categoryCode);
        executeCategoryProcedure(isUpdate, id, categoryCode, categoryName);
    }

    public void bookGenreInput(String genreCode, String genreName) {
        boolean isUpdate = false;
        int id = isUpdate ? inputID("genre") : 0;
        logger.info("genreCode being passed: " + genreCode); // Add this line

        executeGenreProcedure(isUpdate, id, genreCode, genreName);
    }

    public void bookStatusInput(String status, String entityType) {
        boolean isUpdate = false;
        int statusId = isUpdate ? inputID("bookstatus") : 0;

        executeStatusProcedure(isUpdate, statusId, status, entityType);
    }

    public void memberRoleInput(String role) {
        boolean isUpdate = false;
        int roleId = isUpdate ? inputID("role") : 0;
        executeRoleProcedure(isUpdate, roleId, role);
    }

    public void memberStatusInput(String status, String entityType) {
        boolean isUpdate = false;
        int statusId = isUpdate ? inputID("memberstatus") : 0;
        executeMemberStatusProcedure(isUpdate, statusId, status, entityType);
    }


    // DATA UPDATE
    public void updateBook(Book book){
        try {
            executeBookProcedure(true,
                    book.idProperty().get(),
                    book.titleProperty().get(),
                    book.authorProperty().get(),
                    book.categoryIdProperty().get(),  // Use ID instead of code/name
                    book.genreIdProperty().get(),    // Use ID instead of code/name
                    book.statusIdProperty().get()    // Use StatusID instead of Status
            );
        } catch (Exception e) {
            System.err.println("Error updating book record: " + e.getMessage());
        }
    }

    public void updateMember(Member member){
        try {
            executeMemberProcedure(true,
                    member.idProperty().get(),
                    member.nameProperty().get(),
                    member.emailProperty().get(),
                    member.contactNumProperty().get(),
                    member.roleIdProperty().get(),    // Use RoleID instead of Role
                    member.statusIdProperty().get()   // Use StatusID instead of Status
            );
        } catch (Exception e) {
            System.err.println("Error updating member record: " + e.getMessage());
        }
    }

    public void updateBorrow(TransactionViewModel transaction, LocalDate returnDate, int statusId) {
        try {
            executeBorrowProcedure(
                    true, // This marks the operation as an update
                    transaction.getTransactionId(),
                    transaction.getBookId(),
                    transaction.getMemberId(),
                    transaction.getBorrowDate(),
                    transaction.getDueDate(),
                    returnDate, // Pass returnDate explicitly
                    statusId // Pass statusId explicitly
            );
            System.out.println("Borrow record updated successfully.");
        } catch (Exception e) {
            System.err.println("Error updating borrow record: " + e.getMessage());
        }
    }

    public void updateBookStatus(int bookId, int statusId)  {
        try {
            String procedureName = "UpdateBookStatus";
            Object[] parameters = {bookId, statusId};

            executeProcedure(procedureName, parameters);
        } catch (Exception e) {
            System.err.println("Error updating book status: " + e.getMessage());
        }
    }

    public void updateBorrowStatus(int borrowId, int statusId)  {
        try {
            String procedureName = "UpdateTransactionStatus";
            Object[] parameters = {borrowId, statusId};

            executeProcedure(procedureName, parameters);
        } catch (Exception e) {
            System.err.println("Error updating borrow status: " + e.getMessage());
        }
    }

    public void updateBookCategory(BookCategory category) {
        executeCategoryProcedure(true, category.getCategoryId(), category.getCategoryCode(), category.getCategoryName());
    }

    public void updateBookGenre(BookGenre genre) {
        executeGenreProcedure(true, genre.getGenreId(), genre.getGenreCode(), genre.getGenreName());
    }

    public void updateBookStatus(BookStatus status) {
        String entityType = "BOOK";
        executeStatusProcedure(true, status.getStatusId(), status.getStatus(), entityType);
    }

    public void updateMemberRole(MemberRole role) {
        executeRoleProcedure(true, role.getRoleId(), role.getRole());
    }

    public void updateMemberStatus(MemberStatus status) {
        String entityType = "MEMBER";
        executeMemberStatusProcedure(true, status.getStatusId(), status.getStatus(), entityType);
    }


    // DATA DELETION
    public void deleteBook(long bookId) {
        String query = "{CALL DeleteRecord(?, ?, ?)}";
        try (CallableStatement stmt = connection.prepareCall(query)) {
            stmt.setString(1, "Book"); // entityType
            stmt.setLong(2, bookId);    // recordId
            stmt.registerOutParameter(3, Types.VARCHAR); // errorMessage
            stmt.executeUpdate();
            String errorMessage = stmt.getString(3);
            if (errorMessage != null) {
                System.out.println("Error deleting book: " + errorMessage);
            } else {
                System.out.println("Book deleted successfully.");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting book", e);
        }
    }

    public void deleteMember(int memberId) {
        String query = "{CALL DeleteRecord(?, ?, ?)}";
        try (CallableStatement stmt = connection.prepareCall(query)) {
            stmt.setString(1, "Member");  // entityType
            stmt.setInt(2, memberId);     // recordId
            stmt.registerOutParameter(3, Types.VARCHAR); // errorMessage
            stmt.executeUpdate();
            String errorMessage = stmt.getString(3);
            if (errorMessage != null) {
                System.out.println("Error deleting member: " + errorMessage);
            } else {
                System.out.println("Member deleted successfully.");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting member", e);
        }
    }


    // STORED PROCEDURE EXECUTION
    private void executeBookProcedure(boolean isUpdate, int bookID, String title, String author, int categoryId, int genreId, int statusId) {
        try {
            System.out.println("executeBookProcedure is called");
            if (isUpdate) {
                executeProcedure("UpdateBook", new Object[]{bookID, title, author, categoryId, genreId, statusId});
                System.out.println("execute UpdateBook is called");
            } else {
                executeProcedure("InsertBook", new Object[]{title, author, categoryId, genreId, statusId});
                System.out.println("execute InsertBook is called");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error executing book procedure", e);
        }
    }

    private void executeMemberProcedure(boolean isUpdate, int memberID, String name, String email, String phone, int roleId, int statusId) {
        try {
            if (isUpdate) {
                executeProcedure("UpdateMember", new Object[]{memberID, name, email, phone, roleId, statusId});
            } else {
                executeProcedure("InsertMember", new Object[]{name, email, phone, roleId, statusId});
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error executing member procedure", e);
        }
    }

    public void executeBorrowProcedure(boolean isUpdate, int borrowId, int bookId, int memberId, LocalDate borrowDate, LocalDate dueDate, LocalDate returnDate, int statusId) {
        try {
            System.out.println("Execution of procedure commencing...");
            String procedureName;
            Object[] parameters;

            if (isUpdate) {
                procedureName = "UpdateBorrowReturnDate";
                parameters = new Object[]{borrowId, returnDate, statusId}; // Include StatusID for proper updates
            } else {
                procedureName = "InsertBorrow";
                parameters = new Object[]{bookId, memberId, borrowDate, dueDate, statusId};
            }

            executeProcedure(procedureName, parameters);
            System.out.println("Procedure successfully implemented");

            // Debugging output
            System.out.print("Parameters are: ");
            for (int i = 0; i < parameters.length; i++) {
                System.out.print((i + 1) + ". " + parameters[i] + " | ");
            }
            System.out.println();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error executing borrow procedure", e);
        }
    }

    private void executeCategoryProcedure(boolean isUpdate, int categoryId, String categoryCode, String categoryName) {
        if (isUpdate) {
            System.out.println(categoryCode + " - " + categoryName);
            executeProcedure("UpdateBookCategory", new Object[]{categoryId, categoryCode, categoryName});
        } else {
            executeProcedure("InsertBookCategory", new Object[]{categoryCode, categoryName});
        }
    }

    private void executeGenreProcedure(boolean isUpdate, int genreId, String genreCode, String genreName) {
        if (isUpdate) {
            executeProcedure("UpdateBookGenre", new Object[]{genreId, genreCode, genreName});
        } else {
            System.out.println(genreCode + " - " + genreName);
            executeProcedure("InsertBookGenre", new Object[]{genreCode, genreName});
        }
    }

    private void executeStatusProcedure(boolean isUpdate, int statusId, String status, String entityType) {
        if (isUpdate) {
            executeProcedure("UpdateStatus", new Object[]{statusId, status, entityType.equalsIgnoreCase("BOOK") ? "BOOK" : "MEMBER"}); // Assuming entityType is "BOOK" or "MEMBER"
        } else {
            executeProcedure("InsertStatus", new Object[]{status, entityType.equalsIgnoreCase("BOOK") ? "BOOK" : "MEMBER"});
        }
    }

    private void executeRoleProcedure(boolean isUpdate, int roleId, String role) {
        if (isUpdate) {
            executeProcedure("UpdateMemberRole", new Object[]{roleId, role});
        } else {
            executeProcedure("InsertMemberRole", new Object[]{role});
        }
    }

    private void executeMemberStatusProcedure(boolean isUpdate, int statusId, String status, String entityType) {
        if (isUpdate) {
            executeProcedure("UpdateStatus", new Object[]{statusId, status, entityType.equalsIgnoreCase("MEMBER") ? "MEMBER" : "BOOK"}); // Assuming entityType is "MEMBER" or "BOOK"
        } else {
            executeProcedure("InsertStatus", new Object[]{status, entityType.equalsIgnoreCase("MEMBER") ? "MEMBER" : "BOOK"});
        }
    }

    private void executeProcedure(String procedureName, Object[] parameters) {
        try (CallableStatement callableStatement = connection.prepareCall("{CALL " + procedureName + "(" + "?,".repeat(parameters.length) + " ?)}")) {

            for (int i = 0; i < parameters.length; i++) {
                callableStatement.setObject(i + 1, parameters[i]);
            }
            callableStatement.registerOutParameter(parameters.length + 1, Types.VARCHAR);
            callableStatement.execute();
            String errorMessage = callableStatement.getString(parameters.length + 1);
            System.out.println("Total parameters: " + parameters.length);
            if (errorMessage != null) {
                System.out.println("Procedure response: " + errorMessage);
            } else {
                System.out.println("Success.");
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }


    // UTILITY
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

    public static int inputID(String entityType) {
        int id = 0;
        try {
            if (validator.idExists(id, entityType)) {
                System.out.println("ID exists. ");
            } else {
                System.out.println("Invalid " + entityType + " ID.");
            }
        } catch (java.util.InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number.");
        }


        return id;
    }
}
