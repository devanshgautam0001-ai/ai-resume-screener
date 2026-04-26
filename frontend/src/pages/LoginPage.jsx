import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../utils/api";
import { useAuth } from "../hooks/useAuth";

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
      const response = await api.post("/auth/login", form);
      const payload = {
        token: response.data.data.token,
        user: {
          id: response.data.data.userId,
          name: response.data.data.name,
          email: response.data.data.email,
          role: response.data.data.role
        }
      };
      setAuth(payload);
      navigate("/");
    } catch (err) {
      setError(err?.response?.data?.message || "Login failed. Please try again.");
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
