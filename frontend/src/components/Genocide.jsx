import { useState } from "react";
import { genocideCount, genocideMoveToPoorest } from "../api";

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
    <div>
      <h2>Геноцидные эндпоинты</h2>
      <div>
        <h3>Суммарное население 3 городов</h3>
        <input
          placeholder="ID1"
          value={ids.id1}
          onChange={(e) => setIds({ ...ids, id1: e.target.value })}
        />
        <input
          placeholder="ID2"
          value={ids.id2}
          onChange={(e) => setIds({ ...ids, id2: e.target.value })}
        />
        <input
          placeholder="ID3"
          value={ids.id3}
          onChange={(e) => setIds({ ...ids, id3: e.target.value })}
        />
        <button onClick={handleCount}>Посчитать</button>
      </div>
      <div>
        <h3>Переселить в наименьший город</h3>
        <input
          placeholder="ID"
          value={moveId}
          onChange={(e) => setMoveId(e.target.value)}
        />
        <button onClick={handleMove}>Переселить</button>
      </div>
      {result && <pre>{result}</pre>}
    </div>
  );
}
