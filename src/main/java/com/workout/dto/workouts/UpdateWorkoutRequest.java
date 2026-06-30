package com.workout.dto.workouts;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record UpdateWorkoutRequest(
  @NotBlank(message = "種目名は必須です")
  String name,

  @Min(0)
  Integer reps,

  @Min(0)
  Integer sets,
  
  @Min(0)
  Integer weights
) {}
