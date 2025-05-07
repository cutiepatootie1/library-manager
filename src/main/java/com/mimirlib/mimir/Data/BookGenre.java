package com.mimirlib.mimir.Data;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class BookGenre {

    private final SimpleIntegerProperty genreId;
    private final SimpleStringProperty genreCode;
    private final SimpleStringProperty genreName;

    private static BookGenre selectedGenre; // Store selected genre globally


    public BookGenre(int genreId, String genreCode, String genreName) { // , int genreId
        this.genreId = new SimpleIntegerProperty(genreId);
        this.genreCode = new SimpleStringProperty(genreCode);
        this.genreName = new SimpleStringProperty(genreName);
    }

    public static void setSelectedGenre(BookGenre genre) { selectedGenre = genre; }
    public static BookGenre getSelectedGenre() { return selectedGenre; }

    public int getGenreId() { return genreId.get(); }
    public String getGenreCode() { return genreCode.get(); }
    public String getGenreName() { return genreName.get(); }

    public IntegerProperty genreIdProperty() { return genreId; }
    public StringProperty genreCodeProperty() { return genreCode; }
    public StringProperty genreNameProperty() { return genreName; }

    public void setGenreCode(String genreCode) { this.genreName.set(genreCode); }
    public void setGenreName(String genreName) { this.genreName.set(genreName); }
}