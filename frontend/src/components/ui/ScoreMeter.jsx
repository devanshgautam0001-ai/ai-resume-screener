import { motion } from "framer-motion";

export default function ScoreMeter({ score }) {
  const displayScore = score ?? 0;
  const angle = (displayScore / 100) * 360;
  return (
    <div className="glass-card rounded-[2rem] p-6">
      <p className="text-sm text-white/60">Match Score</p>
      <div className="mt-6 flex items-center justify-center">
        <div
          className="relative flex h-44 w-44 items-center justify-center rounded-full"
          style={{
            background: `conic-gradient(#6ee7ff ${angle}deg, rgba(255,255,255,0.08) ${angle}deg)`
          }}
        >
          <div className="absolute h-32 w-32 rounded-full bg-[#08101f] shadow-inner" />
          <motion.div
            initial={{ scale: 0.8, opacity: 0 }}
            animate={{ scale: 1, opacity: 1 }}
            className="relative text-center"
          >
            <div className="text-4xl font-bold">{displayScore}</div>
            <div className="text-sm text-white/50">/ 100</div>
          </motion.div>
        </div>
      </div>
    </div>
  );
}
