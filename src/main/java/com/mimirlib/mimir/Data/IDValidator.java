package com.mimirlib.mimir.Data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class IDValidator {
    private static Connection connection; // Instance variable

    public IDValidator(Connection connection) { // Constructor takes Connection
        IDValidator.connection = connection;
    }

    boolean idExists(int id, String entityType) {
        String tableName;
        String columnName;

        switch (entityType.toLowerCase()) {
            case "book":
                tableName = "Books";
                columnName = "BookID";
                break;
            case "member":
                tableName = "Members";
                columnName = "MemberID";
                break;
            case "borrow":
                tableName = "Borrowing";
                columnName = "BorrowID";
                break;
            case "category":
                tableName = "bookcategories";
                columnName = "CategoryId";
                break;
            case "bookstatus":
                tableName = "bookstatus";
                columnName = "StatusID";
                break;
            case "genre":
                tableName = "bookgenres";
                columnName = "GenreId";
                break;
            case "role":
                tableName = "memberroles";
                columnName = "RoleID";
                break;
            case "memberstatus":
                tableName = "memberstatus";
                columnName = "StatusID";
                break;
            default:
                System.err.println("Invalid entity type: " + entityType);
                return false;
        }

        String query = "SELECT COUNT(*) FROM " + tableName + " WHERE " + columnName + " = ?";
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
