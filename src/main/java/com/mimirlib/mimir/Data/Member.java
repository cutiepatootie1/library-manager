package com.mimirlib.mimir.Data;

import javafx.beans.property.*;

public class Member {
    private final IntegerProperty id;
    private final StringProperty name;
    private final StringProperty email;
    private final StringProperty contactNum;
    private final IntegerProperty roleId;
    private final StringProperty role;
    private final IntegerProperty statusId;
    private final StringProperty status;

    public Member(int id, String name, String email, String contactNum, int roleId, String role, int statusId, String status) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.email = new SimpleStringProperty(email);
        this.contactNum = new SimpleStringProperty(contactNum);

        // Preserve fetched values instead of overriding them
        this.roleId = new SimpleIntegerProperty(roleId);
        this.role = new SimpleStringProperty(role);
        this.statusId = new SimpleIntegerProperty(statusId);
        this.status = new SimpleStringProperty(status);
    }

    // Property accessors
    public IntegerProperty idProperty() { return id; }
    public StringProperty nameProperty() { return name; }
    public StringProperty emailProperty() { return email; }
    public StringProperty contactNumProperty() { return contactNum; }
    public IntegerProperty roleIdProperty() { return roleId; }
    public StringProperty roleProperty() { return role; }
    public IntegerProperty statusIdProperty() { return statusId; }
    public StringProperty statusProperty() { return status; }

    // Standard Getter Methods
    public int getId() { return id.get(); }
    public String getName() { return name.get(); }
    public String getEmail() { return email.get(); }
    public String getContactNum() { return contactNum.get(); }
    public int getRoleId() { return roleId.get(); }
    public String getRole() { return role.get(); }
    public int getStatusId() { return statusId.get(); }
    public String getStatus() { return status.get(); }
}
