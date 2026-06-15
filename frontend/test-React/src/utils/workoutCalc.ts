import type { Workout } from "../hooks/useWorkoutApi";

export type WorkoutStatus = {
  totalVolume: number;
  oneRM: number;
};

export function calcStats(workout: Workout): WorkoutStatus {
  const totalVolume = workout.weights * workout.sets * workout.sets;
  const oneRM = Math.round(workout.weights * (1 + workout.reps / 30));
  return { totalVolume, oneRM };
}