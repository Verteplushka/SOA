export const formatDate = (d) => {
  if (!d) return "";
  if (Array.isArray(d)) {
    const [year, month, day] = d;
    return `${String(day).padStart(2, "0")}-${String(month).padStart(
      2,
      "0"
    )}-${year}`;
  }
  if (typeof d === "string") return d.slice(0, 10);
  return "";
};
