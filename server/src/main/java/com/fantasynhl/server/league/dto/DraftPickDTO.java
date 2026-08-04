package com.fantasynhl.server.league.dto;

public class DraftPickDTO {
    private Long playerId;
    private String playerName;
    private String position;
    private String positionCode;
    private Long teamId;
    private String teamName;
    private int pickOrder;
    private int points;
    private String nhlTeam; // NEW FIELD

    public DraftPickDTO() {}

    public DraftPickDTO(Long playerId, String playerName, String position, String positionCode,
                        Long teamId, String teamName, int pickOrder, int points, String nhlTeam) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.position = position;
        this.positionCode = positionCode;
        this.teamId = teamId;
        this.teamName = teamName;
        this.pickOrder = pickOrder;
        this.points = points;
        this.nhlTeam = nhlTeam;
    }

    // Getters / setters
    public Long getPlayerId() { return playerId; }
    public void setPlayerId(Long playerId) { this.playerId = playerId; }

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getPositionCode() { return positionCode; }
    public void setPositionCode(String positionCode) { this.positionCode = positionCode; }

    public Long getTeamId() { return teamId; }
    public void setTeamId(Long teamId) { this.teamId = teamId; }

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }

    public int getPickOrder() { return pickOrder; }
    public void setPickOrder(int pickOrder) { this.pickOrder = pickOrder; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }

    public String getNhlTeam() { return nhlTeam; }
    public void setNhlTeam(String nhlTeam) { this.nhlTeam = nhlTeam; }
}
