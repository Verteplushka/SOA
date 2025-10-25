import { useState } from "react";
import { deleteByMeters, byNamePrefix, byGovernorAge } from "../api";

export default function SpecialQueries() {
  const [meters, setMeters] = useState("");
  const [prefix, setPrefix] = useState("");
  const [age, setAge] = useState("");
  const [result, setResult] = useState(null);

  const handleDelete = async () => {
    try {
      const res = await deleteByMeters(meters);
      setResult("Удалено: " + res);
    } catch (e) {
      console.error(e);
      setResult(e.toString());
    }
  };

  const handleByName = async () => {
    try {
      const res = await byNamePrefix(prefix);
      setResult(JSON.stringify(res, null, 2));
    } catch (e) {
      console.error(e);
    }
  };

  const handleByAge = async () => {
    try {
      const res = await byGovernorAge(age);
      setResult(JSON.stringify(res, null, 2));
    } catch (e) {
      console.error(e);
    }
  };

  return (
    <div className="container my-4">
      <h2 className="mb-4">Прочие эндпоинты</h2>

      <div className="card mb-4 shadow-sm">
        <div className="card-body">
          <h3 className="card-title mb-3">Удалить по metersAboveSeaLevel</h3>
          <div className="input-group mb-3">
            <input
              type="number"
              className="form-control"
              placeholder="meters"
              value={meters}
              onChange={(e) => setMeters(e.target.value)}
            />
            <button className="btn btn-danger" onClick={handleDelete}>
              Удалить
            </button>
          </div>
        </div>
      </div>

      <div className="card mb-4 shadow-sm">
        <div className="card-body">
          <h3 className="card-title mb-3">Поиск по имени (prefix)</h3>
          <div className="input-group mb-3">
            <input
              type="text"
              className="form-control"
              placeholder="prefix"
              value={prefix}
              onChange={(e) => setPrefix(e.target.value)}
            />
            <button className="btn btn-primary" onClick={handleByName}>
              Найти
            </button>
          </div>
        </div>
      </div>

      <div className="card mb-4 shadow-sm">
        <div className="card-body">
          <h3 className="card-title mb-3">
            Поиск по возрасту губернатора (age)
          </h3>
          <div className="input-group mb-3">
            <input
              type="number"
              className="form-control"
              placeholder="age"
              value={age}
              onChange={(e) => setAge(e.target.value)}
            />
            <button className="btn btn-warning" onClick={handleByAge}>
              Найти
            </button>
          </div>
        </div>
      </div>

      {result && (
        <div className="card shadow-sm">
          <div className="card-body">
            <pre>{result}</pre>
          </div>
        </div>
      )}
    </div>
  );
}
