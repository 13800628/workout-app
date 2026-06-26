import { authHeaders, handleUnauthorized } from "./useAuth";
import type { ApiResult, VoidResult } from "../types/api";
import { extractErrorMessage } from "../utils/apiError";

export type Workout = {
  id: number;
  name: string;
  reps: number;
  sets: number;
  weights: number;
  createdAt: string;
  updateAt: string;
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
    if (!response.ok) {
      const message = await extractErrorMessage(response, `サーバーエラー: ${response.status}`);
      return { ok: false, message};
    }
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
    if (!response.ok) {
      const message = await extractErrorMessage(response, `サーバーエラー: ${response.status}`);
      return { ok: false, message};
    }
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
    if (!response.ok) {
      const message = await extractErrorMessage(response, `サーバーエラー: ${response.status}`);
      return { ok: false, message};
    }
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
    const message = await extractErrorMessage(response, `削除失敗: ${response.status}`);
    return { ok: false, message};
  } catch (error) {
    return { ok: false, message: `通信エラー: ${String(error)}`};
  }
}