// src/api/api.js
import axios from "axios";

// Base URL for your backend
const API = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  withCredentials: true, // if your backend uses cookies for auth
});

// --- User Auth ---
export const registerUser = async (username, email, password) => {
  try {
    const response = await API.post("/auth/register", null, {
      params: { username, email, password },
    });
    return response.data;
  } catch (err) {
    throw err.response?.data || { message: "Registration failed" };
  }
};

export const loginUser = async (username, password) => {
  try {
    const response = await API.post("/auth/login", null, {
      params: { username, password },
    });
    return response.data;
  } catch (err) {
    throw err.response?.data || { message: "Login failed" };
  }
};

export const forgotUsername = async (email) => {
  try {
    const response = await API.post("/auth/forgot-username", null, {
      params: { email },
    });
    return response.data;
  } catch (err) {
    throw err.response?.data || { message: "Failed to recover username" };
  }
};

// --- League Management ---
export const createLeague = async (name) => {
  try {
    const response = await API.post("/league/create", null, { params: { name } });
    return response.data;
  } catch (err) {
    throw err.response?.data || { message: "Failed to create league" };
  }
};

export const joinLeague = async (inviteCode, teamName, userId) => {
  try {
    const response = await API.post("/league/join", null, {
      params: { inviteCode, teamName, userId },
    });
    return response.data;
  } catch (err) {
    throw err.response?.data || { message: "Failed to join league" };
  }
};

export const getLeagueByInviteCode = async (inviteCode) => {
  try {
    const response = await API.get("/league", { params: { inviteCode } });
    return response.data;
  } catch (err) {
    throw err.response?.data || { message: "Failed to fetch league" };
  }
};

export const getLeagueById = async (leagueId) => {
  try {
    const response = await API.get(`/league/${leagueId}`);
    return response.data;
  } catch (err) {
    throw err.response?.data || { message: "Failed to fetch league" };
  }
};

export const getAllLeagues = async () => {
  try {
    const response = await API.get("/league/all");
    return response.data;
  } catch (err) {
    throw err.response?.data || { message: "Failed to fetch all leagues" };
  }
};

// --- Draft API ---
export const startDraft = async (leagueId) => {
  try {
    const response = await API.post("/draft/start", null, { params: { leagueId } });
    return response.data;
  } catch (err) {
    throw err.response?.data || { message: "Failed to start draft" };
  }
};

export const getAvailablePlayers = async (leagueId) => {
  try {
    const response = await API.get("/draft/available", { params: { leagueId } });
    return response.data;
  } catch (err) {
    throw err.response?.data || { message: "Failed to get available players" };
  }
};

export const draftPlayer = async (leagueId, teamId, playerId) => {
  try {
    const response = await API.post("/draft/pick", null, { params: { leagueId, teamId, playerId } });
    return response.data;
  } catch (err) {
    throw err.response?.data || { message: "Failed to draft player" };
  }
};

// --- DRAFT-SPECIFIC TEAM ROSTER ---
// Used **only during the draft** to get a team's roster in the context of the draft
export const getTeamRoster = async (leagueId, teamId) => {
  try {
    const response = await API.get("/draft/team", { params: { leagueId, teamId } });
    return response.data;
  } catch (err) {
    throw err.response?.data || { message: "Failed to get team roster" };
  }
};

export const getDraftStatus = async (leagueId) => {
  try {
    const response = await API.get("/draft/status", { params: { leagueId } });
    return response.data;
  } catch (err) {
    throw err.response?.data || { message: "Failed to get draft status" };
  }
};

// --- Player Management ---
export const assignPlayerRole = async (teamId, playerId, role) => {
  try {
    const response = await API.post(`/team-players/${teamId}/${playerId}/assign-captain`, null, {
      params: { role },
    });
    return response.data;
  } catch (err) {
    throw err.response?.data || { message: "Failed to assign player role" };
  }
};

// --- TEAM API ---
// Get all teams associated with a user
export const getUserTeams = async (ownerId) => {
  try {
    const response = await API.get("/team/my-teams", { params: { ownerId } });
    return response.data; // array of TeamDTO
  } catch (err) {
    throw err.response?.data || { message: "Failed to get user's teams" };
  }
};

// Get a single team by its ID (for ViewRoster and MyRosters pages)
export const getTeamById = async (teamId) => {
  try {
    const response = await API.get(`/team/${teamId}`);
    return response.data; // single TeamDTO
  } catch (err) {
    throw err.response?.data || { message: "Failed to get team by ID" };
  }
};
