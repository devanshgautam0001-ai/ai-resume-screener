/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{js,jsx}"],
  theme: {
    extend: {
      colors: {
        midnight: "#060816",
        panel: "#0c1226",
        glass: "rgba(255,255,255,0.08)",
        neon: "#6ee7ff",
        mint: "#4ade80",
        coral: "#fb7185"
      },
      boxShadow: {
        glow: "0 0 40px rgba(110, 231, 255, 0.18)"
      },
      backgroundImage: {
        mesh: "radial-gradient(circle at top left, rgba(110,231,255,0.16), transparent 30%), radial-gradient(circle at top right, rgba(251,113,133,0.18), transparent 25%), radial-gradient(circle at bottom, rgba(74,222,128,0.16), transparent 30%)"
      }
    }
  },
  plugins: []
};
