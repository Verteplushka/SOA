import { useState } from "react";
import { genocideCount, genocideMoveToPoorest } from "../api/api-service2";
import CitiesTableGenocide from "../components/CitiesTableGenocide";

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

function formatEstDate(arr) {
  if (!Array.isArray(arr) || arr.length < 3) return "";
  return `${arr[0]}-${String(arr[1]).padStart(2, "0")}-${String(
    arr[2]
  ).padStart(2, "0")}`;
}

export default function Genocide() {
  const [ids, setIds] = useState({ id1: "", id2: "", id3: "" });
  const [moveId, setMoveId] = useState("");
  const [totalPopulation, setTotalPopulation] = useState(null);
  const [countError, setCountError] = useState(null);
  const [moveResult, setMoveResult] = useState(null);
  const [moveError, setMoveError] = useState(null);

  const handleCount = async () => {
    setTotalPopulation(null);
    setCountError(null);
    try {
      const res = await genocideCount(ids.id1, ids.id2, ids.id3);
      const total = res.population?.totalPopulation ?? "0";
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
    try {
      const res = await genocideMoveToPoorest(moveId);
      const source = res?.relocationResult?.sourceCity;
      const target = res?.relocationResult?.targetCity;
      if (source && target) {
        setMoveResult([
          { ...source, role: "Source" },
          { ...target, role: "Target" },
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
                  className="form-control"
                  placeholder={k.toUpperCase()}
                  value={ids[k]}
                  onChange={(e) => setIds({ ...ids, [k]: e.target.value })}
                />
              </div>
            ))}
          </div>
          <button className="btn btn-primary" onClick={handleCount}>
            Посчитать
          </button>

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
              className="form-control"
              placeholder="ID"
              value={moveId}
              onChange={(e) => setMoveId(e.target.value)}
            />
            <button className="btn btn-warning" onClick={handleMove}>
              Переселить
            </button>
          </div>
          {moveError && <div className="text-danger mt-3">{moveError}</div>}
          {moveResult && <CitiesTableGenocide cities={moveResult} />}
        </div>
      </div>
    </div>
  );
}
