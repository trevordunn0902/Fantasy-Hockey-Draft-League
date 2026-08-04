package com.fantasynhl.server.league;

import com.fantasynhl.server.user.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "teams")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "total_points")
    private double totalPoints = 0.0;

    // Owner relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    @JsonBackReference
    private User owner;

    // League relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "league_id")
    @JsonBackReference
    private League league;

    // New: one-to-many relationship with TeamPlayer entity
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<TeamPlayer> teamPlayers = new ArrayList<>();

    public Team() {}

    // Helper to add a player to the team
    public void addPlayer(Player player) {
        TeamPlayer teamPlayer = new TeamPlayer();
        teamPlayer.setTeam(this);
        teamPlayer.setPlayer(player);
        teamPlayer.setCaptainRole("NONE");
        teamPlayers.add(teamPlayer);
    }

    // Helper to remove a player
    public void removePlayer(Player player) {
        teamPlayers.removeIf(tp -> tp.getPlayer().equals(player));
    }

    // Getters / Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }

    public League getLeague() { return league; }
    public void setLeague(League league) { this.league = league; }

    public List<TeamPlayer> getTeamPlayers() { return teamPlayers; }
    public void setTeamPlayers(List<TeamPlayer> teamPlayers) { this.teamPlayers = teamPlayers; }

    public double getTotalPoints() { return totalPoints; }
    public void setTotalPoints(double totalPoints) { this.totalPoints = totalPoints; }
}
