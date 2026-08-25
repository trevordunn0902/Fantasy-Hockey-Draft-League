package com.fantasynhl.server.league;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class PlayerService {

    @Autowired
    private PlayerRepository playerRepository;

    private static final String[] NHL_TEAMS = {
        "ANA","BOS","BUF","CGY","CAR","CHI","COL","CBJ","DAL",
        "DET","EDM","FLA","LAK","MIN","MTL","NSH","NJD","NYI","NYR",
        "OTT","PHI","PIT","SJS","SEA","STL","TBL","TOR","UTA","VAN","VGK","WSH","WPG"
    };

    // ===========================
    // NHL Data Fetch / Seed Method
    // ===========================
    public List<Player> updateAllPlayersFromNHLApi() {
        RestTemplate restTemplate = new RestTemplate();
        List<Player> allPlayersFromApi = new ArrayList<>();

        System.out.println("=== Starting NHL Player Update ===");

        // Load existing players into a map for quick lookup
        List<Player> existingPlayers = playerRepository.findAll();
        Map<String, Player> playerMap = new HashMap<>();
        for (Player p : existingPlayers) {
            String positionCode = p.getPositionCode() != null
                    ? p.getPositionCode().toUpperCase()
                    : "";

            String key = p.getName().toLowerCase() + "_" + positionCode;

            playerMap.put(key, p);
        }

        for (String team : NHL_TEAMS) {
            try {
                String url = "https://api-web.nhle.com/v1/roster/" + team + "/current";
                System.out.println("Fetching roster for team: " + team + " -> " + url);

                Map<String, Object> response = restTemplate.getForObject(url, Map.class);
                if (response == null) {
                    System.out.println("No response for team: " + team);
                    continue;
                }

                addOrUpdatePlayersFromGroup(response, "forwards", team, playerMap, allPlayersFromApi);
                addOrUpdatePlayersFromGroup(response, "defensemen", team, playerMap, allPlayersFromApi);
                addOrUpdatePlayersFromGroup(response, "goalies", team, playerMap, allPlayersFromApi);

            } catch (Exception e) {
                System.err.println("Error fetching roster for " + team + ": " + e.getMessage());
            }
        }

        System.out.println("Saving " + allPlayersFromApi.size() + " players...");
        playerRepository.saveAll(allPlayersFromApi);
        System.out.println("=== NHL Player Update Completed ===");

        return allPlayersFromApi;
    }

    @SuppressWarnings("unchecked")
    private void addOrUpdatePlayersFromGroup(
            Map<String, Object> response,
            String group,
            String team,
            Map<String, Player> playerMap,
            List<Player> allPlayersFromApi
    ) {
        List<Map<String, Object>> players = (List<Map<String, Object>>) response.get(group);
        if (players == null) return;

        for (Map<String, Object> playerMapApi : players) {
            try {
                // NHL's unique player ID
                Long nhlId = playerMapApi.get("id") instanceof Number
                        ? ((Number) playerMapApi.get("id")).longValue()
                        : null;

                String firstName = playerMapApi.get("firstName") instanceof Map ?
                        (String) ((Map<String, Object>) playerMapApi.get("firstName")).get("default") : "";

                String lastName = playerMapApi.get("lastName") instanceof Map ?
                        (String) ((Map<String, Object>) playerMapApi.get("lastName")).get("default") : "";

                String fullName = (firstName + " " + lastName).trim();

                String positionCode = (String) playerMapApi.get("positionCode");

                String key = fullName.toLowerCase() + "_"
                        + (positionCode != null ? positionCode.toUpperCase() : "");

                Player p = playerMap.getOrDefault(key, new Player());

                // NHL identity
                p.setNhlId(nhlId);

                // Basic information
                p.setName(fullName);
                p.setNhlTeam(team);
                p.setPositionCode(positionCode);

                // Assign simplified position
                switch (group) {
                    case "forwards" -> p.setPosition("F");
                    case "defensemen" -> p.setPosition("D");
                    case "goalies" -> p.setPosition("G");
                    default -> p.setPosition("N/A");
                }

                // Drafting information
                p.setHeadshotUrl((String) playerMapApi.get("headshot"));

                p.setSweaterNumber(
                        playerMapApi.get("sweaterNumber") instanceof Number
                                ? ((Number) playerMapApi.get("sweaterNumber")).intValue()
                                : 0
                );

                // Add EVERY player to the save list.
                // This ensures existing players receive their NHL ID.
                allPlayersFromApi.add(p);

                // Keep the map current
                playerMap.put(key, p);

            } catch (Exception e) {
                System.err.println(
                        "Skipped player due to parsing error: " + e.getMessage()
                );
            }
        }
    }

    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    public List<Player> getPlayersByTeam(String team) {
        return playerRepository.findByNhlTeamIgnoreCase(team);
    }

    public List<Player> getPlayersByPosition(String position) {
        return playerRepository.findByPositionIgnoreCase(position);
    }

}
