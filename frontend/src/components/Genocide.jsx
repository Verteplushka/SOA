import { useState } from "react";
import { genocideCount, genocideMoveToPoorest } from "../api/api-service2";

export default function Genocide() {
  const [ids, setIds] = useState({ id1: "", id2: "", id3: "" });
  const [moveId, setMoveId] = useState("");
  const [result, setResult] = useState(null);

  const handleCount = async () => {
    try {
      const res = await genocideCount(ids.id1, ids.id2, ids.id3);
      setResult(JSON.stringify(res, null, 2));
    } catch (e) {
      console.error(e);
    }
  };

  const handleMove = async () => {
    try {
      const res = await genocideMoveToPoorest(moveId);
      setResult(JSON.stringify(res, null, 2));
    } catch (e) {
      console.error(e);
    }
  };

  return (
    <div className="container my-4">
      <h2 className="mb-4">Геноцидные эндпоинты</h2>

      <div className="card mb-4 shadow-sm">
        <div className="card-body">
          <h3 className="card-title mb-3">Суммарное население 3 городов</h3>
          <div className="row g-2 mb-3">
            <div className="col-md">
              <input
                type="text"
                className="form-control"
                placeholder="ID1"
                value={ids.id1}
                onChange={(e) => setIds({ ...ids, id1: e.target.value })}
              />
            </div>
            <div className="col-md">
              <input
                type="text"
                className="form-control"
                placeholder="ID2"
                value={ids.id2}
                onChange={(e) => setIds({ ...ids, id2: e.target.value })}
              />
            </div>
            <div className="col-md">
              <input
                type="text"
                className="form-control"
                placeholder="ID3"
                value={ids.id3}
                onChange={(e) => setIds({ ...ids, id3: e.target.value })}
              />
            </div>
          </div>
          <button className="btn btn-primary" onClick={handleCount}>
            Посчитать
          </button>
        </div>
      </div>

      <div className="card mb-4 shadow-sm">
        <div className="card-body">
          <h3 className="card-title mb-3">Переселить в наименьший город</h3>
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
