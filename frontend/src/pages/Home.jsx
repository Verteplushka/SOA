import { useEffect, useState } from "react";
import { searchCities, deleteCity } from "../api";
import { useNavigate } from "react-router-dom";
import CityRow from "../components/CityRow";

const governmentOptions = ["DIARCHY", "KRITARCHY", "REPUBLIC"];

export default function Home() {
  const [cities, setCities] = useState([]);
  const [filters, setFilters] = useState({});
  const [sortField, setSortField] = useState("");
  const [sortDirection, setSortDirection] = useState("ASC");
  const [searchValues, setSearchValues] = useState({});
  const navigate = useNavigate();

  const fetchCities = async () => {
    const requestBody = {
      pagination: { page: 0, size: 100 },
      sort: sortField ? [{ field: sortField, direction: sortDirection }] : [],
      filter: { ...filters, ...searchValues },
    };

    console.log("=== Отправка запроса ===");
    console.log(JSON.stringify(requestBody, null, 2));

    try {
      const res = await searchCities(requestBody);

      console.log("=== Ответ сервера ===");
      console.log(res);

      const cityData = res?.cityPageResponse?.cities?.cities || [];
      const cityArray = Array.isArray(cityData) ? cityData : [cityData];
      setCities(cityArray);
    } catch (e) {
      console.error("Ошибка при получении городов:", e);
      alert("Ошибка при получении городов");
    }
  };

  useEffect(() => {
    fetchCities();
  }, [filters, sortField, sortDirection, searchValues]);

  const handleDelete = async (id) => {
    if (!window.confirm("Удалить этот город?")) return;
    try {
      await deleteCity(id);
      fetchCities();
    } catch (err) {
      alert("Ошибка при удалении города");
    }
  };

  const handleEdit = (id) => {
    navigate(`/edit/${id}`);
  };

  const handleSort = (field) => {
    if (sortField === field) {
      setSortDirection(sortDirection === "ASC" ? "DESC" : "ASC");
    } else {
      setSortField(field);
      setSortDirection("ASC");
    }
  };

  const handleRangeChange = (field, bound, value) => {
    // bound = "min" | "max"
    setSearchValues((prev) => ({
      ...prev,
      [field]: {
        ...prev[field],
        [bound]: value !== "" ? value : null,
      },
    }));
  };

  const handleSelectChange = (field, value) => {
    setSearchValues((prev) => ({ ...prev, [field]: value || null }));
  };

  const columns = [
    { label: "ID", field: "id" },
    { label: "Name", field: "name" },
    { label: "Coord X", field: "coordinates.x" },
    { label: "Coord Y", field: "coordinates.y" },
    { label: "Area", field: "area" },
    { label: "Population", field: "population" },
    { label: "Meters Above Sea Level", field: "metersAboveSeaLevel" },
    { label: "Establishment Date", field: "establishmentDate" },
    { label: "Population Density", field: "populationDensity" },
    { label: "Government", field: "government" },
    { label: "Governor Age", field: "governor.age" },
  ];

  return (
    <div className="container mt-4">
      <h2>Список городов</h2>
      <table className="table table-bordered table-striped mt-3">
        <thead className="table-dark">
          <tr>
            {columns.map((col) => (
              <th key={col.field} onClick={() => handleSort(col.field)}>
                {col.label}{" "}
                {sortField === col.field
                  ? sortDirection === "ASC"
                    ? "▲"
                    : "▼"
                  : ""}
                <br />
                {col.field === "government" ? (
                  <select
                    style={{ width: "100px" }}
                    value={searchValues[col.field] || ""}
                    onChange={(e) =>
                      handleSelectChange(col.field, e.target.value)
                    }
                  >
                    <option value="">Все</option>
                    {governmentOptions.map((gov) => (
                      <option key={gov} value={gov}>
                        {gov}
                      </option>
                    ))}
                  </select>
                ) : (
                  <>
                    <input
                      type="text"
                      placeholder="min"
                      style={{ width: "45px" }}
                      value={searchValues[col.field]?.min || ""}
                      onChange={(e) =>
                        handleRangeChange(col.field, "min", e.target.value)
                      }
                    />
                    <input
                      type="text"
                      placeholder="max"
                      style={{ width: "45px" }}
                      value={searchValues[col.field]?.max || ""}
                      onChange={(e) =>
                        handleRangeChange(col.field, "max", e.target.value)
                      }
                    />
                  </>
                )}
              </th>
            ))}
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {cities.length === 0 ? (
            <tr>
              <td colSpan={columns.length + 1} className="text-center">
                Нет городов
              </td>
            </tr>
          ) : (
            cities.map((city) => (
              <CityRow
                key={city.id}
                city={city}
                onDelete={handleDelete}
                onEdit={handleEdit}
              />
            ))
          )}
        </tbody>
      </table>
    </div>
  );
}
