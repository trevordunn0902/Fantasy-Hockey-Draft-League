package com.fantasynhl.server.league.dto;

import com.fantasynhl.server.league.Team;
import com.fantasynhl.server.league.TeamPlayer;
import java.util.List;
import java.util.stream.Collectors;

public class TeamDTO {
    private Long id;
    private String name;
    private UserDTO owner;
    private LeagueDTO league;
    private List<PlayerDTO> roster;
    private double totalPoints;

    public TeamDTO(Team team) {
        this(team, true);
    }

    // Constructor with flag to prevent infinite loop
    public TeamDTO(Team team, boolean includeLeague) {
        this.id = team.getId();
        this.name = team.getName();
        this.owner = new UserDTO(team.getOwner());

        // Map TeamPlayer -> PlayerDTO, including captainRole
        this.roster = team.getTeamPlayers().stream()
                .map(tp -> new PlayerDTO(tp.getPlayer(), tp.getCaptainRole()))
                .collect(Collectors.toList());

        if (includeLeague && team.getLeague() != null) {
            this.league = new LeagueDTO(team.getLeague(), false);
        }

        // Map total points
        this.totalPoints = team.getTotalPoints();
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public UserDTO getOwner() { return owner; }
    public LeagueDTO getLeague() { return league; }
    public List<PlayerDTO> getRoster() { return roster; }
    public double getTotalPoints() { return totalPoints; }
}
