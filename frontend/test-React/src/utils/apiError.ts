export async function extractErrorMessage(res: Response, fallback: string): Promise<string> {
  try {
    const body = await res.json();
    if (Array.isArray(body.details) && body.details.length > 0) {
      return body.details.join(", ");
    }
    if (body.message) {
      return body.message;
    }
  } catch {
    // 何もしない
  }
  return fallback;
}