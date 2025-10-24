import React, { useState } from "react";
import { addCity } from "../api/citiesApi";
import ErrorBox from "./ErrorBox";

const emptyInput = {
  name: "",
  coordinates: { x: 0, y: 0 },
  area: 1,
  population: 1,
  metersAboveSeaLevel: 0,
  establishmentDate: new Date().toISOString(),
  populationDensity: 1.0,
  government: "REPUBLIC",
  governor: { age: 30 },
};

export default function CityForm() {
  const [model, setModel] = useState(emptyInput);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);

  function update(path, value) {
    const copy = JSON.parse(JSON.stringify(model));
    const parts = path.split(".");
    let cur = copy;
    for (let i = 0; i < parts.length - 1; i++) cur = cur[parts[i]];
    cur[parts[parts.length - 1]] = value;
    setModel(copy);
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError(null);
    setSuccess(null);
    try {
      const payload = { ...model };
      const resp = await addCity(payload);
      setSuccess("Город добавлен");
      setModel(emptyInput);
    } catch (err) {
      console.error(err);
      const msg = err?.response?.data || err.message;
      setError({ message: msg });
    }
  }

  return (
    <div className="card">
      <h3>Добавить город</h3>
      <ErrorBox error={error} />
      {success && (
        <div
          style={{
            background: "#efe",
            padding: 8,
            borderRadius: 6,
            marginBottom: 8,
          }}
        >
          {success}
        </div>
      )}
      <form onSubmit={handleSubmit}>
        <div>
          <label>
            Название
            <br />
            <input
              value={model.name}
              onChange={(e) => update("name", e.target.value)}
              required
            />
          </label>
        </div>
        <div>
          <label>
            Coordinates.x
            <br />
            <input
              type="number"
              value={model.coordinates.x}
              onChange={(e) =>
                update("coordinates.x", Number(e.target.value) || 0)
              }
              required
            />
          </label>
        </div>
        <div>
          <label>
            Coordinates.y
            <br />
            <input
              type="number"
              value={model.coordinates.y}
              onChange={(e) =>
                update("coordinates.y", Number(e.target.value) || 0)
              }
              required
            />
          </label>
        </div>
        <div>
          <label>
            Area
            <br />
            <input
              type="number"
              value={model.area}
              onChange={(e) => update("area", Number(e.target.value) || 1)}
              required
            />
          </label>
        </div>
        <div>
          <label>
            Population
            <br />
            <input
              type="number"
              value={model.population}
              onChange={(e) =>
                update("population", Number(e.target.value) || 1)
              }
              required
            />
          </label>
        </div>
        <div>
          <label>
            Governor age
            <br />
            <input
              type="number"
              value={model.governor.age}
              onChange={(e) =>
                update("governor.age", Number(e.target.value) || 1)
              }
              required
            />
          </label>
        </div>
        <div style={{ marginTop: 8 }}>
          <button type="submit">Создать</button>
        </div>
      </form>
    </div>
  );
}
