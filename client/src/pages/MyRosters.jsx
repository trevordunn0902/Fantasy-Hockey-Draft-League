import React, { useEffect, useState, useContext } from "react";
import { Link } from "react-router-dom";
import { AuthContext } from "../context/AuthContext";
import { getUserTeams } from "../api/api";
import "../styles/pages.css";

const MyRosters = () => {
  const { user } = useContext(AuthContext);
  const [teams, setTeams] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const fetchTeams = async () => {
      if (!user?.id) return;
      try {
        const data = await getUserTeams(user.id);
        setTeams(data);
      } catch (err) {
        setError("Failed to load your teams.");
      } finally {
        setLoading(false);
      }
    };
    fetchTeams();
  }, [user]);

  if (loading) return <p>Loading your rosters...</p>;
  if (error) return <p className="error-text">{error}</p>;

  return (
    <div className="page-container">
      <h1 className="page-title mb-4">My Rosters</h1>
      {teams.length === 0 ? (
        <p>You don’t have any teams yet.</p>
      ) : (
        <div className="card-grid">
          {teams.map((team) => (
            <div key={team.id} className="team-card">
              <h2 className="text-lg font-semibold mb-1">{team.name}</h2>
              <p className="text-sm text-gray-400">
                League: {team.league?.name || `#${team.league?.id || "?"}`}
              </p>

              <Link
                to={`/league/${team.league?.id}/team/${team.id}`}
                className="btn btn-blue mt-2"
              >
                View Roster
              </Link>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default MyRosters;
