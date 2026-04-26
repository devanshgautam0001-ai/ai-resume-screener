import { useApi } from "../hooks/useApi";
import api from "../utils/api";
import LoadingSpinner from "../components/ui/LoadingSpinner";

export default function AdminPage() {
  const { data, loading, error } = useApi(() => api.get("/admin/overview"), []);

  return (
    <div className="space-y-6">
      <div className="glass-card rounded-[2rem] p-6">
        <p className="text-sm text-white/60">Admin dashboard</p>
        <h2 className="mt-3 text-3xl font-bold">System-wide hiring metrics</h2>
        {loading ? (
          <div className="mt-6">
            <LoadingSpinner label="Loading admin insights..." />
          </div>
        ) : error ? (
          <div className="mt-6 text-white/70">Error loading admin data. Please try again.</div>
        ) : (
          <div className="mt-8 grid gap-4 md:grid-cols-2 lg:grid-cols-4">
            <div className="rounded-3xl bg-white/5 p-5">
              <p className="text-sm text-white/60">Total Candidates</p>
              <h3 className="mt-3 text-4xl font-bold">{data?.totalCandidates ?? 0}</h3>
            </div>
            <div className="rounded-3xl bg-white/5 p-5">
              <p className="text-sm text-white/60">Active Jobs</p>
              <h3 className="mt-3 text-4xl font-bold">{data?.activeJobs ?? 0}</h3>
            </div>
            <div className="rounded-3xl bg-white/5 p-5">
              <p className="text-sm text-white/60">Avg Match Score</p>
              <h3 className="mt-3 text-4xl font-bold">{data?.averageMatchScore ?? 0}%</h3>
            </div>
            <div className="rounded-3xl bg-white/5 p-5">
              <p className="text-sm text-white/60">Shortlisted</p>
              <h3 className="mt-3 text-4xl font-bold">{data?.shortlistedCount ?? 0}</h3>
            </div>
          </div>
        )}
      </div>

      {!loading && !error && data && (
        <>
          <div className="glass-card rounded-[2rem] p-6">
            <p className="text-sm text-white/60">Additional metrics</p>
            <h3 className="mt-3 text-2xl font-bold">Platform statistics</h3>
            <div className="mt-6 grid gap-4 md:grid-cols-3">
              <div className="rounded-2xl bg-white/5 p-4">
                <p className="text-sm text-white/60">Total Users</p>
                <h4 className="mt-2 text-2xl font-bold">{data?.totalUsers ?? 0}</h4>
              </div>
              <div className="rounded-2xl bg-white/5 p-4">
                <p className="text-sm text-white/60">Total Jobs</p>
                <h4 className="mt-2 text-2xl font-bold">{data?.totalJobs ?? 0}</h4>
              </div>
              <div className="rounded-2xl bg-white/5 p-4">
                <p className="text-sm text-white/60">Total Analyses</p>
                <h4 className="mt-2 text-2xl font-bold">{data?.totalAnalyses ?? 0}</h4>
              </div>
            </div>
          </div>

          <div className="glass-card rounded-[2rem] p-6">
            <p className="text-sm text-white/60">Top performers</p>
            <h3 className="mt-3 text-2xl font-bold">Highest ranked candidates</h3>
            <div className="mt-6 space-y-3">
              {data?.topCandidates?.map((candidate, index) => (
                <div key={index} className="flex items-center justify-between rounded-2xl bg-white/5 p-4">
                  <div>
                    <p className="font-semibold">{candidate.candidateName}</p>
                    <p className="text-sm text-white/60">{candidate.jobTitle}</p>
                  </div>
                  <div className="text-right">
                    <p className="text-2xl font-bold">{candidate.score}%</p>
                    <p className="text-xs text-white/60">match score</p>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </>
      )}
    </div>
  );
}
