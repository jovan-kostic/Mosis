package com.example.kostic.firstapp;

public class Marker {

    private String user;
    private double longitude;
    private double latitude;
    private String info;

    public Marker(String user, double longitude, double latitude, String info)
    {
        this.setUser(user);
        this.setLongitude(longitude);
        this.setLatitude(latitude);
        this.setInfo(info);
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
