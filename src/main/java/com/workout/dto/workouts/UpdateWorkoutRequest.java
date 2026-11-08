package com.workout.dto.workouts;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public record UpdateWorkoutRequest(
    @NotBlank(message = "種目名は必須です") String name,

    @NotEmpty(message = "セットを1つ以上入力してください") @Size(max = 50, message = "セットは50件までです") @Valid List<WorkoutSetRequest> sets) {
}
