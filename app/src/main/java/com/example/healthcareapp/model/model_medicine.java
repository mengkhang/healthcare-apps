package com.example.healthcareapp.model;

import android.graphics.Bitmap;

import java.io.Serializable;

public class model_medicine implements Serializable {
    private String medName, avoid, des, priceRinggit, priceSen, suit;
    private Bitmap medImage;

    public model_medicine() {
    }

    public String getMedName() {
        return medName;
    }

    public String getMedNameNo_() {
        return medName.replace("_", " ");
    }

    public void setMedName(String medName) {
        this.medName = medName;
    }

    public String getAvoid() {
        return avoid;
    }

    public void setAvoid(String avoid) {
        this.avoid = avoid;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getPriceRinggit() {
        return priceRinggit;
    }

    public void setPriceRinggit(String priceRinggit) {
        this.priceRinggit = priceRinggit;
    }

    public String getPriceSen() {
        return priceSen;
    }

    public void setPriceSen(String priceSen) {
        this.priceSen = priceSen;
    }

    public String getSuit() {
        return suit;
    }

    public void setSuit(String suit) {
        this.suit = suit;
    }

    public Bitmap getMedImage() {
        return medImage;
    }

    public void setMedImage(Bitmap medImage) {
        this.medImage = medImage;
    }
}
