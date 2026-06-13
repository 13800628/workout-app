import { useState } from "react";
import { useSearchParams } from "react-router-dom";
import { useNavigate } from "react-router-dom";
import {
  fetchWorkoutByUserId,
  createWorkout,
  updateWorkout,
  deleteWorkout,
} from "../hooks/useWorkoutApi";
import type { Workout } from "../hooks/useWorkoutApi";
import { removeToken } from "../hooks/useAuth";

// 子コンポーネント
type WorkoutFormProps = {
  formData : {
    name: string;
    reps: string;
    sets: string;
    weights: string;
  };
  onChange: (field: string, value: string) => void;
  onRegister: () => void;
  onGetAll: () => void;
  isLoading: boolean;
};

function WorkoutForm({ formData, onChange, onRegister, onGetAll, isLoading }: WorkoutFormProps) {
  return (
    <div>
      <input
        placeholder="種目名"
        type="text"
        value={formData.name}
        onChange={(e) => onChange("name", e.target.value)}
      />
      <input
        placeholder="回数"
        type="text"
        value={formData.reps}
        onChange={(e) => onChange("reps", e.target.value)}
      />
      <input
        placeholder="セット数"
        type="text"
        value={formData.sets}
        onChange={(e) => onChange("sets", e.target.value)}
      />
      <input
        placeholder="重さ"
        type="text"
        value={formData.weights}
        onChange={(e) => onChange("weights", e.target.value)}
      />
      <div className="button-group">
        <button onClick={onRegister} disabled={isLoading}>
          {isLoading ? "処理中..." : "登録"}
        </button>
        <button onClick={onGetAll} disabled={isLoading}>
          {isLoading ? "処理中..." : "全件取得"}
        </button>
      </div>
    </div>
  )
}

type WorkoutItemProps = {
  item: Workout;
  isEditing: boolean;
  onEditStart: (item: Workout) => void;
  onEditSubmit: (id: number) => void;
  onDelete: (id: number) => void;
  isLoading: boolean;
}

function WorkoutItem({ item, isEditing, onEditStart, onEditSubmit, onDelete, isLoading }: WorkoutItemProps) {
  return (
    <div style={{ borderBottom: "1px solid #ddd", marginBottom: "10px"}}>
      {isEditing ? (
        <button onClick={() => onEditSubmit(item.id)} disabled={isLoading}>
          {isLoading ? "処理中..." : "編集を実行"}
        </button>
      ) : (
        <button onClick={() => onEditStart(item)} disabled={isLoading}>
          {isLoading ? "処理中..." : "編集する"}
        </button>
      )}
      <p>ID: {item.id}</p>
      <p>種目名: {item.name}</p>
      <p>回数: {item.reps}</p>
      <p>セット数: {item.sets}</p>
      <p>重さ: {item.weights}</p>
      <button onClick={() => onDelete(item.id)} disabled={isLoading}>
        {isLoading ? "処理中..." : "削除"}
      </button>
    </div>
  );
}

// メインコンポーネント

export default function Workout() {
  const [workouts, setWorkouts] = useState<Workout[]>([]);
  const [error, setError] = useState("");
  const [targetId, setTargetId] = useState<number | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  const [formData, setFormData] = useState({
    name: "",
    reps: "",
    sets: "",
    weights: "",
  });

  const [searchParams] = useSearchParams();
  const userId = Number(searchParams.get("id"));
  const navigate = useNavigate();

    
    // createWorkoutの実装ができているので、tsx側でcreateWorkoutを呼び出す処理を実装
    const handleChange = (field: string, value: string) => {
      setFormData((prev) => ({ ...prev, [field]: value }));
    };

    const validate = (): boolean => {
      if (!formData.name.trim()) {
        setError("種目名を入力してください");
        return false;
      }
      if (Number(formData.reps) < 0 || Number(formData.sets) < 0 || Number(formData.weights) < 0) {
        setError("数値にマイナスを入力することはできません");
        return false;
      }
      setError("");
      return true;
    };
  

  const handleGetAll = async () => {
    setIsLoading(true);
    try {
      const response = await fetchWorkoutByUserId(userId);
      if (response.ok) {
      setWorkouts(response.data as Workout[]);
     } else {
      setError(response.message);
     }
    } finally {
      setIsLoading(false);
    }
  };

  const handleCreate = async () => {
    if (!validate()) return;
    setIsLoading(true);
    try {
      const response = await createWorkout(
      userId, 
      formData.name,
      Number(formData.reps),
      Number(formData.sets),
      Number(formData.weights)
     );
     if (response.ok) {
       await handleGetAll();
     } else {
      setError(response.message);
     }
    } finally {
      setIsLoading(false);
    }
  };

  const handleUpdate = async (id: number) => {
    if (!validate()) return;
    setIsLoading(true);
    try {
      const response = await updateWorkout(
      id,
      formData.name,
      Number(formData.reps),
      Number(formData.sets),
      Number(formData.weights)
     );
     if (response.ok) {
      setTargetId(null);
      await handleGetAll();
     } else {
      setError(response.message);
     }
    } finally {
      setIsLoading(false);
    }
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm("本当に削除しますか？")) return;
    setIsLoading(true);
    try {
      const response = await deleteWorkout(id);
      if (response.ok) {
        setWorkouts((prev) => (prev ?? []).filter((w) => w.id !== id));
      } else {
        setError(response.message);
      }
    } finally {
      setIsLoading(false);
    }
  };
      
  const handleEditStart = (item: Workout) => {
    setTargetId(item.id);
    setFormData({
      name: item.name,
      reps: String(item.reps),
      sets: String(item.sets),
      weights: String(item.weights),
    });
  };

  const handleLogout = () => {
    removeToken();
    navigate("/login")
  }

  
    return (
      <div className="home-container">
        <header className="home-header">
          <h1>Workout Details</h1>
          <button onClick={handleLogout}>ログアウト</button>
        </header>
        
        {error && <p style={{ color: "red", fontWeight: "bold" }}>{error}</p>}
        {!userId && <p>ユーザーIDが指定されていません</p>}
        <p>Your workout data...</p>

        <WorkoutForm
          formData={formData}
          onChange={handleChange}
          onRegister={handleCreate}
          onGetAll={handleGetAll}
          isLoading={isLoading}
        />

        <h3 className="section-title">記録一覧</h3>
        {workouts.map((item) => (
          <WorkoutItem
            key={item.id}
            item={item}
            isEditing={targetId === item.id}
            onEditStart={handleEditStart}
            onEditSubmit={handleUpdate}
            onDelete={handleDelete}
            isLoading={isLoading}
            />
        ))}
      </div>
    );
  }