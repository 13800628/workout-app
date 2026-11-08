package com.workout.dto.workouts;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record WorkoutSetRequest(
    @NotNull(message = "回数は必須です") @Min(value = 0, message = "回数は0回以上にしてください") Integer reps,

    @NotNull(message = "重量は必須です") @Min(value = 0, message = "重量は0以上にしてください") Integer weights) {
}
