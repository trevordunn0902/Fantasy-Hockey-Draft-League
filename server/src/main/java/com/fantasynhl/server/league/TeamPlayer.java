package com.fantasynhl.server.league;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "team_players")
public class TeamPlayer implements Serializable {

    @EmbeddedId
    private TeamPlayerId id = new TeamPlayerId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("teamId")
    @JsonBackReference
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("playerId")
    @JsonBackReference
    private Player player;

    @Column(name = "captain_role")
    private String captainRole = "NONE"; // CAPTAIN, ASSISTANT, NONE

    public TeamPlayer() {}

    // Getters and setters
    public TeamPlayerId getId() { return id; }
    public void setId(TeamPlayerId id) { this.id = id; }

    public Team getTeam() { return team; }
    public void setTeam(Team team) { this.team = team; }

    public Player getPlayer() { return player; }
    public void setPlayer(Player player) { this.player = player; }

    public String getCaptainRole() { return captainRole; }
    public void setCaptainRole(String captainRole) { this.captainRole = captainRole; }
}
