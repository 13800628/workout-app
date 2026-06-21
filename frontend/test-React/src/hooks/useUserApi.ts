import { authHeaders } from "./useAuth";
import type { ApiResult, VoidResult } from "../types/api";

export type User = {
  id: number;
  username: string;
  age: number;
};


const BASE_URL = "/api/users";

export async function fetchAllUsers(): Promise<ApiResult<User[]>> {
  try {
    const res = await fetch(BASE_URL, {
      headers: authHeaders(),
    });
    if (!res.ok) return { ok: false, message: `サーバーエラー: ${res.status}` };
    const page: { content: User[] } = await res.json();
    const data: User[] = page.content;
    return { ok: true, data };
  } catch (err) {
    return { ok: false, message: `通信エラー: ${String(err)}`};
  }
}

// ここちょっと検討
export async function fetchUserById(userId: number): Promise<ApiResult<User>> {
  try {
    const res = await fetch(`${BASE_URL}/${userId}`, {
      headers: authHeaders(),
    });
    if (res.status === 404) return { ok: false, message: `ユーザーが見つかりません`};
    if (!res.ok) return { ok: false, message: `サーバーエラー: ${res.status}` };

    const data: User = await res.json();
    return { ok: true, data};
  } catch (err) {
    return { ok: false, message: `通信エラー: ${String(err)}`};
  }
}

export async function registerUser(username: string, 
  age: number, 
  password: string, 
): Promise<ApiResult<User>> {
  try {
    const res = await fetch(BASE_URL, {
      method: "POST",
      headers: authHeaders(),
      body: JSON.stringify({ username, age, password}),
    });
    if (!res.ok) return { ok: false, message: `登録失敗: ${res.status}`};
    const data: User = await res.json();
    return { ok: true, data}
  } catch (err) {
    return { ok: false, message: `通信エラー: ${String(err)}`};
  }
}

export async function updateUser(
  userId:number, 
  username: string, 
  age: number, 
): Promise<ApiResult<User>> {
  try {
    const res = await fetch(`${BASE_URL}/${userId}`, {
      method: "PUT",
      headers: authHeaders(),
      body: JSON.stringify({ username, age }),
    });
    if (!res.ok) return { ok: false, message: `更新失敗: ${res.status}` };
    const data: User = await res.json();
    return { ok: true, data };
  } catch (err) {
    return { ok: false, message: `通信エラー: ${String(err)}` };
  }
}

export async function deleteUser(
   userId: number,
  ): Promise<VoidResult> {
  try {
    const res = await fetch(`${BASE_URL}/${userId}`, { 
      method: "DELETE",
      headers: authHeaders(),
    });
    if (res.status === 204) return {ok: true };
    return { ok: false, message: `削除失敗: ${res.status}` };
  } catch (err) {
    return { ok: false, message: `通信エラー: ${String(err)}` };
  }
}

export async function changePassword(
  userId: number,
  oldPassword: string,
  newPassword: string,
) : Promise<VoidResult> {
  try {
    const res = await fetch(`${BASE_URL}/${userId}/password`, {
      method: "PUT",
      headers: authHeaders(),
      body: JSON.stringify({ oldPassword, newPassword }),
    });
    if (res.status === 204) return { ok: true };
    return { ok: false, message: `パスワード変更失敗: ${res.status}` };
  } catch (err) {
    return { ok: false, message: `通信エラー: ${String(err)}` };
  }
}