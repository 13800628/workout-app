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

  // ユーザーIDの比較のための関数
  private void validateOwner(Long userId, Authentication auth) {
    CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
    if (!principal.getUserId().equals(userId)) {
      throw new IllegalArgumentException("アクセス権限がありません");
    }
  }

  // Workoutのオーナー確認用
  private void validateWorkoutOwner(Long workoutId, Authentication auth) {
    CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
    Workout workout = workoutService.getWorkoutById(workoutId);
    if (!workout.getUser().getId().equals(principal.getUserId())) {
      throw new IllegalArgumentException("アクセス権限がありません");
    }
  }


  // HTTPステータスが見えてるから関数に切り出しか？
  // Create - 作成
  @PostMapping("/create")
  public ResponseEntity<WorkoutResponse> createWorkout(
    @Valid @RequestBody WorkoutRequest request,
    Authentication auth) {
    validateOwner(request.userId(), auth);
    Workout workout = workoutService.createWorkout(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(WorkoutResponse.from(workout));
  }

  // ここはUserIdを使ってGETするのでここだけエンドポイントが違う
  @GetMapping("/user/{id}")
  public ResponseEntity<List<WorkoutResponse>> getAllWorkoutsById(
    @PathVariable Long userId,
    Authentication auth) {
    validateOwner(userId, auth);
    List<WorkoutResponse> workouts = workoutService.getAllWorkoutById(userId)
     .stream()
     .map(WorkoutResponse::from)
     .toList();
    return ResponseEntity.ok(workouts);
  }

  @DeleteMapping("/{workoutId}")
  public ResponseEntity<Void> deleteWorkout(
    @PathVariable Long workoutId,
    Authentication auth) {
    validateWorkoutOwner(workoutId, auth);
    CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
    workoutService.deleteWorkout(workoutId, principal.getUserId());
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/{workoutId}/details")
  public ResponseEntity<WorkoutResponse> updateDetails(
    @PathVariable Long workoutId, 
    @Valid @RequestBody UpdateWorkoutRequest request,
    Authentication auth) {
    validateWorkoutOwner(workoutId, auth);
    Workout updated = workoutService.updateAllDetails(workoutId, request);
    return ResponseEntity.ok(WorkoutResponse.from(updated));
  }
}
