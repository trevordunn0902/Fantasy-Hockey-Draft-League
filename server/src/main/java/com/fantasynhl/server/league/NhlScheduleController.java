package com.fantasynhl.server.league;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/nhl/schedule")
public class NhlScheduleController {

    private final NhlScheduleService nhlScheduleService;

    public NhlScheduleController(NhlScheduleService nhlScheduleService) {
        this.nhlScheduleService = nhlScheduleService;
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Integer>> refreshSchedule() {

        Map<String, Integer> result =
                nhlScheduleService.refreshSeasonSchedule();

        return ResponseEntity.ok(result);
    }
}