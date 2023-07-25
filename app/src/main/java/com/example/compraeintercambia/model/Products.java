package com.example.compraeintercambia.model;

import com.google.firebase.database.Exclude;

public class Products {

    private String id;
    private String img;
    private String img2;
    private String type;
    private String price;
    private String description;
    private String changeble;
    private String userName;
    private String userTel;

    public Products(){

    }

    public Products(String id,String img,String img2, String type, String price, String description, String changeble,
                    String userName,String userTel) {
        this.id=id;
        this.img = img;
        this.img2 = img2;
        this.type = type;
        this.price = price;
        this.description = description;
        this.changeble = changeble;
        this.userName=userName;
    }

    public String getId() {
        return id;
    }
    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getImg2() {
        return img2;
    }

    public void setImg2(String img2) {
        this.img2 = img2;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getChangeble() {
        return changeble;
    }

    public void setChangeble(String changeble) {
        this.changeble = changeble;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserTel() {
        return userTel;
    }

    public void setUserTel(String userTel) {
        this.userTel = userTel;
    }
}
