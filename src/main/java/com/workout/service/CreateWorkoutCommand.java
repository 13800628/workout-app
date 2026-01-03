package com.workout.service;

public record CreateWorkoutCommand(String name, Integer reps, Integer sets, Integer weights, Long userId) {
}
