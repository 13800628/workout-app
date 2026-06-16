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
import com.workout.dto.workouts.UpdateWorkoutRequest;
import com.workout.model.User;
import com.workout.model.Workout;
import com.workout.service.UserService;
import com.workout.service.WorkoutService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/workouts")
public class WorkoutController {

  private final WorkoutService workoutService; 
  private final UserService userService;

  //コンストラクタインジェクションに変更
  public WorkoutController(WorkoutService workoutService, UserService userService) {
    this.workoutService = workoutService;
    this.userService = userService;
  }

  // ユーザーIDの比較のための関数
  private void validateOwner(Long userId, Authentication auth) {
    User loginUser = userService.getUserByUsername(auth.getName());
    if (!loginUser.getId().equals(userId)) {
      throw new IllegalArgumentException("アクセス権限がありません");
    }
  }

  // Workoutのオーナー確認用
  private void validateWorkoutOwner(Long workoutId, Authentication auth) {
    User loginUser = userService.getUserByUsername(auth.getName());
    Workout workout = workoutService.getWorkoutById(workoutId);
    if (!workout.getUser().getId().equals(loginUser.getId())) {
      throw new IllegalArgumentException("アクセス権限がありません");
    }
  }

  // メソッド GET, POST, DELETEをまず実装

  // Create - 作成
  @PostMapping("/create")
  public ResponseEntity<Workout> createWorkout(
    @Valid @RequestBody WorkoutRequest request,
    Authentication auth) {
    validateOwner(request.userId(), auth);
    Workout workout = workoutService.createWorkout(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(workout);
  }

  @GetMapping("/{id}")
  public ResponseEntity<List<Workout>> getAllWorkoutsById(
    @PathVariable Long id,
    Authentication auth) {
    validateOwner(id, auth);
    List<Workout> workouts = workoutService.getAllWorkoutById(id);
    return ResponseEntity.ok(workouts);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteWorkout(
    @PathVariable("id") Long id,
    Authentication auth) {
    validateWorkoutOwner(id, auth);
    workoutService.deleteWorkout(id);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/{id}/details")
  public ResponseEntity<Workout> updateDetails(
    @PathVariable Long id, 
    @Valid @RequestBody UpdateWorkoutRequest request,
    Authentication auth) {
    validateWorkoutOwner(id, auth);
    Workout updated = workoutService.updateAllDetails(id, request);
    return ResponseEntity.ok(updated);
  }
}
