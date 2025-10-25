import React, { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { addCity, getCity, updateCity } from "../api/api-service1";
import { XMLParser } from "fast-xml-parser";

const governmentOptions = ["DIARCHY", "KRITARCHY", "REPUBLIC"];
const parser = new XMLParser({ ignoreAttributes: false });

function CityForm() {
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

  const [error, setError] = useState("");

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

    if (typeof value === "string") {
      return value.slice(0, 10);
    }

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
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");

    const formatLocalDateTime = (d) => {
      if (!d) return null;

      let year, month, day;

      if (typeof d === "string") {
        // d = "YYYY-MM-DD"
        [year, month, day] = d.split("-");
      } else if (d instanceof Date) {
        year = d.getFullYear();
        month = String(d.getMonth() + 1).padStart(2, "0");
        day = String(d.getDate()).padStart(2, "0");
      } else {
        return null; // неизвестный формат
      }

      if (!year || !month || !day) return null;
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
      if (id) {
        await updateCity(id, cityToSend);
      } else {
        await addCity(cityToSend);
      }
      navigate("/");
    } catch (err) {
      if (err.response && err.response.data) {
        try {
          const xmlData = parser.parse(err.response.data);
          const message = xmlData.error?.message || "Неизвестная ошибка";
          setError(message);
        } catch (parseErr) {
          setError("Ошибка при разборе ответа сервера");
        }
      } else {
        setError(err.message || "Ошибка при сохранении города");
      }
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
          <div className="col-md-6">
            <label className="form-label">Название города</label>
            <input
              type="text"
              className="form-control"
              name="name"
              value={city.name}
              onChange={handleChange}
              required
            />
          </div>

          <div className="col-md-3">
            <label className="form-label">Координата X</label>
            <input
              type="number"
              className="form-control"
              name="coordinates.x"
              value={city.coordinates.x}
              onChange={handleChange}
              required
            />
          </div>

          <div className="col-md-3">
            <label className="form-label">Координата Y</label>
            <input
              type="number"
              step="0.01"
              className="form-control"
              name="coordinates.y"
              value={city.coordinates.y}
              onChange={handleChange}
              required
            />
          </div>

          <div className="col-md-4">
            <label className="form-label">Площадь</label>
            <input
              type="number"
              className="form-control"
              name="area"
              value={city.area}
              onChange={handleChange}
              required
            />
          </div>

          <div className="col-md-4">
            <label className="form-label">Население</label>
            <input
              type="number"
              className="form-control"
              name="population"
              value={city.population}
              onChange={handleChange}
              required
            />
          </div>

          <div className="col-md-4">
            <label className="form-label">Высота над уровнем моря</label>
            <input
              type="number"
              className="form-control"
              name="metersAboveSeaLevel"
              value={city.metersAboveSeaLevel}
              onChange={handleChange}
            />
          </div>

          <div className="col-md-4">
            <label className="form-label">Дата основания</label>
            <input
              type="date"
              className="form-control"
              name="establishmentDate"
              value={city.establishmentDate}
              onChange={handleChange}
            />
          </div>

          <div className="col-md-4">
            <label className="form-label">Плотность населения</label>
            <input
              type="number"
              step="0.01"
              className="form-control"
              name="populationDensity"
              value={city.populationDensity}
              onChange={handleChange}
            />
          </div>

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

          <div className="col-md-4">
            <label className="form-label">Возраст губернатора</label>
            <input
              type="number"
              className="form-control"
              name="governor.age"
              value={city.governor.age}
              onChange={handleChange}
              required
            />
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
