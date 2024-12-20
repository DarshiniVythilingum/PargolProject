package com.example.practicefinalproj.AdminDashboard;

public class Book {
    private String id;
    private String title;
    private String description;
    private String author;

    public Book(String id, String title, String description, String author) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.author = author;
    }

    //Right-Click -> Generate -> Getter and Setter
    public String getId(){return id;}
    public void setId(String id) {this.id = id;}

    public String getTitle() {return title;}
    public void setTitle(String title) {this.title = title;}

    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}

    public String getAuthor() {return author;}
    public void setAuthor(String author) {this.author = author;}

    //Generate -> toString
    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", author='" + author + '\'' +
                '}';
    }
}
