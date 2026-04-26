import axios from "axios";
import { API_BASE_URL } from "../config/api";

const api = axios.create({
  baseURL: API_BASE_URL
});

api.interceptors.request.use((config) => {
  console.log("[API] Request:", config.method?.toUpperCase(), config.url);
  const stored = JSON.parse(localStorage.getItem("resume-ai-auth") || "{}");
  // Only add Authorization header if token exists and is not empty
  if (stored.token && stored.token.length > 0) {
    config.headers.Authorization = `Bearer ${stored.token}`;
    console.log("[API] Authorization header added");
  }
  return config;
});

api.interceptors.response.use(
  (response) => {
    console.log("[API] Response:", response.status, response.config.url);
    return response;
  },
  (error) => {
    console.error("[API] Error:", error);
    console.error("[API] Error config:", error.config);
    console.error("[API] Error response:", error.response);
    
    // If we get a 403 or 401, clear the invalid token
    if (error.response && (error.response.status === 403 || error.response.status === 401)) {
      const stored = JSON.parse(localStorage.getItem("resume-ai-auth") || "{}");
      if (stored.token) {
        console.log("[API] Clearing invalid auth token due to", error.response.status);
        localStorage.removeItem("resume-ai-auth");
      }
    }
    
    // Handle network errors
    if (!error.response) {
      console.error("[API] Network error - backend may be down or CORS issue:", error.message);
      error.networkError = true;
    }
    
    return Promise.reject(error);
  }
);

export default api;
