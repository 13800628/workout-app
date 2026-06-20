export type ApiResult<T> =
 | { ok: true; data: T}
 | { ok: false; message: string};

export type VoidResult =
 | { ok: true }
 | { ok: false; message: string};