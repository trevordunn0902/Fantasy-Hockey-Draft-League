package com.fantasynhl.server.league;

import com.fantasynhl.server.user.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LeagueService {

    private final LeagueRepository leagueRepository;
    private final TeamRepository teamRepository;

    public LeagueService(LeagueRepository leagueRepository, TeamRepository teamRepository) {
        this.leagueRepository = leagueRepository;
        this.teamRepository = teamRepository;
    }

    // Create a new league
    public League createLeague(String name) {
        League league = new League();
        league.setName(name);
        league.setInviteCode(generateInviteCode());
        league.setMaxTeams(10);
        return leagueRepository.save(league);
    }

    // User joins a league
    public Team joinLeague(String inviteCode, User user, String teamName) {
        League league = leagueRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new RuntimeException("League not found"));

        if (league.getTeams().size() >= league.getMaxTeams()) {
            throw new RuntimeException("League is full");
        }

        Team team = new Team();
        team.setName(teamName);
        team.setOwner(user);
        team.setLeague(league);

        Team saved = teamRepository.save(team);

        // Add team to league's list and persist
        league.getTeams().add(saved);
        leagueRepository.save(league);

        return saved;
    }

    // Fetch league by invite code
    public Optional<League> getLeague(String inviteCode) {
        return leagueRepository.findByInviteCode(inviteCode);
    }

    // Fetch all leagues
    public List<League> getAllLeagues() {
        return leagueRepository.findAll();
    }

    // Generate a 6-character invite code
    private String generateInviteCode() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}