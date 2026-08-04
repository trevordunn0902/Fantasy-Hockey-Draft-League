// src/pages/JoinLeague.jsx
import React, { useState, useContext } from "react";
import { useNavigate } from "react-router-dom";
import { AuthContext } from "../context/AuthContext";
import { joinLeague, getLeagueByInviteCode } from "../api/api";
import "../styles/pages.css";

const JoinLeague = () => {
  const { user } = useContext(AuthContext);
  const navigate = useNavigate();

  const [inviteCode, setInviteCode] = useState("");
  const [teamName, setTeamName] = useState("");
  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [showModal, setShowModal] = useState(false);
  const [joinedLeague, setJoinedLeague] = useState(null);

  if (!user) return <p className="error-text p-4">Login to join a league</p>;

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setIsLoading(true);
    try {
      const league = await getLeagueByInviteCode(inviteCode);
      if (!league) throw new Error("Invalid code");
      await joinLeague(inviteCode, teamName, user.id);
      setJoinedLeague(league);
      setShowModal(true);
    } catch {
      setError("Failed to join league");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="create-league-page">
      <h1>Join League</h1>
      {error && <p className="error-text">{error}</p>}
      <form onSubmit={handleSubmit}>
        <div>
          <label>Invite Code</label>
          <input
            type="text"
            value={inviteCode}
            onChange={(e) => setInviteCode(e.target.value)}
            required
          />
        </div>
        <div>
          <label>Team Name</label>
          <input
            type="text"
            value={teamName}
            onChange={(e) => setTeamName(e.target.value)}
            required
          />
        </div>
        <button type="submit" className={isLoading ? "opacity-70" : "bg-green-600"}>
          {isLoading ? "Joining..." : "Join League"}
        </button>
      </form>

      {showModal && joinedLeague && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h2>League Joined!</h2>
            <p>League: {joinedLeague.name}</p>
            <p>Invite Code: <span>{joinedLeague.inviteCode}</span></p>
            <button
              className="modal-btn bg-blue-600"
              onClick={() => navigator.clipboard.writeText(joinedLeague.inviteCode)}
            >
              Copy Code
            </button>
            <button
              className="modal-btn bg-green-600"
              onClick={() => navigate(`/league/${joinedLeague.inviteCode}`)}
            >
              Go to League
            </button>
            <span className="modal-close" onClick={() => setShowModal(false)}>
              Close
            </span>
          </div>
        </div>
      )}
    </div>
  );
};

export default JoinLeague;
