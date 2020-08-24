package com.example.fesco.classes;

public class Comment {

    private int id;
    private int user_id;
    private String title;
    private String username;
    private String content;
    private String date;
    private int food_related_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getFood_related_id() {
        return food_related_id;
    }

    public void setFood_related_id(int food_related_id) {
        this.food_related_id = food_related_id;
    }
}
