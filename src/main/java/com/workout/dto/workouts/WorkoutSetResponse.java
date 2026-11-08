package com.workout.dto.workouts;

import com.workout.model.WorkoutSet;

public record WorkoutSetResponse(
    Integer setOrder,
    Integer reps,
    Integer weights) {

  public static WorkoutSetResponse from(WorkoutSet set) {
    return new WorkoutSetResponse(
        set.getSetOrder(),
        set.getReps(),
        set.getWeights());
  }
}
