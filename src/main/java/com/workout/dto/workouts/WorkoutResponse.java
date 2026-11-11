package com.workout.dto.workouts;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import com.workout.model.Workout;

public record WorkoutResponse(
    Long id,
    String name,
    List<WorkoutSetResponse> sets,
    Instant createdAt,
    Instant updatedAt) {

  public static WorkoutResponse from(Workout workout) {
    if (workout == null)
      return null;
    return new WorkoutResponse(
        workout.getId(),
        workout.getName(),
        workout.getSets().stream().map(WorkoutSetResponse::from).toList(),
        toInstant(workout.getCreatedAt()),
        toInstant(workout.getUpdatedAt()));
  }

  private static Instant toInstant(LocalDateTime utcDateTime) {
    return utcDateTime == null ? null : utcDateTime.toInstant(ZoneOffset.UTC);
  }
}
