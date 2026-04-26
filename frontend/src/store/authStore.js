import { create } from "zustand";

const stored = JSON.parse(localStorage.getItem("resume-ai-auth") || "{}");

const useAuthStore = create((set) => ({
  token: stored.token || "",
  user: stored.user || null,
  setAuth: (payload) => {
    console.log("[AUTH STORE] setAuth called with:", payload);
    try {
      localStorage.setItem("resume-ai-auth", JSON.stringify(payload));
      console.log("[AUTH STORE] Saved to localStorage successfully");
      set(payload);
      console.log("[AUTH STORE] State updated successfully");
    } catch (error) {
      console.error("[AUTH STORE] Error saving auth:", error);
      throw error;
    }
  },
  logout: () => {
    console.log("[AUTH STORE] logout called");
    localStorage.removeItem("resume-ai-auth");
    set({ token: "", user: null });
    console.log("[AUTH STORE] Logged out successfully");
  }
}));

export default useAuthStore;
