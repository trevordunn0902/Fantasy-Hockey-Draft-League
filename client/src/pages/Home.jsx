// src/pages/Home.jsx
import React, { useEffect, useState, useContext } from "react";
import LeagueCard from "../components/LeagueCard";
import { AuthContext } from "../context/AuthContext";
import { getAllLeagues } from "../api/api";
import "../styles/pages.css";

const Home = () => {
  const { user } = useContext(AuthContext);
  const [leagues, setLeagues] = useState([]);
  const [error, setError] = useState("");

  useEffect(() => {
    const fetchLeagues = async () => {
      try {
        const res = await getAllLeagues();
        setLeagues(res);
      } catch {
        setError("Failed to fetch leagues");
      }
    };
    fetchLeagues();
  }, []);

  return (
    <div className="page-container p-4">
      <h1 className="page-title">Welcome {user ? user.username : "Guest"}</h1>

      {error && <p className="error-text">{error}</p>}

      <div className="card-grid">
        {leagues.map((league) => (
          <LeagueCard key={league.id} league={league} />
        ))}
      </div>
    </div>
  );
};

export default Home;


// test testing test test test