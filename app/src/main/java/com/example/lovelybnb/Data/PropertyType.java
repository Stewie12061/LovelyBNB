package com.example.lovelybnb.Data;

import java.io.Serializable;

public class PropertyType{
    public String propertyImg, propertyName, id;

    public PropertyType(){
    }

    public PropertyType(String propertyImg, String propertyName, String id){
        this.propertyImg = propertyImg;
        this.propertyName = propertyName;
        this.id = id;
    }

    public PropertyType(String propertyImg, String propertyName){
        this.propertyImg = propertyImg;
        this.propertyName = propertyName;
    }

    public String getPropertyImg() {
        return propertyImg;
    }

    public void setPropertyImg(String propertyImg) {
        this.propertyImg = propertyImg;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
