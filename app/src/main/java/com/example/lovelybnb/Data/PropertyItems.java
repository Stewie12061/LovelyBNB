package com.example.lovelybnb.Data;

public class PropertyItems {
    String itemDescription, itemImage, propertyTypeId, itemName, itemPlace, itemPrice, itemRating;

    public PropertyItems(){
    }

    public PropertyItems(String itemDescription, String propertyTypeId, String itemImage, String itemName, String itemPlace, String itemPrice, String itemRating){
        this.itemDescription = itemDescription;
        this.propertyTypeId = propertyTypeId;
        this.itemImage = itemImage;
        this.itemName = itemName;
        this.itemPlace = itemPlace;
        this.itemPrice = itemPrice;
        this.itemRating = itemRating;
    }


    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getItemImage() {
        return itemImage;
    }

    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }

    public String getPropertyTypeId() {
        return propertyTypeId;
    }

    public void setPropertyTypeId(String propertyTypeId) {
        this.propertyTypeId = propertyTypeId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemPlace() {
        return itemPlace;
    }

    public void setItemPlace(String itemPlace) {
        this.itemPlace = itemPlace;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemRating() {
        return itemRating;
    }

    public void setItemRating(String itemRating) {
        this.itemRating = itemRating;
    }

}
