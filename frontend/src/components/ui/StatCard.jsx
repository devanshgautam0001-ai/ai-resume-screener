import { motion } from "framer-motion";

export default function StatCard({ title, value, caption }) {
  return (
    <motion.div
      initial={{ opacity: 0, y: 18 }}
      animate={{ opacity: 1, y: 0 }}
      className="glass-card rounded-3xl p-5"
    >
      <p className="text-sm text-white/60">{title}</p>
      <h3 className="mt-3 text-3xl font-bold">{value}</h3>
      <p className="mt-2 text-sm text-white/50">{caption}</p>
    </motion.div>
  );
}
