import { useEffect, useState } from "react";
import { searchCities, deleteCity } from "../api";
import { useNavigate } from "react-router-dom";
import Filters from "../components/Filters";
import CityRow from "../components/CityRow";

export default function Home() {
  const [cities, setCities] = useState([]);
  const [filters, setFilters] = useState({});
  const navigate = useNavigate();

  const fetchCities = async () => {
    const requestBody = {
      pagination: { page: 0, size: 100 },
      sort: [],
      filter: filters,
    };

    try {
      const res = await searchCities(requestBody);

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
  }, [filters]);

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

  return (
    <div className="container mt-4">
      <h2>Список городов</h2>
      <Filters filters={filters} setFilters={setFilters} />
      <table className="table table-bordered table-striped mt-3">
        <thead className="table-dark">
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Population</th>
            <th>Area</th>
            <th>Governor Age</th>
            <th>Government</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {cities.length === 0 ? (
            <tr>
              <td colSpan={7} className="text-center">
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
