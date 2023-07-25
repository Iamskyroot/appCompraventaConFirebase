package com.example.compraeintercambia.model;

import com.google.firebase.database.Exclude;

public class User {

    private String name;
    private String tel;
    private String email;
    private String password;
    private String type;
    private String type2;
    private String especification;

    public User(){

    }

    public User(String name, String tel, String email, String password, String type,String type2, String especification) {
        this.name = name;
        this.tel = tel;
        this.email = email;
        this.password = password;
        this.type=type;
        this.type2=type2;
        this.especification=especification;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType2() {
        return type2;
    }

    public void setType2(String type2) {
        this.type2 = type2;
    }

    public String getEspecification() {
        return especification;
    }

    public void setEspecification(String especification) {
        this.especification = especification;
    }
}
