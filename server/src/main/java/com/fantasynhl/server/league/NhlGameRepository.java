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

    // ==========================================
    // All upcoming NHL games
    // ==========================================

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

    // ==========================================
    // First upcoming game for selected NHL teams
    // ==========================================

    @Query("""
        SELECT g
        FROM NhlGame g
        WHERE g.season = :season
        AND g.gameDate >= :date
        AND (g.awayTeam IN :teams OR g.homeTeam IN :teams)
        ORDER BY g.gameDate ASC, g.startTimeUtc ASC
        """)
    List<NhlGame> findFirstUpcomingGameForTeams(
            @Param("teams") List<String> teams,
            @Param("date") LocalDate date,
            @Param("season") int season
    );

    // ==========================================
    // Games for selected NHL teams within a date range
    // ==========================================

    @Query("""
        SELECT g
        FROM NhlGame g
        WHERE g.season = :season
        AND g.gameDate >= :startDate
        AND g.gameDate <= :endDate
        AND (g.awayTeam IN :teams OR g.homeTeam IN :teams)
        ORDER BY g.startTimeUtc ASC
        """)
    List<NhlGame> findUpcomingGamesForTeams(
            @Param("teams") List<String> teams,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("season") int season
    );

}