package com.example.lovelybnb.Data;

public class ReadWriteUserDetails {
    public String fullName, Email, Gender, phoneNumber;

    //Constructor
    public ReadWriteUserDetails(){}

    public ReadWriteUserDetails(String fullname, String email, String gender, String phonenumber){
        this.fullName = fullname;
        this.Email = email;
        this.Gender = gender;
        this.phoneNumber = phonenumber;
    }


    public ReadWriteUserDetails(String gender, String fullname, String phonenumber) {
        this.fullName = fullname;
        this.Gender = gender;
        this.phoneNumber = phonenumber;
    }
}
