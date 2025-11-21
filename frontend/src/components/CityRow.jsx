import { governmentMap } from "../utils/governmentMap";

export default function CityRow({ city, onDelete, onEdit }) {
  const formatDate = (d) => {
    if (!d) return "";
    if (Array.isArray(d)) {
      const [year, month, day] = d;
      return `${year}-${String(month).padStart(2, "0")}-${String(day).padStart(
        2,
        "0"
      )}`;
    }
    if (typeof d === "string") return d.slice(0, 10);
    return "";
  };

  return (
    <tr>
      <td>{city.id}</td>
      <td>{city.name}</td>
      <td>{city.coordinates?.x}</td>
      <td>{city.coordinates?.y}</td>
      <td>{city.area}</td>
      <td>{city.population}</td>
      <td>{city.metersAboveSeaLevel}</td>
      <td>
        {city.establishmentDate ? formatDate(city.establishmentDate) : ""}
      </td>
      <td>{city.populationDensity}</td>
      <td>{governmentMap[city.government] || city.government}</td>
      <td>{city.governor?.age}</td>
      <td>
        <button
          className="btn btn-sm btn-primary me-1"
          onClick={() => onEdit(city)}
        >
          Изменить
        </button>
        <button
          className="btn btn-sm btn-danger"
          onClick={() => onDelete(city)}
        >
          Удалить
        </button>
      </td>
    </tr>
  );
}
