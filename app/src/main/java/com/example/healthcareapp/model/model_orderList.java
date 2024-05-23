package com.example.healthcareapp.model;

import com.example.healthcareapp.utils.Time;
import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class model_orderList implements Serializable {
    String orderID, orderTotalAmount, patientUID, receiverEmail, receiverName, receiverPhone, status, courier, trackingNo;
    Boolean isCompleted;
    ArrayList<Map<String, Object>> orderArray;
    String orderDateTime, lastStatusUpdate;
//    transient Timestamp orderDateTime, lastStatusUpdate;
    /*Timestamp type by default cannot be serialized.
    In Java, the transient keyword is used to indicate that a field should not be serialized when the object is converted into a byte stream.
    When an object is serialized (for example, when passing an object between activities using Intent extras), the serialization process involves converting the object's state
    into a byte stream that can be saved to a file, sent over a network, or otherwise persisted.
    By marking a field as transient, you are telling the Java serialization mechanism to exclude that particular field from the serialization process.
    This can be useful in situations where certain fields of an object are not serializable or should not be persisted.*/

    public model_orderList(String orderID, String orderTotalAmount, String patientUID, String receiverEmail,
                           String receiverName, String receiverPhone, String status, Boolean isCompleted,
                           ArrayList<Map<String, Object>> orderArray, Timestamp orderDateTime, Timestamp lastStatusUpdate,
                           String courier, String trackingNo) {
        this.orderID = orderID;
        this.orderTotalAmount = orderTotalAmount;
        this.patientUID = patientUID;
        this.receiverEmail = receiverEmail;
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.status = status;
        this.isCompleted = isCompleted;
        this.orderArray = orderArray;
        this.orderDateTime = Time.convertTimestampToString(orderDateTime);
        this.lastStatusUpdate = Time.convertTimestampToString(lastStatusUpdate);
        this.courier = courier;
        this.trackingNo = trackingNo;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getOrderTotalAmount() {
        return orderTotalAmount;
    }

    public void setOrderTotalAmount(String orderTotalAmount) {
        this.orderTotalAmount = orderTotalAmount;
    }

    public String getPatientUID() {
        return patientUID;
    }

    public void setPatientUID(String patientUID) {
        this.patientUID = patientUID;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getCompleted() {
        return isCompleted;
    }

    public void setCompleted(Boolean completed) {
        isCompleted = completed;
    }

    public ArrayList<Map<String, Object>> getOrderArray() {
        return orderArray;
    }

    public void setOrderArray(ArrayList<Map<String, Object>> orderArray) {
        this.orderArray = orderArray;
    }

    public String getOrderDateTime() {
        return orderDateTime;
    }

    public void setOrderDateTime(Timestamp orderDateTime) {
        this.orderDateTime = Time.convertTimestampToString(orderDateTime);
    }

    public String getLastStatusUpdate() {
        return lastStatusUpdate;
    }

    public void setLastStatusUpdate(Timestamp lastStatusUpdate) {
        this.lastStatusUpdate = Time.convertTimestampToString(lastStatusUpdate);
    }

    public String getCourier() {
        return courier;
    }

    public void setCourier(String courier) {
        this.courier = courier;
    }

    public String getTrackingNo() {
        return trackingNo;
    }

    public void setTrackingNo(String trackingNo) {
        this.trackingNo = trackingNo;
    }
}
