package com.example.healthcareapp.model;

import java.io.Serializable;

public class model_orderArray implements Serializable {
    String medID, medName, price;
    int qty;

    public model_orderArray(String medID, String medName, String price, int qty) {
        this.medID = medID;
        this.medName = medName;
        this.price = price;
        this.qty = qty;
    }

    public String getMedID() {
        return medID;
    }

    public void setMedID(String medID) {
        this.medID = medID;
    }

    public String getMedName() {
        return medName;
    }

    public void setMedName(String medName) {
        this.medName = medName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }
}