import React, { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import { getTeamById, assignPlayerRole } from "../api/api";
import PlayerCard from "../components/PlayerCard";
import "../styles/pages.css";

const ViewRoster = () => {
  const { teamId } = useParams();
  const [players, setPlayers] = useState([]);
  const [teamName, setTeamName] = useState("");
  const [leagueId, setLeagueId] = useState(null);
  const [teamTotalPoints, setTeamTotalPoints] = useState(0);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);
  const [roles, setRoles] = useState({});

  const roleCounts = {
    CAPTAIN: Object.values(roles).filter((r) => r === "CAPTAIN").length,
    ASSISTANT: Object.values(roles).filter((r) => r === "ASSISTANT").length,
  };

  const captainPlayer = players.find((p) => roles[p.id] === "CAPTAIN");
  const assistantPlayers = players.filter((p) => roles[p.id] === "ASSISTANT");

  useEffect(() => {
    const fetchRoster = async () => {
      try {
        const team = await getTeamById(teamId);
        setPlayers(team?.roster || []);
        setTeamName(team?.name || "");
        setTeamTotalPoints(team?.totalPoints || 0);
        setLeagueId(team?.league?.id || null);

        const initialRoles = {};
        (team?.roster || []).forEach((p) => {
          initialRoles[p.id] = p.role || "NONE";
        });
        setRoles(initialRoles);
      } catch {
        setError("Failed to load roster");
      } finally {
        setLoading(false);
      }
    };
    fetchRoster();
  }, [teamId]);

  const handleRoleChange = async (playerId, newRole) => {
    if (newRole === "CAPTAIN" && roleCounts.CAPTAIN >= 1 && roles[playerId] !== "CAPTAIN") {
      alert("Only 1 Captain allowed.");
      return;
    }
    if (newRole === "ASSISTANT" && roleCounts.ASSISTANT >= 2 && roles[playerId] !== "ASSISTANT") {
      alert("Only 2 Assistants allowed.");
      return;
    }

    try {
      await assignPlayerRole(teamId, playerId, newRole);
      setRoles((prev) => ({ ...prev, [playerId]: newRole }));
    } catch {
      alert("Failed to assign role");
    }
  };

  if (loading) return <p>Loading roster...</p>;
  if (error) return <p className="error-text">{error}</p>;

  return (
    <div className="page-container p-4">
      <div className="flex justify-between items-center mb-4">
        <h1 className="page-title">{teamName ? `${teamName} Roster` : "Team Roster"}</h1>
        <Link to={leagueId ? `/draft/${leagueId}` : `/draft`} className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded">Back to Draft</Link>
      </div>

      <div className="mb-6 p-4 bg-gray-100 rounded shadow-sm">
        <h2 className="section-title mb-2">Team Summary</h2>
        <p><strong>Total Points:</strong> {teamTotalPoints}</p>
        <p><strong>Captain:</strong> {captainPlayer ? captainPlayer.name : "None"}</p>
        <p>
          <strong>Assistants:</strong>{" "}
          {assistantPlayers.length ? assistantPlayers.map((p, idx) => (
            <span key={p.id}>{p.name}{idx < assistantPlayers.length - 1 ? ", " : ""}</span>
          )) : "None"}
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-4 gap-4">
        {players.length ? (
          players.map((player) => (
            <PlayerCard key={player.id} player={player} showPoints={true}>
              <div className="card-children text-sm text-gray-600 mb-2">
                {player.positionCode && <span>Position: {player.positionCode}</span>}
                {player.nhlTeam && <span> | Team: {player.nhlTeam}</span>}
              </div>
              <div className="card-children">
                <label className="block text-sm font-semibold mb-1 mt-2">Role:</label>
                <select value={roles[player.id] || "NONE"} onChange={(e) => handleRoleChange(player.id, e.target.value)} className="border rounded p-1 w-full">
                  <option value="NONE">None</option>
                  <option value="CAPTAIN">Captain</option>
                  <option value="ASSISTANT">Assistant</option>
                </select>
              </div>
            </PlayerCard>
          ))
        ) : (
          <p className="text-gray-500">No players in this roster yet.</p>
        )}
      </div>
    </div>
  );
};

export default ViewRoster;
