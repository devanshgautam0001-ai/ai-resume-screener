export const sampleBreakdown = [
  { name: "Skills", value: 88 },
  { name: "Experience", value: 74 },
  { name: "Education", value: 81 }
];

export const sampleRanks = [
  { name: "Aarav", score: 92 },
  { name: "Ishita", score: 84 },
  { name: "Vihan", score: 78 }
];

export function formatRole(role = "") {
  return role.replace("ROLE_", "").toLowerCase();
}
