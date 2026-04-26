import { Link } from "react-router-dom";
import { useApi } from "../hooks/useApi";
import api from "../utils/api";
import StatCard from "../components/ui/StatCard";
import ScoreBreakdownChart from "../components/charts/ScoreBreakdownChart";
import CandidateRankChart from "../components/charts/CandidateRankChart";

export default function DashboardPage() {
  const latestJobId = localStorage.getItem("latestJobId");
  const { data: jobs = [], loading: jobsLoading, error: jobsError } = useApi(() => api.get("/jobs"), []);
  const { data: resumes = [], loading: resumesLoading, error: resumesError } = useApi(() => api.get("/resumes"), []);
  const { data: rankings = [], loading: rankingsLoading, error: rankingsError } = useApi(
    () => (latestJobId ? api.get(`/rankings?jobId=${latestJobId}`) : Promise.resolve({ data: { data: [] } })),
    [latestJobId]
  );

  // Use fallback data if API calls fail
  const displayJobs = jobsError ? [] : jobs;
  const displayResumes = resumesError ? [] : resumes;
  const displayRankings = rankingsError ? [] : rankings;

  if (jobsLoading || resumesLoading || rankingsLoading) {
    return <div className="p-8 text-white">Loading dashboard...</div>;
  }

  const averageScore =
    displayRankings.length > 0 ? Math.round(displayRankings.reduce((sum, item) => sum + item.overallScore, 0) / displayRankings.length) : 0;
  const averageAts =
    displayRankings.length > 0 ? Math.round(displayRankings.reduce((sum, item) => sum + item.atsScore, 0) / displayRankings.length) : 0;

  const scoreBreakdown = [
    { name: "Jobs", value: Math.min(100, displayJobs.length * 20) },
    { name: "Resumes", value: Math.min(100, displayResumes.length * 12) },
    { name: "Avg Match", value: averageScore || 0 }
  ];

  const candidateChart = displayRankings.slice(0, 5).map((item) => ({
    name: item.candidateName,
    score: item.overallScore
  }));

  return (
    <div className="space-y-6">
      <section className="grid gap-4 md:grid-cols-3">
        <StatCard title="Jobs created" value={displayJobs.length} caption="Production job descriptions saved in PostgreSQL" />
        <StatCard title="Resumes uploaded" value={displayResumes.length} caption="Candidates linked to live job records" />
        <StatCard title="Average ATS score" value={`${averageAts}%`} caption="Real screening quality across analyzed candidates" />
      </section>

      <section className="glass-card rounded-[2rem] p-6">
        <p className="text-sm text-white/60">Workflow</p>
        <h2 className="mt-3 text-2xl font-bold">Follow the production screening sequence</h2>
        <div className="mt-6 grid gap-4 md:grid-cols-3">
          <Link to="/jobs" className="rounded-3xl border border-cyan-300/20 bg-cyan-300/10 p-5">
            <p className="text-sm text-cyan-200">Step 1</p>
            <h3 className="mt-2 text-xl font-semibold">Create Job Description</h3>
            <p className="mt-2 text-sm text-white/65">Create and save the role first so every upload is linked to a real job ID.</p>
          </Link>
          <Link to="/upload" className="rounded-3xl border border-white/10 bg-white/5 p-5">
            <p className="text-sm text-cyan-200">Step 2</p>
            <h3 className="mt-2 text-xl font-semibold">Upload Candidate Resumes</h3>
            <p className="mt-2 text-sm text-white/65">Upload one or many resumes for the selected job and parse them with Apache Tika.</p>
          </Link>
          <Link to="/results" className="rounded-3xl border border-white/10 bg-white/5 p-5">
            <p className="text-sm text-cyan-200">Step 3</p>
            <h3 className="mt-2 text-xl font-semibold">Run AI Screening</h3>
            <p className="mt-2 text-sm text-white/65">Generate match scores, ATS scores, missing skills, suggestions, and rankings.</p>
          </Link>
        </div>
      </section>

      <section className="grid gap-6 xl:grid-cols-2">
        <ScoreBreakdownChart data={scoreBreakdown} />
        <CandidateRankChart data={candidateChart.length > 0 ? candidateChart : [{ name: "No analyses yet", score: 0 }]} />
      </section>
    </div>
  );
}
