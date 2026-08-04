import React from "react";
import { Link } from "react-router-dom";
import "../styles/pages.css";

const LeagueCard = ({ league }) => {
  return (
    <div className="league-card">
      <h2>{league.name}</h2>
      <p>Invite Code: <span>{league.inviteCode}</span></p>
      <p>Teams: {league.teams ? league.teams.length : 0}</p>
      <Link to={`/league/${league.inviteCode}`}>
        View League
      </Link>
    </div>
  );
};

export default LeagueCard;
