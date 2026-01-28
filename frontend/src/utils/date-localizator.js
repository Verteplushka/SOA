export const formatDate = (date) => {
  if (!date) {
    return "";
  }
  const [datePart] = date.split("T");
  const [year, month, day] = datePart.split("-");
  return `${day.padStart(2, "0")}-${month.padStart(2, "0")}-${year}`;
};
