package com.example.contact.entity;

import android.net.Uri;

import java.io.Serializable;


public class Contact implements Serializable {
    private boolean isPinned;
    private boolean isFavorite;
    private String firstName;
    private String lastName;
    private String birthDate;
    private String email;
    private String phone;
    private String gender;
    private String imageUri;
    public Contact(boolean isPinned, boolean isFavorite, String firstName, String lastName, String birthDate, String email, String phone, String gender, Uri imageUri) {
        this.isPinned = isPinned;
        this.isFavorite = isFavorite;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.imageUri = imageUri != null ? imageUri.toString() : null;
    }

    public boolean isPinned() { return isPinned; }
    public boolean isFavorite() {
        return isFavorite;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getGender() {
        return gender;
    }

    public Uri getImage() { return imageUri != null ? Uri.parse(imageUri) : null; }

    public void setPinned() {
        this.isPinned = this.isPinned ? false : true;
    }

    @Override
    public String toString() {
        return "\nName : " + firstName +
                "\nAge : " + lastName +
                "\nBirth date : " + birthDate +
                "\nEmail : " + email +
                "\nPhone : " + phone +
                "\nGender : " + gender;
    }
}
