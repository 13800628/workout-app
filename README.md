# Workout Management App

React 19 (Vite) のフロントエンドと、Spring Boot 3.5 & PostgreSQL のバックエンドで作成した、トレーニング管理アプリケーションです。
型安全な開発環境、拡張性を意識したドメインカプセル化、そして不要なDBクエリを徹底的に排除する実戦的なパフォーマンスチューニングをしました。

## 🚀 特徴 (Features)

### フロントエンド (Frontend)
- **シングルページアプリケーション (SPA)**:
React Router 7 を採用した、堅牢でスケールしやすい画面遷移制御。

- **インライン動的編集**:
`targetId` ステートの制御により、一覧画面からダイレクトに表示モードと編集モードをシームレスに切り替え可能。

### バックエンド (Backend)
- **包括的なグローバル例外ハンドリング**:
`@RestControllerAdvice` を用いた一元的なエラー制御と、統一されたレスポンスフォーマット（`ErrorResponse`）の返却。

- **データカプセル化の徹底**:
Java Record による不変（Immutable）な DTO を徹底し、エンティティ内部のメソッドへビジネスロジックをカプセル化（セッターによる散発的な更新を排除）。

- **柔軟なドメイン設計**:
`Consumer<T>`（関数型インターフェース）を活用した、部分更新（PATCH的処理）に対応可能な共通フィールド更新テンプレートの実装。

---

## 技術スタック (Tech Stack)

### フロントエンド
- **Library:** React 19.2.0, React DOM 19.2.0
- **Routing:** React Router 7.0.0
- **Build Tool / Bundler:** Vite 7.2.4
- **Language:** TypeScript 5.9
- **Linter:** ESLint 9

### バックエンド
- **Framework:** Spring Boot 3.5.11 (Java 17/21)
- **Security:** Spring Security, Spring Boot Starter Validation
- **Data Access:** Spring Data JPA (Hibernate)
- **Database:** PostgreSQL
- **Build Tool:** Maven

---

## ディレクトリ構造 (Architecture)

プロジェクト全体は、フロントエンドとバックエンドが明確に分離されつつも、同一起点（Same-Origin）での連携やリバースプロキシを容易に行える見通しの良い構造になっています。

```text
.
├── frontend/                     # フロントエンド（Vite + React プロジェクト）
│   ├── src/
│   │   ├── assets/               # 静的アセット（画像等）
│   │   ├── App.css               # アプリケーション共通スタイル
│   │   ├── App.tsx               # ルーティング・全体レイアウト定義
│   │   ├── Home.tsx              # ユーザー管理画面（CRUD、AA風フォーマット、画面遷移）
│   │   ├── Workout.tsx           # ワークアウト記録管理（インライン編集、水際バリデーション）
│   │   ├── index.css             # グローバルスタイル
│   │   └── main.tsx              # エントリーポイント
│   ├── package.json              # React 19 / Vite 7 依存関係
│   └── vite.config.ts            # Vite 設定ファイル
│
└── backend/                      # バックエンド（Spring Boot プロジェクト）
    ├── src/main/java/com/workout/
    │   ├── WorkoutApplication.java
    │   ├── config/               # SecurityConfig (セキュリティ・認可設定)
    │   ├── controller/           # SpaController, UserController, WorkoutController
    │   ├── dto/                  # Record を使用したデータ転送オブジェクト (Req/Res)
    │   ├── exception/            # GlobalExceptionHandle, ErrorResponse
    │   ├── model/                # User, Workout などのエンティティ、Validator
    │   ├── repository/           # UserRepository, WorkoutRepository
    │   └── service/              # WorkoutService, UserService (ビジネスロジック層)
    └── pom.xml                   # Maven 依存関係

    設計・実装のこだわり (Design Highlights)
1. 1クエリへの劇的なパフォーマンス最適化（リファクタリングの歴史）
データの削除処理において、当初は existsById(id) による存在確認（1クエリ）を行った後に deleteById(id)（JPA内部仕様でさらにセレクトが発生する場合があり、計2〜3クエリ）を呼ぶ冗長な設計でした。
これを、カスタムJPQL/ネイティブクエリによる直接バルク削除（deleteDirectlyById）へリファクタリング。データベースが返す「影響を受けた行数（deletedCount）」を評価して 0 件の場合に例外を投げる設計に変更したことで、完全1クエリ化を達成。I/O往復のオーバーヘッドと、存在確認〜削除の間に発生し得るレースコンディションを根本から解決しています。

2. 関数型アプローチによる共通更新テンプレート
特定のフィールドのみを動的に部分更新する際、サービス層（WorkoutService）に酷似したメソッドが量産されるのを防ぐため、Consumer<Workout> を引数に取るテンプレートメソッドを導入。ビジネスロジックの高凝集化とボイラープレートコードの徹底的な排除を両立しています。

3. フロント・バック双方向での防衛的バリデーション
フロントサイド (Workout.tsx): サーバーへ通信が走る前に validateWorkout() が水際で不正入力をキャッチ。空白チェックに加え、回数・重量などへの「マイナス値入力不可」を判定し、画面へ即座に赤文字でフィードバック。

バックサイド (GlobalExceptionHandle): 万が一フロントをすり抜けた不正リクエストも、JPA/Spring Validation の @Valid が完全捕獲。エラーの起きた「フィールド名：理由」のリストを抽出し、フロントがマッピングしやすい親切なエラー構造（400 Bad Request）に一元変換して返却。

4. クエリパラメータを用いたステートレスな画面遷移
Home.tsx で管理するユーザー情報から Workout.tsx へ遷移する際、複雑なグローバル状態管理ライブラリに依存せず、URLのクエリパラメータ（/workout?id=${userId}）を介して状態を伝播。SPAとしてシンプルかつバグの起きにくいデータフローを実現しています。

共通エラーレスポンス仕様 (Error Response Spec)
本APIがエラー（4xx, 5xx）を返す場合、以下の统一されたJSONフォーマットで返却されます。
{
  "status": 400,
  "message": "入力値が正しくありません",
  "details": [
    "username: ユーザー名は必須です",
    "age: 20歳以上でなければなりません"
  ]
}

主要 API エンドポイント (API Endpoints)
ユーザー管理 (User Management) -> Home.tsx から呼び出し
POST /api/users - 新規ユーザー登録 (201 Created)

GET /api/users - ユーザー一覧の取得（ページネーション対応 / デフォルト: page=0, size=10）

GET /api/users/{id} - 特定ユーザーの取得

PUT /api/users/{id} - ユーザー情報の更新

DELETE /api/users/{id} - ユーザーの削除 (204 No Content)

ワークアウト管理 (Workout Management) -> Workout.tsx から呼び出し
POST /api/workouts/create - 新規ワークアウト記録の作成 (201 Created)

GET /api/workouts/{id} - 特定ユーザーに紐づくワークアウト一覧の取得

PUT /api/workouts/{id}/details - 種目名、重量、回数、セット数の一括更新

DELETE /api/workouts/{id} - ワークアウト記録の削除 (204 No Content)

🔧 セットアップと起動方法 (Getting Started)
1. バックエンドの起動
前提条件: Java 17 または 21 のインストール、PostgreSQL の起動と application.properties への接続情報設定。
cd backend
./mvnw test              # 自動テストの実行
./mvnw spring-boot:run   # アプリケーションのローカル起動

2. フロントエンドの起動
前提条件: Node.js 環境のインストール。
cd frontend
npm install              # 依存関係のインストール
npm run dev              # 開発サーバーの起動 (Vite)