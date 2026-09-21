package com.workout.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.workout.dto.workouts.WorkoutRequest;
import com.workout.dto.workouts.WorkoutResponse;
import com.workout.config.CustomUserDetails;
import com.workout.dto.workouts.UpdateWorkoutRequest;

import com.workout.model.Workout;
import com.workout.service.WorkoutService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/workouts")
public class WorkoutController {

  private final WorkoutService workoutService; 

  //コンストラクタインジェクションに変更
  public WorkoutController(WorkoutService workoutService) {
    this.workoutService = workoutService;
  }

  private Long currentUserId(Authentication auth) {
    CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
    return principal.getUserId();
  }

  // Create - 作成
  @PostMapping
  public ResponseEntity<WorkoutResponse> createWorkout(
    @Valid @RequestBody WorkoutRequest request,
    Authentication auth) {
    Workout workout = workoutService.createWorkout(currentUserId(auth), request);
    return ResponseEntity.status(HttpStatus.CREATED).body(WorkoutResponse.from(workout));
  }

  @GetMapping("/me")
  public ResponseEntity<List<WorkoutResponse>> getMyWorkouts(
    Authentication auth) {
    List<WorkoutResponse> workouts = workoutService.getAllWorkoutById(currentUserId(auth))
     .stream()
     .map(WorkoutResponse::from)
     .toList();
    return ResponseEntity.ok(workouts);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteWorkout(
    @PathVariable Long id,
    Authentication auth) {
    workoutService.deleteWorkout(id, currentUserId(auth));
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/{id}")
  public ResponseEntity<WorkoutResponse> updateDetails(
    @PathVariable Long id,
    @Valid @RequestBody UpdateWorkoutRequest request,
    Authentication auth) {
    Workout updated = workoutService.updateAllDetails(id, currentUserId(auth), request);
    return ResponseEntity.ok(WorkoutResponse.from(updated));
  }
}
