import type { User } from "../hooks/useUserApi";

export function formatUser(user: User): string {
  return `ID: ${user.id}\n名前: ${user.username}\n年齢: ${user.age}`;
}
