package com.example.healthcareapp.Firestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface FirestoreCallback {
    void onFirestoreCallback_exists (Map<String, Object> data);
    void onFirestoreCallback_notexists();
}
