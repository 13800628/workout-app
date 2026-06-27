export type ApiResult<T> =
 | { ok: true; data: T}
 | { ok: false; message: string};

export type VoidResult =
 | { ok: true }
 | { ok: false; message: string};

 // Spring Data JPA のPage<T>のレスポンス形式に対応する共通型
 export type PageResult<T> = {
  content: T[];
  totalPages: number;
  totalElements: number;
  number: number;
  size: number;
 }