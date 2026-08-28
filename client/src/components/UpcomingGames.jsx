import React, { useEffect, useState } from "react";
import { getUpcomingGames } from "../api/api";

const UpcomingGames = () => {
  const [games, setGames] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const fetchGames = async () => {
      try {
        const data = await getUpcomingGames(10);
        setGames(data);
      } catch (err) {
        setError("Failed to load upcoming games.");
      } finally {
        setLoading(false);
      }
    };

    fetchGames();
  }, []);

  if (loading) {
    return <p>Loading upcoming games...</p>;
  }

  if (error) {
    return <p className="error-text">{error}</p>;
  }

  return (
    <div>
      <h2>Upcoming Games</h2>

      {games.length === 0 ? (
        <p>No upcoming games found.</p>
      ) : (
        <div>
          {games.map((game) => (
            <div key={game.gameId}>
              <p>
                {game.awayTeam} @ {game.homeTeam}
              </p>

              <p>
                {new Date(game.startTimeUtc).toLocaleString()}
              </p>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default UpcomingGames;