package com.fantasynhl.server.league;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/players")
@CrossOrigin(origins = "*")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    // Manual endpoint: Seed all NHL players into DB
    @PostMapping("/seed")
    public List<Player> seedAllPlayers() {
        return playerService.updateAllPlayersFromNHLApi();
    }

    // Get all players
    @GetMapping("/all")
    public List<Player> getAllPlayers() {
        return playerService.getAllPlayers();
    }

    // Get players by team code (e.g., "OTT")
    @GetMapping("/team/{team}")
    public List<Player> getPlayersByTeam(@PathVariable String team) {
        return playerService.getPlayersByTeam(team);
    }

    // Get players by position (F, D, G)
    @GetMapping("/position/{position}")
    public List<Player> getPlayersByPosition(@PathVariable String position) {
        return playerService.getPlayersByPosition(position);
    }
}
