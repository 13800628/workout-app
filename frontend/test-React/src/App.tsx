import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Home from "./pages/Home";
import Workout from "./pages/Workout";
import Login from "./pages/Login";
import { isLoggedIn } from "./hooks/useAuth";

function PrivateRoute({ children }: { children: React.ReactNode }) {
  return isLoggedIn() ? <>{children}</> : <Navigate to="/login" />;
}

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/" element={<Home />} />  {/* 認証不要 */}
        <Route path="/workout" element={
          <PrivateRoute>
            <Workout />
          </PrivateRoute>
        } />
      </Routes>
    </BrowserRouter>
  );
}


export default App;