import React, { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { addCity, getCity, toXml } from "../api/api-service1";
import { XMLParser } from "fast-xml-parser";

const governmentOptions = ["DIARCHY", "KRITARCHY", "REPUBLIC"];
const parser = new XMLParser({ ignoreAttributes: false });

const MAX_DATE = "2025-11-08";
const MAX_INT_LENGTH = 9;
const MAX_DOUBLE_LENGTH = 15;
const MAX_NAME_LENGTH = 100;

function CityForm({ existingCity }) {
  const { id } = useParams();
  const navigate = useNavigate();

  const [city, setCity] = useState({
    name: "",
    coordinates: { x: "0", y: "0" },
    area: "0",
    population: "0",
    metersAboveSeaLevel: "0",
    establishmentDate: "",
    populationDensity: "0",
    government: governmentOptions[0],
    governor: { age: "0" },
  });

  const editUrl = existingCity?._links?.update;

  const [error, setError] = useState("");
  const [validationErrors, setValidationErrors] = useState({});

  const parseEstablishmentDate = (value) => {
    if (!value) return "";
    if (Array.isArray(value)) {
      const [year, month, day] = value;
      if (year && month && day) {
        const mm = String(month).padStart(2, "0");
        const dd = String(day).padStart(2, "0");
        return `${year}-${mm}-${dd}`;
      }
    }
    if (typeof value === "string") return value.slice(0, 10);
    return "";
  };

  useEffect(() => {
    if (id) {
      getCity(id)
        .then((res) => {
          const data = res.City;
          setCity({
            name: data.name,
            coordinates: {
              x: data.coordinates.x,
              y: data.coordinates.y,
            },
            area: data.area,
            population: data.population,
            metersAboveSeaLevel: data.metersAboveSeaLevel || 0,
            establishmentDate: parseEstablishmentDate(data.establishmentDate),
            populationDensity: data.populationDensity || 0,
            government: data.government,
            governor: { age: data.governor.age || 0 },
          });
        })
        .catch(() => setError("Не удалось загрузить данные города"));
    }
  }, [id]);

  const validateField = (name, value) => {
    let err = "";

    const isInt = (val) => /^-?\d+$/.test(val);
    const isFloat = (val) => /^-?\d+(\.\d+)?$/.test(val);

    switch (name) {
      case "name":
        if (!value.trim()) err = "Название не может быть пустым";
        else if (value.length > MAX_NAME_LENGTH)
          err = `Максимальная длина — ${MAX_NAME_LENGTH} символов`;
        break;

      case "coordinates.x":
      case "coordinates.y":
        if (!isFloat(value)) err = "Введите корректное число";
        else if (value.length > MAX_DOUBLE_LENGTH)
          err = `Максимум ${MAX_DOUBLE_LENGTH} символов`;
        break;

      case "area":
        if (!isInt(value)) err = "Введите целое число";
        else if (value.length > MAX_INT_LENGTH)
          err = `Максимум ${MAX_INT_LENGTH} цифр`;
        else if (Number(value) < 0) err = "Площадь не может быть отрицательной";
        break;

      case "population":
        if (!isInt(value)) err = "Введите целое число";
        else if (value.length > MAX_INT_LENGTH)
          err = `Максимум ${MAX_INT_LENGTH} цифр`;
        else if (Number(value) < 0)
          err = "Население не может быть отрицательным";
        break;

      case "metersAboveSeaLevel":
        if (!isInt(value)) err = "Введите целое число";
        else if (value.length > MAX_INT_LENGTH)
          err = `Максимум ${MAX_INT_LENGTH} цифр`;
        break;

      case "populationDensity":
        if (!isFloat(value)) err = "Введите корректное число";
        else if (value.length > 10) err = "Максимум 10 символов";
        else if (Number(value) < 0)
          err = "Плотность населения не может быть отрицательной";
        break;

      case "governor.age":
        if (!isInt(value)) err = "Введите целое число";
        else if (Number(value) < 18 || Number(value) >= 99)
          err = "Возраст должен быть от 18 до 99";
        break;

      case "establishmentDate":
        if (value && value > MAX_DATE)
          err = "Дата основания не может быть в будущем";
        break;

      default:
        break;
    }

    setValidationErrors((prev) => ({ ...prev, [name]: err }));
    return err === "";
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    if (name.includes("coordinates.")) {
      const key = name.split(".")[1];
      setCity((prev) => ({
        ...prev,
        coordinates: { ...prev.coordinates, [key]: value },
      }));
    } else if (name.includes("governor.")) {
      const key = name.split(".")[1];
      setCity((prev) => ({
        ...prev,
        governor: { ...prev.governor, [key]: value },
      }));
    } else {
      setCity((prev) => ({ ...prev, [name]: value }));
    }
    validateField(name, value);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");

    // Проверяем все поля
    const fields = [
      "name",
      "coordinates.x",
      "coordinates.y",
      "area",
      "population",
      "metersAboveSeaLevel",
      "populationDensity",
      "governor.age",
      "establishmentDate",
    ];
    const allValid = fields.every((f) =>
      validateField(f, f.includes(".") ? eval(`city.${f}`) : city[f])
    );

    if (!allValid) {
      setError("Исправьте ошибки перед сохранением");
      return;
    }

    const formatLocalDateTime = (d) => {
      if (!d) return null;
      const [year, month, day] = d.split("-");
      return `${year}-${month}-${day}T00:00:00`;
    };

    const cityToSend = {
      ...city,
      coordinates: {
        x: Number(city.coordinates.x),
        y: Number(city.coordinates.y),
      },
      area: Number(city.area),
      population: Number(city.population),
      metersAboveSeaLevel: Number(city.metersAboveSeaLevel),
      populationDensity: Number(city.populationDensity),
      governor: { age: Number(city.governor.age) },
      establishmentDate: formatLocalDateTime(city.establishmentDate),
    };

    try {
      if (editUrl) {
        await fetch(editUrl, {
          method: "PUT",
          headers: { "Content-Type": "application/xml" },
          body: toXml(cityToSend, "CityInput"),
        });
      } else {
        await addCity(cityToSend);
      }
      navigate("/");
    } catch (err) {
      setError("Ошибка при сохранении города");
      console.log(err);
    }
  };

  return (
    <div className="container mt-4">
      <h2 className="mb-4">{id ? "Редактировать город" : "Создать город"}</h2>

      {error && (
        <div className="alert alert-danger" role="alert">
          {error}
        </div>
      )}

      <form onSubmit={handleSubmit}>
        <div className="row g-3">
          {/* Название */}
          <div className="col-md-6">
            <label className="form-label">Название города</label>
            <input
              type="text"
              className={`form-control ${
                validationErrors.name ? "is-invalid" : ""
              }`}
              name="name"
              value={city.name}
              onChange={handleChange}
              required
            />
            {validationErrors.name && (
              <div className="invalid-feedback">{validationErrors.name}</div>
            )}
          </div>

          {/* Координаты */}
          {["x", "y"].map((axis) => (
            <div className="col-md-3" key={axis}>
              <label className="form-label">
                Координата {axis.toUpperCase()}
              </label>
              <input
                type="text"
                className={`form-control ${
                  validationErrors[`coordinates.${axis}`] ? "is-invalid" : ""
                }`}
                name={`coordinates.${axis}`}
                value={city.coordinates[axis]}
                onChange={handleChange}
                required
              />
              {validationErrors[`coordinates.${axis}`] && (
                <div className="invalid-feedback">
                  {validationErrors[`coordinates.${axis}`]}
                </div>
              )}
            </div>
          ))}

          {/* Числовые поля */}
          {[
            { label: "Площадь", name: "area" },
            { label: "Население", name: "population" },
            { label: "Высота над уровнем моря", name: "metersAboveSeaLevel" },
            { label: "Плотность населения", name: "populationDensity" },
          ].map((f) => (
            <div className="col-md-4" key={f.name}>
              <label className="form-label">{f.label}</label>
              <input
                type="text"
                className={`form-control ${
                  validationErrors[f.name] ? "is-invalid" : ""
                }`}
                name={f.name}
                value={city[f.name]}
                onChange={handleChange}
              />
              {validationErrors[f.name] && (
                <div className="invalid-feedback">
                  {validationErrors[f.name]}
                </div>
              )}
            </div>
          ))}

          {/* Дата основания */}
          <div className="col-md-4">
            <label className="form-label">Дата основания</label>
            <input
              type="date"
              className={`form-control ${
                validationErrors.establishmentDate ? "is-invalid" : ""
              }`}
              name="establishmentDate"
              value={city.establishmentDate}
              onChange={handleChange}
              max={MAX_DATE}
            />
            {validationErrors.establishmentDate && (
              <div className="invalid-feedback">
                {validationErrors.establishmentDate}
              </div>
            )}
          </div>

          {/* Форма правления */}
          <div className="col-md-4">
            <label className="form-label">Форма правления</label>
            <select
              className="form-select"
              name="government"
              value={city.government}
              onChange={handleChange}
              required
            >
              {governmentOptions.map((gov) => (
                <option key={gov} value={gov}>
                  {gov}
                </option>
              ))}
            </select>
          </div>

          {/* Возраст губернатора */}
          <div className="col-md-4">
            <label className="form-label">Возраст губернатора</label>
            <input
              type="text"
              className={`form-control ${
                validationErrors["governor.age"] ? "is-invalid" : ""
              }`}
              name="governor.age"
              value={city.governor.age}
              onChange={handleChange}
              required
            />
            {validationErrors["governor.age"] && (
              <div className="invalid-feedback">
                {validationErrors["governor.age"]}
              </div>
            )}
          </div>
        </div>

        <div className="mt-4">
          <button type="submit" className="btn btn-primary me-2">
            {id ? "Сохранить" : "Создать"}
          </button>
          <button
            type="button"
            className="btn btn-secondary"
            onClick={() => navigate("/")}
          >
            Отмена
          </button>
        </div>
      </form>
    </div>
  );
}

export default CityForm;
