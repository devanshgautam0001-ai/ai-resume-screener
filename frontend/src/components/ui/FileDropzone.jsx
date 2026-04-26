import { UploadCloud } from "lucide-react";
import { useRef } from "react";

export default function FileDropzone({ onFileSelect, fileName }) {
  const inputRef = useRef(null);

  return (
    <div
      onClick={() => inputRef.current?.click()}
      onDragOver={(event) => event.preventDefault()}
      onDrop={(event) => {
        event.preventDefault();
        const files = Array.from(event.dataTransfer.files || []);
        if (files.length > 0) onFileSelect(files);
      }}
      className="glass-card flex cursor-pointer flex-col items-center justify-center rounded-[2rem] border border-dashed border-cyan-300/30 p-10 text-center"
    >
      <UploadCloud className="h-12 w-12 text-cyan-300" />
      <h3 className="mt-4 text-xl font-semibold">Drag and drop resumes</h3>
      <p className="mt-2 text-sm text-white/55">Supports PDF and DOCX. Select one or multiple files.</p>
      <p className="mt-4 rounded-full bg-white/10 px-4 py-2 text-xs text-white/80">
        {fileName || "No file selected yet"}
      </p>
      <input
        ref={inputRef}
        type="file"
        accept=".pdf,.doc,.docx"
        multiple
        className="hidden"
        onChange={(event) => onFileSelect(Array.from(event.target.files || []))}
      />
    </div>
  );
}
