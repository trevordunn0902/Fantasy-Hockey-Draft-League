import React from "react";
import "../styles/pages.css";

const POSITION_LABELS = {
  G: "Goalie",
  D: "Defense",
  C: "Center",
  L: "Left Wing",
  R: "Right Wing",
};

const PlayerCard = ({
  player,
  drafted,
  isMyTeam,
  children,
  showPoints = true,
}) => {
  return (
    <div
      className={`player-card ${
        drafted ? "drafted" : ""
      } ${isMyTeam && drafted ? "my-team" : ""}`}
    >
      <div className="player-card-top">

        <img
          className="player-team-logo"
          src={`/logos/${player.nhlTeam}.png`}
          alt={player.nhlTeam}
          onError={(e) => (e.target.style.display = "none")}
        />

        <span className={`position-badge position-${player.positionCode}`}>
          {player.positionCode}
        </span>

      </div>

      <div className="player-name">
        {player.playerName || player.name}
      </div>

      <div className="player-team-name">
        {player.nhlTeam}
      </div>

      <div className="player-position">
        {POSITION_LABELS[player.positionCode]}
      </div>

      {showPoints && player.points !== undefined && (
        <div className="player-points">
          Fantasy Points
          <span>{player.points}</span>
        </div>
      )}

      <div className="player-actions">
        {children}
      </div>
    </div>
  );
};

export default PlayerCard;