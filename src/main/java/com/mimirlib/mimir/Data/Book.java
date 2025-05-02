package com.mimirlib.mimir.Data;

import javafx.beans.property.*;

public class Book {
    private final IntegerProperty id;
    private final StringProperty title;
    private final StringProperty author;
    private final IntegerProperty categoryId;
    private final StringProperty categoryCode;
    private final StringProperty categoryName;
    private final IntegerProperty genreId;
    private final StringProperty genreCode;
    private final StringProperty genreName;
    private final IntegerProperty statusId;
    private final StringProperty status;

    public Book(int id, String title, String author, int categoryId, String categoryCode, String categoryName, int genreId, String genreCode, String genreName, int statusId, String status) {
        this.id = new SimpleIntegerProperty(id);
        this.title = new SimpleStringProperty(title);
        this.author = new SimpleStringProperty(author);
        this.categoryId = new SimpleIntegerProperty(categoryId);
        this.categoryCode = new SimpleStringProperty(categoryCode);
        this.categoryName = new SimpleStringProperty(categoryName);
        this.genreId = new SimpleIntegerProperty(genreId);
        this.genreCode = new SimpleStringProperty(genreCode);
        this.genreName = new SimpleStringProperty(genreName);
        this.statusId = new SimpleIntegerProperty(statusId);
        this.status = new SimpleStringProperty(status);
    }

    // Property Accessors
    public IntegerProperty idProperty() { return id; }
    public StringProperty titleProperty() { return title; }
    public StringProperty authorProperty() { return author; }
    public IntegerProperty categoryIdProperty() { return categoryId; }
    public StringProperty categoryCodeProperty() { return categoryCode; }
    public StringProperty categoryNameProperty() { return categoryName; }
    public IntegerProperty genreIdProperty() { return genreId; }
    public StringProperty genreCodeProperty() { return genreCode; }
    public StringProperty genreNameProperty() { return genreName; }
    public IntegerProperty statusIdProperty() { return statusId; }
    public StringProperty statusProperty() { return status; }

    // Getter Methods
    public int getId() { return id.get(); }
    public String getTitle() { return title.get(); }
    public String getAuthor() { return author.get(); }
    public int getCategoryId() { return categoryId.get(); }
    public String getCategoryCode() { return categoryCode.get(); }
    public String getCategoryName() { return categoryName.get(); }
    public int getGenreId() { return genreId.get(); }
    public String getGenreCode() { return genreCode.get(); }
    public String getGenreName() { return genreName.get(); }
    public int getStatusId() { return statusId.get(); }
    public String getStatus() { return status.get(); }
}
