import { create } from "zustand";

const stored = JSON.parse(localStorage.getItem("resume-ai-auth") || "{}");

const useAuthStore = create((set) => ({
  token: stored.token || "",
  user: stored.user || null,
  setAuth: (payload) => {
    localStorage.setItem("resume-ai-auth", JSON.stringify(payload));
    set(payload);
  },
  logout: () => {
    localStorage.removeItem("resume-ai-auth");
    set({ token: "", user: null });
  }
}));

export default useAuthStore;
