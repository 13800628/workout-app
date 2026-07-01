import { useState } from "react";
import type { Workout } from "../../hooks/useWorkoutApi";
import { calcStats } from "../../utils/workoutCalc";

type WorkoutCalculatorProps = {
  workouts: Workout[];
};

export function WorkoutCalculator({ workouts }: WorkoutCalculatorProps) {
  const [selectedId, setSelectedId] = useState<number | null>(null);

  const selected = workouts.find((w) => w.id === selectedId) ?? null;
  const stats = selected ? calcStats(selected) : null;

  return (
    <div className="result-section">
      <h3>計算セクション</h3>
      <select
        onChange={(e) => setSelectedId(Number(e.target.value))}
        value={selectedId ?? ""}
      >
        <option value="">種目を選択してください</option>
        {workouts.map((w) => (
          <option key={w.id} value={w.id}>
            {w.name}
          </option>
        ))}
      </select>

      {stats && selected && (
        <div style={{ marginTop: 16 }}>
          <p>種目名: {selected.name}</p>
          <p>総重量: {stats.totalVolume} kg</p>
          <p>1RM : {stats.oneRM} kg</p>
        </div>
      )}
    </div>
  );
}