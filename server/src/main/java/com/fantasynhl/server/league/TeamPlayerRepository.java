package com.fantasynhl.server.league;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamPlayerRepository extends JpaRepository<TeamPlayer, TeamPlayerId> {

    long countByTeamIdAndCaptainRole(Long teamId, String string);
    boolean existsByTeamIdAndCaptainRole(Long teamId, String string);
}
