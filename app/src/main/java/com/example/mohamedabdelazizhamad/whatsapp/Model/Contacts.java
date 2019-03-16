package com.example.mohamedabdelazizhamad.whatsapp.Model;

public class Contacts {
    private String name;
    private String status;
    private String image;
    private String onlineStatus;
    public Contacts() {
    }

    public Contacts(String name, String status, String image,String onlineStatus) {
        this.name = name;
        this.status = status;
        this.image = image;
        this.onlineStatus = onlineStatus;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }
}
