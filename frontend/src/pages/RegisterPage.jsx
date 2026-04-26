import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../utils/api";
import { useAuth } from "../hooks/useAuth";

export default function RegisterPage() {
  const navigate = useNavigate();
  const { setAuth } = useAuth();
  const [form, setForm] = useState({
    name: "Recruiter User",
    email: "recruiter@resumeai.dev",
    password: "password123",
    role: "ROLE_RECRUITER"
  });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const onSubmit = async (event) => {
    event.preventDefault();
    setError("");
    setLoading(true);
    try {
      const response = await api.post("/auth/register", form);
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
      setError(err?.response?.data?.message || "Registration failed. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center px-4">
      <form onSubmit={onSubmit} className="glass-card w-full max-w-lg rounded-[2rem] p-8">
        <p className="text-sm uppercase tracking-[0.35em] text-cyan-300/70">Create account</p>
        <h1 className="mt-4 text-4xl font-bold">Start hiring with AI</h1>
        <div className="mt-8 grid gap-4 md:grid-cols-2">
          <input className="rounded-2xl border border-white/10 bg-white/5 px-4 py-3 outline-none" placeholder="Name" value={form.name} onChange={(event) => setForm({ ...form, name: event.target.value })} />
          <input className="rounded-2xl border border-white/10 bg-white/5 px-4 py-3 outline-none" placeholder="Email" value={form.email} onChange={(event) => setForm({ ...form, email: event.target.value })} />
          <input type="password" className="rounded-2xl border border-white/10 bg-white/5 px-4 py-3 outline-none" placeholder="Password" value={form.password} onChange={(event) => setForm({ ...form, password: event.target.value })} />
          <select className="rounded-2xl border border-white/10 bg-[#0c1226] px-4 py-3 outline-none" value={form.role} onChange={(event) => setForm({ ...form, role: event.target.value })}>
            <option value="ROLE_RECRUITER">Recruiter</option>
            <option value="ROLE_ADMIN">Admin</option>
            <option value="ROLE_CANDIDATE">Candidate</option>
          </select>
        </div>
        <button className="mt-6 w-full rounded-2xl bg-cyan-300 px-4 py-3 font-semibold text-slate-900" disabled={loading}>
          {loading ? "Creating account..." : "Sign up"}
        </button>
        {error && <p className="mt-4 text-sm text-rose-300">{error}</p>}
        <p className="mt-4 text-sm text-white/60">
          Already have an account? <Link to="/login" className="text-cyan-300">Login</Link>
        </p>
      </form>
    </div>
  );
}
