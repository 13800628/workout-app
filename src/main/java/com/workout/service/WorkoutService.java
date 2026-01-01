package com.workout.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.workout.controller.AllDetailsRequest;
import com.workout.model.User;
import com.workout.model.Workout;
import com.workout.repository.UserRepository;
import com.workout.repository.WorkoutRepository;

@Service
public class WorkoutService {
 
  @Autowired
  private WorkoutRepository workoutRepository;
  private UserRepository userRepository;

  public WorkoutService(UserRepository userRepository, WorkoutRepository workoutRepository) {
    this.userRepository = userRepository;
    this.workoutRepository = workoutRepository;
  }
  
  public Workout createWorkout(String name, Integer reps, Integer sets, Integer weights, Long userId) {
    User user = null;
    if (userId != null) {
      user = userRepository.findById(userId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + userId));
    }
    Workout workout = new Workout(name, reps, sets, weights, user);
    return workoutRepository.save(workout);
  }

  // GET/POST/DELETEの実装
  public List<Workout> getAllWorkoutById(Long id) {
    return workoutRepository.findByUserId(id);
  }
  
  // booleanか引数なしかは今後検討(仮)
  @Transactional
  public boolean deletedWorkout(Long id) {
    if (workoutRepository.existsById(id)) {
      workoutRepository.deleteById(id);
      return true;
    } else {
      return false;
    }
  }

  public Workout updateName(Long id, String name) {
    Workout existingWorkout = workoutRepository.findById(id).orElseThrow(() -> new RuntimeException("Workout not found"));
    existingWorkout.setName(name);
    return workoutRepository.save(existingWorkout);
  }

  public Workout updateReps(Long id, Integer reps) {
    Workout existingWorkout = workoutRepository.findById(id).orElseThrow(() -> new RuntimeException("Workout not found"));
    existingWorkout.setReps(reps);
    return workoutRepository.save(existingWorkout);
  }
  
  public Workout updateSets(Long id, Integer sets) {
    Workout exstingWorkout = workoutRepository.findById(id).orElseThrow(() -> new RuntimeException("Workout not found"));
    exstingWorkout.setSets(sets);
    return workoutRepository.save(exstingWorkout);
  }
  
  public Workout updateWeights(Long id, Integer weights) {
    Workout exsitingWorkout = workoutRepository.findById(id).orElseThrow(() -> new RuntimeException("Workout not found"));
    exsitingWorkout.setWeights(weights);
    return workoutRepository.save(exsitingWorkout);
  }

  @Transactional
  public Workout updateAllDetails(Long id, AllDetailsRequest request) {
    Workout existingWorkout = workoutRepository.findById(id).orElseThrow(() -> new RuntimeException("Workout not found"));

    existingWorkout.setName(request.getName());
    existingWorkout.setReps(request.getReps());
    existingWorkout.setSets(request.getSets());
    existingWorkout.setWeights(request.getWeights());


    return workoutRepository.save(existingWorkout);
  }
}

// エラーはWorkout,WorkoutRepositoryクラスの実装で解決