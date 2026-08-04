import React from "react";
import { Link } from "react-router-dom";
import "../styles/pages.css";

const TeamCard = ({ team }) => {
  return (
    <div className="team-card">
      <h3>{team.name}</h3>
      <p>Owner: {team.owner?.email}</p>
      <p>Roster Size: {team.roster ? team.roster.length : 0}</p>
      {team.league && (
        <Link to={`/league/${team.league.inviteCode}`}>
          View League
        </Link>
      )}
    </div>
  );
};

export default TeamCard;
