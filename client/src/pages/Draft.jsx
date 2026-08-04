import React, { useEffect, useState, useContext } from "react";
import { useParams, Link } from "react-router-dom";
import { AuthContext } from "../context/AuthContext";
import { getDraftStatus, getAvailablePlayers, draftPlayer } from "../api/api";
import DraftBoard from "../components/DraftBoard";
import RosterSidebar from "../components/RosterSidebar";
import "../styles/pages.css";

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

const Draft = () => {
  const { user } = useContext(AuthContext);
  const { leagueId } = useParams();

  const [draftStatus, setDraftStatus] = useState(null);
  const [availablePlayers, setAvailablePlayers] = useState([]);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);
  const [positionFilter, setPositionFilter] = useState("");
  const [searchTerm, setSearchTerm] = useState("");
  const [teamFilter, setTeamFilter] = useState("");
  const [notification, setNotification] = useState("");
  const [confirmingPick, setConfirmingPick] = useState(null);

  const fetchDraftStatus = async () => {
    try {
      const status = await getDraftStatus(leagueId);
      setDraftStatus(status);

      if (status.lastPick) {
        setNotification(
          `${status.lastPick.teamName} selected ${status.lastPick.playerName} (${status.lastPick.positionCode}, ${status.lastPick.nhlTeam || "N/A"})`
        );
        setTimeout(() => setNotification(""), 5000);
      }
    } catch (err) {
      setError("Failed to fetch draft status. Make sure you are part of this league.");
    }
  };

  const fetchAvailablePlayersList = async () => {
    try {
      const players = await getAvailablePlayers(leagueId);
      setAvailablePlayers(players);
    } catch (err) {
      setError("Failed to fetch available players.");
    }
  };

  const handleDraftPick = async (player) => {
    if (!draftStatus?.teams) return;
    const myTeam = draftStatus.teams.find((t) => t.owner?.id === user.id);
    if (!myTeam) return;

    if (confirmingPick?.playerId === player.playerId) {
      try {
        await draftPlayer(leagueId, myTeam.id, player.playerId);
        // Update both draft status and available players immediately
        await Promise.all([fetchDraftStatus(), fetchAvailablePlayersList()]);
        setError("");
        setConfirmingPick(null);
      } catch (err) {
        setError(err.message || "Failed to make draft pick. Check position limits or try again.");
        setConfirmingPick(null);
      }
    } else {
      setConfirmingPick(player);
      setTimeout(() => setConfirmingPick(null), 5000);
    }
  };

  // Polling both draftStatus and availablePlayers
  useEffect(() => {
    if (!leagueId) return;
    setLoading(true);

    const fetchAll = async () => {
      await Promise.all([fetchDraftStatus(), fetchAvailablePlayersList()]);
      setLoading(false);
    };
    fetchAll();

    const interval = setInterval(fetchAll, 3000); // refresh every 3s
    return () => clearInterval(interval);
  }, [leagueId]);

  if (loading) return <p className="draft-loading">Loading draft...</p>;
  if (!draftStatus || !Array.isArray(draftStatus.teams)) return <p className="draft-loading">Loading teams...</p>;

  const myTeam = draftStatus.teams.find((t) => t.owner?.id === user.id);
  const myTeamId = myTeam?.id || null;

  let filteredPlayers = availablePlayers
    .filter((p) => !positionFilter || p.positionCode === positionFilter)
    .filter((p) => !searchTerm || (p.playerName && p.playerName.toLowerCase().includes(searchTerm.toLowerCase())))
    .filter((p) => !teamFilter || p.nhlTeam === teamFilter);

  return (
    <div className="draft-page">
      <h1 className="page-title mb-4">Draft Board</h1>

      {notification && (
        <div className="global-notification fixed top-4 left-1/2 transform -translate-x-1/2 bg-blue-100 border border-blue-300 px-4 py-2 rounded shadow-md animate-fade-in-out z-50">
          {notification}
        </div>
      )}
      {error && (
        <div className="global-notification fixed top-16 left-1/2 transform -translate-x-1/2 bg-red-100 border border-red-300 px-4 py-2 rounded shadow-md animate-fade-in-out z-50">
          {error}
        </div>
      )}
      {confirmingPick && (
        <div className="global-notification fixed top-28 left-1/2 transform -translate-x-1/2 bg-yellow-100 border border-yellow-300 px-4 py-2 rounded shadow-md animate-fade-in-out z-50">
          Confirm drafting {confirmingPick.playerName}? Click again to confirm.
        </div>
      )}

      {/* Filters */}
      <div className="flex gap-2 mb-4">
        <select value={positionFilter} onChange={(e) => setPositionFilter(e.target.value)} className="border px-2 py-1 rounded">
          <option value="">All Positions</option>
          <option value="G">Goalie</option>
          <option value="D">Defense</option>
          <option value="C">Center</option>
          <option value="L">Left Wing</option>
          <option value="R">Right Wing</option>
        </select>
        <input type="text" placeholder="Search player" value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)} className="border px-2 py-1 rounded flex-1" />
        <select value={teamFilter} onChange={(e) => setTeamFilter(e.target.value)} className="border px-2 py-1 rounded">
          <option value="">All Teams</option>
          {NHL_TEAMS.map((t) => (<option key={t.abbrev} value={t.abbrev}>{t.name}</option>))}
        </select>
      </div>

      <div className="draft-layout">
        <div className="draft-board-section">
          <DraftBoard draftStatus={draftStatus} onDraftPick={handleDraftPick} userTeamId={myTeamId} availablePlayers={filteredPlayers} />
        </div>
        {myTeamId && <RosterSidebar teamId={myTeamId} />}
      </div>

      <h2 className="section-title mt-6 mb-2">League Teams</h2>
      <div className="card-grid">
        {draftStatus.teams.map((team) => (
          <div key={team.id} className="team-card">
            <h3>{team.name}</h3>
            <p>Owner: {team.owner?.email || "Unknown"}</p>
            <Link to={`/league/${leagueId}/team/${team.id}`} className="btn btn-blue mt-2">View Roster</Link>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Draft;
