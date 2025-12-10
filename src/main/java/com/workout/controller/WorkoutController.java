package com.workout.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.workout.model.Workout;
import com.workout.service.WorkoutService;

@RestController
@RequestMapping("/api/workouts")
@CrossOrigin(origins = "http://localhost:5173")
//@CrossOrigin(origins = "") のちに新しいページ追加
public class WorkoutController {
  
  @Autowired
  private WorkoutService workoutService; // のちに実装
  
  // メソッド　GET, POST, DELETEをまず実装
  
  // Create - 作成
  @PostMapping("/create")
  public ResponseEntity<Workout> createWorkout(@RequestBody WorkoutRequest request) {
    Workout workout = workoutService.createWorkout(request.getName(), request.getReps(), request.getSets(), request.getWeights(), request.getUserId());
    return new ResponseEntity<>(workout, HttpStatus.CREATED);
  }
  
  @GetMapping("/{id}")
  public ResponseEntity<List<Workout>> getAllWorkoutsById(@PathVariable Long id) {
    List<Workout> workouts = workoutService.getAllWorkoutById(id);
    return new ResponseEntity<>(workouts, HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteWorkout(@PathVariable("id") Long id) {
    boolean deleted = workoutService.deletedWorkout(id);
    if (deleted) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT); 
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @PutMapping("/{id}/setName")
public ResponseEntity<Workout> updateName(
        @PathVariable Long id,
        @RequestBody NameRequest request
) {
    Workout updated = workoutService.updateName(id, request.getName());
    return ResponseEntity.ok(updated);
}

@PutMapping("/{id}/setReps")
public ResponseEntity<Workout> updateReps(
        @PathVariable Long id,
        @RequestBody RepsRequest request
) {
    Workout updated = workoutService.updateReps(id, request.getReps());
    return ResponseEntity.ok(updated);
}

@PutMapping("/{id}/setSets")
public ResponseEntity<Workout> updateSets(
        @PathVariable Long id,
        @RequestBody SetsRequest request
) {
    Workout updated = workoutService.updateSets(id, request.getSets());
    return ResponseEntity.ok(updated);
}

@PutMapping("/{id}/setWeights")
public ResponseEntity<Workout> updateWeights(
         @PathVariable Long id,
         @RequestBody WeightsRequest request
) {
  Workout updated = workoutService.updateWeights(id, request.getWeights());
  return ResponseEntity.ok(updated);
}

// upteUserId{/{id}/setsUserId}の実装

/**
 * 
 * @param id
 * @param request
 * @return
 */

@PutMapping("/{id}/details")
public ResponseEntity<Workout> updateDatails(
        @PathVariable Long id,
        @RequestBody AllDetailsRequest request
) {
  Workout updated = workoutService.updateAllDetails(id, request);
  return ResponseEntity.ok(updated);
}
}




// エラー箇所はWorkoutクラスとWorkoutServiceクラスの実装