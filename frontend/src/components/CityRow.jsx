export default function CityRow({ city, onDelete, onEdit }) {
  return (
    <tr>
      <td>{city.id}</td>
      <td>{city.name}</td>
      <td>{city.population}</td>
      <td>{city.area}</td>
      <td>{city.governor?.age}</td>
      <td>{city.government}</td>
      <td>
        <button onClick={() => onEdit(city.id)}>Изменить</button>
        <button onClick={() => onDelete(city.id)}>Удалить</button>
      </td>
    </tr>
  );
}
