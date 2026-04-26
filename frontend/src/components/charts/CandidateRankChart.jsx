import { Cell, ResponsiveContainer, Tooltip, XAxis, YAxis, BarChart, Bar } from "recharts";

const colors = ["#6ee7ff", "#4ade80", "#fb7185"];

// Custom tick formatter to truncate long names
const formatName = (name) => {
  if (!name) return "";
  return name.length > 15 ? `${name.substring(0, 15)}...` : name;
};

export default function CandidateRankChart({ data }) {
  const isSingleCandidate = data.length === 1;

  return (
    <div className="glass-card rounded-3xl p-6">
      <p className="text-sm text-white/60">Automatic candidate ranking</p>
      <div className="h-80">
        <ResponsiveContainer width="100%" height="100%">
          <BarChart 
            data={data} 
            layout="vertical"
            margin={{ top: 20, right: 30, left: 120, bottom: 20 }}
            barCategoryGap={20}
          >
            <XAxis 
              type="number" 
              tick={{ fill: "rgba(255,255,255,0.7)", fontSize: 12 }}
              axisLine={{ stroke: "rgba(255,255,255,0.1)" }}
              tickLine={{ stroke: "rgba(255,255,255,0.1)" }}
            />
            <YAxis 
              type="category" 
              dataKey="name" 
              tick={{ 
                fill: "rgba(255,255,255,0.8)", 
                fontSize: 13,
                fontWeight: 500
              }}
              tickFormatter={formatName}
              axisLine={{ stroke: "rgba(255,255,255,0.1)" }}
              tickLine={{ stroke: "rgba(255,255,255,0.1)" }}
              width={100}
            />
            <Tooltip 
              contentStyle={{ 
                backgroundColor: "rgba(17, 24, 43, 0.9)", 
                border: "1px solid rgba(255,255,255,0.1)",
                borderRadius: "8px",
                color: "white"
              }}
              formatter={(value) => [`${value}%`, "Score"]}
              labelFormatter={(name) => name}
            />
            <Bar 
              dataKey="score" 
              radius={[0, 8, 8, 0]}
              barSize={isSingleCandidate ? 40 : 32}
            >
              {data.map((entry, index) => (
                <Cell key={entry.name} fill={colors[index % colors.length]} />
              ))}
            </Bar>
          </BarChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
}
