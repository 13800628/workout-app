package com.workout.dto.workouts;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

// Controllerクラスで使うWorkoutRequestクラスの定義
// コードはシンプル
public record WorkoutRequest(
  @NotBlank(message = "種目名は必須です")
  String name,

  @Min(0)
  Integer reps,

  @Min(0)
  Integer sets,
  
  @Min(0)
  Integer weights,

  @NotNull
  Long userId
) {}
