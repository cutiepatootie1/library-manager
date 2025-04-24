package com.mimirlib.mimir.Data;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Member extends DatabaseConnection {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty name;
    private final SimpleStringProperty email;
    private final SimpleStringProperty contactNum;
    private final SimpleStringProperty role;
    private final SimpleStringProperty status;

    public Member(int memId, String memName, String memEmail, String memContactNum, String memRole, String memStatus) {
        this.id = new SimpleIntegerProperty(memId);
        this.name = new SimpleStringProperty(memName);
        this.email = new SimpleStringProperty(memEmail);
        this.contactNum = new SimpleStringProperty(memContactNum);
        this.role = new SimpleStringProperty(memRole);
        this.status = new SimpleStringProperty(memStatus);
    }

    public int getId() {return id.get();}

    /**
    // Getters
    public String getName() {return name.get();}
    public String getEmail() {return email.get();}
    public String getContactNum() {return contactNum.get();}
    public String getRole() {return role.get();}
    public String getStatus() {return status.get();}

    // Setters
    public void setId(int memId) {id.set(memId);}
    public void setName(String memName) {name.set(memName);}
    public void setEmail(String memEmail) {email.set(memEmail);}
    public void setContactNum(String memContactNum) {contactNum.set(memContactNum);}
    public void setRole(String memRole) {role.set(memRole);}
    public void setStatus(String memStatus) {status.set(memStatus);}
**/

    // Property Getters (for use in JavaFX bindings)
    public SimpleIntegerProperty idProperty() {return id;}
    public SimpleStringProperty nameProperty() {return name;}
    public SimpleStringProperty emailProperty() {return email;}
    public SimpleStringProperty contactNumProperty() {return contactNum;}
    public SimpleStringProperty roleProperty() {return role;}
    public SimpleStringProperty statusProperty() {return status;}
}

