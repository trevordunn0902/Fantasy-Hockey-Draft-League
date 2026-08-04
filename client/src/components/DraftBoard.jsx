import React from "react";
import PlayerCard from "./PlayerCard";
import { AnimatePresence, motion } from "framer-motion";

// NHL teams mapping (abbrev → name)
const NHL_TEAMS = [
  { abbrev: "ANA", name: "Anaheim Ducks" },
  { abbrev: "BOS", name: "Boston Bruins" },
  { abbrev: "BUF", name: "Buffalo Sabres" },
  { abbrev: "CAR", name: "Carolina Hurricanes" },
  { abbrev: "CBJ", name: "Columbus Blue Jackets" },
  { abbrev: "CGY", name: "Calgary Flames" },
  { abbrev: "CHI", name: "Chicago Black Hawks" },
  { abbrev: "COL", name: "Colorado Avalanche" },
  { abbrev: "DAL", name: "Dallas Stars" },
  { abbrev: "DET", name: "Detroit Red Wings" },
  { abbrev: "EDM", name: "Edmonton Oilers" },
  { abbrev: "FLA", name: "Florida Panthers" },
  { abbrev: "LAK", name: "Los Angeles Kings" },
  { abbrev: "MIN", name: "Minnesota Wild" },
  { abbrev: "MTL", name: "Montreal Canadiens" },
  { abbrev: "NJD", name: "New Jersey Devils" },
  { abbrev: "NSH", name: "Nashville Predators" },
  { abbrev: "NYI", name: "New York Islanders" },
  { abbrev: "NYR", name: "New York Rangers" },
  { abbrev: "OTT", name: "Ottawa Senators" },
  { abbrev: "PHI", name: "Philadelphia Flyers" },
  { abbrev: "PIT", name: "Pittsburgh Penguins" },
  { abbrev: "SEA", name: "Seattle Kraken" },
  { abbrev: "SJS", name: "San Jose Sharks" },
  { abbrev: "STL", name: "St. Louis Blues" },
  { abbrev: "TBL", name: "Tampa Bay Lightning" },
  { abbrev: "TOR", name: "Toronto Maple Leafs" },
  { abbrev: "UTA", name: "Utah Mammoth" },
  { abbrev: "VAN", name: "Vancouver Canucks" },
  { abbrev: "VGK", name: "Vegas Golden Knights" },
  { abbrev: "WPG", name: "Winnipeg Jets" },
  { abbrev: "WSH", name: "Washington Capitals" },
];

const DraftBoard = ({ draftStatus, onDraftPick, userTeamId, availablePlayers }) => {
  if (!draftStatus) return null;

  const currentTeam = draftStatus.teams.find(
    (team) => team.id === draftStatus.currentTeamId
  );

  return (
    <div className="draft-board">
      {draftStatus?.started ? (
        <p
          className={`current-turn mb-4 ${
            draftStatus.currentTeamId === userTeamId ? "turn-highlight" : ""
          }`}
        >
          Current Turn: {currentTeam ? currentTeam.name : "Loading..."}{" "}
          {draftStatus.currentTeamId === userTeamId && (
            <span className="your-turn-text">(Your Turn!)</span>
          )}
        </p>
      ) : (
        <p className="text-gray-500 mb-4">Draft has not started yet.</p>
      )}

      <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-3 2xl:grid-cols-4">
        <AnimatePresence>
          {availablePlayers.map((player) => {
            const drafted = player.pickOrder > 0;
            const isMyTeam = player.teamId === userTeamId;

            // Use nhlTeam from API if available; fallback to abbreviation mapping
            const nhlTeam =
              player.nhlTeam ||
              (player.teamName
                ? NHL_TEAMS.find((t) => t.name === player.teamName)?.abbrev || "Unknown"
                : "Unknown");

            return (
              <motion.div
                key={player.playerId}
                layout
                initial={{
                  opacity: 0,
                  scale: 0.95
                }}
                animate={{
                  opacity: 1,
                  scale: 1
                }}
                exit={{
                  opacity: 0,
                  x: 200,
                  scale: 0.8,
                  transition: {
                    duration: 0.35
                  }
                }}
              >
                <PlayerCard
                  player={{ ...player, nhlTeam }}
                  drafted={drafted}
                  isMyTeam={isMyTeam}
                  showPoints={false}
                >
                  <button
                    onClick={() => onDraftPick(player)}
                    disabled={
                      !draftStatus?.started ||
                      draftStatus.currentTeamId !== userTeamId ||
                      drafted
                    }
                    className={`draft-btn ${
                      !drafted && draftStatus.currentTeamId === userTeamId
                        ? "draft-btn-active"
                        : "draft-btn-disabled"
                    }`}
                  >
                    {drafted ? `Drafted (#${player.pickOrder})` : "Draft Player"}
                  </button>
                </PlayerCard>
              </motion.div>
            );
          })}
        </AnimatePresence>
      </div>
    </div>
  );
};

export default DraftBoard;
