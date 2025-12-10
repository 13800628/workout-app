/**
 * id から別ページに飛んで、Workout操作
 * id 入力ボタン押すと、該当 id の Workout 一覧が表示される
 * idでif分岐で、trueならfetchして表示、falseなら何もしない
 * fetchのURLはuserとは別のものになる
 * buttonの実装(handleFetchByIdなど)も同様に追加
 * 
 */

import { useState } from "react";

type User = {
  id: number;
  username: string;
  age: number;
}

function Home() {
  const [username, setUsername] = useState("");
  const [age, setAge] = useState("");
  const [userId, setUserId] = useState("");
  const [result, setResult] = useState("");

  const baseUrl = "http://localhost:8080/api/users";
  //const workoutUrl = "http://localhost:8080/api/workouts";
  
  // =========================================================================
  // CRUD 操作 各種


  // 登録処理
  const handleRegister = async () => {
    try {
      const res = await fetch(`${baseUrl}/register`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          username: username,
          age: Number(age),
        }),
      });
     
      const data = await res.json();
      setResult(formatResult(data));
    } catch (err) {
      setResult("エラー起きたかも" + String(err));
    }
  };
  


  const handleGetAll = async () => {
    try {
      const res = await fetch(baseUrl);
      const data = await res.json();
      setResult(formatResult(data));
    } catch (err) {
      setResult("エラー" + String(err));
    }
  };

  const handleGetById = async () => {
    try {
      const res = await fetch(`${baseUrl}/${userId}`);
      if (!res.ok) {
        setResult("ユーザーが見つからない");
        return;
      }
      const data = await res.json();
      setResult(formatResult(data));
    } catch (err) {
      setResult("エラー" + String(err));
    }
  };

  const handleUpdate = async () => {
    try {
      const res = await fetch(`${baseUrl}/${userId}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          username: username,
          age: Number(age),
        }),
      });

      const data = await res.json();
      setResult(formatResult(data));
    } catch (err) {
      setResult("エラー" + String (err));
    }
  };

  const handleDelete = async () => {
    try {
      const res = await fetch(`${baseUrl}/${userId}`, {
        method: "DELETE",
      });

      if (res.status === 204) {
        setResult("削除完了");
      } else {
        setResult("失敗");
      }
    } catch (err) {
      setResult("エラー" + String (err));
    }
  }

  // =========================================================================

  const formatResult = (data: User | User[] | string): string => {
  // 複数ユーザーの場合
  if (Array.isArray(data)) {
    return data
      .map(
        (u) =>
          `ID: ${u.id}\n名前: ${u.username}\n年齢: ${u.age}`  // <br>ではなく\n
      )
      .join('\n\n');  // <br><br>ではなく\n\n
  }

  // 単体オブジェクト（User型）の場合
  if (
    typeof data === 'object' &&
    data !== null &&
    'id' in data && 
    'username' in data &&
    'age' in data
  ) {
    return `
┌───────────────┐
│ ID: ${data.id}
│ 名前: ${data.username}
│ 年齢: ${data.age}
└───────────────┘`.trim();
  }

  return String(data);
};


  // 別クラスで作成したので、代わりに別ページに飛ばす実装に変更
  const handleGoToWorkoutPage = () => {
    if (!userId) {
      alert("ユーザーIDを入力してください");
      return;
    }
    window.location.href = `/workout?id=${userId}`;
  }



  return (
    <div style={{ padding: 20 , fontSize: 18}} className="gradation">
      <h1>ユーザー登録テスト</h1>
      
      <input
        placeholder="名前"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
      />
      <br />

      <input
        placeholder="年齢"
        type="number"
        value={age}
        onChange={(e) => setAge(e.target.value)}
      />
      <br />

      <input
        placeholder="ID"
        type="number"
        value={userId}
        onChange={(e) => setUserId(e.target.value)}
      />

      
      <h3>操作</h3>
      <div className="button-group">
      <button onClick={handleRegister}>登録</button>
      <button onClick={handleGetAll}>全部取得</button>
      <button onClick={handleGetById}>ID 取得</button>
      <button onClick={handleUpdate}>更新</button>
      <button onClick={handleDelete}>削除</button>
      <button onClick={handleGoToWorkoutPage}>Workoutページへ</button>
      </div>
      <div>
        <h3>結果</h3>
        <div style={{ whiteSpace: 'pre-line', marginTop: '10px'}}>{result}</div>
      </div>
    </div>
  );
}
export default Home;