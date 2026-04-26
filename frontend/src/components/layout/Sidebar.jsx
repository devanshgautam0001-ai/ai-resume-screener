import { BarChart3, BriefcaseBusiness, FileUp, LayoutDashboard, LogOut, Shield } from "lucide-react";
import { NavLink } from "react-router-dom";
import { useAuth } from "../../hooks/useAuth";

const items = [
  { to: "/", label: "Dashboard", icon: LayoutDashboard },
  { to: "/jobs", label: "1. Create Job", icon: BriefcaseBusiness },
  { to: "/upload", label: "2. Upload Resumes", icon: FileUp },
  { to: "/results", label: "3. Screening Results", icon: BarChart3 },
  { to: "/admin", label: "Admin", icon: Shield }
];

export default function Sidebar() {
  const { logout } = useAuth();

  return (
    <aside className="glass-card flex h-full flex-col rounded-3xl p-5">
      <div className="mb-8">
        <p className="text-xs uppercase tracking-[0.35em] text-cyan-300/70">ResumeAI</p>
        <h2 className="mt-3 text-2xl font-bold">Screen smarter</h2>
      </div>

      <nav className="flex flex-1 flex-col gap-2">
        {items.map(({ to, label, icon: Icon }) => (
          <NavLink
            key={to}
            to={to}
            className={({ isActive }) =>
              `flex items-center gap-3 rounded-2xl px-4 py-3 text-sm transition ${
                isActive ? "bg-cyan-400/15 text-cyan-200" : "text-white/70 hover:bg-white/5"
              }`
            }
          >
            <Icon className="h-4 w-4" />
            {label}
          </NavLink>
        ))}
      </nav>

      <button
        onClick={logout}
        className="mt-8 flex items-center gap-3 rounded-2xl border border-white/10 px-4 py-3 text-sm text-white/70 hover:bg-white/5"
      >
        <LogOut className="h-4 w-4" />
        Logout
      </button>
    </aside>
  );
}
