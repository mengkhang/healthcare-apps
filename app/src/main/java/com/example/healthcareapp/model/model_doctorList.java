package com.example.healthcareapp.model;

import java.io.Serializable;

public class model_doctorList implements Serializable { //implements serializable so it can pass as parameter in intent.putExtra()
    String Name;
    String specialties, accomplishment, uid;

    public model_doctorList(String name, String specialties, String accomplishment,  String uid) {
        Name = name;
        this.specialties = specialties;
        this.accomplishment = accomplishment;
        this.uid = uid;
    }


    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }




    public String getSpecialties() {
        return specialties;
    }

    public void setSpecialties(String specialties) {
        this.specialties = specialties;
    }

    public String getAccomplishment() {
        return accomplishment;
    }

    public void setAccomplishment(String accomplishment) {
        this.accomplishment = accomplishment;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

}
