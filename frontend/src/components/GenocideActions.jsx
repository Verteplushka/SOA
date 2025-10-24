import React, { useState } from "react";
import { genocideCount, genocideMoveToPoorest } from "../api/citiesApi";
import ErrorBox from "./ErrorBox";

export default function GenocideActions() {
  const [ids, setIds] = useState({ id1: "", id2: "", id3: "" });
  const [countResult, setCountResult] = useState(null);
  const [relocationResult, setRelocationResult] = useState(null);
  const [error, setError] = useState(null);

  async function handleCount(e) {
    e.preventDefault();
    setError(null);
    setCountResult(null);
    try {
      const r = await genocideCount(ids.id1, ids.id2, ids.id3);
      setCountResult(r.population || r.totalPopulation || r);
    } catch (err) {
      setError({ message: err?.response?.data || err.message });
    }
  }

  async function handleMove(e) {
    e.preventDefault();
    setError(null);
    setRelocationResult(null);
    try {
      const r = await genocideMoveToPoorest(ids.id1);
      setRelocationResult(r);
    } catch (err) {
      setError({ message: err?.response?.data || err.message });
    }
  }

  return (
    <div className="card">
      <h3>Спец. операции</h3>
      <ErrorBox error={error} />
      <form onSubmit={handleCount} style={{ marginBottom: 8 }}>
        <div>
          <label>
            ID1{" "}
            <input
              value={ids.id1}
              onChange={(e) => setIds((s) => ({ ...s, id1: e.target.value }))}
            />
          </label>
        </div>
        <div>
          <label>
            ID2{" "}
            <input
              value={ids.id2}
              onChange={(e) => setIds((s) => ({ ...s, id2: e.target.value }))}
            />
          </label>
        </div>
        <div>
          <label>
            ID3{" "}
            <input
              value={ids.id3}
              onChange={(e) => setIds((s) => ({ ...s, id3: e.target.value }))}
            />
          </label>
        </div>
        <div>
          <button type="submit">Суммарное население</button>
        </div>
      </form>

      {countResult && <pre>Result: {JSON.stringify(countResult)}</pre>}

      <hr />

      <form onSubmit={handleMove}>
        <div>
          <label>
            Source ID{" "}
            <input
              value={ids.id1}
              onChange={(e) => setIds((s) => ({ ...s, id1: e.target.value }))}
            />
          </label>
        </div>
        <div>
          <button type="submit">Переселить в poorest</button>
        </div>
      </form>

      {relocationResult && (
        <pre>{JSON.stringify(relocationResult, null, 2)}</pre>
      )}
    </div>
  );
}
