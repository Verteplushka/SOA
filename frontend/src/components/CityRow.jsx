import { governmentMap } from "../utils/government-localizator";
import { formatDate } from "../utils/date-localizator";

export default function CityRow({ city, onDelete, onEdit }) {
  return (
    <tr>
      <td>{city.id}</td>
      <td>{city.name}</td>
      <td>{city.coordinates?.x}</td>
      <td>{city.coordinates?.y}</td>
      <td>{city.area}</td>
      <td>{city.population}</td>
      <td>{city.metersAboveSeaLevel}</td>
      <td>{formatDate(city.establishmentDate)}</td>
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
