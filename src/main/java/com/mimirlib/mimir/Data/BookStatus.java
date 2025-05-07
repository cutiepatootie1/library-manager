package com.mimirlib.mimir.Data;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

public class BookStatus {

    private final SimpleIntegerProperty statusId;
    private final SimpleStringProperty status;

    private static BookStatus selectedStatus; // Store selected status globally


    public BookStatus(String status, int statusId) {
        this.statusId = new SimpleIntegerProperty(statusId);
        this.status = new SimpleStringProperty(status);
    }

    public static void setSelectedStatus(BookStatus status) { selectedStatus = status; }
    public static BookStatus getSelectedStatus() { return selectedStatus; }

    public int getStatusId() { return statusId.get(); }
    public String getStatus() { return status.get(); }

    public IntegerProperty statusIdProperty() { return statusId; }
    public StringProperty statusProperty() { return status; }

    // public void setStatusId(int statusId) { this.statusId.set(statusId); }
    public void setStatus(String status) { this.status.set(status); }
}