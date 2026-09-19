/**
 * id から別ページに飛んで、Workout操作
 * id 入力ボタン押すと、該当 id の Workout 一覧が表示される
 * idでif分岐で、trueならfetchして表示、falseなら何もしない
 * fetchのURLはuserとは別のものになる
 * buttonの実装(handleFetchByIdなど)も同様に追加
 * 
 */

import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  updateMe,
  deleteMe,
  fetchMe,
  changeMyPassword,
} from "../hooks/useUserApi";
import type { User } from "../hooks/useUserApi";
import { formatUser } from "../utils/formatUser";

// 子コンポーネント

type ProfileFormProps = {
  username: string;
  age: string;
  onChangeUsername: (v: string) => void;
  onChangeAge: (v: string) => void;
};

function ProfileForm({ username, age, onChangeUsername, onChangeAge 
}: ProfileFormProps) {
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
    </div>
  );
}

type ProfileActionProps = {
  onUpdate: () => void;
  onDelete: () => void;
  onGoToWorkout: () => void;
  isLoading: boolean;
  canGoToWorkout: boolean;
};

function ProfileActions({ onUpdate, onDelete, onGoToWorkout, isLoading, canGoToWorkout }: ProfileActionProps) {
  return (
    <div className="button-group">
      <button onClick={onUpdate} disabled={isLoading}>
        {isLoading ? "処理中..." : "更新"}
      </button>
      <button onClick={onDelete} disabled={isLoading}>
        {isLoading ? "処理中" : "削除"}
      </button>
      <button onClick={onGoToWorkout} disabled={isLoading || !canGoToWorkout}>
        Workoutページへ
      </button>
    </div>
  );
}


type PasswordFormProps = {
  oldPassword: string;
  newPassword: string;
  onChangeOld: (v: string) => void;
  onChangeNew: (v: string) => void;
  onSubmit: () => void;
  isLoading: boolean;
};

function PasswordForm({ oldPassword, newPassword, onChangeOld, onChangeNew, onSubmit, isLoading }: PasswordFormProps) {
  return (
    <div className="input-form">
      <input
       placeholder="現在のパスワード"
       type="password"
       value={oldPassword}
       onChange={(e) => onChangeOld(e.target.value)}
      />
      <input
       placeholder="新しいパスワード"
       type="password"
       value={newPassword}
       onChange={(e) => onChangeNew(e.target.value)}
      />
      <div className="button-group">
        <button onClick={onSubmit} disabled={isLoading}>
          {isLoading ? "処理中..." : "変更する"}
        </button>
      </div>
    </div>
  );
}

type SelectionProps = {
  page: number;
  onPrev: () => void;
  onNext: () => void;
};

function SectionPager({ page, onPrev, onNext }: SelectionProps) {
  return (
    <div className="pagination-controls">
      <button onClick={onPrev} disabled={page === 0}>
        前へ
      </button>
      <span className="pagination-range">
        {page === 0 ? "プロフィール" : "パスワード変更"}
      </span>
      <button onClick={onNext} disabled={page === 1}>
        次へ
      </button>
    </div>
  );
}

// メインコンポーネント

function Home() {
  const [page, setPage] = useState(0);
  const [myId, setMyId] = useState<number | null>(null);
  const [username, setUsername] = useState("");
  const [age, setAge] = useState("");
  const [result, setResult] = useState("");
  const [oldPassword, setOldPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [passwordMessage, setPasswordMessage] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  const navigate = useNavigate();
  
 
  useEffect(() => {
    const loadMe = async () => {
      setIsLoading(true);
      try {
        const res = await fetchMe(navigate);
        if (res.ok) {
          const user = res.data as User;
          setMyId(user.id);
          setUsername(user.username);
          setAge(String(user.age));
          setResult(formatUser(user));
        } else {
          setResult(res.message);
        }
      } finally {
        setIsLoading(false);
      }
    };
    loadMe();
  }, []);


  const handleUpdate = async () => {
    setIsLoading(true);
    try {
      const res = await updateMe(username, Number(age), navigate);
      if (res.ok) {
        setResult(formatUser(res.data));
      } else {
        setResult(res.message);
      }
    } finally {
      setIsLoading(false);
    }
  };

  const handleDelete = async () => {
    if (!window.confirm("本当にアカウントを削除しますか？")) return;
    setIsLoading(true);
    try {
      const res = await deleteMe(navigate);
      if (res.ok) {
        navigate("/login")
      } else {
        setResult(res.message);
      }
    } finally {
      setIsLoading(false);
    }
  };

  // 自分のidを使って自動でWorkoutページへ
  const handleGoToWorkoutPage = () => {
    if (myId === null) return;
    navigate(`/workout?id=${myId}`);
  };

  // パスワードの再設定関数
  const handleChangePassword = async () => {
    setIsLoading(true);
    try {
      const res = await changeMyPassword(oldPassword, newPassword, navigate);
      if (res.ok) {
      setResult("パスワードを変更しました");
      setOldPassword("");
      setNewPassword("");
     } else {
      setPasswordMessage(res.message);
     }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="home-container">
      <header className="home-header">
        <h1>マイページ</h1>
      </header>

      <SectionPager
       page={page}
       onPrev={() => setPage(0)}
       onNext={() => setPage(1)}
      />

      {page === 0 && (
        <>
        <ProfileForm
         username={username}
         age={age}
         onChangeUsername={setUsername}
         onChangeAge={setAge}
        />
        <ProfileActions
         onUpdate={handleUpdate}
         onDelete={handleDelete}
         onGoToWorkout={handleGoToWorkoutPage}
         isLoading={isLoading}
         canGoToWorkout={myId !== null}
        />
        <div className="result-section">
          <h3>ユーザー情報</h3>
          <pre style={{ whiteSpace: "pre-wrap" }}>{result}</pre>
        </div>
        </>
      )}
      
      {page === 1 && (
        <>
         <PasswordForm
          oldPassword={oldPassword}
          newPassword={newPassword}
          onChangeOld={setOldPassword}
          onChangeNew={setNewPassword}
          onSubmit={handleChangePassword}
          isLoading={isLoading}
        />
        {passwordMessage && <p>{passwordMessage}</p>}
       </>
      )}
    </div>
  );
}
export default Home;