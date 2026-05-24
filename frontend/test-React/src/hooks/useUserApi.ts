export type User = {
  id: number;
  username: string;
  age: number;
};

type ApiResult = 
| {ok: true; data: User | User[] }
| {ok: false; message: string };

const BASE_URL = "/api/users";

export async function fetchAllUsers(): Promise<ApiResult> {
  try {
    const res = await fetch(BASE_URL);
    if (!res.ok) return { ok: false, message: `サーバーエラー: ${res.status}` };
    const data: User[] = await res.json();
    return { ok: true, data };
  } catch (err) {
    return { ok: false, message: `通信エラー: ${String(err)}`};
  }
}

export async function fetchUserById(userId: number): Promise<ApiResult> {
  try {
    const res = await fetch(`${BASE_URL}/${userId}`);

    if (res.status == 404) return { ok: false, message: `ユーザーが見つかりません`};
    if (!res.ok) return { ok: false, message: `サーバーエラー: ${res.status}` };

    const data: User = await res.json();
    return { ok: true, data};
  } catch (err) {
    return { ok: false, message: `通信エラー: ${String(err)}`};
  }
}

export async function registerUser(username: string, age: number): Promise<ApiResult> {
  try {
    const res = await fetch(BASE_URL, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username, age}),
    });
    if (!res.ok) return { ok: false, message: `登録失敗: ${res.status}`};
    const data: User = await res.json();
    return { ok: true, data}
  } catch (err) {
    return { ok: false, message: `通信エラー: ${String(err)}`};
  }
}

export async function updateUser(userId:number, username: string, age: number): Promise<ApiResult> {
  try {
    const res = await fetch(`${BASE_URL}/${userId}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ userId, username, age }),
    });
    if (!res.ok) return { ok: false, message: `更新失敗: ${res.status}` };
    const data: User = await res.json();
    return { ok: true, data };
  } catch (err) {
    return { ok: false, message: `通信エラー: ${String(err)}` };
  }
}

export type DeleteResult = { ok: true} | { ok: false, message: string};

export async function deleteUser(userId: number): Promise<DeleteResult> {
  try {
    const res = await fetch(`${BASE_URL}`, { method: "DELETE" });

    if (res.status === 204) return {ok: true };
    return { ok: false, message: `削除失敗: ${res.status}` };
  } catch (err) {
    return { ok: false, message: `通信エラー: ${String(err)}` };
  }
}