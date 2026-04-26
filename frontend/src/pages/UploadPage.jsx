import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import FileDropzone from "../components/ui/FileDropzone";
import LoadingSpinner from "../components/ui/LoadingSpinner";
import SkillBadge from "../components/ui/SkillBadge";
import api from "../utils/api";

export default function UploadPage() {
  const navigate = useNavigate();
  const [jobs, setJobs] = useState([]);
  const [selectedJobId, setSelectedJobId] = useState(localStorage.getItem("latestJobId") || "");
  const [files, setFiles] = useState([]);
  const [uploading, setUploading] = useState(false);
  const [uploadedResumes, setUploadedResumes] = useState([]);
  const [error, setError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  const [loadingResumes, setLoadingResumes] = useState(false);

  useEffect(() => {
    api.get("/jobs")
      .then((response) => setJobs(response.data.data || []))
      .catch(() => setJobs([]));
  }, []);

  // Fetch existing resumes for the selected job
  useEffect(() => {
    if (selectedJobId) {
      fetchResumesForJob(selectedJobId);
    }
  }, [selectedJobId]);

  const fetchResumesForJob = async (jobId) => {
    setLoadingResumes(true);
    try {
      const response = await api.get(`/resumes/job/${jobId}`);
      setUploadedResumes(response.data.data || []);
    } catch (err) {
      console.error("Failed to fetch resumes:", err);
      setUploadedResumes([]);
    } finally {
      setLoadingResumes(false);
    }
  };

  const uploadFiles = async () => {
    if (!selectedJobId) {
      setError("Create and select a job description before uploading resumes.");
      return;
    }
    if (files.length === 0) {
      setError("Select at least one resume file.");
      return;
    }

    setUploading(true);
    setError("");
    setSuccessMessage("");

    try {
      const formData = new FormData();
      files.forEach((file) => formData.append("files", file));
      const response = await api.post(`/resumes/upload/${selectedJobId}`, formData, { timeout: 120000 });
      const resumes = response.data.data.resumes || [];
      
      // Refresh the resumes list from database to get latest data
      await fetchResumesForJob(selectedJobId);
      
      if (resumes[0]?.id) {
        localStorage.setItem("latestResumeId", String(resumes[0].id));
      }
      
      setSuccessMessage(`${resumes.length} resume(s) uploaded and parsed successfully`);
      setFiles([]); // Clear file selection after successful upload
    } catch (err) {
      setError(err?.response?.data?.message || err.message || "Upload failed");
    } finally {
      setUploading(false);
    }
  };

  const selectedJob = jobs.find((job) => String(job.id) === String(selectedJobId));

  return (
    <div className="space-y-6">
      <div className="glass-card rounded-[2rem] p-6">
        <p className="text-sm text-white/60">Step 2 of 3</p>
        <h2 className="mt-3 text-2xl font-bold">Upload resumes for a saved job description</h2>
        <div className="mt-6 grid gap-4 md:grid-cols-[1fr_auto]">
          <select
            className="rounded-2xl border border-white/10 bg-[#11182b] px-4 py-3 outline-none"
            value={selectedJobId}
            onChange={(event) => {
              setSelectedJobId(event.target.value);
              localStorage.setItem("latestJobId", event.target.value);
            }}
          >
            <option value="">Select a job description</option>
            {jobs.map((job) => (
              <option key={job.id} value={job.id}>
                {job.title} - {job.company}
              </option>
            ))}
          </select>
          <Link to="/jobs" className="rounded-2xl border border-cyan-300/30 px-5 py-3 text-center text-cyan-200">
            Create New Job
          </Link>
        </div>
        {selectedJob && (
          <p className="mt-4 text-sm text-white/65">
            Uploading into: <span className="text-cyan-200">{selectedJob.title}</span> at{" "}
            <span className="text-cyan-200">{selectedJob.company}</span>
          </p>
        )}
      </div>

      <FileDropzone onFileSelect={setFiles} fileName={files.length > 0 ? `${files.length} file(s) selected` : ""} />

      <div className="flex flex-wrap items-center gap-4">
        <button onClick={uploadFiles} className="rounded-2xl bg-cyan-300 px-5 py-3 font-semibold text-slate-900">
          Upload and Parse Resumes
        </button>
        {uploading && <LoadingSpinner label="Uploading resumes and extracting structured data..." />}
      </div>

      {error && (
        <div className="rounded-2xl border border-rose-400/20 bg-rose-400/10 px-4 py-3 text-sm text-rose-200">
          {error}
        </div>
      )}

      {successMessage && (
        <div className="rounded-2xl border border-emerald-400/20 bg-emerald-400/10 px-4 py-3 text-sm text-emerald-200">
          {successMessage}
        </div>
      )}

      {uploadedResumes.length > 0 && (
        <>
          <div className="glass-card rounded-[2rem] p-6">
            <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
              <div>
                <h2 className="text-2xl font-bold">Parsed resumes ready for screening</h2>
                <p className="text-white/60">{uploadedResumes.length} candidate profile(s) stored against the selected job.</p>
              </div>
              <span className="rounded-full bg-emerald-400/15 px-4 py-2 text-sm text-emerald-200">
                {loadingResumes ? "Loading..." : "Upload complete"}
              </span>
            </div>
          </div>

          {uploadedResumes.map((resume) => (
            <div key={resume.id} className="glass-card rounded-[2rem] p-6">
              <div className="flex flex-col gap-4 md:flex-row md:items-start md:justify-between">
                <div className="flex-1">
                  <div className="flex flex-col gap-2 md:flex-row md:items-center md:justify-between">
                    <div>
                      <h2 className="text-2xl font-bold">{resume.candidateName}</h2>
                      <p className="text-white/60">{resume.email}</p>
                      {resume.phone && <p className="text-sm text-white/50">{resume.phone}</p>}
                    </div>
                    <span className="rounded-full bg-cyan-400/15 px-4 py-2 text-sm text-cyan-200">
                      {resume.status}
                    </span>
                  </div>
                  
                  <div className="mt-4">
                    <p className="text-sm text-white/60">File: <span className="text-white/80">{resume.originalFileName}</span></p>
                  </div>

                  <div className="mt-6">
                    <p className="text-sm text-white/60">Skills</p>
                    <div className="mt-3 flex flex-wrap gap-2">
                      {resume.skills && resume.skills.length > 0 ? (
                        resume.skills.map((skill) => (
                          <SkillBadge key={`${resume.id}-${skill}`} label={skill} />
                        ))
                      ) : (
                        <p className="text-sm text-white/40">No skills extracted</p>
                      )}
                    </div>
                  </div>

                  {resume.experiences && resume.experiences.length > 0 && (
                    <div className="mt-6">
                      <p className="text-sm text-white/60">Experience</p>
                      <div className="mt-3 space-y-2">
                        {resume.experiences.map((exp, idx) => (
                          <div key={`${resume.id}-exp-${idx}`} className="rounded-xl border border-white/10 bg-white/5 px-4 py-3 text-sm text-white/75">
                            {exp}
                          </div>
                        ))}
                      </div>
                    </div>
                  )}

                  {resume.education && resume.education.length > 0 && (
                    <div className="mt-6">
                      <p className="text-sm text-white/60">Education</p>
                      <div className="mt-3 space-y-2">
                        {resume.education.map((edu, idx) => (
                          <div key={`${resume.id}-edu-${idx}`} className="rounded-xl border border-white/10 bg-white/5 px-4 py-3 text-sm text-white/75">
                            {edu}
                          </div>
                        ))}
                      </div>
                    </div>
                  )}

                  {resume.projects && resume.projects.length > 0 && (
                    <div className="mt-6">
                      <p className="text-sm text-white/60">Projects</p>
                      <div className="mt-3 space-y-2">
                        {resume.projects.map((proj, idx) => (
                          <div key={`${resume.id}-proj-${idx}`} className="rounded-xl border border-white/10 bg-white/5 px-4 py-3 text-sm text-white/75">
                            {proj}
                          </div>
                        ))}
                      </div>
                    </div>
                  )}
                </div>
              </div>
            </div>
          ))}

          <div className="glass-card rounded-[2rem] p-6">
            <p className="text-sm text-white/60">Next step</p>
            <h3 className="mt-3 text-2xl font-bold">Run AI screening and view candidate rankings</h3>
            <p className="mt-3 max-w-3xl text-white/65">
              Your resumes are now linked to a real job record. Continue to the results workspace to generate match scores,
              ATS scores, missing skills, suggestions, and leaderboard rankings.
            </p>
            <div className="mt-6 flex flex-wrap gap-4">
              <Link to="/results" className="rounded-2xl bg-cyan-300 px-5 py-3 font-semibold text-slate-900">
                Open Results Workspace
              </Link>
              <button 
                onClick={() => {
                  localStorage.setItem("latestJobId", selectedJobId);
                  navigate("/results");
                }}
                className="rounded-2xl border border-cyan-300/30 px-5 py-3 text-cyan-200"
              >
                Continue to Screening
              </button>
            </div>
          </div>
        </>
      )}
    </div>
  );
}
