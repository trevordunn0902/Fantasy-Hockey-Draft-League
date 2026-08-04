package com.fantasynhl.server.league;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "draft_picks",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"league_id", "player_id"})} // ensures one player per league
)
public class DraftPick {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "league_id", nullable = false)
    private League league;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    private String position; // Record player's position at pick time
    private int pickOrder;   // Draft turn order number
    private LocalDateTime createdAt = LocalDateTime.now();

    public DraftPick() {}

    // Getters / setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public League getLeague() { return league; }
    public void setLeague(League league) { this.league = league; }

    public Team getTeam() { return team; }
    public void setTeam(Team team) { this.team = team; }

    public Player getPlayer() { return player; }
    public void setPlayer(Player player) { this.player = player; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public int getPickOrder() { return pickOrder; }
    public void setPickOrder(int pickOrder) { this.pickOrder = pickOrder; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}