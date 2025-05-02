package com.mimirlib.mimir.Data;

import javafx.beans.property.*;

public class TransactionViewModel {
    private final IntegerProperty transactionId;
    private final IntegerProperty bookId;
    private final StringProperty bookTitle;
    private final IntegerProperty memberId;
    private final StringProperty borrowerName;
    private final ObjectProperty<java.time.LocalDate> borrowDate;
    private final ObjectProperty<java.time.LocalDate> dueDate;
    private final ObjectProperty<java.time.LocalDate> returnDate;
    private final IntegerProperty statusId;  // Added status ID for backend tracking
    private final StringProperty bookStatus; // Added readable status for UI display

    public TransactionViewModel(int transactionId, int bookId, String bookTitle, int memberId, String borrowerName, java.time.LocalDate borrowDate, java.time.LocalDate dueDate, java.time.LocalDate returnDate, int statusId, String bookStatus) {
        this.transactionId = new SimpleIntegerProperty(transactionId);
        this.bookId = new SimpleIntegerProperty(bookId);
        this.bookTitle = new SimpleStringProperty(bookTitle);
        this.memberId = new SimpleIntegerProperty(memberId);
        this.borrowerName = new SimpleStringProperty(borrowerName);
        this.borrowDate = new SimpleObjectProperty<>(borrowDate);
        this.dueDate = new SimpleObjectProperty<>(dueDate);
        this.returnDate = new SimpleObjectProperty<>(returnDate);
        this.statusId = new SimpleIntegerProperty(statusId);
        this.bookStatus = new SimpleStringProperty(bookStatus);
    }

    // Property accessors
    public IntegerProperty transactionIdProperty() { return transactionId; }
    public IntegerProperty bookIdProperty() { return bookId; }
    public StringProperty bookTitleProperty() { return bookTitle; }
    public IntegerProperty memberIdProperty() { return memberId; }
    public StringProperty borrowerNameProperty() { return borrowerName; }
    public ObjectProperty<java.time.LocalDate> borrowDateProperty() { return borrowDate; }
    public ObjectProperty<java.time.LocalDate> dueDateProperty() { return dueDate; }
    public ObjectProperty<java.time.LocalDate> returnDateProperty() { return returnDate; }
    public IntegerProperty statusIdProperty() { return statusId; }
    public StringProperty bookStatusProperty() { return bookStatus; }

    // Getters for direct field retrieval
    public int getTransactionId() { return transactionId.get(); }
    public int getBookId() { return bookId.get(); }
    public String getBookTitle() { return bookTitle.get(); }
    public int getMemberId() { return memberId.get(); }
    public String getBorrowerName() { return borrowerName.get(); }
    public java.time.LocalDate getBorrowDate() { return borrowDate.get(); }
    public java.time.LocalDate getDueDate() { return dueDate.get(); }
    public java.time.LocalDate getReturnDate() { return returnDate.get(); }
    public int getStatusId() { return statusId.get(); }  // Added status ID getter
    public String getBookStatus() { return bookStatus.get(); }  // Added readable status getter

    // Setter methods if needed for future updates
    public void setReturnDate(java.time.LocalDate returnDate) {
        this.returnDate.set(returnDate);
    }

    public void setStatusId(int statusId) {
        this.statusId.set(statusId);
    }

    public void setBookStatus(String bookStatus) {
        this.bookStatus.set(bookStatus);
    }
}
