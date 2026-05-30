package com.workout.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.workout.model.Workout;
import com.workout.service.WorkoutService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/workouts")
// @CrossOrigin(origins = "") のちに新しいページ追加
public class WorkoutController {

  @Autowired
  private WorkoutService workoutService; // のちに実装

  // メソッド GET, POST, DELETEをまず実装

  // Create - 作成
  @PostMapping("/create")
  public ResponseEntity<Workout> createWorkout(@Valid @RequestBody WorkoutRequest request) {
    Workout workout = workoutService.createWorkout(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(workout);
  }

  @GetMapping("/{id}")
  public ResponseEntity<List<Workout>> getAllWorkoutsById(@PathVariable Long id) {
    List<Workout> workouts = workoutService.getAllWorkoutById(id);
    return ResponseEntity.ok(workouts);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteWorkout(@PathVariable("id") Long id) {
    workoutService.deletedWorkout(id);

    return ResponseEntity.noContent().build();
  }

  /**
   * 
   * @param id
   * @param request
   * @return
   */

  @PutMapping("/{id}/details")
  public ResponseEntity<Workout> updateDetails(@PathVariable Long id, @Valid @RequestBody UpdateWorkoutRequest request) {
    Workout updated = workoutService.updateAllDetails(id, request);

    return ResponseEntity.ok(updated);
  }
}
