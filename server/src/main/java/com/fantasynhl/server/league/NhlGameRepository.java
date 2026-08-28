package com.fantasynhl.server.league;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

     @Query("""
        SELECT g
        FROM NhlGame g
        WHERE g.season = :season
          AND g.gameDate >= :date
        ORDER BY g.startTimeUtc ASC
    """)
    List<NhlGame> findUpcomingGames(
            @Param("date") LocalDate date,
            @Param("season") int season
    );

}