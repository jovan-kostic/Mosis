package com.example.kostic.firstapp;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.Marker;

public class EventMarker {

    private Marker marker;
    private Circle circle;
    private String user;
    private double longitude;
    private double latitude;
    private String info;

    public EventMarker(Marker marker, Circle circle, String user, double longitude, double latitude, String info)
    {
        this.setCircle(circle);
        this.setMarker(marker);
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

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public Circle getCircle() { return circle; }

    public void setCircle(Circle circle) { this.circle = circle; }
}