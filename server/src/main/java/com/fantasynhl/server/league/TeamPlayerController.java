package com.fantasynhl.server.league;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/team-players")
public class TeamPlayerController {

    private final TeamPlayerService teamPlayerService;

    public TeamPlayerController(TeamPlayerService teamPlayerService) {
        this.teamPlayerService = teamPlayerService;
    }

    @PostMapping("/{teamId}/{playerId}/assign-captain")
    public ResponseEntity<String> assignCaptainRole(
            @PathVariable Long teamId,
            @PathVariable Long playerId,
            @RequestParam String role) {

        teamPlayerService.assignCaptainRole(teamId, playerId, role);
        return ResponseEntity.ok("Captain role assigned successfully");
    }
}
