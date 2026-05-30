export type Workout = {
  id: number;
  name: string;
  reps: number;
  sets: number;
  weights: number;
};

type ApiResult =
  | { ok: true; data: Workout | Workout[] }
  | { ok: false; message: string };

type DeleteResult = { ok: true } | { ok: false; message: string };

const BASE_URL = "/api/workouts";

export async function fetchWorkoutByUserId(userId: number): Promise<ApiResult>  {
  try {
    const response = await fetch(`${BASE_URL}/${userId}`);
    if (!response.ok) return { ok: false, message: `サーバーエラー: ${response.status}`};
    const responseData = await response.json();
    return { ok: true, data: responseData };
  }catch (error) {
    return { ok: false, message: `通信エラー: ${String(error)}` };
  }
}

export async function createWorkout(
  userId: number,
  name: string,
  reps: number,
  sets: number,
  weights: number
): Promise<ApiResult> {
  try {
    // ここのエンドポイントの確認必須(エラーになる可能性あり)
    const response = await fetch(`${BASE_URL}/create`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ userId, name, reps, sets, weights }),
    });
    if (!response.ok) return { ok: false, message: `登録失敗: ${response.status}`};
    const data: Workout = await response.json();
    return { ok: true, data };
  } catch (error) {
    return { ok: false, message: `通信エラー: ${String(error)}` };
  }
}

export async function updateWorkout(
  id: number,
  name: string,
  reps: number,
  sets: number,
  weights: number
): Promise<ApiResult> {
  try {
    const response = await fetch(`${BASE_URL}/${id}/update`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ name, reps, sets, weights }),
    });
    if (!response.ok) return { ok: false, message: `更新失敗: ${response.status}`};
    const data: Workout = await response.json();
    return { ok: true, data };
  } catch (error) {
    return { ok: false, message: `通信エラー: ${String(error)}` };
  }
}

export async function deleteWorkout(id: number): Promise<DeleteResult> {
  try {
    const response = await fetch(`${BASE_URL}/${id}`, { method: "DELETE" });
    if (response.status === 200) return { ok: true };
    return { ok: false, message: `削除失敗: ${response.status}` };
  } catch (error) {
    return { ok: false, message: `通信エラー: ${String(error)}`};
  }
}