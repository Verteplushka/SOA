import { useEffect, useState, useCallback } from "react";
import { searchCities } from "../api/api-service1";
import { useNavigate } from "react-router-dom";
import CityRow from "../components/CityRow";
import { governmentMap, getGovernmentKey } from "../utils/governmentMap";

const MAX_NAME_LENGTH = 100;
const MAX_INT_LENGTH = 10;
const MAX_AGE = 99;
const MIN_AGE = 1;

export default function Home() {
  const [cities, setCities] = useState([]);
  const [filters, setFilters] = useState({});
  const [sortField, setSortField] = useState("");
  const [sortDirection, setSortDirection] = useState("ASC");
  const [searchValues, setSearchValues] = useState({});
  const [errors, setErrors] = useState({});
  const [fetchError, setFetchError] = useState("");
  const [deleteError, setDeleteError] = useState("");
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [totalPages, setTotalPages] = useState(1);
  const navigate = useNavigate();

  const fetchCities = useCallback(async () => {
    setFetchError("");
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
      const rawCities = res?.cityPageResponse?.cities?.city || [];
      const normalized = Array.isArray(rawCities) ? rawCities : [rawCities];

      const citiesWithLinks = normalized.map((city) => {
        const linksArray = Array.isArray(city.links)
          ? city.links
          : [city.links];
        const linksMap = {};
        linksArray.forEach((l) => {
          if (l?.rel && l?.href) linksMap[l.rel] = l.href;
        });
        return { ...city, _links: linksMap };
      });

      setCities(citiesWithLinks);
      const total = res?.cityPageResponse?.pagination?.totalPages;
      setTotalPages(total ?? 1);
    } catch (e) {
      console.error("Ошибка при получении городов:", e);
      // setFetchError("Ошибка при получении городов");
      setCities([]);
    }
  }, [filters, searchValues, sortField, sortDirection, page, size]);

  useEffect(() => {
    fetchCities();
  }, [fetchCities]);

  const handleDelete = async (city) => {
    const deleteUrl = city._links?.delete;
    if (!deleteUrl) {
      alert("Ссылка для удаления не найдена");
      return;
    }

    try {
      const res = await fetch(deleteUrl, { method: "DELETE" });
      if (!res.ok) throw new Error(`Ошибка ${res.status}`);
      fetchCities();
    } catch (e) {
      console.error(e);
      alert("Ошибка при удалении города");
    }
  };

  const handleEdit = (city) =>
    navigate(`/edit/${city.id}`, { state: { city } });

  const handleSort = (field) => {
    if (sortField === field) {
      setSortDirection(sortDirection === "ASC" ? "DESC" : "ASC");
    } else {
      setSortField(field);
      setSortDirection("ASC");
    }
  };

  const validateField = (field, value) => {
    let err = "";
    if (value === "") return err;

    const integerFields = [
      "population",
      "metersAboveSeaLevel",
      "governor.age",
      "area",
    ];
    const floatFields = ["coordinates.x", "coordinates.y", "populationDensity"];

    if (field === "name") {
      if (value.length > MAX_NAME_LENGTH)
        err = `Максимум ${MAX_NAME_LENGTH} символов`;
    }

    if (integerFields.includes(field)) {
      if (!/^-?\d*$/.test(value)) err = "Введите целое число";
      else if (value.length > MAX_INT_LENGTH)
        err = `Максимум ${MAX_INT_LENGTH} цифр`;
      else if (field === "governor.age") {
        const num = Number(value);
        if (num < MIN_AGE || num > MAX_AGE)
          err = "Возраст должен быть от 1 до 99";
      } else if (["population", "area"].includes(field)) {
        const num = Number(value);
        if (num < 0) err = "Значение не может быть отрицательным";
      }
    }

    if (floatFields.includes(field)) {
      if (!/^-?\d*\.?\d*$/.test(value))
        err = "Введите число (разрешены цифры и точка)";
      else {
        const [intPart, decPart] = value.replace("-", "").split(".");
        if (intPart?.length > 10) err = "Максимум 10 цифр до точки";
        if (decPart && decPart.length > 10)
          err = "Максимум 10 цифр после точки";
      }
    }

    return err;
  };

  const handleRangeChange = (field, bound, value) => {
    const err = validateField(field, value);
    setErrors((prev) => ({ ...prev, [field]: err }));

    setSearchValues((prev) => ({
      ...prev,
      [field]: { ...prev[field], [bound]: value !== "" ? value : null },
    }));
  };

  const handleSelectChange = (field, value) => {
    setSearchValues((prev) => ({ ...prev, [field]: value || null }));
  };

  const handleIntegerKeyDown = (e) => {
    const allowedKeys = [
      "Backspace",
      "Delete",
      "ArrowLeft",
      "ArrowRight",
      "Tab",
      "-",
    ];
    if (!/[0-9]/.test(e.key) && !allowedKeys.includes(e.key))
      e.preventDefault();
    if (e.key === "-" && e.target.selectionStart !== 0) e.preventDefault();
  };

  const handleFloatKeyDown = (e, value, precision = 6) => {
    const allowedKeys = [
      "Backspace",
      "Delete",
      "ArrowLeft",
      "ArrowRight",
      "Tab",
      "-",
      ".",
    ];
    if (!/[0-9]/.test(e.key) && !allowedKeys.includes(e.key)) {
      e.preventDefault();
      return;
    }

    const selectionStart = e.target.selectionStart;
    const selectionEnd = e.target.selectionEnd;

    if (e.key === "-" && (selectionStart !== 0 || value.includes("-"))) {
      e.preventDefault();
      return;
    }

    if (e.key === "." && value.includes(".")) {
      e.preventDefault();
      return;
    }

    if (/[0-9]/.test(e.key) && value.includes(".")) {
      const [intPart, decPart] = value.split(".");
      if (
        selectionStart > value.indexOf(".") &&
        decPart &&
        decPart.length >= precision &&
        selectionStart === selectionEnd
      ) {
        e.preventDefault();
        return;
      }
    }
  };

  const columns = [
    { label: "ID", field: "id" },
    { label: "Имя", field: "name" },
    { label: "X", field: "coordinates.x" },
    { label: "Y", field: "coordinates.y" },
    { label: "Площадь", field: "area" },
    { label: "Население", field: "population" },
    { label: "Метров над уровнем моря", field: "metersAboveSeaLevel" },
    { label: "Дата основания", field: "establishmentDate" },
    { label: "Плотность населения", field: "populationDensity" },
    { label: "Форма правления", field: "government" },
    { label: "Возраст губернатора", field: "governor.age" },
  ];

  return (
    <div className="container mt-4">
      <h2>Список городов</h2>

      {fetchError && (
        <div className="alert alert-danger mt-3">{fetchError}</div>
      )}
      {deleteError && (
        <div className="alert alert-warning mt-3">{deleteError}</div>
      )}

      <table className="table table-bordered table-striped mt-3">
        <thead className="table-dark">
          <tr>
            {columns.map((col) => (
              <th key={col.field}>
                <div className="d-flex justify-content-between align-items-center">
                  <span>{col.label}</span>
                  <button
                    onClick={() => handleSort(col.field)}
                    style={{
                      border: "none",
                      background: "transparent",
                      color: "white",
                      cursor: "pointer",
                      padding: 0,
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
                    value={
                      searchValues[col.field]
                        ? governmentMap[searchValues[col.field]]
                        : "ЛЮБАЯ"
                    }
                    onChange={(e) =>
                      handleSelectChange(
                        col.field,
                        e.target.value === "ЛЮБАЯ"
                          ? null
                          : getGovernmentKey(e.target.value)
                      )
                    }
                  >
                    {Object.values(governmentMap).map((label) => (
                      <option key={label} value={label}>
                        {label}
                      </option>
                    ))}
                  </select>
                ) : col.field === "name" ? (
                  <>
                    <input
                      type="text"
                      style={{ width: "100px" }}
                      value={searchValues[col.field] || ""}
                      onChange={(e) => {
                        const val = e.target.value;
                        const err = validateField("name", val);
                        setErrors((prev) => ({ ...prev, name: err }));
                        handleSelectChange(col.field, val);
                      }}
                      placeholder="Поиск"
                    />
                    {errors.name && (
                      <div className="text-danger small">{errors.name}</div>
                    )}
                  </>
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
                      type="text"
                      placeholder="min"
                      style={{ width: "45px" }}
                      onKeyDown={(e) =>
                        [
                          "coordinates.x",
                          "coordinates.y",
                          "populationDensity",
                        ].includes(col.field)
                          ? handleFloatKeyDown(e, e.target.value, 3)
                          : handleIntegerKeyDown(e)
                      }
                      value={searchValues[col.field]?.min || ""}
                      onChange={(e) =>
                        handleRangeChange(col.field, "min", e.target.value)
                      }
                    />

                    <input
                      type="text"
                      placeholder="max"
                      style={{ width: "45px" }}
                      onKeyDown={(e) =>
                        [
                          "coordinates.x",
                          "coordinates.y",
                          "populationDensity",
                        ].includes(col.field)
                          ? handleFloatKeyDown(e, e.target.value, 3)
                          : handleIntegerKeyDown(e)
                      }
                      value={searchValues[col.field]?.max || ""}
                      onChange={(e) =>
                        handleRangeChange(col.field, "max", e.target.value)
                      }
                    />
                    {errors[col.field] && (
                      <div className="text-danger small">
                        {errors[col.field]}
                      </div>
                    )}
                  </>
                )}
              </th>
            ))}
            <th>Действия</th>
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
            onClick={() => setPage(0)}
            disabled={page === 0}
          >
            {"<<"}
          </button>
          <button
            className="btn btn-sm btn-outline-primary me-1"
            onClick={() => setPage((p) => Math.max(p - 1, 0))}
            disabled={page === 0}
          >
            {"<"}
          </button>
          <span>
            Страница {page + 1} из {totalPages}
          </span>
          <button
            className="btn btn-sm btn-outline-primary ms-1"
            onClick={() => setPage((p) => Math.min(p + 1, totalPages - 1))}
            disabled={page >= totalPages - 1}
          >
            {">"}
          </button>
          <button
            className="btn btn-sm btn-outline-primary ms-1"
            onClick={() => setPage(totalPages - 1)}
            disabled={page >= totalPages - 1}
          >
            {">>"}
          </button>
        </div>

        <div>
          <label>
            Записей на странице:{" "}
            <select
              value={size}
              onChange={(e) => setSize(Number(e.target.value))}
            >
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
