import { useState } from "react";
import {
  deleteByMeters,
  byNamePrefix,
  byGovernorAge,
} from "../api/api-service1";
import SimpleCitiesTable from "../components/SimpleCitiesTable";

function parseErrorMessage(xmlString) {
  try {
    const parser = new DOMParser();
    const xml = parser.parseFromString(xmlString, "text/xml");
    const errorNode = xml.getElementsByTagName("error")[0];
    const messageNode = xml.getElementsByTagName("message")[0];

    if (errorNode && errorNode.textContent === "NOT_FOUND") {
      return "Город с заданным именем не найден";
    }

    if (messageNode) return messageNode.textContent;
  } catch (e) {
    console.error("Ошибка парсинга XML:", e);
  }
  return "Неизвестная ошибка";
}

const MAX_INT_LENGTH = 9;
const MAX_NAME_LENGTH = 100;

export default function SpecialQueries() {
  const [meters, setMeters] = useState("");
  const [prefix, setPrefix] = useState("");
  const [age, setAge] = useState("");

  const [deleteResult, setDeleteResult] = useState(null);
  const [deleteError, setDeleteError] = useState(null);
  const [prefixError, setPrefixError] = useState(null);
  const [ageError, setAgeError] = useState(null);
  const [validationErrors, setValidationErrors] = useState({});
  const [tableResult, setTableResult] = useState([]);

  // === Валидации ===
  const validateMeters = (value) => {
    let err = "";
    if (!/^-?\d+$/.test(value)) err = "Введите целое число";
    else if (value.length > MAX_INT_LENGTH)
      err = `Максимум ${MAX_INT_LENGTH} цифр`;
    setValidationErrors((prev) => ({ ...prev, meters: err }));
    return !err;
  };

  const validatePrefix = (value) => {
    let err = "";
    if (!value.trim()) err = "Префикс не может быть пустым";
    else if (value.length > MAX_NAME_LENGTH)
      err = `Максимальная длина — ${MAX_NAME_LENGTH} символов`;
    setValidationErrors((prev) => ({ ...prev, prefix: err }));
    return !err;
  };

  const validateAge = (value) => {
    let err = "";
    if (!/^\d+$/.test(value)) err = "Введите целое число";
    else if (Number(value) < 1 || Number(value) > 99)
      err = "Возраст должен быть от 1 до 99";
    setValidationErrors((prev) => ({ ...prev, age: err }));
    return !err;
  };

  const handleDelete = async () => {
    setDeleteResult(null);
    setDeleteError(null);
    if (!validateMeters(meters)) {
      setDeleteError("Исправьте ошибки перед удалением");
      return;
    }
    try {
      await deleteByMeters(meters);
      setDeleteResult("Объект успешно удален!");
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
    if (!validatePrefix(prefix)) {
      setPrefixError("Исправьте ошибки перед поиском");
      return;
    }

    try {
      const res = await byNamePrefix(prefix);
      if (res?.error === "NOT_FOUND") {
        setPrefixError("Город с заданным именем не найден");
        setTableResult([]);
        return;
      }

      const cities = res?.citiesResponse?.cities?.city || [];
      if (!cities || (Array.isArray(cities) && cities.length === 0)) {
        setPrefixError("Город с заданным именем не найден");
        setTableResult([]);
        return;
      }

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
    if (!validateAge(age)) {
      setAgeError("Исправьте ошибки перед поиском");
      return;
    }
    try {
      const res = await byGovernorAge(age);
      const cities = res?.citiesResponse?.cities?.city || [];
      setTableResult(Array.isArray(cities) ? cities : [cities]);
    } catch (e) {
      const msg = e.response?.data
        ? parseErrorMessage(e.response.data)
        : e.message || e.toString();
      setAgeError(msg);
    }
  };

  const handleMetersInput = (e) => {
    const value = e.target.value;
    if (/^-?\d*$/.test(value)) {
      setMeters(value);
      validateMeters(value);
    }
  };

  const handleAgeInput = (e) => {
    const value = e.target.value;
    if (/^\d*$/.test(value)) {
      setAge(value);
      validateAge(value);
    }
  };

  return (
    <div className="container my-4">
      <h2 className="mb-4">Прочие эндпоинты</h2>

      <div className="card mb-4 shadow-sm">
        <div className="card-body">
          <h3 className="card-title mb-3">
            Удалить по метрам над уровнем моря
          </h3>
          <div className="input-group mb-2">
            <input
              type="text"
              className={`form-control ${
                validationErrors.meters ? "is-invalid" : ""
              }`}
              placeholder="meters"
              value={meters}
              onChange={handleMetersInput}
            />
            <button className="btn btn-danger" onClick={handleDelete}>
              Удалить
            </button>
          </div>
          {validationErrors.meters && (
            <div className="invalid-feedback d-block">
              {validationErrors.meters}
            </div>
          )}
          {deleteResult && <div className="text-success">{deleteResult}</div>}
          {deleteError && <div className="text-danger">{deleteError}</div>}
        </div>
      </div>

      <div className="card mb-4 shadow-sm">
        <div className="card-body">
          <h3 className="card-title mb-3">Поиск по имени</h3>
          <div className="input-group mb-2">
            <input
              type="text"
              className={`form-control ${
                validationErrors.prefix ? "is-invalid" : ""
              }`}
              placeholder="prefix"
              value={prefix}
              onChange={(e) => {
                setPrefix(e.target.value);
                validatePrefix(e.target.value);
              }}
            />
            <button className="btn btn-primary" onClick={handleByName}>
              Найти
            </button>
          </div>
          {validationErrors.prefix && (
            <div className="invalid-feedback d-block">
              {validationErrors.prefix}
            </div>
          )}
          {prefixError && <div className="text-danger">{prefixError}</div>}
        </div>
      </div>

      <div className="card mb-4 shadow-sm">
        <div className="card-body">
          <h3 className="card-title mb-3">
            Поиск городов с губернатором старше заданного возраста
          </h3>
          <div className="input-group mb-2">
            <input
              type="text"
              className={`form-control ${
                validationErrors.age ? "is-invalid" : ""
              }`}
              placeholder="age"
              value={age}
              onChange={handleAgeInput}
            />
            <button className="btn btn-warning" onClick={handleByAge}>
              Найти
            </button>
          </div>
          {validationErrors.age && (
            <div className="invalid-feedback d-block">
              {validationErrors.age}
            </div>
          )}
          {ageError && <div className="text-danger">{ageError}</div>}
        </div>
      </div>

      {tableResult.length > 0 && <SimpleCitiesTable cities={tableResult} />}
    </div>
  );
}
