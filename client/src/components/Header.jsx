import React, { useContext } from "react";
import { AuthContext } from "../context/AuthContext";
import { Link, useNavigate } from "react-router-dom";
import "../styles/pages.css";

const Header = () => {
  const { user, logout } = useContext(AuthContext);
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <header className="app-header">
  <div className="logo">
    <Link to="/">Fantasy NHL</Link>
  </div>
  <nav>
    <Link to="/" className="btn btn-purple">Home</Link>

    {user ? (
      <>
        <Link to="/create-league" className="btn btn-green">Create League</Link>
        <Link to="/join-league" className="btn btn-blue">Join League</Link>
        <Link to="/my-rosters" className="btn btn-purple">My Rosters</Link>

        <span className="user-email">{user.email}</span>
        <button onClick={handleLogout} className="btn btn-red">Logout</button>
      </>
    ) : (
      <>
        <Link to="/login">Login</Link>
        <Link to="/register">Register</Link>
      </>
    )}
  </nav>
</header>

  );
};

export default Header;
