package com.mimirlib.mimir.Data;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

public class MemberStatus {

    private final SimpleIntegerProperty statusId;
    private final SimpleStringProperty status;

    public MemberStatus(String status, int statusId) {
        this.statusId = new SimpleIntegerProperty(statusId);
        this.status = new SimpleStringProperty(status);
    }

    public int getStatusId() { return statusId.get(); }
    public String getStatus() { return status.get(); }

    public IntegerProperty statusIdProperty() { return statusId; }
    public StringProperty statusProperty() { return status; }

    // public void setStatusId(int statusId) { this.statusId.set(statusId); }
    public void setStatus(String status) { this.status.set(status); }
}