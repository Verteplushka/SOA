import { useState } from "react";
import { genocideCount, genocideMoveToPoorest } from "../api/api-service2";
import CitiesTableGenocide from "../components/CitiesTableGenocide";

function parseErrorMessage(xmlString) {
  try {
    const parser = new DOMParser();
    const xml = parser.parseFromString(xmlString, "text/xml");
    const messageNode = xml.getElementsByTagName("error")[0];
    if (messageNode) return messageNode.textContent;
  } catch (e) {
    console.error("Ошибка парсинга XML:", e);
  }
  return "Неизвестная ошибка";
}

export default function Genocide() {
  const [ids, setIds] = useState({ id1: "", id2: "", id3: "" });
  const [moveId, setMoveId] = useState("");
  const [totalPopulation, setTotalPopulation] = useState(null);
  const [countError, setCountError] = useState(null);
  const [moveResult, setMoveResult] = useState(null);
  const [moveError, setMoveError] = useState(null);
  const [validationErrors, setValidationErrors] = useState({});
  const [duplicateWarning, setDuplicateWarning] = useState("");

  const MAX_LENGTH = 9;

  const validateInput = (key, value) => {
    let error = "";

    if (value.trim() === "") {
      error = "Поле не может быть пустым";
    } else if (!/^\d+$/.test(value)) {
      error = "Разрешены только цифры";
    } else if (value.length > MAX_LENGTH) {
      error = `Максимальная длина — ${MAX_LENGTH} цифр`;
    }

    setValidationErrors((prev) => ({ ...prev, [key]: error }));
    return error === "";
  };

  const formatEstablishmentDate = (value) => {
    if (!value) return "";

    let d;

    if (typeof value === "string" && /^\d{4}-\d{2}-\d{2}T/.test(value)) {
      d = new Date(value + "Z");
    } else if (typeof value === "number") {
      d = new Date(value * 1000);
    } else if (Array.isArray(value)) {
      const [y, m, d0] = value;
      d = new Date(y, m - 1, d0);
    } else {
      d = new Date(value);
    }

    if (isNaN(d.getTime())) return "";

    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}-${String(d.getDate()).padStart(2, "0")}`;
  };

  const formatCreationDate = (value) => {
    if (!value) return "";

    const d = new Date(value);
    if (isNaN(d.getTime())) return "";

    return d.toLocaleString("ru-RU", {
      year: "numeric",
      month: "2-digit",
      day: "2-digit",
      hour: "2-digit",
      minute: "2-digit",
      second: "2-digit",
    });
  };

  const handleIdChange = (key, value) => {
    setIds((prev) => ({ ...prev, [key]: value }));
    validateInput(key, value);
  };

  const checkDuplicateIds = () => {
    const values = Object.values(ids).filter((v) => v.trim() !== "");
    const duplicates = values.filter(
      (v, i) => values.indexOf(v) !== i && v !== "",
    );
    setDuplicateWarning(
      duplicates.length > 0 ? "Введены повторяющиеся ID городов!" : "",
    );
  };

  const handleNumericKeyDown = (e) => {
    const allowedKeys = [
      "Backspace",
      "Delete",
      "ArrowLeft",
      "ArrowRight",
      "Tab",
    ];
    if (!/[0-9]/.test(e.key) && !allowedKeys.includes(e.key)) {
      e.preventDefault();
    }
  };

  const handleCount = async () => {
    setTotalPopulation(null);
    setCountError(null);

    const valid = Object.keys(ids).every((k) => validateInput(k, ids[k]));
    if (!valid) {
      return;
    }
    checkDuplicateIds();

    try {
      const res = await genocideCount(ids.id1, ids.id2, ids.id3);
      console.log("Ответ сервера:", res);

      const total = res.result?.totalPopulation ?? "0";
      setTotalPopulation(total);
    } catch (e) {
      const msg = e.response?.data
        ? parseErrorMessage(e.response.data)
        : e.message || e.toString();
      setCountError(msg);
    }
  };

  const handleMove = async () => {
    setMoveResult(null);
    setMoveError(null);

    if (!validateInput("moveId", moveId)) {
      return;
    }

    try {
      const res = await genocideMoveToPoorest(moveId);
      console.log("Ответ сервера:", res);

      const source = res.result?.sourceCity?.city;
      const target = res.result?.targetCity?.city;

      if (source && target) {
        const formatDate = (ts) => {
          if (!ts) return "";
          const d = new Date(ts * 1000);
          const yyyy = d.getFullYear();
          const mm = String(d.getMonth() + 1).padStart(2, "0");
          const dd = String(d.getDate()).padStart(2, "0");
          return `${yyyy}-${mm}-${dd}`;
        };

        console.log(source.establishmentDate);
        console.log(target.establishmentDate);
        const formatCity = (city) => ({
          ...city,
          creationDate: formatCreationDate(city.creationDate),
          establishmentDate: formatEstablishmentDate(city.establishmentDate),
        });
        console.log(formatCity(source).establishmentDate);
        console.log(formatCity(target).establishmentDate);

        setMoveResult([
          { ...formatCity(source), role: "Откуда" },
          { ...formatCity(target), role: "Куда" },
        ]);
      }
    } catch (e) {
      const msg = e.response?.data
        ? parseErrorMessage(e.response.data)
        : e.message || e.toString();
      setMoveError(msg);
    }
  };

  return (
    <div className="container my-4">
      <h2 className="mb-4">Геноцидные эндпоинты</h2>

      <div className="card mb-4 shadow-sm">
        <div className="card-body">
          <h3 className="card-title mb-3">Суммарное население 3 городов</h3>
          <div className="row g-2 mb-3">
            {["id1", "id2", "id3"].map((k) => (
              <div className="col-md" key={k}>
                <input
                  type="text"
                  className={`form-control ${validationErrors[k] ? "is-invalid" : ""}`}
                  placeholder={k.toUpperCase()}
                  value={ids[k]}
                  onChange={(e) => handleIdChange(k, e.target.value)}
                  onKeyDown={handleNumericKeyDown}
                />
                {validationErrors[k] && (
                  <div className="invalid-feedback">{validationErrors[k]}</div>
                )}
              </div>
            ))}
          </div>
          <button className="btn btn-primary" onClick={handleCount}>
            Посчитать
          </button>
          {duplicateWarning && (
            <div className="text-warning mt-3">{duplicateWarning}</div>
          )}
          {countError && <div className="text-danger mt-3">{countError}</div>}
          {totalPopulation !== null && (
            <div className="mt-3">
              <strong>Суммарное население:</strong> {totalPopulation}
            </div>
          )}
        </div>
      </div>

      <div className="card mb-4 shadow-sm">
        <div className="card-body">
          <h3 className="card-title mb-3">
            Переселить в город с наихудшим уровнем жизни
          </h3>
          <div className="input-group mb-3">
            <input
              type="text"
              className={`form-control ${validationErrors.moveId ? "is-invalid" : ""}`}
              placeholder="ID"
              value={moveId}
              onChange={(e) => {
                setMoveId(e.target.value);
                validateInput("moveId", e.target.value);
              }}
              onKeyDown={handleNumericKeyDown}
            />
            <button className="btn btn-warning" onClick={handleMove}>
              Переселить
            </button>
            {validationErrors.moveId && (
              <div className="invalid-feedback d-block">
                {validationErrors.moveId}
              </div>
            )}
          </div>
          {moveError && <div className="text-danger mt-3">{moveError}</div>}
          {moveResult && <CitiesTableGenocide cities={moveResult} />}
        </div>
      </div>
    </div>
  );
}
