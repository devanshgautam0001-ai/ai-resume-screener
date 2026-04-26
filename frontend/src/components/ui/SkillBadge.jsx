export default function SkillBadge({ label, accent = "cyan" }) {
  const theme = accent === "rose" ? "bg-rose-400/15 text-rose-200" : "bg-cyan-400/15 text-cyan-200";
  return <span className={`rounded-full px-3 py-1 text-xs font-medium ${theme}`}>{label}</span>;
}
