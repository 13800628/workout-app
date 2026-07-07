package com.workout.dto.workouts;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

// Controllerクラスで使うWorkoutRequestクラスの定義
// コードはシンプル
public record WorkoutRequest(
  @NotBlank(message = "種目名は必須です")
  String name,

  @NotNull(message = "回数は必須です")
  @Min(0)
  Integer reps,

  @NotNull(message = "セット数は必須です")
  @Min(0)
  Integer sets,
  
  @NotNull(message = "重量は必須です")
  @Min(0)
  Integer weights,

  @NotNull(message = "ユーザーIDは必須です")
  Long userId
) {}
