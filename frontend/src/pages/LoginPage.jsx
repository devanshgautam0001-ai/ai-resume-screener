import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../utils/api";
import { useAuth } from "../hooks/useAuth";
import { API_BASE_URL } from "../config/api";

export default function LoginPage() {
  const navigate = useNavigate();
  const { setAuth } = useAuth();
  const [form, setForm] = useState({ email: "admin@resumeai.dev", password: "password123" });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const onSubmit = async (event) => {
    event.preventDefault();
    setError("");
    setLoading(true);
    try {
      console.log("Attempting login with:", form.email);
      const response = await api.post("/auth/login", form);
      console.log("Login response:", response.data);
      
      const payload = {
        token: response.data.data.token,
        user: {
          id: response.data.data.userId,
          name: response.data.data.name,
          email: response.data.data.email,
          role: response.data.data.role
        }
      };
      
      console.log("Setting auth payload:", payload);
      setAuth(payload);
      console.log("Navigating to dashboard...");
      navigate("/");
    } catch (err) {
      console.error("Login error:", err);
      console.error("Error response:", err?.response);
      
      let errorMessage = "Login failed. Please try again.";
      
      if (err.networkError) {
        errorMessage = `Network error: Unable to connect to server (${API_BASE_URL}). Please ensure the backend is running.`;
      } else if (err.code === "ECONNREFUSED") {
        errorMessage = `Connection refused: Backend server is not running at ${API_BASE_URL}. Please start the backend server.`;
      } else if (err?.response?.data?.message) {
        errorMessage = err.response.data.message;
      } else if (err?.message) {
        errorMessage = err.message;
      }
      
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center px-4">
      <form onSubmit={onSubmit} className="glass-card w-full max-w-md rounded-[2rem] p-8">
        <p className="text-sm uppercase tracking-[0.35em] text-cyan-300/70">ResumeAI</p>
        <h1 className="mt-4 text-4xl font-bold">Welcome back</h1>
        <p className="mt-2 text-white/60">Login to continue screening candidates with AI precision.</p>
        <div className="mt-8 space-y-4">
          <input
            className="w-full rounded-2xl border border-white/10 bg-white/5 px-4 py-3 outline-none"
            placeholder="Email"
            value={form.email}
            onChange={(event) => setForm({ ...form, email: event.target.value })}
          />
          <input
            type="password"
            className="w-full rounded-2xl border border-white/10 bg-white/5 px-4 py-3 outline-none"
            placeholder="Password"
            value={form.password}
            onChange={(event) => setForm({ ...form, password: event.target.value })}
          />
        </div>
        <button className="mt-6 w-full rounded-2xl bg-cyan-300 px-4 py-3 font-semibold text-slate-900" disabled={loading}>
          {loading ? "Logging in..." : "Login"}
        </button>
        {error && <p className="mt-4 text-sm text-rose-300">{error}</p>}
        <p className="mt-4 text-sm text-white/60">
          New here? <Link to="/register" className="text-cyan-300">Create an account</Link>
        </p>
      </form>
    </div>
  );
}
