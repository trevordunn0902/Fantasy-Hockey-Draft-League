import React, { useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { resetPassword } from "../api/api";
import "../styles/pages.css";

const ResetPassword = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  const token = searchParams.get("token");

  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();

    setMessage("");
    setError("");

    if (!token) {
      setError("Invalid or missing password reset link.");
      return;
    }

    if (password !== confirmPassword) {
      setError("Passwords do not match.");
      return;
    }

    try {
      const response = await resetPassword(token, password);

      setMessage(response || "Password reset successfully.");

      setPassword("");
      setConfirmPassword("");
    } catch (err) {
      setError(
        err?.message ||
          "Unable to reset password. The link may be invalid or expired."
      );
    }
  };

  return (
    <div className="create-league-page">
      <h1>Reset Password</h1>

      <p>Enter your new password below.</p>

      {message && <p className="success-text">{message}</p>}
      {error && <p className="error-text">{error}</p>}

      <form onSubmit={handleSubmit}>
        <div>
          <label>New Password</label>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </div>

        <div>
          <label>Confirm New Password</label>
          <input
            type="password"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
            required
          />
        </div>

        <button type="submit" className="bg-blue-600">
          Reset Password
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

export default ResetPassword;