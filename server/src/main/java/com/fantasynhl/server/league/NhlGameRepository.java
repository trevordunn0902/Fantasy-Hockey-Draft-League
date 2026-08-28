package com.fantasynhl.server.league;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface NhlGameRepository extends JpaRepository<NhlGame, Long> {

    Optional<NhlGame> findByGameId(Long gameId);

    List<NhlGame> findBySeason(int season);

    List<NhlGame> findByGameDateBetween(
            LocalDate startDate,
            LocalDate endDate
    );

    List<NhlGame> findByAwayTeamOrHomeTeam(
            String awayTeam,
            String homeTeam
    );

    
}