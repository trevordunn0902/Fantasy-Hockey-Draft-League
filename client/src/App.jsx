// src/App.jsx
import React from "react";
import { Routes, Route } from "react-router-dom";
import Header from "./components/Header.jsx";
import Footer from "./components/Footer.jsx";
import Home from "./pages/Home.jsx";
import League from "./pages/League.jsx";
import Draft from "./pages/Draft.jsx";
import Login from "./pages/Login.jsx";
import Register from "./pages/Register.jsx";
import NotFound from "./pages/NotFound.jsx";
import CreateLeague from "./pages/CreateLeague";
import JoinLeague from "./pages/JoinLeague";
import ViewRoster from "./pages/ViewRoster";
import MyRosters from "./pages/MyRosters";

function App() {
  return (
    <div className="app-container">
      <Header />
      <main className="main-content">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/league/:inviteCode" element={<League />} />
          <Route path="/draft/:leagueId" element={<Draft />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/league/:leagueId/team/:teamId" element={<ViewRoster />} />
          <Route path="/my-rosters" element={<MyRosters />} />
          <Route path="*" element={<NotFound />} />
          <Route path="/create-league" element={<CreateLeague />} />
          <Route path="/join-league" element={<JoinLeague />} />
        </Routes>
      </main>
      <Footer />
    </div>
  );
}

export default App;
