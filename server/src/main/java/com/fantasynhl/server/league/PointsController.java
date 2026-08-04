package com.fantasynhl.server.league;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/points")
@CrossOrigin(origins = "*")
public class PointsController {

    private final PointsService pointsService;

    public PointsController(PointsService pointsService) {
        this.pointsService = pointsService;
    }

    /**
     * Manually trigger points update for all players and teams.
     * Scheduled updates happen automatically at 4:30 AM EST.
     */
    @PostMapping("/update")
    public String updatePoints() {
        pointsService.updatePoints();
        return "Player and team points updated successfully!";
    }
}
