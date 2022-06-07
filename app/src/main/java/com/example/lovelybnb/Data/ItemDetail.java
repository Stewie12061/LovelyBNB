package com.example.lovelybnb.Data;

public class ItemDetail {
    String address, checkIn, checkOut, hostAvatar, hostEmail, hostName, hostPhone;

    public ItemDetail(){}

    public ItemDetail(String address, String checkIn, String checkOut, String hostAvatar, String hostEmail, String hostName, String hostPhone){
        this.address = address;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.hostAvatar = hostAvatar;
        this.hostEmail = hostEmail;
        this.hostName = hostName;
        this.hostPhone = hostPhone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(String checkIn) {
        this.checkIn = checkIn;
    }

    public String getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(String checkOut) {
        this.checkOut = checkOut;
    }

    public String getHostAvatar() {
        return hostAvatar;
    }

    public void setHostAvatar(String hostAvatar) {
        this.hostAvatar = hostAvatar;
    }

    public String getHostEmail() {
        return hostEmail;
    }

    public void setHostEmail(String hostEmail) {
        this.hostEmail = hostEmail;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getHostPhone() {
        return hostPhone;
    }

    public void setHostPhone(String hostPhone) {
        this.hostPhone = hostPhone;
    }
}
