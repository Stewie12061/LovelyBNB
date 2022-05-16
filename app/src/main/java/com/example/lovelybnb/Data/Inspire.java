package com.example.lovelybnb.Data;

public class Inspire {
    String inspirePlace;
    String inspireDes;
    String inspireImg;

    public Inspire(){

    }

    public Inspire(String inspirePlace, String inspireDes, String inspireImg){
        this.inspirePlace = inspirePlace;
        this.inspireDes = inspireDes;
        this.inspireImg = inspireImg;
    }

    public String getInspirePlace() {
        return inspirePlace;
    }

    public void setInspirePlace(String inspirePlace) {
        this.inspirePlace = inspirePlace;
    }

    public String getInspireDes() {
        return inspireDes;
    }

    public void setInspireDes(String inspireDes) {
        this.inspireDes = inspireDes;
    }

    public String getInspireImg() {
        return inspireImg;
    }

    public void setInspireImg(String inspireImg) {
        this.inspireImg = inspireImg;
    }
}
