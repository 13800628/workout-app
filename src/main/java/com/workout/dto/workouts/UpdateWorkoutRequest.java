package com.workout.dto.workouts;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record UpdateWorkoutRequest(
  @NotBlank(message = "種目名は必須です")
  String name,

  @Min(1)
  Integer reps,

  @Min(1)
  Integer sets,
  
  @Positive
  Integer weights
) {}
