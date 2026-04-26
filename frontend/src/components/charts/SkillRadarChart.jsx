import { PolarAngleAxis, PolarGrid, Radar, RadarChart, ResponsiveContainer } from "recharts";

export default function SkillRadarChart({ data }) {
  return (
    <div className="glass-card rounded-3xl p-6">
      <p className="text-sm text-white/60">Skill match visualization</p>
      <div className="h-72">
        <ResponsiveContainer width="100%" height="100%">
          <RadarChart data={data}>
            <PolarGrid stroke="rgba(255,255,255,0.12)" />
            <PolarAngleAxis dataKey="skill" tick={{ fill: "rgba(255,255,255,0.7)", fontSize: 12 }} />
            <Radar dataKey="match" stroke="#6ee7ff" fill="#6ee7ff" fillOpacity={0.4} />
          </RadarChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
}
