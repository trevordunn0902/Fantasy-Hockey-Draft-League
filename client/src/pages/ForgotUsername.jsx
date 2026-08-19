// src/pages/ForgotUsername.jsx
import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { forgotUsername } from "../api/api";
import "../styles/pages.css";

const ForgotUsername = () => {
  const navigate = useNavigate();

  const [email, setEmail] = useState("");
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();

    setMessage("");
    setError("");
    setLoading(true);

    try {
      const response = await forgotUsername(email);

    setMessage(response);
    } catch {
      setError("Something went wrong. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="create-league-page auth-page">
      <h1>Forgot Username?</h1>

      <p>
        Enter the email address associated with your account and we'll send
        your username to you.
      </p>

      {message && <p className="success-text">{message}</p>}
      {error && <p className="error-text">{error}</p>}
      
      {!message && (
        <form onSubmit={handleSubmit} className="auth-form">
          <div>
            <label>Email</label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>

          <button type="submit" className="bg-blue-600" disabled={loading}>
            {loading ? "Sending..." : "Send Username"}
          </button>
        </form>
      )}

      <button
        type="button"
        className="secondary-button"
        onClick={() => navigate("/login")}
      >
        Back to Login
      </button>
    </div>
  );
};

export default ForgotUsername;