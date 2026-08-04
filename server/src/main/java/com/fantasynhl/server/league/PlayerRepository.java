package com.fantasynhl.server.league;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    List<Player> findByNhlTeamIgnoreCase(String nhlTeam);
    List<Player> findByPositionIgnoreCase(String position);
}
