package com.fantasynhl.server.league;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/player-stats")
@CrossOrigin(origins = "*")
public class PlayerStatsController {

    private final PlayerStatsService playerStatsService;

    public PlayerStatsController(PlayerStatsService playerStatsService) {
        this.playerStatsService = playerStatsService;
    }

    // Manual trigger for updating all player stats
    @PostMapping("/update")
    public String updateAllPlayerStats() {
        playerStatsService.updateAllPlayerStats();
        return "Player stats updated successfully, including points and team totals!";
    }
}
