package com.workout.model;

public interface WorkoutValidator {
  
  static void validateAll(String name, Integer reps, Integer sets, Integer weights) {
    validateName(name);
    validateReps(reps);
    validateSets(sets);
    validateWeights(weights);
  }

  static void validateName(String name) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("トレーニング名は必須です");
    }
  }

  static void validateReps(Integer reps) {
    if (reps == null || reps < 0) {
      throw new IllegalArgumentException("回数は0回以上にしてください");
    }
  }

  static void validateSets(Integer sets) {
    if (sets == null || sets < 0) {
      throw new IllegalArgumentException("セット数は0回以上にしてください");
    }
  }

  static void validateWeights(Integer weights) {
    if (weights == null || weights < 0) {
      throw new IllegalArgumentException("重量は0以上にしてください");
    }
  }
}
