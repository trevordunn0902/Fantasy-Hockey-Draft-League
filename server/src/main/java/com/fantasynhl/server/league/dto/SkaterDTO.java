package com.fantasynhl.server.league.dto;

public class SkaterDTO {

    private Long playerId;
    private String skaterFullName;
    private int goals;
    private int assists;
    private int points;

    public SkaterDTO() {
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public String getSkaterFullName() {
        return skaterFullName;
    }

    public void setSkaterFullName(String skaterFullName) {
        this.skaterFullName = skaterFullName;
    }

    public int getGoals() {
        return goals;
    }

    public void setGoals(int goals) {
        this.goals = goals;
    }

    public int getAssists() {
        return assists;
    }

    public void setAssists(int assists) {
        this.assists = assists;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}