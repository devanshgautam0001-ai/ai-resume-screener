import { useState } from "react";
import { Link } from "react-router-dom";
import api from "../utils/api";
import SkillBadge from "../components/ui/SkillBadge";

export default function JobsPage() {
  const [form, setForm] = useState({
    title: "",
    company: "",
    location: "",
    employmentType: "Full-time",
    minExperienceYears: 1,
    description: "",
    requiredSkills: ""
  });
  const [job, setJob] = useState(null);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  const submit = async (event) => {
    event.preventDefault();
    setSaving(true);
    setError("");
    try {
      const response = await api.post("/jobs/create", {
        ...form,
        minExperienceYears: Number(form.minExperienceYears),
        requiredSkills: form.requiredSkills.split(",").map((item) => item.trim()).filter(Boolean)
      });
      setJob(response.data.data);
      localStorage.setItem("latestJobId", String(response.data.data.id));
      localStorage.setItem("latestJobTitle", response.data.data.title);
    } catch (err) {
      setError(err?.response?.data?.message || err.message || "Failed to create job");
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="grid gap-6 xl:grid-cols-[1.2fr_0.8fr]">
      <form onSubmit={submit} className="glass-card rounded-[2rem] p-6">
        <h2 className="text-2xl font-bold">Create Job Description</h2>
        <p className="mt-2 text-white/60">Start the workflow by creating a job record. Resume uploads and AI analysis will link to this job ID.</p>
        <div className="mt-6 grid gap-4">
          <input className="rounded-2xl border border-white/10 bg-white/5 px-4 py-3 outline-none" placeholder="Role title" value={form.title} onChange={(event) => setForm({ ...form, title: event.target.value })} />
          <input className="rounded-2xl border border-white/10 bg-white/5 px-4 py-3 outline-none" placeholder="Company name" value={form.company} onChange={(event) => setForm({ ...form, company: event.target.value })} />
          <div className="grid gap-4 md:grid-cols-3">
            <input className="rounded-2xl border border-white/10 bg-white/5 px-4 py-3 outline-none" placeholder="Location" value={form.location} onChange={(event) => setForm({ ...form, location: event.target.value })} />
            <input className="rounded-2xl border border-white/10 bg-white/5 px-4 py-3 outline-none" placeholder="Employment type" value={form.employmentType} onChange={(event) => setForm({ ...form, employmentType: event.target.value })} />
            <input type="number" min="0" className="rounded-2xl border border-white/10 bg-white/5 px-4 py-3 outline-none" placeholder="Minimum years" value={form.minExperienceYears} onChange={(event) => setForm({ ...form, minExperienceYears: event.target.value })} />
          </div>
          <textarea rows="8" className="rounded-3xl border border-white/10 bg-white/5 px-4 py-3 outline-none" placeholder="Paste the full job description" value={form.description} onChange={(event) => setForm({ ...form, description: event.target.value })} />
          <input className="rounded-2xl border border-white/10 bg-white/5 px-4 py-3 outline-none" placeholder="Required skills separated by commas" value={form.requiredSkills} onChange={(event) => setForm({ ...form, requiredSkills: event.target.value })} />
        </div>
        {error && <p className="mt-4 text-sm text-rose-300">{error}</p>}
        <button className="mt-6 rounded-2xl bg-cyan-300 px-5 py-3 font-semibold text-slate-900">
          {saving ? "Saving..." : "Save Job Description"}
        </button>
      </form>

      <div className="glass-card rounded-[2rem] p-6">
        <p className="text-sm text-white/60">Required skills preview</p>
        <h3 className="mt-3 text-xl font-bold">Role requirements</h3>
        <div className="mt-6 flex flex-wrap gap-2">
          {form.requiredSkills.split(",").filter(Boolean).map((skill) => (
            <SkillBadge key={skill} label={skill.trim()} />
          ))}
        </div>
        {job && (
          <>
            <p className="mt-6 text-sm text-emerald-300">Job #{job.id} saved successfully for {job.company}.</p>
            <Link to="/upload" className="mt-4 inline-flex rounded-2xl border border-cyan-300/30 px-5 py-3 text-cyan-200">
              Continue to Resume Upload
            </Link>
          </>
        )}
      </div>
    </div>
  );
}
