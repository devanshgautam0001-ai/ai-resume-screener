import { Bell, Search } from "lucide-react";
import { useAuth } from "../../hooks/useAuth";

export default function Navbar() {
  const { user } = useAuth();

  return (
    <header className="glass-card rounded-3xl px-6 py-4">
      <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
        <div>
          <p className="text-sm text-white/60">AI Resume Screening System</p>
          <h1 className="text-2xl font-bold">Recruitment intelligence, in real time</h1>
        </div>
        <div className="flex items-center gap-4">
          <div className="hidden items-center gap-2 rounded-2xl border border-white/10 bg-white/5 px-4 py-2 md:flex">
            <Search className="h-4 w-4 text-white/50" />
            <input
              className="bg-transparent text-sm text-white outline-none placeholder:text-white/40"
              placeholder="Search candidates or jobs"
            />
          </div>
          <button className="rounded-2xl border border-white/10 bg-white/5 p-3 text-white/70">
            <Bell className="h-4 w-4" />
          </button>
          <div className="rounded-2xl border border-cyan-400/20 bg-cyan-400/10 px-4 py-2">
            <p className="text-xs text-white/60">{user?.role || "recruiter"}</p>
            <p className="text-sm font-semibold">{user?.name || "Team Recruiter"}</p>
          </div>
        </div>
      </div>
    </header>
  );
}
