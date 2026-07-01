import { useState } from "react";
import { useSearchParams , useNavigate} from "react-router-dom";
import {
  fetchWorkoutByUserId,
  createWorkout,
  updateWorkout,
  deleteWorkout,
} from "../hooks/useWorkoutApi";
import type { Workout } from "../hooks/useWorkoutApi";
import { removeToken } from "../hooks/useAuth";
import { WorkoutForm } from "../components/workout/WorkoutForm";
import { WorkoutItem } from "../components/workout/WorkoutItem";
import { WorkoutCalculator } from "../components/workout/WorkoutCalculator";



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
  const idParam = searchParams.get("id");
  const userId = idParam ? Number(idParam) : null;
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
    
    // データ習得の共通関数
    const fetchWorkoutsAllData = async () => {
      if (userId === null) {
        setError("ユーザーIDが指定されていません");
        return;
      }
      const response = await fetchWorkoutByUserId(userId, navigate);
      if (response.ok) {
        setWorkouts(response.data as Workout[]);
      } else {
        setError(response.message);
      }
    }
  

  const handleGetAll = async () => {
    setIsLoading(true);
    try {
      await fetchWorkoutsAllData();
    } finally {
      setIsLoading(false);
    }
  };

  const handleCreate = async () => {
    if (!validate()) return;
    if (userId === null) {
      setError("ユーザーIDが指定されていません");
      return;
    }
    setIsLoading(true);
    try {
      const response = await createWorkout(
      userId, 
      formData.name,
      Number(formData.reps),
      Number(formData.sets),
      Number(formData.weights),
      navigate
     );
     if (response.ok) {
      await fetchWorkoutsAllData();
      setFormData({ name: "", reps: "", sets: "", weights: "" });
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
      Number(formData.weights),
      navigate
     );
     if (response.ok) {
      setTargetId(null);
      await fetchWorkoutsAllData();
      setFormData({ name: "", reps: "", sets: "", weights: "" });
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
      const response = await deleteWorkout(id, navigate);
      if (response.ok) {
        await fetchWorkoutsAllData();
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
        <WorkoutCalculator workouts={workouts}/>
      </div>
    );
  }