import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { registerUser } from "../hooks/useUserApi";


export default function Register() {
  const [username, setUsername] = useState("");
  const [age, setAge] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  const handleRegister = async () => {
    setIsLoading(true);
    setError("");
    try {
      const res = await registerUser(username, Number(age), password);
      if (res.ok) {
        navigate("/login");
      } else {
        setError(res.message);
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="home-container">
      <header className="home-header">
        <h1>新規登録</h1>
        <p className="safety-note">個人情報を入力しないでください</p>
      </header>

      <div className="input-form">
        <input
         placeholder="名前"
         value={username}
         onChange={(e) => setUsername(e.target.value)}
        />
        <input
         placeholder="年齢"
         value={age}
         onChange={(e) => setAge(e.target.value)}
        />
        <input
         placeholder="パスワード"
         value={password}
         onChange={(e) => setPassword(e.target.value)}
        />

        {error && <p style={{ color: "red" }}>{error}</p>}

        <div className="button-group">
          <button onClick={handleRegister} disabled={isLoading}>
            {isLoading ? "処理中..." : "登録する"}
          </button>
          <button onClick={() => navigate("/login")} disabled={isLoading}>
            ログイン画面へ
          </button>
        </div>
      </div>
    </div>
  );
}