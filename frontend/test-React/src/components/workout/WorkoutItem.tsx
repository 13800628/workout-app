import type { Workout } from "../../hooks/useWorkoutApi";

type WorkoutItemProps = {
  item: Workout;
  isEditing: boolean;
  onEditStart: (item: Workout) => void;
  onEditSubmit: (id: number) => void;
  onDelete: (id: number) => void;
  isLoading: boolean;
};

// 表示用のコンポーネント
// 修正の余地ありか
export function WorkoutItem({
  item,
  isEditing,
  onEditStart,
  onEditSubmit,
  onDelete,
  isLoading,
}: WorkoutItemProps) {
  const formattedDate = new Date(item.createdAt).toLocaleDateString("ja-JP", {
    timeZone: "Asia/Tokyo",
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
  });

  return (
    <div style={{ borderBottom: "1px solid #ddd", marginBottom: "10px" }}>
      {isEditing ? (
        <button onClick={() => onEditSubmit(item.id)} disabled={isLoading}>
          {isLoading ? "処理中..." : "編集を実行"}
        </button>
      ) : (
        <button onClick={() => onEditStart(item)} disabled={isLoading}>
          {isLoading ? "処理中..." : "編集する"}
        </button>
      )}
      <p>ID: {item.id}</p>
      <p>種目名: {item.name}</p>
      {item.sets.map((set) => (
        <p key={set.setOrder}>
          {set.setOrder}セット目: {set.weights}kg {set.reps}回
        </p>
      ))}
      <p>記録日時: {formattedDate}</p>
      <button onClick={() => onDelete(item.id)} disabled={isLoading}>
        {isLoading ? "処理中..." : "削除"}
      </button>
    </div>
  );
}
