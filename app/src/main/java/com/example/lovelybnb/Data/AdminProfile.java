package com.example.lovelybnb.Data;

public class AdminProfile {
    public String FullName, Email, Gender, PhoneNumber, Avatar;

    public AdminProfile(){}

    public AdminProfile(String Avatar, String FullName, String email, String gender, String PhoneNumber){
        this.Avatar = Avatar;
        this.FullName = FullName;
        this.Email = email;
        this.Gender = gender;
        this.PhoneNumber = PhoneNumber;
    }

}
