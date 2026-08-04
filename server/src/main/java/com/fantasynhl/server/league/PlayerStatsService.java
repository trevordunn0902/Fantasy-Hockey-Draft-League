package com.fantasynhl.server.league;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PlayerStatsService {

    private final PlayerRepository playerRepository;
    private final PointsService pointsService;

    // Maps shortened or variant first names to their official Hockey Reference versions
    private static final Map<String, String> FIRST_NAME_MAP = Map.ofEntries(
        
        Map.entry("mikey", "michael"),
        Map.entry("tommy", "thomas"),
        Map.entry("will", "william"),
        Map.entry("ben", "benjamin"),
        Map.entry("zack", "zachary"),
        Map.entry("nick", "nicklaus"),
        Map.entry("max", "maxwell"),
        Map.entry("matt", "matthew"),
        Map.entry("alex", "alexander"),
        Map.entry("evgeny", "evgenii"),
        Map.entry("roope", "roope"), 
        Map.entry("teuvo", "teuvo teravainen"),
        Map.entry("emil martinsen lilleberg", "emil lilleberg")
    );



    public PlayerStatsService(PlayerRepository playerRepository, PointsService pointsService) {
        this.playerRepository = playerRepository;
        this.pointsService = pointsService;
    }

    @Transactional
    public void updateAllPlayerStats() {
        List<Player> allPlayers = playerRepository.findAll();
        Map<String, Player> playerMap = new HashMap<>();
        for (Player p : allPlayers) {
            String key = normalizeName(p.getName()) + "|" + p.getPositionCode().toUpperCase();
            playerMap.put(key, p);
        }

        System.out.println("=== Starting Player Stats Update from Hockey Reference ===");

        int skatersUpdated = scrapeSkaterStats(playerMap);
        int goaliesUpdated = scrapeGoalieStats(playerMap);

        playerRepository.saveAll(allPlayers);
        pointsService.updatePoints();

        System.out.println("=== Player Stats Update Completed ===");
        System.out.println("Summary: Skaters updated: " + skatersUpdated + ", Goalies updated: " + goaliesUpdated);
    }

    private int scrapeSkaterStats(Map<String, Player> playerMap) {
        String url = "https://www.hockey-reference.com/leagues/NHL_2026_skaters.html";
        int updatedCount = 0;
        try {
            Document doc = Jsoup.connect(url).get();
            Element table = doc.selectFirst("table.stats_table");
            if (table == null) {
                System.out.println("Skater stats table not found.");
                return updatedCount;
            }

            Elements rows = table.select("tbody > tr");
            for (Element row : rows) {
                if (row.hasClass("thead")) continue;

                String name = row.select("td[data-stat='name_display']").text().trim();
                String positionCode = mapPositionCode(row.select("td[data-stat='pos']").text().trim());

                int goals = parseIntSafe(row.select("td[data-stat='goals']").text());
                int assists = parseIntSafe(row.select("td[data-stat='assists']").text());

                Player player = findNearestPlayer(normalizeName(name), positionCode, playerMap);
                if (player != null) {
                    player.setGoals(goals);
                    player.setAssists(assists);
                    updatedCount++;
                } else {
                    System.out.println("Skater not found in DB: '" + name + "' (" + positionCode + ") -> normalized as '" + normalizeName(name) + "'");
                    playerMap.entrySet().stream()
                            .filter(e -> e.getKey().split("\\|")[1].equals(positionCode))
                            .sorted((e1, e2) -> Integer.compare(
                                    levenshteinDistance(normalizeName(name), e1.getKey().split("\\|")[0]),
                                    levenshteinDistance(normalizeName(name), e2.getKey().split("\\|")[0])
                            ))
                            .limit(3);
                }
            }
        } catch (IOException e) {
            System.err.println("Error scraping skater stats: " + e.getMessage());
        }
        return updatedCount;
    }

    private int scrapeGoalieStats(Map<String, Player> playerMap) {
        String url = "https://www.hockey-reference.com/leagues/NHL_2026_goalies.html";
        int updatedCount = 0;
        try {
            Document doc = Jsoup.connect(url).get();
            Element table = doc.selectFirst("table.stats_table");
            if (table == null) {
                System.out.println("Goalie stats table not found.");
                return updatedCount;
            }

            Elements rows = table.select("tbody > tr");
            for (Element row : rows) {
                if (row.hasClass("thead")) continue;

                String name = row.select("td[data-stat='name_display']").text().trim();
                int wins = parseIntSafe(row.select("td[data-stat='goalie_wins']").text());
                int shutouts = parseIntSafe(row.select("td[data-stat='goalie_shutouts']").text());

                Player player = findNearestPlayer(normalizeName(name), "G", playerMap);
                if (player != null) {
                    player.setWins(wins);
                    player.setShutouts(shutouts);
                    updatedCount++;
                } else {
                    System.out.println("Goalie not found in DB: '" + name + "' -> normalized as '" + normalizeName(name) + "'");
                    playerMap.entrySet().stream()
                            .filter(e -> e.getKey().split("\\|")[1].equals("G"))
                            .sorted((e1, e2) -> Integer.compare(
                                    levenshteinDistance(normalizeName(name), e1.getKey().split("\\|")[0]),
                                    levenshteinDistance(normalizeName(name), e2.getKey().split("\\|")[0])
                            ))
                            .limit(3);
                }
            }
        } catch (IOException e) {
            System.err.println("Error scraping goalie stats: " + e.getMessage());
        }
        return updatedCount;
    }

    private int parseIntSafe(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String normalizeName(String name) {
        if (name == null || name.isEmpty()) return "";

        // Handle "Last, First" format
        if (name.contains(",")) {
            String[] parts = name.split(",");
            name = parts[1].trim() + " " + parts[0].trim();
        }

        // Remove suffixes like Jr, III, II
        name = name.replaceAll("\\b(Jr|III|II)\\b\\.?", "").trim();

        // Remove extra symbols like * + ?
        name = name.replaceAll("[*+?]", "").trim();

        // Normalize accents (é → e, etc.)
        name = Normalizer.normalize(name, Normalizer.Form.NFD)
                        .replaceAll("\\p{M}", "");

        // Normalize punctuation:
        // Step 1: Replace all apostrophes (’ or ') with hyphen for consistent treatment
        name = name.replaceAll("[’']", "-");
        // Step 2: Replace hyphens with space so “Jean-Luc” → “Jean Luc”
        name = name.replaceAll("[-]", " ");
        // Step 3: Remove any leftover non-letter characters
        name = name.replaceAll("[^a-zA-Z ]", "");
        // Step 4: Collapse multiple spaces and lowercase
        name = name.replaceAll("\\s+", " ").toLowerCase().trim();

        // Map nicknames to formal first names
        String[] parts = name.split(" ");
        if (parts.length > 0 && FIRST_NAME_MAP.containsKey(parts[0])) {
            parts[0] = FIRST_NAME_MAP.get(parts[0]);
        }

        // Rebuild the normalized name
        String normalized = String.join(" ", parts);

        // Apply FULL_NAME_MAP overrides
        if (FIRST_NAME_MAP.containsKey(normalized)) {
            normalized = FIRST_NAME_MAP.get(normalized);
        }

        return normalized;
    }

    private String mapPositionCode(String hrPos) {
        hrPos = hrPos.toUpperCase();
        return switch (hrPos) {
            case "C", "LW", "L", "RW", "R", "F", "W" -> "F";
            case "D" -> "D";
            case "G" -> "G";
            default -> hrPos;
        };
    }

    private Player findNearestPlayer(String normalizedName, String positionCode, Map<String, Player> playerMap) {
        if (normalizedName.isEmpty() || normalizedName.contains("league average")) return null;

        String exactKey = normalizedName + "|" + positionCode;
        if (playerMap.containsKey(exactKey)) return playerMap.get(exactKey);

        Player closestPlayer = null;
        int minDistance = Integer.MAX_VALUE;

        for (Map.Entry<String, Player> entry : playerMap.entrySet()) {
            String[] parts = entry.getKey().split("\\|");
            if (parts.length != 2) continue;

            boolean positionMatches;
            if (positionCode.equals("F") || positionCode.equals("W") || positionCode.equals("C")) {
                positionMatches = parts[1].equals("C") || parts[1].equals("L") || parts[1].equals("R");
            } else if (positionCode.equals("D")) {
                // Allow exact name matches to override position mismatch
                positionMatches = parts[1].equals("D") || parts[0].equals(normalizedName);
            } else {
                positionMatches = positionCode.equals(parts[1]);
            }

            if (!positionMatches) continue;

            if (isSimilar(normalizedName, parts[0].replace("-", " "))) {
                int distance = levenshteinDistance(normalizedName, parts[0].replace("-", " "));
                if (distance < minDistance) {
                    minDistance = distance;
                    closestPlayer = entry.getValue();
                }
            }
        }

        return closestPlayer;
    }

    private int levenshteinDistance(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        for (int i = 0; i <= a.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= b.length(); j++) dp[0][j] = j;

        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j - 1],
                            Math.min(dp[i - 1][j], dp[i][j - 1]));
                }
            }
        }
        return dp[a.length()][b.length()];
    }

    private boolean isSimilar(String a, String b) {
        if (a == null || b == null) return false;

        int distance = levenshteinDistance(a, b);
        int maxAllowed = Math.max(2, (int) Math.ceil(Math.max(a.length(), b.length()) * 0.2));

        return distance <= maxAllowed;
    }

    public void verifyAndFixPlayerPoints(boolean autoFix) {
    List<Player> allPlayers = playerRepository.findAll();
    int mismatches = 0;

    for (Player p : allPlayers) {
        int expectedPoints = p.getGoals() + p.getAssists();
        if (p.getPoints() != expectedPoints) {
            mismatches++;
            System.out.println("Points mismatch: " + p.getName() +
                               " | DB points: " + p.getPoints() +
                               " | Expected: " + expectedPoints);
            if (autoFix) {
                p.setPoints(expectedPoints);
            }
        }
    }
}


    @Scheduled(cron = "0 25 4 * * *", zone = "America/New_York")
    public void scheduledUpdate() {
        updateAllPlayerStats();
    }
}
