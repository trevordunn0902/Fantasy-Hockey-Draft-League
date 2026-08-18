import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { forgotPassword } from "../api/api";
import "../styles/pages.css";

const ForgotPassword = () => {
  const navigate = useNavigate();

  const [email, setEmail] = useState("");
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();

    setMessage("");
    setError("");

    try {
      const response = await forgotPassword(email);
      setMessage(
        response ||
          "If an account exists with that email address, a password reset link has been sent."
      );
    } catch {
      setError("Unable to process password reset request.");
    }
  };

  return (
    <div className="create-league-page">
      <h1>Forgot Password?</h1>

      <p>
        Enter your email address and we'll send you a link to reset your
        password.
      </p>

      {message && <p className="success-text">{message}</p>}
      {error && <p className="error-text">{error}</p>}

      <form onSubmit={handleSubmit}>
        <div>
          <label>Email</label>
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </div>

        <button type="submit" className="bg-blue-600">
          Send Reset Link
        </button>
      </form>

      <button
        type="button"
        onClick={() => navigate("/login")}
        className="secondary-button"
      >
        Back to Login
      </button>
    </div>
  );
};

export default ForgotPassword;