import React, { useEffect, useState, useContext } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { getLeagueByInviteCode, startDraft, getDraftStatus, getTeamById } from "../api/api";
import TeamCard from "../components/TeamCard";
import { AuthContext } from "../context/AuthContext";
import "../styles/pages.css";

const League = () => {
  const { inviteCode } = useParams();
  const navigate = useNavigate();
  const { user } = useContext(AuthContext);

  const [league, setLeague] = useState(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [draftStarted, setDraftStarted] = useState(false);
  const [leaderboard, setLeaderboard] = useState([]);

  // Fetch league and leaderboard
  const fetchLeague = async () => {
    try {
      const res = await getLeagueByInviteCode(inviteCode);
      setLeague(res);

      const draftStatus = await getDraftStatus(res.id);
      setDraftStarted(Boolean(draftStatus?.started));

      const leaderboardData = await Promise.all(
        res.teams.map(async (team) => {
          let totalPoints = team.totalPoints;
          if (totalPoints === undefined) {
            const teamDetails = await getTeamById(team.id);
            totalPoints = teamDetails.totalPoints || 0;
          }
          return { id: team.id, name: team.name, totalPoints };
        })
      );

      leaderboardData.sort((a, b) => b.totalPoints - a.totalPoints);
      setLeaderboard(leaderboardData);

    } catch (err) {
      console.error(err);
      setError("Failed to fetch league");
    }
  };

  const handleStartDraft = async () => {
    if (!league || draftStarted) return;
    const isMember = league.teams.some((t) => t.owner.id === user?.id);
    if (!isMember) {
      setError("Only members can start draft");
      return;
    }
    setLoading(true);
    try {
      await startDraft(league.id);
      await fetchLeague();
      navigate(`/draft/${league.id}`);
    } catch (err) {
      console.error(err);
      setError("Failed to start draft");
    } finally {
      setLoading(false);
    }
  };

  // Auto-refresh league every 2–3 seconds
  useEffect(() => {
    if (!inviteCode) return;
    fetchLeague();
    const interval = setInterval(fetchLeague, 3000); // Poll every 3s
    return () => clearInterval(interval);
  }, [inviteCode]);

  if (error) return <p className="error-text p-4">{error}</p>;
  if (!league) return <p className="p-4">Loading...</p>;

  const isMember = league.teams.some((t) => t.owner.id === user?.id);

  return (
    <div className="page-container p-4">
      {/* Top Section */}
      <div className="text-center mb-8">
        <h1 className="page-title">{league.name}</h1>
        <p>
          Invite Code: <span className="font-mono">{league.inviteCode}</span>
        </p>
      </div>

      {/* Middle Section */}
      <div className="league-middle-section">
        {/* Left: Leaderboard */}
        <div className="leaderboard-section league-card p-4">
          <h2 className="section-title mb-4">Leaderboard</h2>
          {leaderboard.length > 0 ? (
            <div className="overflow-x-auto">
              <table className="leaderboard-table w-full text-left">
                <thead>
                  <tr>
                    <th>Rank</th>
                    <th>Team</th>
                    <th>Points</th>
                  </tr>
                </thead>
                <tbody>
                  {leaderboard.map((team, idx) => {
                    const isUserTeam = league.teams[idx]?.owner?.id === user?.id || team.id === user?.teamId;
                    return (
                      <tr key={team.id} className={isUserTeam ? "highlight-user-team" : ""}>
                        <td>{idx + 1}</td>
                        <td>{team.name}</td>
                        <td>{team.totalPoints}</td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          ) : (
            <p>No leaderboard data available.</p>
          )}
        </div>

        {/* Right: Teams + Buttons */}
        <div className="teams-section flex flex-col gap-6">
          <h2 className="section-title">Teams</h2>
          <div className="teamcards-grid">
            {league.teams.map((team) => (
              <TeamCard key={team.id} team={team} />
            ))}
          </div>

          {/* Buttons */}
          <div className="flex flex-wrap gap-2">
            <button
              className={`league-btn ${!isMember || draftStarted || loading ? "disabled-btn" : "start-draft-btn"}`}
              disabled={!isMember || draftStarted || loading}
              onClick={handleStartDraft}
            >
              {loading ? "Starting..." : draftStarted ? "Draft Started" : "Start Draft"}
            </button>

            <button
              className={`league-btn ${!isMember ? "disabled-btn" : "view-draft-btn"}`}
              disabled={!isMember}
              onClick={() => navigate(`/draft/${league.id}`)}
            >
              View Draft
            </button>
          </div>

          {/* Error Messages */}
          <div className="flex flex-col gap-1">
            {draftStarted && <p className="text-sm text-gray-500">Draft has already started for this league.</p>}
            {!isMember && <p className="error-text">You must be a member to view or start the draft.</p>}
          </div>
        </div>
      </div>
    </div>
  );
};

export default League;
