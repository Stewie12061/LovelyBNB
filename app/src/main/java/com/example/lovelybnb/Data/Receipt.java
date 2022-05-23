package com.example.lovelybnb.Data;

import android.widget.ImageView;

public class Receipt {
    String receiptTimeCheckin, receiptTimeCheckout, peopleQuantity, receiptName, receiptPrice, receiptAddress, receiptPlace, receiptDaycheckin, receiptDaycheckout, receiptImg, dayStay, receiptContact;

    public Receipt(){}

    public Receipt(String receiptTimeCheckin, String receiptTimeCheckout, String peopleQuantity,String receiptContact,String receiptName, String receiptAddress, String receiptPlace, String receiptPrice, String receiptDaycheckin, String receiptDaycheckout,String receiptImg, String dayStay){
        this.receiptAddress = receiptAddress;
        this.receiptName = receiptName;
        this.receiptPlace = receiptPlace;
        this.receiptPrice = receiptPrice;
        this.receiptDaycheckin = receiptDaycheckin;
        this.receiptDaycheckout = receiptDaycheckout;
        this.receiptImg = receiptImg;
        this.dayStay = dayStay;
        this.receiptContact = receiptContact;
        this.peopleQuantity = peopleQuantity;
        this.receiptTimeCheckin = receiptTimeCheckin;
        this.receiptTimeCheckout = receiptTimeCheckout;
    }

    public String getReceiptTimeCheckin() {
        return receiptTimeCheckin;
    }

    public void setReceiptTimeCheckin(String receiptTimeCheckin) {
        this.receiptTimeCheckin = receiptTimeCheckin;
    }

    public String getReceiptTimeCheckout() {
        return receiptTimeCheckout;
    }

    public void setReceiptTimeCheckout(String receiptTimeCheckout) {
        this.receiptTimeCheckout = receiptTimeCheckout;
    }

    public String getPeopleQuantity() {
        return peopleQuantity;
    }

    public void setPeopleQuantity(String peopleQuantity) {
        this.peopleQuantity = peopleQuantity;
    }

    public String getReceiptContact() {
        return receiptContact;
    }

    public void setReceiptContact(String receiptContact) {
        this.receiptContact = receiptContact;
    }

    public String getDayStay() {
        return dayStay;
    }

    public void setDayStay(String dayStay) {
        this.dayStay = dayStay;
    }

    public String getReceiptName() {
        return receiptName;
    }

    public void setReceiptName(String receiptName) {
        this.receiptName = receiptName;
    }

    public String getReceiptPrice() {
        return receiptPrice;
    }

    public void setReceiptPrice(String receiptPrice) {
        this.receiptPrice = receiptPrice;
    }

    public String getReceiptAddress() {
        return receiptAddress;
    }

    public void setReceiptAddress(String receiptAddress) {
        this.receiptAddress = receiptAddress;
    }

    public String getReceiptPlace() {
        return receiptPlace;
    }

    public void setReceiptPlace(String receiptPlace) {
        this.receiptPlace = receiptPlace;
    }

    public String getReceiptDaycheckin() {
        return receiptDaycheckin;
    }

    public void setReceiptDaycheckin(String receiptDaycheckin) {
        this.receiptDaycheckin = receiptDaycheckin;
    }

    public String getReceiptDaycheckout() {
        return receiptDaycheckout;
    }

    public void setReceiptDaycheckout(String receiptDaycheckout) {
        this.receiptDaycheckout = receiptDaycheckout;
    }

    public String getReceiptImg() {
        return receiptImg;
    }

    public void setReceiptImg(String receiptImg) {
        this.receiptImg = receiptImg;
    }
}
