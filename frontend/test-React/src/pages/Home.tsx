/**
 * id から別ページに飛んで、Workout操作
 * id 入力ボタン押すと、該当 id の Workout 一覧が表示される
 * idでif分岐で、trueならfetchして表示、falseなら何もしない
 * fetchのURLはuserとは別のものになる
 * buttonの実装(handleFetchByIdなど)も同様に追加
 * 
 */

import { useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  fetchAllUsers,
  fetchUserById,
  registerUser,
  updateUser,
  deleteUser,
} from "../hooks/useUserApi";
import type { User } from "../hooks/useUserApi";
import { formatUser, formatUsers } from "../utils/formatUser";
import { isLoggedIn } from "../hooks/useAuth";

// 子コンポーネント

type UserFormProps = {
  username: string;
  age: string;
  userId: string;
  password: string;
  onChangeUsername: (v: string) => void;
  onChangeAge: (v: string) => void;
  onChangeUserId: (v: string) => void;
  onChangePassword: (v: string) => void;
};

function UserForm({
  username,
  age,
  userId,
  password,
  onChangeUsername,
  onChangeAge,
  onChangeUserId,
  onChangePassword,
}: UserFormProps) {
  return (
    <div className="input-form">
      <input
      placeholder="名前"
      value={username}
      onChange={(e) => onChangeUsername(e.target.value)}
      />
      <input
        placeholder="年齢"
        type="number"
        value={age}
        onChange={(e) => onChangeAge(e.target.value)}
      />
      <input
        placeholder="対象ユーザーID"
        type="number"
        value={userId}
        onChange={(e) => onChangeUserId(e.target.value)}
      />
      <input
        placeholder="パスワード"
        type="string"
        value={password}
        onChange={(e) => onChangePassword(e.target.value)}
      />
    </div>
  );
}

type ActionButtonProps = {
  onRegister: () => void;
  onGetAll: () => void;
  onGetById: () => void;
  onUpdate: () => void;
  onDelete: () => void;
  onGoToWorkout: () => void;
};

function ActionButtons({
  onRegister,
  onGetAll,
  onGetById,
  onUpdate,
  onDelete,
  onGoToWorkout,
}: ActionButtonProps) {
  return (
    <div className="button-group">
      <button onClick={onRegister}>登録</button>
      <button onClick={onGetAll}>全部取得</button>
      <button onClick={onGetById}>ID 取得</button>
      <button onClick={onUpdate}>更新</button>
      <button onClick={onDelete}>削除</button>
      <button onClick={onGoToWorkout}>Workoutページへ
      </button>
    </div>
  );
}

function ResultPanel({ result }: { result: string }) {
  return (
    <div className="result-section">
      <h3>ユーザー情報</h3>
      <pre style={{ whiteSpace: "pre-wrap"}}>{result}</pre>
    </div>
  )
}

// メインコンポーネント

function Home() {
  const [username, setUsername] = useState("");
  const [age, setAge] = useState("");
  const [userId, setUserId] = useState("");
  const [result, setResult] = useState("");
  const [password, setPassword] = useState("");

  const navigate = useNavigate();
  const userIdNum = Number(userId);
  
  // =========================================================================
  // CRUD 操作 各種


  // 登録処理
  const handleRegister = async () => {
    const res = await registerUser(username, Number(age), password);
    setResult(res.ok ? formatUser(res.data as User) : res.message); 
  };
  


  const handleGetAll = async () => {
    const res = await fetchAllUsers();
    setResult(res.ok ? formatUsers(res.data as User[]) : res.message);
  };

  const handleGetById = async () => {
    if (!userIdNum) { setResult("ユーザーIDを入力してください"); return; }
    const res = await fetchUserById(userIdNum);
    setResult(res.ok ? formatUser(res.data as User): res.message);
  };

  const handleUpdate = async () => {
    if (!userIdNum) { setResult("ユーザーIDを入力してください"); return; }
    const res = await updateUser(userIdNum, username, Number(age));
    setResult(res.ok ? formatUser(res.data as User): res.message);
  };

  const handleDelete = async () => {
    if (!userIdNum) { setResult("ユーザーIDを入力してください"); return; }
    const res = await deleteUser(userIdNum);
    setResult(res.ok ? "削除完了": res.message);
  }

  // 別クラスで作成したので、代わりに別ページに飛ばす実装に変更
  const handleGoToWorkoutPage = () => {
    if (!userId) {
      alert("ユーザーIDを入力してください");
      return;
    }
    console.log("isLoggedIn:", isLoggedIn());
    if (!isLoggedIn()) {
      navigate(`/login?redirect=/workout?id=${userId}`);
      return;
    }
    navigate(`/workout?id=${userId}`);
  }



  return (
    <div className="home-container">
      <header className="home-header">
        <h1>ユーザー管理</h1>
        <p className="safety-note">個人情報は入力しないでください</p>
      </header>

      <UserForm
      username={username}
      age={age}
      userId={userId}
      password={password}
      onChangeUsername={setUsername}
      onChangeAge={setAge}
      onChangeUserId={setUserId}
      onChangePassword={setPassword}
      />

      <h3 className="section-title">操作</h3>
      <ActionButtons
        onRegister={handleRegister}
        onGetAll={handleGetAll}
        onGetById={handleGetById}
        onUpdate={handleUpdate}
        onDelete={handleDelete}
        onGoToWorkout={handleGoToWorkoutPage}
        />

      <ResultPanel result={result} />
     </div>
  );
}
export default Home;