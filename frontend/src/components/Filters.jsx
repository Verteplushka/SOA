import { useState } from "react";

export default function Filters({ filters, setFilters }) {
  const [name, setName] = useState("");

  const applyFilter = () => {
    setFilters({ ...filters, name });
  };

  return (
    <div style={{ marginBottom: "1rem" }}>
      <input
        placeholder="Фильтр по имени"
        value={name}
        onChange={(e) => setName(e.target.value)}
      />
      <button onClick={applyFilter}>Применить</button>
    </div>
  );
}
