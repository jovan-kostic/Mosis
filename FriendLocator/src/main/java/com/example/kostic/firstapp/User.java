package com.example.kostic.firstapp;

public class User {

    private String username;
    private Integer rank;

    public User(String username, int rank)
    {
        this.setUsername(username);
        this.setRank(rank);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }
}
