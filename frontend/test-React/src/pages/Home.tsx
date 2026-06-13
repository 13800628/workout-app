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
  changePassword,
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
  onOpenModal: () => void;
  isLoading: boolean;
};

function ActionButtons({
  onRegister,
  onGetAll,
  onGetById,
  onUpdate,
  onDelete,
  onGoToWorkout,
  onOpenModal,
  isLoading,
}: ActionButtonProps) {
  return (
    <div className="button-group">
      <button onClick={onRegister} disabled={isLoading}>
        {isLoading ? "処理中..." : "登録"}
      </button>
      <button onClick={onGetAll} disabled={isLoading}>
        {isLoading ? "処理中..." : "全部取得"}
      </button>
      <button onClick={onGetById} disabled={isLoading}>
        {isLoading ? "処理中..." : "ID 取得"}
      </button>
      <button onClick={onUpdate} disabled={isLoading}>
        {isLoading ? "処理中" : "更新"}
      </button>
      <button onClick={onDelete} disabled={isLoading}>
        {isLoading ? "処理中..." : "削除"}
      </button>
      <button onClick={onOpenModal} disabled={isLoading}>
         {isLoading ? "処理中..." : "パスワード変更"}
      </button>
      <button onClick={onGoToWorkout} disabled={isLoading}>
        Workoutページへ
      </button>
    </div>
  );
}

// ここのresultが表示される文字が小さいので今後少し改善
function ResultPanel({ result }: { result: string }) {
  return (
    <div className="result-section">
      <h3>ユーザー情報</h3>
      <pre style={{ whiteSpace: "pre-wrap"}}>{result}</pre>
    </div>
  )
}

// モーダルコンポーネント
// エラーメッセージが表示されなかったのでモーダル内で管理する。今後は責務などの問題で切り出す余地ありか。
type PasswordModalProps = {
  isOpen: boolean;
  oldPassword: string;
  newPassword: string;
  error: string;
  onChangeOld: (v: string) => void;
  onChangeNew: (v: string) => void;
  onSubmit: () => void;
  onClose: () => void;
}; 

function PasswordModal({
  isOpen,
  oldPassword,
  newPassword,
  error,
  onChangeOld,
  onChangeNew,
  onSubmit,
  onClose,
} : PasswordModalProps) {
  if (!isOpen) return null;

  return (
    <div style={{
      position: "fixed",
      top: 0, left: 0, right: 0, bottom: 0,
      backgroundColor: "rgba(0,0,0,0.5)",
      display: "flex",
      alignItems: "center",
      justifyContent: "center",
      zIndex: 1000,
    }}>
      <div style={{
        background: "white",
        borderRadius: 12,
        padding: 30,
        width: 320,
      }}>
        <h3>パスワード変更</h3>
        {error && <p style={{ color: "red" }}>{error}</p>}
        <input
          placeholder="現在のパスワード"
          type="password"
          value={oldPassword}
          onChange={(e) => onChangeOld(e.target.value)}
          style={{ width: "100%", marginBottom: 10, padding: 8 }}
        />
        <input
          placeholder="新しいパスワード"
          type="password"
          value={newPassword}
          onChange={(e) => onChangeNew(e.target.value)}
          style={{ width: "100%", marginBottom: 10, padding: 8 }}
        />
        <div style={{ display: "flex", gap: 10 }}>
          <button onClick={onSubmit}>変更する</button>
          <button onClick={onClose}>キャンセル</button>
        </div>
      </div>
    </div>
  );
}

// メインコンポーネント

function Home() {
  const [username, setUsername] = useState("");
  const [age, setAge] = useState("");
  const [userId, setUserId] = useState("");
  const [result, setResult] = useState("");
  const [password, setPassword] = useState("");
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [oldPassword, setOldPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [modalError, setModalError] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  const navigate = useNavigate();
  const userIdNum = Number(userId);
  
  // =========================================================================
  // CRUD 操作 各種


  // 登録処理
  const handleRegister = async () => {
    setIsLoading(true);
    try {
      const res = await registerUser(username, Number(age), password);
      setResult(res.ok ? formatUser(res.data as User) : res.message);
    } finally {
      setIsLoading(false);
    } 
  };
  


  const handleGetAll = async () => {
    setIsLoading(true);
    try {
      const res = await fetchAllUsers();
      setResult(res.ok ? formatUsers(res.data as User[]) : res.message);
    } finally {
      setIsLoading(false);
    }
  };

  const handleGetById = async () => {
    if (!userIdNum) { setResult("ユーザーIDを入力してください"); return; }
    const res = await fetchUserById(userIdNum);
    setResult(res.ok ? formatUser(res.data as User): res.message);
  };

  const handleUpdate = async () => {
    if (!userIdNum) { setResult("ユーザーIDを入力してください"); return; }
    setIsLoading(true);
    try {
      const res = await updateUser(userIdNum, username, Number(age));
      setResult(res.ok ? formatUser(res.data as User): res.message);
    } finally {
      setIsLoading(false);
    }
  };

  const handleDelete = async () => {
    if (!userIdNum) { setResult("ユーザーIDを入力してください"); return; }
    setIsLoading(true);
    try {
      const res = await deleteUser(userIdNum);
      setResult(res.ok ? "削除完了": res.message);
    } finally {
      setIsLoading(false);
    }
  };

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
  };

  // パスワードの再設定関数
  const handleChangePassword = async () => {
    if (!userIdNum) { setResult("ユーザーIDを入力してください"); return; }
    const res = await changePassword(userIdNum, oldPassword, newPassword);
    setIsLoading(true);
    try {
      if (res.ok) {
      setResult("パスワードを変更しました");
      setIsModalOpen(false);
      setOldPassword("");
      setNewPassword("");
     } else {
      setModalError(res.message);
     }
    } finally {
      setIsLoading(false);
    }
  };




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
        onOpenModal={() => setIsModalOpen(true)}
        isLoading={isLoading}
      />

      <PasswordModal
        isOpen={isModalOpen}
        oldPassword={oldPassword}
        newPassword={newPassword}
        error={modalError}
        onChangeOld={setOldPassword}
        onChangeNew={setNewPassword}
        onSubmit={handleChangePassword}
        onClose={() => {
          setIsModalOpen(false);
          setModalError("");
        }}
      />

      <ResultPanel result={result} />
     </div>
  );
}
export default Home;