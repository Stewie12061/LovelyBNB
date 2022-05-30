package com.example.lovelybnb.Data;

public class ReadWriteUserDetails {
    public String FullName, Email, Gender, PhoneNumber, Role;

    //Constructor
    public ReadWriteUserDetails(){}

    public ReadWriteUserDetails(String Role, String FullName, String email, String gender, String PhoneNumber){
        this.Role = Role;
        this.FullName = FullName;
        this.Email = email;
        this.Gender = gender;
        this.PhoneNumber = PhoneNumber;
    }
    public ReadWriteUserDetails(String FullName, String email, String gender, String PhoneNumber){
        this.FullName = FullName;
        this.Email = email;
        this.Gender = gender;
        this.PhoneNumber = PhoneNumber;
    }


    public ReadWriteUserDetails(String gender, String FullName, String PhoneNumber) {
        this.FullName = FullName;
        this.Gender = gender;
        this.PhoneNumber = PhoneNumber;
    }
}
