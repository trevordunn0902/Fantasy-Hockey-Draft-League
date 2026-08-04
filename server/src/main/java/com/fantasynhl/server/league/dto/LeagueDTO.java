package com.fantasynhl.server.league.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.fantasynhl.server.league.League;

public class LeagueDTO {
    private Long id;
    private String name;
    private String inviteCode;
    private boolean inviteOnly;
    private List<TeamDTO> teams;

    public LeagueDTO(League league) {
        this(league, true);
    }

    // Constructor with flag to prevent infinite loop
    public LeagueDTO(League league, boolean includeTeams) {
        this.id = league.getId();
        this.name = league.getName();
        this.inviteCode = league.getInviteCode();
        this.inviteOnly = league.isInviteOnly();

        if (includeTeams) {
            this.teams = league.getTeams().stream()
                               .map(team -> new TeamDTO(team, false))
                               .collect(Collectors.toList());
        }
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getInviteCode() { return inviteCode; }
    public boolean isInviteOnly() { return inviteOnly; }
    public List<TeamDTO> getTeams() { return teams; }
}