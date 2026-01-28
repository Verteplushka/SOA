import CityForm from "../components/CityForm";
import { useLocation } from "react-router-dom";

export default function EditCity() {
  const { state } = useLocation();
  const { city } = state;

  return <CityForm existingCity={city} />;
}
