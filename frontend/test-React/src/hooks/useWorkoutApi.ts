import { authHeaders, handleUnauthorized } from "./useAuth";
import type { ApiResult, VoidResult } from "../types/api";

export type Workout = {
  id: number;
  name: string;
  reps: number;
  sets: number;
  weights: number;
};


const BASE_URL = "/api/workouts";

export async function fetchWorkoutByUserId(
  userId: number,
  navigate: (path: string) => void
): Promise<ApiResult<Workout[]>>  {
  try {
    const response = await fetch(`${BASE_URL}/${userId}`, {
      headers: authHeaders(),
    });
    handleUnauthorized(response.status, navigate);
    if (!response.ok) return { ok: false, message: `サーバーエラー: ${response.status}`};
    const data: Workout[] = await response.json();
    return { ok: true, data: data };
  }catch (error) {
    return { ok: false, message: `通信エラー: ${String(error)}` };
  }
}

export async function createWorkout(
  userId: number,
  name: string,
  reps: number,
  sets: number,
  weights: number,
  navigate: (path: string) => void
): Promise<ApiResult<Workout>> {
  try {
    const response = await fetch(`${BASE_URL}/create`, {
      method: "POST",
      headers: authHeaders(),
      body: JSON.stringify({ userId, name, reps, sets, weights }),
    });
    handleUnauthorized(response.status, navigate);
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
  weights: number,
  navigate: (path: string) => void
): Promise<ApiResult<Workout>> {
  try {
    const response = await fetch(`${BASE_URL}/${id}/details`, {
      method: "PUT",
      headers: authHeaders(),
      body: JSON.stringify({ name, reps, sets, weights }),
    });
    handleUnauthorized(response.status, navigate);
    if (!response.ok) return { ok: false, message: `更新失敗: ${response.status}`};
    const data: Workout = await response.json();
    return { ok: true, data };
  } catch (error) {
    return { ok: false, message: `通信エラー: ${String(error)}` };
  }
}

export async function deleteWorkout(
  id: number,
  navigate: (path: string) => void
): Promise<VoidResult> {
  try {
    const response = await fetch(`${BASE_URL}/${id}`, { 
      method: "DELETE" ,
      headers: authHeaders(),
    });
    handleUnauthorized(response.status, navigate);
    if (response.status === 204) return { ok: true };
    return { ok: false, message: `削除失敗: ${response.status}` };
  } catch (error) {
    return { ok: false, message: `通信エラー: ${String(error)}`};
  }
}