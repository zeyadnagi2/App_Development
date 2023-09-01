package com.example.bloodapp.model;

public class User {
    String name ,email,id,idnumber,bloodgroup,phonenumber,profilepictureuri,search, type;

    public User() {
    }

    public User(String name, String email, String id, String idnumber, String bloodgroup, String phonenumber, String profilepictureuri, String search, String type) {
        this.name = name;
        this.email = email;
        this.id = id;
        this.idnumber = idnumber;
        this.bloodgroup = bloodgroup;
        this.phonenumber = phonenumber;
        this.profilepictureuri = profilepictureuri;
        this.search = search;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }

    public String getIdnumber() {
        return idnumber;
    }

    public String getBloodgroup() {
        return bloodgroup;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public String getProfilepictureuri() {
        return profilepictureuri;
    }

    public String getSearch() {
        return search;
    }

    public String getType() {
        return type;
    }
}
