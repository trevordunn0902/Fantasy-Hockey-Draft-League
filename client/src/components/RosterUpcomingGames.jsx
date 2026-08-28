import React, { useEffect, useState } from "react";
import { getUpcomingGamesForTeam } from "../api/api";

const RosterUpcomingGames = ({ teamId, teamName }) => {
  const [games, setGames] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

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
    return <p>Loading upcoming games...</p>;
  }

  if (error) {
    return <p className="error-text">{error}</p>;
  }

  if (games.length === 0) {
    return (
      <div>
        <h2>This Week's Games for {teamName}</h2>
        <p>No upcoming games found.</p>
      </div>
    );
  }

  return (
    <div>
      <h2>This Week's Games for {teamName}</h2>

      {games.map((game) => (
        <div key={game.id}>
          <p>
            <strong>{game.gameDate}</strong>
          </p>

          <p>
            {game.awayTeam} @ {game.homeTeam}
          </p>

          <p>
            {new Date(game.startTimeUtc).toLocaleTimeString([], {
              hour: "numeric",
              minute: "2-digit",
            })}
          </p>
        </div>
      ))}
    </div>
  );
};

export default RosterUpcomingGames;