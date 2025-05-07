package com.mimirlib.mimir.Data;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class BookCategory {

    private final SimpleIntegerProperty categoryId;
    private final SimpleStringProperty categoryCode;
    private final SimpleStringProperty categoryName;

    private static BookCategory selectedCategory; // Store selected category globally


    public BookCategory(int categoryId, String categoryCode, String categoryName) { // , int categoryId
        this.categoryId = new SimpleIntegerProperty(categoryId);
        this.categoryCode = new SimpleStringProperty(categoryCode);
        this.categoryName = new SimpleStringProperty(categoryName);
    }

    public static void setSelectedCategory(BookCategory category) { selectedCategory = category; }
    public static BookCategory getSelectedCategory() { return selectedCategory; }


    public int getCategoryId() { return categoryId.get(); }
    public String getCategoryCode() { return categoryCode.get(); }
    public String getCategoryName() { return categoryName.get(); }

    public IntegerProperty categoryIdProperty() { return categoryId; }
    public StringProperty categoryCodeProperty() { return categoryCode; }
    public StringProperty categoryNameProperty() { return categoryName; }

    public void setCategoryCode(String categoryCode) { this.categoryName.set(categoryCode); }
    public void setCategoryName(String categoryName) { this.categoryName.set(categoryName); }
}