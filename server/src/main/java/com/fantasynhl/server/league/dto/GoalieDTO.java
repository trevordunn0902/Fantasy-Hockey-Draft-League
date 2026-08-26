package com.fantasynhl.server.league.dto;

public class GoalieDTO {

    private Long playerId;
    private String goalieFullName;
    private int wins;
    private int shutouts;
    private int points;

    public GoalieDTO() {
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public String getGoalieFullName() {
        return goalieFullName;
    }

    public void setGoalieFullName(String goalieFullName) {
        this.goalieFullName = goalieFullName;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getShutouts() {
        return shutouts;
    }

    public void setShutouts(int shutouts) {
        this.shutouts = shutouts;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}