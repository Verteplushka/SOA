import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Header from "./components/Header";
import Home from "./pages/Home";
import CreateCity from "./pages/CreateCity";
import EditCity from "./pages/EditCity";
import Genocide from "./components/Genocide";
import SpecialQueries from "./components/SpecialQueries";

export default function App() {
  return (
    <Router>
      <Header />
      <main style={{ padding: "1rem" }}>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/create" element={<CreateCity />} />
          <Route path="/edit/:id" element={<EditCity />} />
          <Route path="/genocide" element={<Genocide />} />
          <Route path="/special" element={<SpecialQueries />} />
        </Routes>
      </main>
    </Router>
  );
}
