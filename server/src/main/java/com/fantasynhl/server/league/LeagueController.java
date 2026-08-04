package com.fantasynhl.server.league;

import com.fantasynhl.server.user.User;
import com.fantasynhl.server.user.UserRepository;
import com.fantasynhl.server.league.dto.LeagueDTO;
import com.fantasynhl.server.league.dto.TeamDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/league")
public class LeagueController {

    private final LeagueService leagueService;
    private final UserRepository userRepository;
    private final DraftService draftService;

    public LeagueController(LeagueService leagueService, UserRepository userRepository, DraftService draftService) {
        this.leagueService = leagueService;
        this.userRepository = userRepository;
        this.draftService = draftService;
    }

    // Create a new league
    @PostMapping("/create")
    public ResponseEntity<LeagueDTO> createLeague(@RequestParam String name) {
        League league = leagueService.createLeague(name);
        return ResponseEntity.ok(new LeagueDTO(league));
    }

    // Join a league
    @PostMapping("/join")
    public ResponseEntity<TeamDTO> joinLeague(
            @RequestParam String inviteCode,
            @RequestParam String teamName,
            @RequestParam Long userId
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Team team = leagueService.joinLeague(inviteCode, user, teamName);
        return ResponseEntity.ok(new TeamDTO(team));
    }

    // Start the draft
    @PostMapping("/draft/start")
    public ResponseEntity<String> startDraft(@RequestParam Long leagueId) {
        draftService.startDraft(leagueId);
        return ResponseEntity.ok("Draft started successfully");
    }

    // Make a draft pick
    @PostMapping("/draft/pick")
    public ResponseEntity<String> makePick(
            @RequestParam Long leagueId,
            @RequestParam Long teamId,
            @RequestParam Long playerId
    ) {
        draftService.draftPlayer(leagueId, teamId, playerId);
        return ResponseEntity.ok("Pick made successfully");
    }

    // Get draft status
    @GetMapping("/draft/status")
    public ResponseEntity<Map<String, Object>> getDraftStatus(@RequestParam Long leagueId) {
        return ResponseEntity.ok(draftService.getLeagueDraftStatus(leagueId));
    }

    // Get league info by invite code
    @GetMapping
    public ResponseEntity<LeagueDTO> getLeague(@RequestParam String inviteCode) {
        return leagueService.getLeague(inviteCode)
                .map(LeagueDTO::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Fetch all leagues
    @GetMapping("/all")
    public ResponseEntity<List<LeagueDTO>> getAllLeagues() {
        List<LeagueDTO> leagues = leagueService.getAllLeagues()
                .stream()
                .map(LeagueDTO::new)
                .toList();
        return ResponseEntity.ok(leagues);
    }
}