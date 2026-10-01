import type { Workout } from "../hooks/useWorkoutApi";

export type WorkoutStatus = {
  totalVolume: number;
  oneRM: number;
};

// 今後必要な機能が増えればここに追加
export function calcStats(workout: Workout): WorkoutStatus {
  const totalVolume = workout.weights * workout.reps * workout.sets;
  const oneRM = Math.round(workout.weights * (1 + workout.reps / 30));
  return { totalVolume, oneRM };
}