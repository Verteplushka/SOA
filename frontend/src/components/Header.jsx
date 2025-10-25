import { Link } from "react-router-dom";

export default function Header() {
  return (
    <header className="bg-light p-3 mb-4 shadow-sm">
      <div className="container d-flex flex-column flex-md-row align-items-center justify-content-between">
        <h1 className="h3 mb-2 mb-md-0">City Management</h1>
        <nav className="nav">
          <Link className="nav-link px-2 text-dark" to="/">
            Главная
          </Link>
          <Link className="nav-link px-2 text-dark" to="/create">
            Создать город
          </Link>
          <Link className="nav-link px-2 text-dark" to="/genocide">
            Геноцид
          </Link>
          <Link className="nav-link px-2 text-dark" to="/special">
            Прочие запросы
          </Link>
        </nav>
      </div>
      <hr className="my-3" />
    </header>
  );
}
