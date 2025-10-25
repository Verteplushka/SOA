import { useEffect, useState } from "react";
import { searchCities, deleteCity } from "../api/api";
import { useNavigate } from "react-router-dom";
import Filters from "../components/Filters";
import CityRow from "../components/CityRow";

export default function Home() {
  const [cities, setCities] = useState([]);
  const [filters, setFilters] = useState({});
  const navigate = useNavigate();

  const fetchCities = async () => {
    try {
      const res = await searchCities({
        pagination: { page: 0, size: 100 },
        sort: [],
        filter: filters,
      });
      setCities(res?.CityPageResponse?.cities?.city || []);
    } catch (e) {
      console.error(e);
    }
  };

  useEffect(() => {
    fetchCities();
  }, [filters]);

  const handleDelete = async (id) => {
    if (!window.confirm("Удалить этот город?")) return;
    await deleteCity(id);
    fetchCities();
  };

  const handleEdit = (id) => {
    navigate(`/edit/${id}`);
  };

  return (
    <div>
      <Filters filters={filters} setFilters={setFilters} />
      <table border={1} cellPadding={5}>
        <thead>
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
          {cities.map((city) => (
            <CityRow
              key={city.id}
              city={city}
              onDelete={handleDelete}
              onEdit={handleEdit}
            />
          ))}
        </tbody>
      </table>
    </div>
  );
}
