package com.workout.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.workout.dto.workouts.UpdateWorkoutRequest;
import com.workout.dto.workouts.WorkoutRequest;
import com.workout.model.User;
import com.workout.model.Workout;
import com.workout.repository.UserRepository;
import com.workout.repository.WorkoutRepository;


@Service
public class WorkoutService {
 
  private final WorkoutRepository workoutRepository;
  private final UserRepository userRepository;

  public WorkoutService(UserRepository userRepository, WorkoutRepository workoutRepository) {
    this.userRepository = userRepository;
    this.workoutRepository = workoutRepository;
  }

  @Transactional
  public Workout createWorkout(WorkoutRequest request) {
    User user = (request.userId() != null)
        ? userRepository.findById(request.userId())
            .orElseThrow(() -> new IllegalArgumentException("ユーザーが見つかりません"))
        : null;

    return workoutRepository.save(new Workout(
        request.name(), request.reps(), request.sets(), request.weights(), user));
  }

  @Transactional(readOnly = true)
  public List<Workout> getAllWorkoutById(Long id) {
    return workoutRepository.findByUserId(id);
  }

  @Transactional(readOnly = true)
  public Workout getWorkoutById(Long id) {
     if (id == null) {
      throw new IllegalArgumentException("IDを指定してください");
     }
    return workoutRepository.findById(id)
           .orElseThrow(() -> new IllegalArgumentException("Workoutが見つかりません: " + id));
  }
  
  @Transactional
  public void deleteWorkout(Long id) {
    int deletedCount = workoutRepository.deleteDirectlyById(id);

    if (deletedCount == 0) {
      throw new IllegalArgumentException("Workout ID: " + id + "は存在しません");
    }
  }

  @Transactional
  public Workout updateAllDetails(Long id, UpdateWorkoutRequest request) {    
    Workout workout = getWorkoutById(id);

    workout.updateAllWorkoutDetails(
        request.name(),
        request.reps(),
        request.sets(),
        request.weights()
    );

    return workoutRepository.save(workout);
  }
}
