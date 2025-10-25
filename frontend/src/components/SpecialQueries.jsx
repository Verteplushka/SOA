import { useState } from "react";
import {
  deleteByMeters,
  byNamePrefix,
  byGovernorAge,
} from "../api/api-service1";
import SimpleCitiesTable from "../components/SimpleCitiesTable";

function getNestedValue(obj) {
  if (obj === null || obj === undefined) return "";
  if (typeof obj === "object") return JSON.stringify(obj);
  return obj;
}

function parseErrorMessage(xmlString) {
  try {
    const parser = new DOMParser();
    const xml = parser.parseFromString(xmlString, "text/xml");
    const messageNode = xml.getElementsByTagName("message")[0];
    if (messageNode) return messageNode.textContent;
  } catch (e) {
    console.error("Ошибка парсинга XML:", e);
  }
  return "Неизвестная ошибка";
}

export default function SpecialQueries() {
  const [meters, setMeters] = useState("");
  const [prefix, setPrefix] = useState("");
  const [age, setAge] = useState("");

  const [deleteResult, setDeleteResult] = useState(null);
  const [deleteError, setDeleteError] = useState(null);
  const [prefixError, setPrefixError] = useState(null);
  const [ageError, setAgeError] = useState(null);
  const [tableResult, setTableResult] = useState([]);

  const handleDelete = async () => {
    setDeleteResult(null);
    setDeleteError(null);
    try {
      const res = await deleteByMeters(meters);
      setDeleteResult(`Объект успешно удален!`);
    } catch (e) {
      const msg = e.response?.data
        ? parseErrorMessage(e.response.data)
        : e.message || e.toString();
      setDeleteError(msg);
    }
  };

  const handleByName = async () => {
    setPrefixError(null);
    setTableResult([]);
    try {
      const res = await byNamePrefix(prefix);
      const cities = res?.ArrayList?.item || [];
      setTableResult(Array.isArray(cities) ? cities : [cities]);
    } catch (e) {
      const msg = e.response?.data
        ? parseErrorMessage(e.response.data)
        : e.message || e.toString();
      setPrefixError(msg);
    }
  };

  const handleByAge = async () => {
    setAgeError(null);
    setTableResult([]);
    try {
      const res = await byGovernorAge(age);
      const cities = res?.ArrayList?.item || [];
      setTableResult(Array.isArray(cities) ? cities : [cities]);
    } catch (e) {
      const msg = e.response?.data
        ? parseErrorMessage(e.response.data)
        : e.message || e.toString();
      setAgeError(msg);
    }
  };

  return (
    <div className="container my-4">
      <h2 className="mb-4">Прочие эндпоинты</h2>

      <div className="card mb-4 shadow-sm">
        <div className="card-body">
          <h3 className="card-title mb-3">Удалить по metersAboveSeaLevel</h3>
          <div className="input-group mb-2">
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
          {deleteResult && <div className="text-success">{deleteResult}</div>}
          {deleteError && <div className="text-danger">{deleteError}</div>}
        </div>
      </div>

      <div className="card mb-4 shadow-sm">
        <div className="card-body">
          <h3 className="card-title mb-3">Поиск по имени (prefix)</h3>
          <div className="input-group mb-2">
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
          {prefixError && <div className="text-danger">{prefixError}</div>}
        </div>
      </div>

      <div className="card mb-4 shadow-sm">
        <div className="card-body">
          <h3 className="card-title mb-3">
            Поиск по возрасту губернатора (age)
          </h3>
          <div className="input-group mb-2">
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
          {ageError && <div className="text-danger">{ageError}</div>}
        </div>
      </div>

      {tableResult.length > 0 && <SimpleCitiesTable cities={tableResult} />}
    </div>
  );
}
