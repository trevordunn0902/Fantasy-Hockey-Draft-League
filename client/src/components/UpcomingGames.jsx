import React, { useEffect, useState } from "react";
import { getUpcomingGames } from "../api/api";
import "../styles/upcoming-games.css";

const UpcomingGames = () => {
  const [games, setGames] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const fetchGames = async () => {
      try {
        const data = await getUpcomingGames(20);
        setGames(data);
      } catch (err) {
        setError("Failed to load upcoming games.");
      } finally {
        setLoading(false);
      }
    };

    fetchGames();
  }, []);

  const getTeamLogo = (teamCode) => {
    return `https://assets.nhle.com/logos/nhl/svg/${teamCode}_light.svg`;
  };

  if (loading) {
    return (
      <section className="upcoming-schedule">
        <div className="upcoming-schedule-header">
          <h2>Upcoming Games</h2>
        </div>

        <div className="upcoming-schedule-status">
          Loading upcoming games...
        </div>
      </section>
    );
  }

  if (error) {
    return (
      <section className="upcoming-schedule">
        <div className="upcoming-schedule-header">
          <h2>Upcoming Games</h2>
        </div>

        <div className="upcoming-schedule-status error-text">
          {error}
        </div>
      </section>
    );
  }

  if (games.length === 0) {
    return (
      <section className="upcoming-schedule">
        <div className="upcoming-schedule-header">
          <h2>Upcoming Games</h2>
        </div>

        <div className="upcoming-schedule-status">
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
    <section className="upcoming-schedule">
      <div className="upcoming-schedule-header">
        <div>
          <h2>Upcoming Games</h2>
          <p>NHL Schedule</p>
        </div>

        <div className="upcoming-schedule-count">
          {games.length} {games.length === 1 ? "Game" : "Games"}
        </div>
      </div>

      <div className="upcoming-schedule-games">
        {Object.entries(gamesByDate).map(([date, dateGames]) => {
          const formattedDate = formatDate(date);

          return (
            <div className="upcoming-schedule-day" key={date}>
              <div className="upcoming-schedule-day-header">
                <span className="upcoming-schedule-weekday">
                  {formattedDate.weekday}
                </span>

                <span className="upcoming-schedule-date">
                  {formattedDate.date}
                </span>
              </div>

              <div className="upcoming-schedule-day-games">
                {dateGames.map((game) => (
                  <div className="upcoming-game" key={game.id}>

                    {/* Away Team */}
                    <div className="upcoming-game-team upcoming-game-away">
                      <span className="upcoming-game-label">
                        Away
                      </span>

                      <span className="upcoming-game-abbreviation">
                        {game.awayTeam}
                      </span>

                      <img
                        src={getTeamLogo(game.awayTeam)}
                        alt={`${game.awayTeam} logo`}
                        className="upcoming-game-logo"
                      />
                    </div>

                    {/* Game Time */}
                    <div className="upcoming-game-center">
                      <span className="upcoming-game-time">
                        {formatTime(game.startTimeUtc)}
                      </span>

                      <span className="upcoming-game-at">
                        @
                      </span>
                    </div>

                    {/* Home Team */}
                    <div className="upcoming-game-team upcoming-game-home">
                      <img
                        src={getTeamLogo(game.homeTeam)}
                        alt={`${game.homeTeam} logo`}
                        className="upcoming-game-logo"
                      />

                      <span className="upcoming-game-abbreviation">
                        {game.homeTeam}
                      </span>

                      <span className="upcoming-game-label">
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

export default UpcomingGames;