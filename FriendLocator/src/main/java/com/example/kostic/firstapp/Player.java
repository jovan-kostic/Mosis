package com.example.kostic.firstapp;

import com.google.android.gms.maps.model.Marker;

public class Player {

    private Marker marker;
    private String username;
    private Double longitude;
    private Double latitude;
    private String team;
    private Integer rank;

    public Player(Marker marker, String username, Double longitude, Double latitude, String team, int rank)
    {
        this.setMarker(marker);
        this.setUsername(username);
        this.setLongitude(longitude);
        this.setLatitude(latitude);
        this.setTeam(team);
        this.setRank(rank);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }
}
