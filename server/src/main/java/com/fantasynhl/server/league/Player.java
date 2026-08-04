package com.fantasynhl.server.league;

import jakarta.persistence.*;

@Entity
@Table(name = "players")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Basic Info
    private String name;

    @Column(name = "nhl_team")
    private String nhlTeam;

    private String position;

    // Extended player details
    @Column(name = "position_code")
    private String positionCode;      // e.g., "C", "L", "R", "D", "G"

    @Column(name = "headshot_url")
    private String headshotUrl;       // URL for player headshot image

    @Column(name = "sweater_number")
    private int sweaterNumber;        

    // Stats fields for skaters
    private int goals;
    private int assists;

    // Stats fields for goalies
    private int wins;
    private int shutouts;

    // Player total points
    private int points;

    // Captain role
    @Column(name = "captain_role")
    private String captainRole = "NONE"; // Possible values: "CAPTAIN", "ASSISTANT", "NONE"

    // Default constructor required by JPA
    public Player() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNhlTeam() {
        return nhlTeam;
    }

    public void setNhlTeam(String nhlTeam) {
        this.nhlTeam = nhlTeam;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPositionCode() {
        return positionCode;
    }

    public void setPositionCode(String positionCode) {
        this.positionCode = positionCode;
    }

    public String getHeadshotUrl() {
        return headshotUrl;
    }

    public void setHeadshotUrl(String headshotUrl) {
        this.headshotUrl = headshotUrl;
    }

    public int getSweaterNumber() {
        return sweaterNumber;
    }

    public void setSweaterNumber(int sweaterNumber) {
        this.sweaterNumber = sweaterNumber;
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

    public String getCaptainRole() {
        return captainRole;
    }

    public void setCaptainRole(String captainRole) {
        this.captainRole = captainRole;
    }
}
