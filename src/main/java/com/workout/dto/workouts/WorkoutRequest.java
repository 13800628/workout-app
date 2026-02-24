package com.workout.dto.workouts;

// Controllerクラスで使うWorkoutRequestクラスの定義
// コードはシンプル
/**
 * request.getName(), request.getReps(), request.getSets() のGetter/Setter
 */
public record WorkoutRequest(
  String name,
  Integer reps,
  Integer sets,
  Integer weights,
  Long userId
) {}
