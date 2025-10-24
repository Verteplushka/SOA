import { Link } from "react-router-dom";

export default function Header() {
  return (
    <header>
      <h1>City Management</h1>
      <nav>
        <Link to="/">Главная</Link> |<Link to="/create">Создать город</Link> |
        <Link to="/genocide">Геноцид</Link> |
        <Link to="/special">Прочие запросы</Link>
      </nav>
      <hr />
    </header>
  );
}
