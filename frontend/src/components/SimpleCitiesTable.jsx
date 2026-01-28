import { localizeGovernment } from "../utils/government-localizator";
import { formatDate } from "../utils/date-localizator";

export default function SimpleCitiesTable({ cities }) {
  if (!cities || cities.length === 0) return null;

  const columns = [
    "ID",
    "Имя",
    "X",
    "Y",
    "Площадь",
    "Население",
    "Метров над уровнем моря",
    "Плотность населения",
    "Форма правления",
    "Возраст губернатора",
    "Дата основания",
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
                  <td>{city.id}</td>
                  <td>{city.name}</td>
                  <td>{city.coordinates?.x ?? 0}</td>
                  <td>{city.coordinates?.y ?? 0}</td>
                  <td>{city.area}</td>
                  <td>{city.population}</td>
                  <td>{city.metersAboveSeaLevel}</td>
                  <td>{city.populationDensity}</td>
                  <td>{localizeGovernment(city.government) ?? "—"}</td>
                  <td>{city.governor?.age ?? "—"}</td>
                  <td>{formatDate(city.establishmentDate)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
