import React, { useEffect, useState } from "react";
import { getCity } from "../api/citiesApi";
import ErrorBox from "./ErrorBox";

export default function CityDetails({ id }) {
  const [city, setCity] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (id) load();
  }, [id]);
  async function load() {
    setError(null);
    try {
      const r = await getCity(id);
      setCity(r.City || r.city || r);
    } catch (e) {
      setError({ message: e?.response?.data || e.message });
    }
  }

  if (!id) return null;
  return (
    <div className="card">
      <h3>Детали города {id}</h3>
      <ErrorBox error={error} />
      {city ? <pre>{JSON.stringify(city, null, 2)}</pre> : <p>Загрузка...</p>}
    </div>
  );
}
