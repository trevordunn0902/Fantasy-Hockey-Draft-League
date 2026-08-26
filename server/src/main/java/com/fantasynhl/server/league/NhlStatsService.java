package com.fantasynhl.server.league;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.fantasynhl.server.league.dto.GoalieDTO;
import com.fantasynhl.server.league.dto.SkaterDTO;

import java.util.*;

@Service
public class NhlStatsService {

    private final RestTemplate restTemplate;
    private final PlayerRepository playerRepository;
    private final PointsService pointsService;

    private static final String SKATER_STATS_URL =
            "https://api.nhle.com/stats/rest/en/skater/summary";

    private static final String GOALIE_STATS_URL =
            "https://api.nhle.com/stats/rest/en/goalie/summary";

    private static final int CURRENT_SEASON_ID = 20252026;

    public NhlStatsService(
            PlayerRepository playerRepository,
            PointsService pointsService
    ) {
        this.playerRepository = playerRepository;
        this.pointsService = pointsService;
        this.restTemplate = new RestTemplate();
    }

    // ==========================================
    // Fetch NHL Skater Stats
    // ==========================================

    public List<SkaterDTO> getSkaterStats(int seasonId) {

        String url = SKATER_STATS_URL
                + "?limit=-1"
                + "&sort=points"
                + "&cayenneExp=seasonId=" + seasonId;

        SkaterStatsResponse response =
                restTemplate.getForObject(url, SkaterStatsResponse.class);

        if (response == null || response.getData() == null) {
            return List.of();
        }

        return response.getData();
    }

    // ==========================================
    // Fetch NHL Goalie Stats
    // ==========================================

    public List<GoalieDTO> getGoalieStats(int seasonId) {

        String url = GOALIE_STATS_URL
                + "?limit=-1"
                + "&sort=points"
                + "&cayenneExp=seasonId=" + seasonId;

        GoalieStatsResponse response =
                restTemplate.getForObject(url, GoalieStatsResponse.class);

        if (response == null || response.getData() == null) {
            return List.of();
        }

        return response.getData();
    }

    // ==========================================
    // Update Player Stats
    // ==========================================

    @Transactional
    public Map<String, Integer> updatePlayerStats(int seasonId) {

        System.out.println("=== Starting NHL Stats Update ===");
        System.out.println("Season: " + seasonId);

        List<SkaterDTO> skaterStats = getSkaterStats(seasonId);
        List<GoalieDTO> goalieStats = getGoalieStats(seasonId);

        System.out.println("Skater stats received: " + skaterStats.size());
        System.out.println("Goalie stats received: " + goalieStats.size());

        // Load all players from database
        List<Player> players = playerRepository.findAll();

        // Create NHL ID lookup map
        Map<Long, Player> playerMap = new HashMap<>();

        for (Player player : players) {

            if (player.getNhlId() != null) {
                playerMap.put(player.getNhlId(), player);
            }
        }

        int skatersUpdated = 0;
        int skatersUnmatched = 0;

        int goaliesUpdated = 0;
        int goaliesUnmatched = 0;

        // ==========================================
        // Update Skaters
        // ==========================================

        for (SkaterDTO stats : skaterStats) {

            Player player = playerMap.get(stats.getPlayerId());

            if (player == null) {
                skatersUnmatched++;
                continue;
            }

            player.setGoals(stats.getGoals());
            player.setAssists(stats.getAssists());

            skatersUpdated++;
        }

        // ==========================================
        // Update Goalies
        // ==========================================

        for (GoalieDTO stats : goalieStats) {

            Player player = playerMap.get(stats.getPlayerId());

            if (player == null) {
                goaliesUnmatched++;
                continue;
            }

            player.setWins(stats.getWins());
            player.setShutouts(stats.getShutouts());

            goaliesUpdated++;
        }

        // ==========================================
        // Save Updated Players
        // ==========================================

        playerRepository.saveAll(players);

        System.out.println("Skaters updated: " + skatersUpdated);
        System.out.println("Skaters unmatched: " + skatersUnmatched);
        System.out.println("Goalies updated: " + goaliesUpdated);
        System.out.println("Goalies unmatched: " + goaliesUnmatched);

        System.out.println("=== NHL Stats Update Completed ===");

        Map<String, Integer> result = new LinkedHashMap<>();

        result.put("skaterStatsReceived", skaterStats.size());
        result.put("skatersUpdated", skatersUpdated);
        result.put("skatersUnmatched", skatersUnmatched);

        result.put("goalieStatsReceived", goalieStats.size());
        result.put("goaliesUpdated", goaliesUpdated);
        result.put("goaliesUnmatched", goaliesUnmatched);

        return result;
    }

    // ==========================================
    // API Response Wrappers
    // ==========================================

    public static class SkaterStatsResponse {

        private List<SkaterDTO> data;

        public List<SkaterDTO> getData() {
            return data;
        }

        public void setData(List<SkaterDTO> data) {
            this.data = data;
        }
    }

    public static class GoalieStatsResponse {

        private List<GoalieDTO> data;

        public List<GoalieDTO> getData() {
            return data;
        }

        public void setData(List<GoalieDTO> data) {
            this.data = data;
        }
    }

    @Scheduled(cron = "0 25 4 * * *", zone = "America/New_York")
    public void scheduledUpdateStats() {

        System.out.println("=== Starting Scheduled NHL Player Update ===");

        updatePlayerStats(CURRENT_SEASON_ID);

        System.out.println("=== NHL Stats Updated ===");

        pointsService.updatePoints();

        System.out.println("=== Fantasy Points Updated ===");
        System.out.println("=== Scheduled Player Update Completed ===");
    }
}