package com.workout.dto.workouts;

import java.time.LocalDateTime;

import com.workout.model.Workout;

public record WorkoutResponse(
  Long id,
  String name,
  Integer reps,
  Integer sets,
  Integer weights,
  LocalDateTime createdAt,
  LocalDateTime updatedAt
) {

  public static WorkoutResponse from(Workout workout) {
    if (workout == null) return null;
    return new WorkoutResponse(
      workout.getId(),
      workout.getName(),
      workout.getReps(),
      workout.getSets(),
      workout.getWeights(),
      workout.getCreatedAt(),
      workout.getUpdatedAt()
    );
  }
} 
