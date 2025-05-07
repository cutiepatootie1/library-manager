package com.mimirlib.mimir.Data;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

public class MemberRole {

    private final SimpleIntegerProperty roleId;
    private final SimpleStringProperty role;
    private static MemberRole selectedRole;  // Store selected role globally

    public MemberRole(String role , int roleId) {
        this.roleId = new SimpleIntegerProperty(roleId);
        this.role = new SimpleStringProperty(role);
    }

    // Getter for selected role
    public static MemberRole getSelectedRole() { return selectedRole; }

    // Setter for selected role
    public static void setSelectedRole(MemberRole role) { selectedRole = role; }

    public int getRoleId() { return roleId.get(); }
    public String getRole() { return role.get(); }

    public IntegerProperty roleIdProperty() { return roleId; }
    public StringProperty roleProperty() { return role; }

    // public void setRoleId(int roleId) { this.roleId.set(roleId); }
    public void setRole(String role) { this.role.set(role); }
}