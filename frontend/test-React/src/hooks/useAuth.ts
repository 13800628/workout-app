const TOKEN_KEY = 'jwt_token';

export function saveToken(token: string): void {
  sessionStorage.setItem(TOKEN_KEY, token);
}
  
export function getToken(): string | null {
  return sessionStorage.getItem(TOKEN_KEY);
}

export function removeToken(): void {
  sessionStorage.removeItem(TOKEN_KEY);
}

export function isLoggedIn(): boolean {
  return getToken() !== null;
}

// トークンの付与が固定化されてるので可用性が低い。
export function authHeaders(): HeadersInit {
  const token = getToken();
  return {
    "Content-Type": "application/json",
    ...(token ? { "Authorization": `Bearer ${token}`} : {}),
  };
}

export function handleUnauthorized(status: number, navigate: (path: string) => void): void {
  if (status === 401) {
    removeToken();
    navigate("/login?message=セッションが切れました。再度ログインしてください");
  }
}