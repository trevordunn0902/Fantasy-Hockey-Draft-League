package com.fantasynhl.server.league;

import org.springframework.stereotype.Service;

import com.fantasynhl.server.league.dto.TeamDTO;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeamService {

    private final TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    /**
     * Get all teams owned by a given user
     */
    public List<TeamDTO> getTeamsByOwnerId(Long ownerId) {
        List<Team> teams = teamRepository.findByOwnerId(ownerId);
        return teams.stream()
                .map(team -> new TeamDTO(team))
                .collect(Collectors.toList());
    }

    /**
     * Get a single team by its ID
     */
    public TeamDTO getTeamById(Long teamId) {
        return teamRepository.findById(teamId)
                .map(TeamDTO::new)
                .orElseThrow(() -> new RuntimeException("Team not found"));
    }
}
