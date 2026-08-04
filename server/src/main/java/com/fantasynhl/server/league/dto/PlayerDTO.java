package com.fantasynhl.server.league.dto;

import com.fantasynhl.server.league.Player;

public class PlayerDTO {
    private Long id;
    private String name;
    private String position;
    private String nhlTeam;
    private String positionCode;
    private int points;
    private String role;

    public PlayerDTO(Player player) {
        this.id = player.getId();
        this.name = player.getName();
        this.position = player.getPosition();
        this.nhlTeam = player.getNhlTeam();
        this.positionCode = player.getPositionCode();
        this.points = player.getPoints(); 
        this.role = "None";
    }

    public PlayerDTO(Player player, String role) {
        this.id = player.getId();
        this.name = player.getName();
        this.position = player.getPosition();
        this.positionCode = player.getPositionCode();
        this.nhlTeam = player.getNhlTeam();
        this.points = player.getPoints();
        this.role = role != null ? role : "None";
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getPosition() { return position; }
    public String getNhlTeam() { return nhlTeam; }
    public String getPositionCode() { return positionCode; }
    public int getPoints() { return points; } 
    public String getRole() { return role; }
}
