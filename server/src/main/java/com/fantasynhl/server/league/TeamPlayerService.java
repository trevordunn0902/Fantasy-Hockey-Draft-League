package com.fantasynhl.server.league;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TeamPlayerService {

    private final TeamPlayerRepository teamPlayerRepository;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;

    public TeamPlayerService(
            TeamPlayerRepository teamPlayerRepository,
            TeamRepository teamRepository,
            PlayerRepository playerRepository
    ) {
        this.teamPlayerRepository = teamPlayerRepository;
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
    }

    @Transactional
    public void assignCaptainRole(Long teamId, Long playerId, String role) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found"));

        TeamPlayerId teamPlayerId = new TeamPlayerId(team.getId(), player.getId());
        TeamPlayer teamPlayer = teamPlayerRepository.findById(teamPlayerId)
                .orElseThrow(() -> new RuntimeException("Player not part of this team"));

        if (!role.equalsIgnoreCase("CAPTAIN") &&
            !role.equalsIgnoreCase("ASSISTANT") &&
            !role.equalsIgnoreCase("NONE")) {
            throw new RuntimeException("Invalid captain role. Use CAPTAIN, ASSISTANT, or NONE.");
        }

        role = role.toUpperCase();

        // Check constraints
        if ("CAPTAIN".equals(role)) {
            boolean hasCaptain = teamPlayerRepository.existsByTeamIdAndCaptainRole(teamId, "CAPTAIN");
            if (hasCaptain && !"CAPTAIN".equals(teamPlayer.getCaptainRole())) {
                throw new RuntimeException("This team already has a captain");
            }
        } else if ("ASSISTANT".equals(role)) {
            long assistantCount = teamPlayerRepository.countByTeamIdAndCaptainRole(teamId, "ASSISTANT");
            if (assistantCount >= 2 && !"ASSISTANT".equals(teamPlayer.getCaptainRole())) {
                throw new RuntimeException("This team already has 2 assistants");
            }
        }

        teamPlayer.setCaptainRole(role);
        teamPlayerRepository.save(teamPlayer);
    }
}
