package com.example.healthcareapp.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class model_appointmentList implements Serializable {

    String patientUID, date, time, patientName, patientPhone, ConsultationType, appointmentID, doctorUID;
    LocalDateTime startDateTime, endDateTime;

    public model_appointmentList() {

    }

    public String getPatientUID() {
        return patientUID;
    }

    public void setPatientUID(String patientUID) {
        this.patientUID = patientUID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientPhone() {
        return patientPhone;
    }

    public void setPatientPhone(String patientPhone) {
        this.patientPhone = patientPhone;
    }

    public String getConsultationType() {
        return ConsultationType;
    }

    public void setConsultationType(String consultationType) {
        ConsultationType = consultationType;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public String getAppointmentID() {
        return appointmentID;
    }

    public void setAppointmentID(String appointmentID) {
        this.appointmentID = appointmentID;
    }

    public String getDoctorUID() {
        return doctorUID;
    }

    public void setDoctorUID(String doctorUID) {
        this.doctorUID = doctorUID;
    }
}
