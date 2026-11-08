package com.workout.dto.workouts;

import java.time.LocalDateTime;
import java.util.List;

import com.workout.model.Workout;

public record WorkoutResponse(
    Long id,
    String name,
    List<WorkoutSetResponse> sets,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {

  public static WorkoutResponse from(Workout workout) {
    if (workout == null)
      return null;
    return new WorkoutResponse(
        workout.getId(),
        workout.getName(),
        workout.getSets().stream().map(WorkoutSetResponse::from).toList(),
        workout.getCreatedAt(),
        workout.getUpdatedAt());
  }
}
