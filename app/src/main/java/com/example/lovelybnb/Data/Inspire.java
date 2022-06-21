package com.example.lovelybnb.Data;

public class Inspire {
    String place;
    String description;
    String image;

    public Inspire() {

    }

    public Inspire(String place, String description, String image) {
        this.place = place;
        this.description = description;
        this.image = image;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
