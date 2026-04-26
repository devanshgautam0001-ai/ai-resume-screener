import useAuthStore from "../store/authStore";

export function useAuth() {
  const { token, user, setAuth, logout } = useAuthStore();
  console.log("[USE AUTH] Hook called - Token:", token ? 'exists' : 'missing', "User:", user ? 'exists' : 'missing');
  return { token, user, setAuth, logout };
}
