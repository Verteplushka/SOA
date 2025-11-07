import { useEffect, useState } from "react";
import { searchCities, deleteCity } from "../api/api-service1";
import { useNavigate } from "react-router-dom";
import { useCallback } from "react";
import CityRow from "../components/CityRow";

const governmentOptions = ["ALL", "DIARCHY", "KRITARCHY", "REPUBLIC"];

export default function Home() {
  const [cities, setCities] = useState([]);
  const [filters, setFilters] = useState({});
  const [sortField, setSortField] = useState("");
  const [sortDirection, setSortDirection] = useState("ASC");
  const [searchValues, setSearchValues] = useState({});
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [totalPages, setTotalPages] = useState(1);

  const navigate = useNavigate();

  const fetchCities = useCallback(async () => {
    const preparedFilters = { ...filters, ...searchValues };

    if (preparedFilters.establishmentDate) {
      const ed = preparedFilters.establishmentDate;
      preparedFilters.establishmentDate = {
        min: ed.min ? `${ed.min}T00:00:00` : undefined,
        max: ed.max ? `${ed.max}T23:59:59` : undefined,
      };
    }

    const coordsX = preparedFilters["coordinates.x"];
    const coordsY = preparedFilters["coordinates.y"];
    if (coordsX || coordsY) {
      preparedFilters.coordinates = {};
      if (coordsX) preparedFilters.coordinates.x = coordsX;
      if (coordsY) preparedFilters.coordinates.y = coordsY;
      delete preparedFilters["coordinates.x"];
      delete preparedFilters["coordinates.y"];
    }

    const requestBody = {
      pagination: { page, size },
      sort: sortField ? [{ field: sortField, direction: sortDirection }] : [],
      filter: preparedFilters,
    };

    try {
      const res = await searchCities(requestBody);
      const cityData = res?.cityPageResponse?.cities?.city || [];
      setCities(Array.isArray(cityData) ? cityData : [cityData]);

      const total = res?.cityPageResponse?.pagination?.totalPages;
      setTotalPages(total ?? 1);
    } catch (e) {
      console.error("Ошибка при получении городов:", e);
      alert("Ошибка при получении городов");
    }
  }, [filters, searchValues, sortField, sortDirection, page, size]);

  const handlePageChange = (newPage) => {
    if (newPage >= 0 && newPage < totalPages) setPage(newPage);
  };

  const handleSizeChange = (e) => {
    setSize(Number(e.target.value));
    setPage(0);
  };

  useEffect(() => {
    fetchCities();
  }, [fetchCities]);

  const handleDelete = async (id) => {
    if (!window.confirm("Удалить этот город?")) return;
    try {
      await deleteCity(id);
      fetchCities();
    } catch {
      alert("Ошибка при удалении города");
    }
  };

  const handleEdit = (id) => navigate(`/edit/${id}`);

  const handleSort = (field) => {
    if (sortField === field) {
      setSortDirection(sortDirection === "ASC" ? "DESC" : "ASC");
    } else {
      setSortField(field);
      setSortDirection("ASC");
    }
  };

  const handleRangeChange = (field, bound, value) => {
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
              <th key={col.field}>
                <div
                  style={{
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "space-between",
                  }}
                >
                  <span>{col.label}</span>
                  <button
                    onClick={() => handleSort(col.field)}
                    style={{
                      border: "none",
                      background: "transparent",
                      color: "white",
                      cursor: "pointer",
                      padding: 0,
                      marginLeft: "4px",
                    }}
                    title="Сортировать"
                  >
                    {sortField === col.field
                      ? sortDirection === "ASC"
                        ? "▲"
                        : "▼"
                      : "↕"}
                  </button>
                </div>

                {col.field === "government" ? (
                  <select
                    style={{ width: "100px" }}
                    value={searchValues[col.field] || "ALL"}
                    onChange={(e) =>
                      handleSelectChange(
                        col.field,
                        e.target.value === "ALL" ? null : e.target.value
                      )
                    }
                  >
                    {governmentOptions.map((gov) => (
                      <option key={gov} value={gov}>
                        {gov}
                      </option>
                    ))}
                  </select>
                ) : col.field === "name" ? (
                  <input
                    type="text"
                    style={{ width: "100px" }}
                    value={searchValues[col.field] || ""}
                    onChange={(e) =>
                      handleSelectChange(col.field, e.target.value)
                    }
                    placeholder="Поиск"
                  />
                ) : col.field === "establishmentDate" ? (
                  <>
                    <input
                      type="date"
                      style={{ width: "80px" }}
                      value={searchValues[col.field]?.min || ""}
                      onChange={(e) =>
                        handleRangeChange(col.field, "min", e.target.value)
                      }
                    />
                    <input
                      type="date"
                      style={{ width: "80px" }}
                      value={searchValues[col.field]?.max || ""}
                      onChange={(e) =>
                        handleRangeChange(col.field, "max", e.target.value)
                      }
                    />
                  </>
                ) : col.field === "id" ? null : (
                  <>
                    <input
                      type="number"
                      step="any"
                      placeholder="min"
                      style={{ width: "45px" }}
                      value={searchValues[col.field]?.min || ""}
                      onChange={(e) => {
                        const val = e.target.value;
                        if (val === "" || /^-?\d*\.?\d*$/.test(val)) {
                          handleRangeChange(col.field, "min", val);
                        }
                      }}
                    />
                    <input
                      type="number"
                      step="any"
                      placeholder="max"
                      style={{ width: "45px" }}
                      value={searchValues[col.field]?.max || ""}
                      onChange={(e) => {
                        const val = e.target.value;
                        if (val === "" || /^-?\d*\.?\d*$/.test(val)) {
                          handleRangeChange(col.field, "max", val);
                        }
                      }}
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

      <div className="d-flex justify-content-between align-items-center mt-3">
        <div>
          <button
            className="btn btn-sm btn-outline-primary me-1"
            onClick={() => handlePageChange(0)}
            disabled={page === 0}
          >
            {"<<"}
          </button>
          <button
            className="btn btn-sm btn-outline-primary me-1"
            onClick={() => handlePageChange(page - 1)}
            disabled={page === 0}
          >
            {"<"}
          </button>
          <span>
            Страница {page + 1} из {totalPages}
          </span>
          <button
            className="btn btn-sm btn-outline-primary ms-1"
            onClick={() => handlePageChange(page + 1)}
            disabled={page >= totalPages - 1}
          >
            {">"}
          </button>
          <button
            className="btn btn-sm btn-outline-primary ms-1"
            onClick={() => handlePageChange(totalPages - 1)}
            disabled={page >= totalPages - 1}
          >
            {">>"}
          </button>
        </div>

        <div>
          <label>
            Записей на странице:{" "}
            <select value={size} onChange={handleSizeChange}>
              {[5, 10, 20, 50, 100].map((s) => (
                <option key={s} value={s}>
                  {s}
                </option>
              ))}
            </select>
          </label>
        </div>
      </div>
    </div>
  );
}
