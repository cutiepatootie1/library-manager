package com.mimirlib.mimir.Data;


import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Book extends DatabaseConnection {

    private final SimpleIntegerProperty id  ;
    private final SimpleStringProperty title;
    private final SimpleStringProperty category;
    private final SimpleStringProperty author;
    private final SimpleStringProperty genre;
    private final SimpleStringProperty status;

    public Book(int id,String title, String author, String category, String genre, String status) {
        this.id = new SimpleIntegerProperty(id);
        this.title = new SimpleStringProperty(title);
        this.author =  new SimpleStringProperty(author);
        this.category =  new SimpleStringProperty(category);
        this.genre =  new SimpleStringProperty(genre);
        this.status =  new SimpleStringProperty(status);
    }

    public IntegerProperty idProperty(){return id;}
    public StringProperty titleProperty(){return title;}
    public StringProperty authProperty(){return author;}
    public StringProperty catProperty(){return category;}
    public StringProperty genreProperty(){return genre;}
    public StringProperty statusProperty(){return status;}

    public void setBookID(int id){
        this.id.set(id);
    }
    public void setTitle(String title){
        this.title.set(title);
    }
    public void setAuthor(String author){
        this.author.set(author);
    }
    public void setCategory(String category){
        this.category.set(category);
    }
    public void setGenre(String genre){
        this.genre.set(genre);
    }
    public void setStatus(String status){
        this.status.set(status);
    }

    public long getId(){ return id.get();}
    public String getTitle(){return title.get();}
    public String getAuthor(){return author.get();}
    public String getCategory(){return category.get();}
    public String getGenre(){return genre.get();}
    public String getStatus(){return status.get();}
}