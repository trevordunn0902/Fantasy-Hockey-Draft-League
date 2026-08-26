package com.fantasynhl.server.league;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/players")
@CrossOrigin(origins = "*")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    @Autowired
    private NhlStatsService nhlStatsService;

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

    @PostMapping("/stats/update/{seasonId}")
    public Map<String, Integer> updatePlayerStats(@PathVariable int seasonId) {
        return nhlStatsService.updatePlayerStats(seasonId);
    }

    // ==========================================
    // NHL Stats API Test
    // ==========================================

    @GetMapping("/stats/test/{seasonId}")
    public Map<String, Object> testNhlStats(@PathVariable int seasonId) {

        List<Player> databasePlayers = playerService.getAllPlayers();

        List<com.fantasynhl.server.league.dto.SkaterDTO> skaterStats =
                nhlStatsService.getSkaterStats(seasonId);

        List<com.fantasynhl.server.league.dto.GoalieDTO> goalieStats =
                nhlStatsService.getGoalieStats(seasonId);

        // Build a set of NHL IDs from our database
        Set<Long> databaseNhlIds = new HashSet<>();

        for (Player player : databasePlayers) {
            if (player.getNhlId() != null) {
                databaseNhlIds.add(player.getNhlId());
            }
        }

        // Find matches
        int matchedSkaters = 0;
        int unmatchedSkaters = 0;

        for (var stats : skaterStats) {
            if (databaseNhlIds.contains(stats.getPlayerId())) {
                matchedSkaters++;
            } else {
                unmatchedSkaters++;
            }
        }

        int matchedGoalies = 0;
        int unmatchedGoalies = 0;

        for (var stats : goalieStats) {
            if (databaseNhlIds.contains(stats.getPlayerId())) {
                matchedGoalies++;
            } else {
                unmatchedGoalies++;
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();

        result.put("seasonId", seasonId);

        result.put("databasePlayers", databasePlayers.size());
        result.put("databasePlayersWithNhlId", databaseNhlIds.size());

        result.put("skaterStatsReturned", skaterStats.size());
        result.put("skaterStatsMatched", matchedSkaters);
        result.put("skaterStatsUnmatched", unmatchedSkaters);

        result.put("goalieStatsReturned", goalieStats.size());
        result.put("goalieStatsMatched", matchedGoalies);
        result.put("goalieStatsUnmatched", unmatchedGoalies);

        return result;
    }
}