import { authHeaders, handleUnauthorized } from "./useAuth";
import type { ApiResult, VoidResult } from "../types/api";
import { extractErrorMessage } from "../utils/apiError";


export type User = {
  id: number;
  username: string;
  age: number;
};


const BASE_URL = "/api/users";

// 登録は未認証で叩くようにするのでauthHeaderつけない
export async function registerUser(username: string, 
  age: number, 
  password: string, 
): Promise<ApiResult<User>> {
  try {
    const res = await fetch(BASE_URL, {
      method: "POST",
      headers: { "Content-Type": "application/json"},
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

// 自分だけのデータを取得する関数
export async function fetchMe(
  navigate: (path: string) => void
): Promise<ApiResult<User>> {
  try {
    const res = await fetch(`${BASE_URL}/me`, {
      headers: authHeaders(),
    });
    handleUnauthorized(res.status, navigate);
    if (!res.ok) {
      const message = await extractErrorMessage(res, `サーバーエラー: ${res.status}`);
      return { ok: false, message};
    }
    const data: User = await res.json();
    return { ok: true, data};
  } catch (err) {
    return { ok: false, message: `通信エラー: ${String(err)}` };
  }
}

// 自分のデータのみ更新
export async function updateMe(
  username: string,
  age: number,
  navigate: (path: string) => void
): Promise<ApiResult<User>> {
  try {
    const res = await fetch(`${BASE_URL}/me`, {
      method: "PUT",
      headers: authHeaders(),
      body: JSON.stringify({ username, age }),
    });
    handleUnauthorized(res.status, navigate);
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

// 自分のみを削除
export async function deleteMe(
  navigate: (path: string) => void
): Promise<VoidResult> {
  try {
    const res = await fetch(`${BASE_URL}/me`, {
      method: "DELETE",
      headers: authHeaders(),
    });
    handleUnauthorized(res.status, navigate);
    if (res.status === 204) return { ok: true };
    const message = await extractErrorMessage(res, `削除失敗: ${res.status}`);
    return { ok: false, message };
  } catch (err) {
    return { ok: false, message: `通信エラー: ${String(err)}`};
  }
}

// 自分のみ更新
export async function changeMyPassword(
  oldPassword: string,
  newPassword: string,
  navigate: (path: string) => void
): Promise<VoidResult> {
  try {
    const res = await fetch(`${BASE_URL}/me/password`, {
      method: "PUT",
      headers: authHeaders(),
      body: JSON.stringify({ oldPassword, newPassword }),
    });
    handleUnauthorized(res.status, navigate);
    if (res.status === 204) return { ok: true };
    const message = await extractErrorMessage(res, `パスワード変更失敗: ${res.status}`);
    return { ok: false, message };
  } catch (err) {
    return { ok: false, message: `通信エラー: ${String(err)}` };
  }
}