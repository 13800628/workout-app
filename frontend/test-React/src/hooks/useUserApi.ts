import { authHeaders } from "./useAuth";
import type { ApiResult, VoidResult, PageResult } from "../types/api";
import { extractErrorMessage } from "../utils/apiError";
import { DEFAULT_PAGE_SIZE } from "../config/pagination";

export type User = {
  id: number;
  username: string;
  age: number;
};


const BASE_URL = "/api/users";

// ページネーション対応するように変更
export async function fetchAllUsers(
  page = 0,
  size: number = DEFAULT_PAGE_SIZE,
): Promise<ApiResult<PageResult<User>>> {
  try {
    const res = await fetch(`${BASE_URL}?page=${page}&size=${size}`, {
      headers: authHeaders(),
    });
    if (!res.ok) {
      const message = await extractErrorMessage(res, `サーバーエラー: ${res.status}`);
      return { ok: false, message};
    }
    const data: PageResult<User> = await res.json();
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
    if (!res.ok) {
      const message = await extractErrorMessage(res, `サーバーエラー: ${res.status}`);
      return { ok: false, message };
    }

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
    if (!res.ok) {
      const message = await extractErrorMessage(res, `サーバーエラー: ${res.status}`);
      return { ok: false, message };
    }
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
    if (!res.ok) {
      const message = await extractErrorMessage(res, `サーバーエラー: ${res.status}`);
      return { ok: false, message };
    }
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
    const message = await extractErrorMessage(res, `削除失敗: ${res.status}`);
    return { ok: false, message};
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
    const message = await extractErrorMessage(res, `パスワード変更失敗: ${res.status}`);
    return { ok: false, message };
  } catch (err) {
    return { ok: false, message: `通信エラー: ${String(err)}` };
  }
}