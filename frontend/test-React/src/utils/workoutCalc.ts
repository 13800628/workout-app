import type { Workout } from "../hooks/useWorkoutApi";

export type WorkoutStatus = {
  totalVolume: number;
  oneRM: number;
};

// 今後必要な機能が増えればここに追加
export function calcStats(workout: Workout): WorkoutStatus {
  const totalVolume = workout.sets.reduce(
    (sum, s) => sum + s.weights * s.reps,
    0,
  );
  const oneRM = workout.sets.reduce(
    (max, s) => Math.max(max, Math.round(s.weights * (1 + s.reps / 30))),
    0,
  );
  return { totalVolume, oneRM };
}