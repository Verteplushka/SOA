import { useEffect, useState, useCallback } from "react";
import { searchCities } from "../api/api-service1";
import { useNavigate } from "react-router-dom";
import CityRow from "../components/CityRow";

const governmentOptions = ["ALL", "DIARCHY", "KRITARCHY", "REPUBLIC"];

// 🔹 Константы ограничений
const MAX_NAME_LENGTH = 100;
const MAX_INT_LENGTH = 10;
const MAX_DOUBLE_LENGTH = 15;
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

  // 🔹 Получение списка городов
  const fetchCities = useCallback(async () => {
    setFetchError(""); // сброс ошибки
    const preparedFilters = { ...filters, ...searchValues };

    // Корректировка даты
    if (preparedFilters.establishmentDate) {
      const ed = preparedFilters.establishmentDate;
      preparedFilters.establishmentDate = {
        min: ed.min ? `${ed.min}T00:00:00` : undefined,
        max: ed.max ? `${ed.max}T23:59:59` : undefined,
      };
    }

    // Обработка координат
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
      setFetchError("Ошибка при получении городов");
      setCities([]);
    }
  }, [filters, searchValues, sortField, sortDirection, page, size]);

  useEffect(() => {
    fetchCities();
  }, [fetchCities]);

  const handleDelete = async (city) => {
    if (!window.confirm("Удалить этот город?")) return;

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

  // 🔹 Проверка корректности ввода
  const validateField = (field, value) => {
    let err = "";
    if (value === "") return err;

    const integerFields = [
      "population",
      "populationDensity",
      "metersAboveSeaLevel",
      "governor.age",
    ];
    const floatFields = ["coordinates.x", "coordinates.y", "area"];

    if (field === "name") {
      if (value.length > MAX_NAME_LENGTH)
        err = `Максимум ${MAX_NAME_LENGTH} символов`;
    }

    if ([...integerFields, ...floatFields].includes(field)) {
      const isInteger = integerFields.includes(field);
      const numberRegex = isInteger ? /^-?\d+$/ : /^-?\d*(\.?\d*)?$/;

      if (!numberRegex.test(value)) {
        err = isInteger ? "Введите целое число" : "Введите число";
      } else if (
        value.length > (isInteger ? MAX_INT_LENGTH : MAX_DOUBLE_LENGTH)
      ) {
        err = `Максимум ${
          isInteger ? MAX_INT_LENGTH : MAX_DOUBLE_LENGTH
        } символов`;
      } else {
        const num = Number(value);

        if (["area", "population", "populationDensity"].includes(field)) {
          if (num < 0) err = "Значение не может быть отрицательным";
        }

        if (field === "governor.age") {
          if (num < MIN_AGE || num > MAX_AGE)
            err = "Возраст должен быть от 1 до 99";
        }
      }
    }

    return err;
  };

  const handleRangeChange = (field, bound, value) => {
    const err = validateField(field, value);
    setErrors((prev) => ({ ...prev, [field]: err }));

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

      {/* 🔹 Ошибки запросов */}
      {fetchError && (
        <div className="alert alert-danger mt-3" role="alert">
          {fetchError}
        </div>
      )}
      {deleteError && (
        <div className="alert alert-warning mt-3" role="alert">
          {deleteError}
        </div>
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

                {/* 🔹 Поля фильтров */}
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
                      value={searchValues[col.field]?.min || ""}
                      onChange={(e) => {
                        let val = e.target.value.replace(/[^0-9.]/g, ""); // только цифры и точка
                        const parts = val.split(".");
                        if (parts.length > 2)
                          val = parts[0] + "." + parts.slice(1).join("");
                        const err = validateField(col.field, val);
                        setErrors((prev) => ({ ...prev, [col.field]: err }));
                        handleRangeChange(col.field, "min", val);
                      }}
                    />
                    <input
                      type="text"
                      placeholder="max"
                      style={{ width: "45px" }}
                      value={searchValues[col.field]?.max || ""}
                      onChange={(e) => {
                        let val = e.target.value.replace(/[^0-9.]/g, "");
                        const parts = val.split(".");
                        if (parts.length > 2)
                          val = parts[0] + "." + parts.slice(1).join("");
                        const err = validateField(col.field, val);
                        setErrors((prev) => ({ ...prev, [col.field]: err }));
                        handleRangeChange(col.field, "max", val);
                      }}
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

      {/* 🔹 Пагинация */}
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
