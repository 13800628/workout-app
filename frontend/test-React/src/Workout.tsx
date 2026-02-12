import { useState } from "react";



type Workout ={
  id: number;
  name: string;
  reps: number;
  sets: number;
  weights: number;
}




export default function Workout() {
  const [workout, setWorkout] = useState<Workout | Workout[] |null>(null);
  const [error, setError] = useState<string>("");
  const [formData, setFormData] = useState({
    name: "",
    reps: "",
    sets: "",
    weights: "",
  });

  const params = new URLSearchParams(window.location.search);
  const workoutId = params.get("id");

  //const API_BASE_URL = import.meta.env.VITE_API_URL || "http://localhost:8080";
  const workoutUrl = "/api/workouts";
    
    // createWorkoutの実装ができているので、tsx側でcreateWorkoutを呼び出す処理を実装
    const handleCreateWorkout = async () => {
      try {
        const res = await fetch(`${workoutUrl}/create`, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            name: formData.name,
            reps: Number(formData.reps),
            sets: Number(formData.sets),
            weights: Number(formData.weights),
            
            // workoutIdだとworkoutのidのため、userIdの依存にしないと不意になエラーが発生するかも
            userId: Number(workoutId),
          }),
          });
          const data = await res.json();
          setWorkout(data);
          setError("Error")
        } catch (err) {
          console.error("エラーが発生しました: ", err, error);
        }
      };

   const handleDeleteWorkout = async (id: number) => {
    if (!window.confirm("本当に削除しますか？")) return;

    try {
    const res = await fetch(`${workoutUrl}/${id}`, {
      method: "DELETE",
    });

    if (res.ok) {
      alert("削除に成功しました");
      console.log("削除成功: ID", id);
      // 削除に成功したら、画面上のリストからそのデータを消す
      // workoutが配列の場合の処理
      if (Array.isArray(workout)) {
        setWorkout(workout.filter((item) => item.id !== id));
      } else {
        setWorkout(null);
      }
    } else {
      console.error("削除失敗: ステータス", res.status);
      alert("削除に失敗しました（データが存在しない可能性があります）");
    }
  } catch (err) {
    console.error("通信エラー:", err);
  }
};
  

  const handleGetAll = async () => {
    try {
      const res = await fetch(`${workoutUrl}/${workoutId}`);
      const data = await res.json();
      setWorkout(data);
      console.log(data);
    } catch (err) {
      console.error("error", err);
      setError("Error");
    }
  };

  const handleUpdateAllDetails = async () => {
    if (!workoutId) return;
    
    try {
      const res = await fetch(`${workoutUrl}/${workoutId}/details`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          name: formData.name,
          reps: Number(formData.reps),
          sets: Number(formData.sets),
          weights: Number(formData.weights), 
        }),
      });
      const data = await res.json();
      setWorkout(data);
      console.log("更新成功:", data);
    } catch (err) {
      console.error("エラーが発生しました: ", err);
    }
  }
      
  
    return (
      <div style={{ padding: 20, fontSize: 18 }} className="gradation">
        <h2>Workout Details</h2>

        {!workoutId && <p>ユーザーIDが指定されていません</p>}
        <p>Your workout data...</p>

        <div>
            <input
              placeholder="種目名"
              type="text"
              value={formData.name}
              onChange={(e) =>
                setFormData({ ...formData, name: e.target.value })
              }
            />

            <input
              placeholder="回数"
              type="number"
              value={formData.reps}
              onChange={(e) =>
                setFormData({ ...formData, reps: e.target.value })
              }
            />

            <input
              placeholder="セット数"
              type="number"
              value={formData.sets}
              onChange={(e) =>
                setFormData({ ...formData, sets: e.target.value })
              }
            />

            <input
              placeholder="重さ"
              type="number"
              value={formData.weights}
              onChange={(e) =>
                setFormData({ ...formData, weights: e.target.value })
              }
            />
          </div>

        <h3></h3>
        <div className="button-group">
          <button onClick={handleCreateWorkout}>登録</button>

          <button onClick={() => {handleUpdateAllDetails();}}>更新</button>

          <button onClick={handleGetAll}>全件取得</button>

        </div>
        <h3>記録一覧</h3>
        {Array.isArray(workout) ? (
          workout.map((item) => (
            <div key={item.id} style={{ borderBottom: "1px solid #ddd", marginBottom: "10px" }}>
              <button onClick={() => handleDeleteWorkout(item.id)}>削除</button>
              <p><span className="font-semibold">ID:</span>{item.id}</p>
              <p><span className="font-semibold">種目名:</span>{item.name}</p>
              <p><span className="font-semibold">回数:</span>{item.reps}</p>
              <p><span className="font-semibold">セット数:</span>{item.sets}</p>
              <p><span className="font-semibold">重量:</span>{item.weights}</p>
              </div>
          ))
        ) : (
          workout && (
            <div style={{ borderBottom: "1px solid #ddd", marginBottom: "10px" }}>
              <p><span className="font-semibold">ID:</span> {workout.id}</p>
              <p><span className="font-semibold">種目名:</span> {workout.name}</p>
              <p><span className="font-semibold">回数:</span> {workout.reps}</p>
              <p><span className="font-semibold">セット数:</span> {workout.sets}</p>
              <p><span className="font-semibold">重量:</span> {workout.weights} Kg</p>
            </div>
            )
          )
        }
      </div>
    );
  }