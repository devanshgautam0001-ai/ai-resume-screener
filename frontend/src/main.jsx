import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter } from "react-router-dom";
import App from "./App";
import "./index.css";

// Clear any stale auth tokens on app load for development
const clearStaleTokens = () => {
  try {
    const stored = localStorage.getItem("resume-ai-auth");
    if (stored) {
      const parsed = JSON.parse(stored);
      // Clear if token is empty or invalid
      if (!parsed.token || parsed.token.length === 0) {
        localStorage.removeItem("resume-ai-auth");
      }
    }
  } catch (e) {
    // If parsing fails, clear the corrupted data
    localStorage.removeItem("resume-ai-auth");
  }
};

clearStaleTokens();

ReactDOM.createRoot(document.getElementById("root")).render(
  <React.StrictMode>
    <BrowserRouter>
      <App />
    </BrowserRouter>
  </React.StrictMode>
);
