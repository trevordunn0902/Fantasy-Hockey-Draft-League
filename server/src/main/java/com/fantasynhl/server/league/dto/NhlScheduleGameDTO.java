package com.fantasynhl.server.league.dto;

public class NhlScheduleGameDTO {

    private Long id;
    private int season;
    private int gameType;
    private String gameDate;
    private String startTimeUTC;
    private String venueTimezone;
    private String gameState;
    private String gameScheduleState;

    private VenueDTO venue;
    private NhlScheduleTeamDTO awayTeam;
    private NhlScheduleTeamDTO homeTeam;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getGameDate() {
        return gameDate;
    }

    public void setGameDate(String gameDate) {
        this.gameDate = gameDate;
    }

    public String getStartTimeUTC() {
        return startTimeUTC;
    }

    public void setStartTimeUTC(String startTimeUTC) {
        this.startTimeUTC = startTimeUTC;
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

    public VenueDTO getVenue() {
        return venue;
    }

    public void setVenue(VenueDTO venue) {
        this.venue = venue;
    }

    public NhlScheduleTeamDTO getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(NhlScheduleTeamDTO awayTeam) {
        this.awayTeam = awayTeam;
    }

    public NhlScheduleTeamDTO getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(NhlScheduleTeamDTO homeTeam) {
        this.homeTeam = homeTeam;
    }
}