import React from "react";
import "../styles/pages.css";

const Footer = () => {
  return (
    <footer className="app-footer">
      <p>&copy; {new Date().getFullYear()} Trevor's Fantasy Hockey Draft. All rights reserved.</p>
    </footer>
  );
};

export default Footer;
