import axios from "axios";

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL
});

api.interceptors.request.use((config) => {
  const stored = JSON.parse(localStorage.getItem("resume-ai-auth") || "{}");
  // Only add Authorization header if token exists and is not empty
  if (stored.token && stored.token.length > 0) {
    config.headers.Authorization = `Bearer ${stored.token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    // If we get a 403 or 401, clear the invalid token
    if (error.response && (error.response.status === 403 || error.response.status === 401)) {
      const stored = JSON.parse(localStorage.getItem("resume-ai-auth") || "{}");
      if (stored.token) {
        console.log("Clearing invalid auth token due to", error.response.status);
        localStorage.removeItem("resume-ai-auth");
      }
    }
    return Promise.reject(error);
  }
);

export default api;
