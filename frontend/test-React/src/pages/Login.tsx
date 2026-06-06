import { useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { saveToken } from "../hooks/useAuth";

export default function Login() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const redirect = searchParams.get("redirect") || "/";

  const handleLogin = async () => {
    try {
      const res = await fetch("/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password }),
      });

      if (!res.ok) {
        setError("ユーザー名またはパスワードが違います");
        return;
      }

      const data = await res.json();
      saveToken(data.token);
      navigate(redirect);
    } catch (err) {
      setError(`通信エラー: ${String(err)}`);
    }
  };

  return (
    <div className="home-container">
      <header className="home-header">
        <h1>ログイン</h1>
      </header>

      <div className="input-form">
        <input
          placeholder="ユーザー名"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
        />
        <input
          placeholder="パスワード"
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
      </div>

      {error && <p style={{ color: "red" }}>{error}</p>}

      <div className="button-group">
        <button onClick={handleLogin}>ログイン</button>
      </div>
    </div>
  );
}