import { BrowserRouter, Routes, Route } from "react-router-dom";
import Home from "./Home";
import Workout from "./Workout";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/workout" element={<Workout />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;