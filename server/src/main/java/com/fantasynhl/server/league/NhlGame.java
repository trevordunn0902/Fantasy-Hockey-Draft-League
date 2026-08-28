package com.fantasynhl.server.league;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(
    name = "nhl_games",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "game_id")
    }
)
public class NhlGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "game_id", nullable = false, unique = true)
    private Long gameId;

    @Column(nullable = false)
    private int season;

    @Column(name = "game_type", nullable = false)
    private int gameType;

    @Column(name = "game_date", nullable = false)
    private LocalDate gameDate;

    @Column(name = "start_time_utc", nullable = false)
    private Instant startTimeUtc;

    private String venue;

    @Column(name = "venue_timezone")
    private String venueTimezone;

    @Column(name = "game_state")
    private String gameState;

    @Column(name = "game_schedule_state")
    private String gameScheduleState;

    @Column(name = "away_team", nullable = false)
    private String awayTeam;

    @Column(name = "home_team", nullable = false)
    private String homeTeam;

    // Default constructor required by JPA
    public NhlGame() {}

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public int getGameType() {
        return gameType;
    }

    public void setGameType(int gameType) {
        this.gameType = gameType;
    }

    public LocalDate getGameDate() {
        return gameDate;
    }

    public void setGameDate(LocalDate gameDate) {
        this.gameDate = gameDate;
    }

    public Instant getStartTimeUtc() {
        return startTimeUtc;
    }

    public void setStartTimeUtc(Instant startTimeUtc) {
        this.startTimeUtc = startTimeUtc;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getVenueTimezone() {
        return venueTimezone;
    }

    public void setVenueTimezone(String venueTimezone) {
        this.venueTimezone = venueTimezone;
    }

    public String getGameState() {
        return gameState;
    }

    public void setGameState(String gameState) {
        this.gameState = gameState;
    }

    public String getGameScheduleState() {
        return gameScheduleState;
    }

    public void setGameScheduleState(String gameScheduleState) {
        this.gameScheduleState = gameScheduleState;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(String awayTeam) {
        this.awayTeam = awayTeam;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
    }
}