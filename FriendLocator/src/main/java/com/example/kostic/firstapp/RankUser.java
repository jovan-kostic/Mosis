package com.example.kostic.firstapp;

public class RankUser {

    private Integer place;
    private String username;
    private String team;
    private Integer rank;

    public RankUser(int place, String username, String team, int rank)
    {
        this.setPlace(place);
        this.setUsername(username);
        this.setTeam(team);
        this.setRank(rank);
    }

    public Integer getPlace() {return place;}

    public void setPlace(Integer place) {this.place = place;}

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


}
