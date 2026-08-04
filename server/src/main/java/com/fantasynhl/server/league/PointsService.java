package com.fantasynhl.server.league;

import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PointsService {

    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;

    public PointsService(PlayerRepository playerRepository, TeamRepository teamRepository) {
        this.playerRepository = playerRepository;
        this.teamRepository = teamRepository;
    }

    @Transactional
    public void updatePoints() {
        // 1) Update player base points (no captain multipliers)
        List<Player> allPlayers = playerRepository.findAll();

        for (Player player : allPlayers) {
            double points = calculateBasePlayerPoints(player);
            // store as integer as you did previously
            player.setPoints((int) points);
        }

        playerRepository.saveAll(allPlayers);

        // 2) Update team total points using TeamPlayer captain roles
        List<Team> allTeams = teamRepository.findAll();

        for (Team team : allTeams) {
            double totalPoints = 0.0;
            for (TeamPlayer tp : team.getTeamPlayers()) {
                Player p = tp.getPlayer();
                double basePoints = calculateBasePlayerPoints(p);
                double multiplier = 1.0;
                String role = tp.getCaptainRole();
                if ("CAPTAIN".equalsIgnoreCase(role)) multiplier = 2.0;
                else if ("ASSISTANT".equalsIgnoreCase(role)) multiplier = 1.5;
                totalPoints += basePoints * multiplier;
            }
            team.setTotalPoints(totalPoints);
        }

        teamRepository.saveAll(allTeams);
    }

    private double calculateBasePlayerPoints(Player player) {
        double points = 0.0;

        if ("G".equalsIgnoreCase(player.getPositionCode())) {
            points = player.getWins() * 3 + player.getShutouts();
        } else { // Skaters
            points = player.getGoals() * 3 + player.getAssists() * 2;
        }

        return points;
    }

    // Scheduled update every night at 4:30 AM EST
    @Scheduled(cron = "0 30 4 * * *", zone = "America/New_York")
    public void scheduledUpdatePoints() {
        updatePoints();
    }
}
