package com.triet.quyetchat;

public class Friends {
  private String date;
  private String name;
  private String image;
  private String id;
  private boolean online;


    public Friends() {
    }

    public Friends(String date, String name, String image, String id, boolean online) {
        this.date = date;
        this.name = name;
        this.image = image;
        this.id = id;
        this.online = online;

    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
