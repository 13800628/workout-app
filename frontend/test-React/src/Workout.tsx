/**
 * ページ移動はできたが、ボタンの動作がうまくいかないので改善
 * コンソールでエラーが複数出ているので修正していく
 * (URLか設定かの問題かと推測、調査必須)
 */



import { useState, useEffect } from "react";
//import { data } from "react-router-dom";



type Workout ={
  id: number;
  name: string;
  reps: number;
  sets: number;
  weights: number;
}




export default function Workout() {
  const [workout, setWorkout] = useState<Workout | null>(null);
  const [error, setError] = useState<string>("");
  const [formData, setFormData] = useState({
    name: "",
    reps: "",
    sets: "",
    weights: "",
  });

  const params = new URLSearchParams(window.location.search);
  const workoutId = params.get("id");

  const workoutUrl =
  (import.meta.env?.VITE_API_URL ?? "http://localhost:8080") + "/api/workouts";

  useEffect(() => {
  if (!workoutId) return;

  const fetchWorkout = async () => {
    try {
      const res = await fetch(`${workoutUrl}/${workoutId}`);
      const data = await res.json();
      setWorkout(data);
    } catch (err) {
      console.error("取得に失敗:", err);
    }
  };

  fetchWorkout();
}, [workoutId]);
    
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
      }
  

    /**
     * handleSetWorkoutName, handleSetWorkoutReps,, handleSetWorkoutSets
     */
    const handleSetWorkoutName = async () => {
      if (!workout || !workoutId) return;
      try {
        const res = await fetch(`${workoutUrl}/${workoutId}/setName`, {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            name: formData.name,
          }),
        });
        const data = await res.json();
        setWorkout(data);
      } catch (err) {
        console.error("エラーが発生しました: ", err);
      }
        }

    const handleSetWorkoutReps = async () => {
      if (!workout || !workoutId) return;
      try {
        const res = await fetch(`${workoutUrl}/${workoutId}/setReps`, {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            reps: Number(formData.reps),
          }),
        });
        const data = await res.json();
        setWorkout(data);
      } catch (err) {
        console.error("エラーが発生しました: ", err);
      }
    }

    const handleSetWorkoutSets = async () => {
      if (!workout || !workoutId) return;
      try {
        const res = await fetch(`${workoutUrl}/${workoutId}/setSets`, {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            sets: Number(formData.sets),
          }),
      });
      const data = await res.json();
      setWorkout(data);
    } catch (err) {
      console.error("エラーが発生しました: ", err);
    }
   }

   const handleSetWorkoutWeights = async () => {
    if (!workout || !workoutId) return;
    try {
      const res = await fetch(`${workoutUrl}/${workoutId}/setWeights`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          weights: Number(formData.weights),
        }),
      });
      const data = await res.json();
      setWorkout(data);
    } catch (err) {
      console.error("エラーが発生しました: ", err);
    }
   } 
   // /{id}/setsUserIdをエンドポイントに受け取るhandleSetWorkoutUserIdを実装

   const AllHandleController = async () => {
    await handleSetWorkoutName();
    await handleSetWorkoutReps();
    await handleSetWorkoutSets();
    await handleSetWorkoutWeights();
    // handleSetWorkoutUserIdの追加
    //console.log(data);

   };

  /** const formatResult = (data: Workout | Workout[] | number): string => {
    if (Array.isArray(data)) {
      return data
        .map(
          (u) =>
            `ID: ${u.id}\n種目名: ${u.name}\n回数: ${u.reps}\nセット数: ${u.sets}\n重量: ${u.weights} Kg`
        )
        .join("\n\n");
    }
    if (
      typeof data === "object" &&
      data !== null &&
      "id" in data &&
      "name" in data &&
      "reps" in data &&
      "sets" in data &&
      "weights" in data
    ) {
      return `
┌───────────────┐
 ID: ${data.id}
 種目名: ${data.name}
 回数: ${data.reps}
 セット数: ${data.sets}
 重量: ${data.weights}
└───────────────┘`.trim();
    }
    return String(data);
  }; */

  

  const handleGetAll = async () => {
   try {
   const res = await fetch(`${workoutUrl}/${workoutId}`);
   const data = await res.json();
   setWorkout(data);
   console.log(data);
    } catch (err) {
   console.error("error",err);
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

        {workout ? (
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
        ) : (
          <p>Loading workout data...</p>
        )}

        <h3>操作</h3>
        <div className="button-group">
          <button onClick={handleCreateWorkout}>登録</button>
          <button
            onClick={() => {
              handleUpdateAllDetails();
            }}
          >
            ワークアウト更新
          </button>
          <button onClick={handleGetAll}>全件取得</button>
          <button onClick={AllHandleController}>一つ一つ更新</button>
        </div>
        <h3>記録</h3>
       

         <p><span className="font-semibold">ID:</span> {workout?.id}</p>
            <p><span className="font-semibold">種目名:</span> {workout?.name}</p>
            <p><span className="font-semibold">回数:</span> {workout?.reps}</p>
            <p><span className="font-semibold">セット数:</span> {workout?.sets}</p>
            <p><span className="font-semibold">重量:</span> {workout?.weights} Kg</p>
      </div>
    );
  }