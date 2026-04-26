import { Bar, BarChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts";

export default function ScoreBreakdownChart({ data }) {
  return (
    <div className="glass-card rounded-3xl p-6">
      <p className="text-sm text-white/60">Scoring breakdown</p>
      <div className="h-72">
        <ResponsiveContainer width="100%" height="100%">
          <BarChart data={data}>
            <CartesianGrid stroke="rgba(255,255,255,0.08)" vertical={false} />
            <XAxis dataKey="name" tick={{ fill: "rgba(255,255,255,0.7)" }} />
            <YAxis tick={{ fill: "rgba(255,255,255,0.7)" }} />
            <Tooltip />
            <Bar dataKey="value" radius={[12, 12, 0, 0]} fill="#4ade80" />
          </BarChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
}
