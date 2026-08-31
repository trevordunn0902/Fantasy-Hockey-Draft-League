import React, { useEffect, useState } from "react";
import { getUpcomingGamesForTeam } from "../api/api";
import "../styles/roster-upcoming-games.css";

const RosterUpcomingGames = ({ teamId, teamName }) => {
  const [games, setGames] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const getTeamLogo = (teamCode) => {
    return `https://assets.nhle.com/logos/nhl/svg/${teamCode}_light.svg`;
  };

  useEffect(() => {
    const fetchGames = async () => {
      try {
        const data = await getUpcomingGamesForTeam(teamId);
        setGames(data);
      } catch (err) {
        setError("Failed to load upcoming games.");
      } finally {
        setLoading(false);
      }
    };

    if (teamId) {
      fetchGames();
    }
  }, [teamId]);

  if (loading) {
    return (
      <section className="roster-schedule">
        <div className="roster-schedule-header">
          <h2>This Week's Games</h2>
          <p>{teamName}</p>
        </div>

        <div className="roster-schedule-status">
          Loading upcoming games...
        </div>
      </section>
    );
  }

  if (error) {
    return (
      <section className="roster-schedule">
        <div className="roster-schedule-header">
          <h2>This Week's Games</h2>
          <p>{teamName}</p>
        </div>

        <div className="roster-schedule-status error-text">
          {error}
        </div>
      </section>
    );
  }

  if (games.length === 0) {
    return (
      <section className="roster-schedule">
        <div className="roster-schedule-header">
          <h2>This Week's Games</h2>
          <p>{teamName}</p>
        </div>

        <div className="roster-schedule-status">
          No upcoming games found.
        </div>
      </section>
    );
  }

  // Group games by date
  const gamesByDate = games.reduce((groups, game) => {
    if (!groups[game.gameDate]) {
      groups[game.gameDate] = [];
    }

    groups[game.gameDate].push(game);

    return groups;
  }, {});

  const formatDate = (dateString) => {
    const date = new Date(`${dateString}T00:00:00`);

    return {
      weekday: date.toLocaleDateString([], {
        weekday: "long",
      }),
      date: date.toLocaleDateString([], {
        month: "long",
        day: "numeric",
      }),
    };
  };

  const formatTime = (startTimeUtc) => {
    return new Date(startTimeUtc).toLocaleTimeString([], {
      hour: "numeric",
      minute: "2-digit",
    });
  };

  return (
    <section className="roster-schedule">
      <div className="roster-schedule-header">
        <div>
          <h2>This Week's Games</h2>
          <p>{teamName}</p>
        </div>

        <div className="roster-schedule-count">
          {games.length} {games.length === 1 ? "Game" : "Games"}
        </div>
      </div>

      <div className="roster-schedule-games">
        {Object.entries(gamesByDate).map(([date, dateGames]) => {
          const formattedDate = formatDate(date);

          return (
            <div className="roster-schedule-day" key={date}>
              <div className="roster-schedule-day-header">
                <span className="roster-schedule-weekday">
                  {formattedDate.weekday}
                </span>

                <span className="roster-schedule-date">
                  {formattedDate.date}
                </span>
              </div>

              <div className="roster-schedule-day-games">
                {dateGames.map((game) => (
                  <div className="roster-game" key={game.id}>

                    {/* Away Team */}
                    <div className="roster-game-team roster-game-away">
                      <span className="roster-game-label">
                        Away
                      </span>

                      <span className="roster-game-abbreviation">
                        {game.awayTeam}
                      </span>

                      <img
                        src={getTeamLogo(game.awayTeam)}
                        alt={`${game.awayTeam} logo`}
                        className="roster-game-logo"
                      />
                    </div>

                    {/* Game Time */}
                    <div className="roster-game-center">
                      <span className="roster-game-time">
                        {formatTime(game.startTimeUtc)}
                      </span>

                      <span className="roster-game-at">
                        @
                      </span>
                    </div>

                    {/* Home Team */}
                    <div className="roster-game-team roster-game-home">
                      <img
                        src={getTeamLogo(game.homeTeam)}
                        alt={`${game.homeTeam} logo`}
                        className="roster-game-logo"
                      />

                      <span className="roster-game-abbreviation">
                        {game.homeTeam}
                      </span>

                      <span className="roster-game-label">
                        Home
                      </span>
                    </div>

                  </div>
                ))}
              </div>
            </div>
          );
        })}
      </div>
    </section>
  );
};

export default RosterUpcomingGames;