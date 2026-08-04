package com.fantasynhl.server.league;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DraftPickRepository extends JpaRepository<DraftPick, Long> {

    // Fetch all picks in a league
    List<DraftPick> findByLeague(League league);

    // Fetch all picks in a league for a specific team
    List<DraftPick> findByLeagueAndTeam(League league, Team team);

    // Check if a player has already been drafted in a league
    boolean existsByLeagueAndPlayer(League league, Player player);

    // Fetch a pick by league and player (optional, if needed)
    Optional<DraftPick> findByLeagueAndPlayer(League league, Player player);
}