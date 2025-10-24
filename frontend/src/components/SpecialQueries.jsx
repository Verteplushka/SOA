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
    <div>
      <h2>Прочие эндпоинты</h2>
      <div>
        <h3>Удалить по metersAboveSeaLevel</h3>
        <input
          placeholder="meters"
          value={meters}
          onChange={(e) => setMeters(e.target.value)}
        />
        <button onClick={handleDelete}>Удалить</button>
      </div>
      <div>
        <h3>Поиск по имени (prefix)</h3>
        <input
          placeholder="prefix"
          value={prefix}
          onChange={(e) => setPrefix(e.target.value)}
        />
        <button onClick={handleByName}>Найти</button>
      </div>
      <div>
        <h3>Поиск по возрасту губернатора (age)</h3>
        <input
          placeholder="age"
          value={age}
          onChange={(e) => setAge(e.target.value)}
        />
        <button onClick={handleByAge}>Найти</button>
      </div>
      {result && <pre>{result}</pre>}
    </div>
  );
}
