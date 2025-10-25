import React from "react";

function getNestedValue(obj, key) {
  if (obj === null || obj === undefined) return "";

  if (key === "coordinates" && typeof obj === "object") {
    return `x: ${obj.x}, y: ${obj.y}`;
  }

  if (key === "governor" && typeof obj === "object") {
    return obj.age ?? "";
  }

  if (key === "establishmentDate" && Array.isArray(obj)) {
    const [year, month, day] = obj;
    if (year && month && day)
      return `${year}-${String(month).padStart(2, "0")}-${String(day).padStart(
        2,
        "0"
      )}`;
    return "";
  }

  if (typeof obj === "object") return JSON.stringify(obj);
  return obj;
}

export default function SimpleCitiesTable({ cities }) {
  if (!cities || cities.length === 0) return null;

  const columns = [
    "id",
    "name",
    "x",
    "y",
    "area",
    "population",
    "metersAboveSeaLevel",
    "establishmentDate",
    "populationDensity",
    "government",
    "governor",
  ];

  return (
    <div className="card shadow-sm mb-4">
      <div className="card-body">
        <h3 className="card-title mb-3">Результаты</h3>
        <div style={{ overflowX: "auto" }}>
          <table className="table table-bordered table-striped">
            <thead className="table-dark">
              <tr>
                {columns.map((col) => (
                  <th key={col}>{col}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {cities.map((city, i) => (
                <tr key={city.id || i}>
                  {columns.map((col) => {
                    if (col === "x")
                      return <td key={col}>{city.coordinates?.x ?? ""}</td>;
                    if (col === "y")
                      return <td key={col}>{city.coordinates?.y ?? ""}</td>;
                    return <td key={col}>{getNestedValue(city[col], col)}</td>;
                  })}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
