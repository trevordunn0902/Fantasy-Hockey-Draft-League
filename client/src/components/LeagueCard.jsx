import React from "react";
import { Link } from "react-router-dom";
import "../styles/pages.css";

const LeagueCard = ({ league }) => {
  return (
    <div className="league-card">
      <h2>{league.name}</h2>

      <p>Teams: {league.teams ? league.teams.length : 0}</p>

      <p>{league.inviteOnly ? "Private League" : "Public League"}</p>

      <Link to={`/league/${league.id}`}>
        View League
      </Link>
    </div>
  );
};

export default LeagueCard;
