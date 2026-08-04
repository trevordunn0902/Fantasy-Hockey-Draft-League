// src/pages/CreateLeague.jsx
import React, { useState, useContext } from "react";
import { useNavigate } from "react-router-dom";
import { createLeague, joinLeague } from "../api/api";
import { AuthContext } from "../context/AuthContext";
import "../styles/pages.css"; // Import the page-specific CSS

const CreateLeague = () => {
  const { user } = useContext(AuthContext);
  const navigate = useNavigate();

  const [leagueName, setLeagueName] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState("");
  const [showModal, setShowModal] = useState(false);
  const [inviteCode, setInviteCode] = useState("");
  const [createdLeagueName, setCreatedLeagueName] = useState("");

  if (!user) {
    return (
      <p className="create-league-page-error">
        You must be logged in to create a league.
      </p>
    );
  }

  const handleCreateLeague = async (e) => {
    e.preventDefault();
    setError("");
    setIsLoading(true);

    try {
      const leagueData = await createLeague(leagueName);

      const teamName = prompt("Enter your team name to join this league:");
      if (!teamName) {
        setIsLoading(false);
        alert("You must enter a team name to continue.");
        return;
      }

      await joinLeague(leagueData.inviteCode, teamName, user.id);

      setInviteCode(leagueData.inviteCode);
      setCreatedLeagueName(leagueData.name);
      setShowModal(true);

      localStorage.setItem("lastCreatedLeague", JSON.stringify(leagueData));
    } catch (err) {
      console.error(err);
      setError("Failed to create or join league. Please try again.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="create-league-page">
      <h1>Create a League</h1>

      {error && <p className="error-text">{error}</p>}

      <form onSubmit={handleCreateLeague}>
        <div>
          <label>League Name</label>
          <input
            type="text"
            value={leagueName}
            onChange={(e) => setLeagueName(e.target.value)}
            placeholder="Enter a league name"
            required
          />
        </div>

        <button
          type="submit"
          className={isLoading ? "opacity-70" : ""}
          disabled={!leagueName || isLoading}
        >
          {isLoading ? "Creating League..." : "Create League"}
        </button>
      </form>

      {showModal && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h2>League Created!</h2>
            <p>
              <strong>{createdLeagueName}</strong> has been created successfully.
            </p>
            <p>
              Invite Code: <span>{inviteCode}</span>
            </p>

            <div className="flex flex-col gap-2">
              <button
                className="modal-btn bg-blue-600"
                onClick={() => {
                  navigator.clipboard.writeText(inviteCode);
                  alert("Invite code copied!");
                }}
              >
                Copy Invite Code
              </button>

              <button
                className="modal-btn bg-green-600"
                onClick={() => navigate(`/league/${inviteCode}`)}
              >
                Go to League
              </button>

              <button
                className="modal-close"
                onClick={() => setShowModal(false)}
              >
                Close
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default CreateLeague;
