package com.fantasynhl.server.league;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/nhl/schedule")
public class NhlScheduleController {

    private static final int CURRENT_SEASON = 20262027;

    private final NhlScheduleService nhlScheduleService;
    private final NhlGameRepository nhlGameRepository;

    public NhlScheduleController(
            NhlScheduleService nhlScheduleService,
            NhlGameRepository nhlGameRepository
    ) {
        this.nhlScheduleService = nhlScheduleService;
        this.nhlGameRepository = nhlGameRepository;
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Integer>> refreshSchedule() {

        Map<String, Integer> result =
                nhlScheduleService.refreshSeasonSchedule();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<NhlGame>> getUpcomingGames(@RequestParam(defaultValue = "10") int limit) {

        List<NhlGame> games =
                nhlGameRepository.findUpcomingGames(
                        LocalDate.now(),
                        CURRENT_SEASON
                );
         if (games.size() > limit) {
            games = games.subList(0, limit);
        }

        return ResponseEntity.ok(games);
    }
}