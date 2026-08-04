import React, { useEffect, useState } from "react";
import { getTeamById } from "../api/api";
import "../styles/pages.css";

const RosterSidebar = ({ teamId }) => {
  const [roster, setRoster] = useState([]);
  const [error, setError] = useState("");

  useEffect(() => {
    if (!teamId) return;

    const fetchRoster = async () => {
      try {
        const team = await getTeamById(teamId);
        setRoster(team?.roster || []);
      } catch (err) {
        setError("Failed to load roster.");
      }
    };

    fetchRoster();

    const interval = setInterval(fetchRoster, 3000);
    return () => clearInterval(interval);
  }, [teamId]);

  return (
    <div className="roster-sidebar">
      <h2 className="text-lg font-semibold mb-3">My Roster</h2>
      {error && <p className="text-red-400 mb-2">{error}</p>}

      {roster.length === 0 ? (
        <p>No players yet.</p>
      ) : (
        <ul>
          {roster.map((p) => (
            <li key={p.id} className="mb-1">
              {p.name} – {p.positionCode}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default RosterSidebar;
