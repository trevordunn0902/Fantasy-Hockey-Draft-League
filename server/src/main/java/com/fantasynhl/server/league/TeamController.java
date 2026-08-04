package com.fantasynhl.server.league;

import org.springframework.web.bind.annotation.*;

import com.fantasynhl.server.league.dto.TeamDTO;

import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/team")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    /**
     * Get all teams owned by the logged-in user
     */
    @GetMapping("/my-teams")
    public ResponseEntity<List<TeamDTO>> getMyTeams(@RequestParam Long ownerId) {
        List<TeamDTO> teams = teamService.getTeamsByOwnerId(ownerId);
        return ResponseEntity.ok(teams);
    }

    /**
     * Get a specific team by ID
     */
    @GetMapping("/{teamId}")
    public ResponseEntity<TeamDTO> getTeamById(@PathVariable Long teamId) {
        TeamDTO team = teamService.getTeamById(teamId);
        return ResponseEntity.ok(team);
    }
}
