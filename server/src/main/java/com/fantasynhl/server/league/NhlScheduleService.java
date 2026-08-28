package com.fantasynhl.server.league;

import com.fantasynhl.server.league.dto.NhlScheduleGameDTO;
import com.fantasynhl.server.league.dto.NhlScheduleResponse;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Service
public class NhlScheduleService {

    private final RestTemplate restTemplate;
    private final NhlGameRepository nhlGameRepository;
    private final TeamRepository teamRepository;

    private static final String SCHEDULE_URL =
            "https://api-web.nhle.com/v1/club-schedule-season/";

    private static final int CURRENT_SEASON = 20262027;

    private static final List<String> NHL_TEAMS = List.of(
            "ANA", "BOS", "BUF", "CAR", "CBJ", "CGY", "CHI", "COL",
            "DAL", "DET", "EDM", "FLA", "LAK", "MIN", "MTL", "NJD",
            "NSH", "NYI", "NYR", "OTT", "PHI", "PIT", "SEA", "SJS",
            "STL", "TBL", "TOR", "UTA", "VAN", "VGK", "WPG", "WSH"
    );

    public NhlScheduleService(
            NhlGameRepository nhlGameRepository,
            TeamRepository teamRepository
    ) {
        this.nhlGameRepository = nhlGameRepository;
        this.teamRepository = teamRepository;
        this.restTemplate = new RestTemplate();
    }

    /**
     * Fetch the entire NHL season schedule and synchronize it
     * with the database.
     */
    @Transactional
    public Map<String, Integer> refreshSeasonSchedule() {

        System.out.println("=== Starting NHL Schedule Update ===");
        System.out.println("Season: " + CURRENT_SEASON);

        int teamsProcessed = 0;
        int gamesReceived = 0;

        /*
         * Store every unique game returned by the NHL API.
         * We don't touch the database until all 32 teams
         * have successfully returned their schedules.
         */
        Map<Long, NhlScheduleGameDTO> fetchedGames = new LinkedHashMap<>();

        // ==========================================
        // Phase 1: Fetch all team schedules
        // ==========================================

        for (String team : NHL_TEAMS) {

            System.out.println("Fetching schedule for: " + team);

            try {

                String url = SCHEDULE_URL
                        + team
                        + "/"
                        + CURRENT_SEASON;

                NhlScheduleResponse response =
                        restTemplate.getForObject(
                                url,
                                NhlScheduleResponse.class
                        );

                if (response == null || response.getGames() == null) {

                    throw new RuntimeException(
                            "No schedule returned for " + team
                    );
                }

                teamsProcessed++;
                gamesReceived += response.getGames().size();

                for (NhlScheduleGameDTO gameDTO : response.getGames()) {

                    if (gameDTO.getId() == null) {
                        continue;
                    }

                    /*
                     * Each game appears in both teams' schedules.
                     * Using the NHL game ID as the map key
                     * automatically removes duplicates.
                     */
                    fetchedGames.put(
                            gameDTO.getId(),
                            gameDTO
                    );
                }

            } catch (Exception e) {

                /*
                 * Abort the entire refresh.
                 *
                 * Most importantly, we have not modified
                 * the database yet.
                 */
                System.out.println(
                        "=== NHL Schedule Update ABORTED ==="
                );

                System.out.println(
                        "Failed to fetch schedule for "
                                + team
                                + ": "
                                + e.getMessage()
                );

                throw new RuntimeException(
                        "Failed to fetch NHL schedule for "
                                + team,
                        e
                );
            }
        }

        // ==========================================
        // Phase 2: Validate complete API response
        // ==========================================

        if (teamsProcessed != NHL_TEAMS.size()) {

            throw new RuntimeException(
                    "Schedule refresh aborted. Expected "
                            + NHL_TEAMS.size()
                            + " teams but received "
                            + teamsProcessed
            );
        }

        System.out.println(
                "All " + teamsProcessed
                        + " NHL team schedules retrieved successfully."
        );

        System.out.println(
                "Unique games received: "
                        + fetchedGames.size()
        );

        // ==========================================
        // Phase 3: Synchronize database
        // ==========================================

        List<NhlGame> existingGames =
                nhlGameRepository.findBySeason(CURRENT_SEASON);

        Map<Long, NhlGame> existingGameMap = new HashMap<>();

        for (NhlGame game : existingGames) {
            existingGameMap.put(game.getGameId(), game);
        }

        Set<Long> apiGameIds = fetchedGames.keySet();

        List<NhlGame> gamesToSave = new ArrayList<>();
        List<NhlGame> gamesToDelete = new ArrayList<>();

        int gamesInserted = 0;
        int gamesUpdated = 0;
        int gamesDeleted = 0;

        // ==========================================
        // Insert / Update
        // ==========================================

        for (NhlScheduleGameDTO gameDTO : fetchedGames.values()) {

            NhlGame existingGame =
                    existingGameMap.get(gameDTO.getId());

            if (existingGame != null) {

                updateGame(
                        existingGame,
                        gameDTO
                );

                gamesToSave.add(existingGame);
                gamesUpdated++;

            } else {

                NhlGame newGame =
                        convertToEntity(gameDTO);

                gamesToSave.add(newGame);
                gamesInserted++;
            }
        }

        // ==========================================
        // Find removed games
        // ==========================================

        for (NhlGame existingGame : existingGames) {

            if (!apiGameIds.contains(existingGame.getGameId())) {

                gamesToDelete.add(existingGame);
            }
        }

        gamesDeleted = gamesToDelete.size();

        // ==========================================
        // Apply database changes
        // ==========================================

        if (!gamesToSave.isEmpty()) {
            nhlGameRepository.saveAll(gamesToSave);
        }

        if (!gamesToDelete.isEmpty()) {
            nhlGameRepository.deleteAll(gamesToDelete);
        }

        // ==========================================
        // Completed
        // ==========================================

        System.out.println("=== NHL Schedule Update Completed ===");
        System.out.println("Teams processed: " + teamsProcessed);
        System.out.println("Games received: " + gamesReceived);
        System.out.println("Unique games: " + fetchedGames.size());
        System.out.println("Games inserted: " + gamesInserted);
        System.out.println("Games updated: " + gamesUpdated);
        System.out.println("Games deleted: " + gamesDeleted);

        Map<String, Integer> result = new LinkedHashMap<>();

        result.put("teamsProcessed", teamsProcessed);
        result.put("gamesReceived", gamesReceived);
        result.put("uniqueGames", fetchedGames.size());
        result.put("gamesInserted", gamesInserted);
        result.put("gamesUpdated", gamesUpdated);
        result.put("gamesDeleted", gamesDeleted);

        return result;
    }

    /**
     * Find the next 7 days of NHL games for a fantasy team.
     *
     * The window begins on the date of the team's earliest
     * upcoming game, rather than simply using today's date.
     */
    public List<NhlGame> getUpcomingGamesForTeam(Long teamId) {

        // ==========================================
        // Find fantasy team
        // ==========================================

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Team not found: " + teamId
                        )
                );

        // ==========================================
        // Get NHL teams represented on the roster
        // ==========================================

        List<String> nhlTeams = team.getTeamPlayers()
                .stream()
                .map(TeamPlayer::getPlayer)
                .map(Player::getNhlTeam)
                .filter(Objects::nonNull)
                .filter(teamCode -> !teamCode.isBlank())
                .distinct()
                .toList();

        if (nhlTeams.isEmpty()) {
            return Collections.emptyList();
        }

        System.out.println(
                "Finding upcoming games for fantasy team: "
                        + team.getName()
        );

        System.out.println(
                "NHL teams on roster: " + nhlTeams
        );

        // ==========================================
        // Find earliest upcoming game
        // ==========================================

        LocalDate today = LocalDate.now();

        List<NhlGame> nextGames =
                nhlGameRepository.findFirstUpcomingGameForTeams(
                        nhlTeams,
                        today,
                        CURRENT_SEASON
                );

        if (nextGames.isEmpty()) {
            return Collections.emptyList();
        }

        LocalDate startDate =
                nextGames.get(0).getGameDate();

        // ==========================================
        // Build 7-day window
        // ==========================================

        LocalDate endDate =
                startDate.plusDays(6);

        System.out.println(
                "Roster game window: "
                        + startDate
                        + " through "
                        + endDate
        );

        // ==========================================
        // Fetch games within window
        // ==========================================

        return nhlGameRepository.findUpcomingGamesForTeams(
                nhlTeams,
                startDate,
                endDate,
                CURRENT_SEASON
        );
    }

    /**
     * Convert NHL API DTO into our database entity.
     */
    private NhlGame convertToEntity(
            NhlScheduleGameDTO dto
    ) {

        NhlGame game = new NhlGame();

        game.setGameId(dto.getId());
        game.setSeason(dto.getSeason());
        game.setGameType(dto.getGameType());

        game.setGameDate(
                LocalDate.parse(dto.getGameDate())
        );

        game.setStartTimeUtc(
                Instant.parse(dto.getStartTimeUTC())
        );

        if (dto.getVenue() != null) {
            game.setVenue(
                    dto.getVenue().getDefaultName()
            );
        }

        game.setVenueTimezone(
                dto.getVenueTimezone()
        );

        game.setGameState(
                dto.getGameState()
        );

        game.setGameScheduleState(
                dto.getGameScheduleState()
        );

        if (dto.getAwayTeam() != null) {
            game.setAwayTeam(
                    dto.getAwayTeam().getAbbrev()
            );
        }

        if (dto.getHomeTeam() != null) {
            game.setHomeTeam(
                    dto.getHomeTeam().getAbbrev()
            );
        }

        return game;
    }

    /**
     * Update an existing game with the latest NHL data.
     */
    private void updateGame(
            NhlGame game,
            NhlScheduleGameDTO dto
    ) {

        game.setSeason(dto.getSeason());
        game.setGameType(dto.getGameType());

        game.setGameDate(
                LocalDate.parse(dto.getGameDate())
        );

        game.setStartTimeUtc(
                Instant.parse(dto.getStartTimeUTC())
        );

        if (dto.getVenue() != null) {
            game.setVenue(
                    dto.getVenue().getDefaultName()
            );
        }

        game.setVenueTimezone(
                dto.getVenueTimezone()
        );

        game.setGameState(
                dto.getGameState()
        );

        game.setGameScheduleState(
                dto.getGameScheduleState()
        );

        if (dto.getAwayTeam() != null) {
            game.setAwayTeam(
                    dto.getAwayTeam().getAbbrev()
            );
        }

        if (dto.getHomeTeam() != null) {
            game.setHomeTeam(
                    dto.getHomeTeam().getAbbrev()
            );
        }
    }
}