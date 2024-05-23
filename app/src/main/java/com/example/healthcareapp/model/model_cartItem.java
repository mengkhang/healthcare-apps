package com.example.healthcareapp.model;

public class model_cartItem {
    private String medicineID, medicineName;
    private int quantity;
    private String ringgit, sen;
    private String totalPrice;

    public model_cartItem(String medicineID, String medicineName, int quantity, String ringgit, String sen, String totalPrice) {
        this.medicineID = medicineID;
        this.medicineName = medicineName;
        this.quantity = quantity;
        this.ringgit = ringgit;
        this.sen = sen;
        this.totalPrice = totalPrice;
    }

    public String getMedicineID() {
        return medicineID;
    }

    public void setMedicineID(String medicineID) {
        this.medicineID = medicineID;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getRinggit() {
        return ringgit;
    }

    public void setRinggit(String ringgit) {
        this.ringgit = ringgit;
    }

    public String getSen() {
        return sen;
    }

    public void setSen(String sen) {
        this.sen = sen;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getTotalPrice() {
        return totalPrice;
    }
}
