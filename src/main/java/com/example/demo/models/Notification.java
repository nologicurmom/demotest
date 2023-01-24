package com.example.demo.models;
import org.springframework.data.mongodb.core.mapping.Document;
import java.io.Serializable;

@Document(collection = "notification")
public class Notification implements Serializable {
    private String tokenUser;

    private int idEnchere;

    private String description;

    private String dateNotification;

    public Notification() {
    }

    public Notification(String tokenUser,int idEnchere, String description, String dateNotification) {
        this.tokenUser = tokenUser;
        this.idEnchere = idEnchere;
        this.description = description;
        this.dateNotification = dateNotification;
    }

    public String getTokenUser() {
        return tokenUser;
    }

    public void setTokenUser(String tokenUser) {
        this.tokenUser = tokenUser;
    }
    public int getIdEnchere() {
        return idEnchere;
    }

    public void setIdEnchere(int idEnchere) {
        this.idEnchere = idEnchere;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDateNotification() {
        return dateNotification;
    }

    public void setDateNotification(String dateNotification) {
        this.dateNotification = dateNotification;
    }
}
