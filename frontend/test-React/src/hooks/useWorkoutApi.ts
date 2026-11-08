import { authHeaders, handleUnauthorized } from "./useAuth";
import type { ApiResult, VoidResult } from "../types/api";
import { extractErrorMessage } from "../utils/apiError";

export type Workout = {
  id: number;
  name: string;
  sets: WorkoutSet[];
  createdAt: string;
  updatedAt: string;
};

export type WorkoutSet = {
  setOrder: number;
  reps: number;
  weights: number;
};

export type WorkoutSetInput = {
  reps: number;
  weights: number;
};


const BASE_URL = "/api/workouts";

export async function fetchMyWorkouts(
  navigate: (path: string) => void
): Promise<ApiResult<Workout[]>>  {
  try {
    const response = await fetch(`${BASE_URL}/me`, {
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
  name: string,
  sets: WorkoutSetInput[],
  navigate: (path: string) => void
): Promise<ApiResult<Workout>> {
  try {
    const response = await fetch(BASE_URL, {
      method: "POST",
      headers: authHeaders(),
      body: JSON.stringify({ name, sets }),
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
  sets: WorkoutSetInput[],
  navigate: (path: string) => void
): Promise<ApiResult<Workout>> {
  try {
    const response = await fetch(`${BASE_URL}/${id}`, {
      method: "PUT",
      headers: authHeaders(),
      body: JSON.stringify({ name, sets }),
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