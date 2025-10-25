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
                {[
                  "Role",
                  "ID",
                  "Name",
                  "X",
                  "Y",
                  "Area",
                  "Population",
                  "Meters Above Sea Level",
                  "Population Density",
                  "Government",
                  "Governor Age",
                  "Establishment Date",
                ].map((h) => (
                  <th key={h}>{h}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {cities.map((city, i) => (
                <tr key={city.id || i}>
                  <td>{city.role}</td>
                  <td>{city.id}</td>
                  <td>{city.name}</td>
                  <td>{city.coordinates?.x ?? 0}</td>
                  <td>{city.coordinates?.y ?? 0}</td>
                  <td>{city.area}</td>
                  <td>{city.population}</td>
                  <td>{city.metersAboveSeaLevel}</td>
                  <td>{city.populationDensity}</td>
                  <td>{city.government ?? "—"}</td>
                  <td>{city.governor?.age ?? "—"}</td>
                  <td>
                    {getNestedValue(
                      city.establishmentDate,
                      "establishmentDate"
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
