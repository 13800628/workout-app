import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  fetchMyWorkouts,
  createWorkout,
  updateWorkout,
  deleteWorkout,
} from "../hooks/useWorkoutApi";
import type { Workout, WorkoutSetInput } from "../hooks/useWorkoutApi";
import { removeToken } from "../hooks/useAuth";
import {
  WorkoutForm,
  type WorkoutFormData,
  type WorkoutSetFormData,
} from "../components/workout/WorkoutForm";
import { WorkoutItem } from "../components/workout/WorkoutItem";
import { WorkoutCalculator } from "../components/workout/WorkoutCalculator";

const createEmptySet = (): WorkoutSetFormData => ({ reps: "", weights: "" });
const createEmptyForm = (): WorkoutFormData => ({
  name: "",
  sets: [createEmptySet()],
});

const toSetInputs = (form: WorkoutFormData): WorkoutSetInput[] =>
  form.sets.map((s) => ({
    reps: Number(s.reps),
    weights: Number(s.weights),
  }));

// メインコンポーネント

export default function Workout() {
  const [workouts, setWorkouts] = useState<Workout[]>([]);
  const [error, setError] = useState("");
  const [targetId, setTargetId] = useState<number | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  const [formData, setFormData] = useState<WorkoutFormData>(createEmptyForm);

  // createWorkoutの実装ができているので、tsx側でcreateWorkoutを呼び出す処理を実装
  const handleNameChange = (value: string) => {
    setFormData((prev) => ({ ...prev, name: value }));
  };

  const handleSetChange = (
    index: number,
    field: keyof WorkoutSetFormData,
    value: string,
  ) => {
    setFormData((prev) => ({
      ...prev,
      sets: prev.sets.map((s, i) =>
        i === index ? { ...s, [field]: value } : s,
      ),
    }));
  };

  const handleAddSet = () => {
    setFormData((prev) => ({
      ...prev,
      sets: [...prev.sets, createEmptySet()],
    }));
  };

  const handleRemoveSet = (index: number) => {
    setFormData((prev) => {
      if (prev.sets.length <= 1) return prev;
      return { ...prev, sets: prev.sets.filter((_, i) => i !== index) };
    });
  };

  const validate = (): boolean => {
    if (!formData.name.trim()) {
      setError("種目名を入力してください");
      return false;
    }
    if (
      formData.sets.some((s) => Number(s.reps) < 0 || Number(s.weights) < 0)
    ) {
      setError("数値にマイナスを入力することはできません");
      return false;
    }
    setError("");
    return true;
  };

  // データ習得の共通関数(自分のみ)
  const fetchWorkoutsAllData = async () => {
    const response = await fetchMyWorkouts(navigate);
    if (response.ok) {
      setWorkouts(response.data as Workout[]);
    } else {
      setError(response.message);
    }
  };

  // 画面表示時に自動で自分のWorkoutを取得
  useEffect(() => {
    fetchWorkoutsAllData();
  }, []);

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
    setIsLoading(true);
    try {
      const response = await createWorkout(
        formData.name,
        toSetInputs(formData),
        navigate,
      );
      if (response.ok) {
        await fetchWorkoutsAllData();
        setFormData(createEmptyForm());
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
        toSetInputs(formData),
        navigate,
      );
      if (response.ok) {
        setTargetId(null);
        await fetchWorkoutsAllData();
        setFormData(createEmptyForm());
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
    const sets = item.sets.map((s) => ({
      reps: String(s.reps),
      weights: String(s.weights),
    }));
    setFormData({
      name: item.name,
      sets: sets.length > 0 ? sets : [createEmptySet()],
    });
  };

  const handleLogout = () => {
    removeToken();
    navigate("/login");
  };

  return (
    <div className="home-container">
      <header className="home-header">
        <h1>Workout Details</h1>
        <button onClick={handleLogout}>ログアウト</button>
      </header>

      <div className="workout-scroll">
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
        <WorkoutCalculator workouts={workouts} />
      </div>

      {/* 固定領域 */}
      <div className="workout-form-area">
        {error && <p style={{ color: "red", fontWeight: "bold" }}>{error}</p>}

        <WorkoutForm
          formData={formData}
          onNameChange={handleNameChange}
          onSetChange={handleSetChange}
          onAddSet={handleAddSet}
          onRemoveSet={handleRemoveSet}
          onRegister={handleCreate}
          onGetAll={handleGetAll}
          isLoading={isLoading}
        />
      </div>
    </div>
  );
}
