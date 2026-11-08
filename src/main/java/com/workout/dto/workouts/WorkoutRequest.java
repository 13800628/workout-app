package com.workout.dto.workouts;

import java.util.List;
import jakarta.validation.constraints.Size;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

// Controllerクラスで使うWorkoutRequestクラスの定義
// コードはシンプル
public record WorkoutRequest(
    @NotBlank(message = "種目名は必須です") String name,

    @NotEmpty(message = "セットを一つ以上入力してください") @Size(max = 50, message = "セット数は50件までです") @Valid List<WorkoutSetRequest> sets) {
}
