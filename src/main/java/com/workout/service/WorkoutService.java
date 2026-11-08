package com.workout.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.workout.dto.workouts.UpdateWorkoutRequest;
import com.workout.exception.user.UserDomainException;
import com.workout.exception.workout.WorkoutDomainException;
import com.workout.dto.workouts.WorkoutRequest;
import com.workout.dto.workouts.WorkoutSetRequest;
import com.workout.model.User;
import com.workout.model.Workout;
import com.workout.model.WorkoutSet;
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
  public Workout createWorkout(Long userId, WorkoutRequest request) {
    // 同じく誤検知
    User user = userRepository.findById(userId)
        .orElseThrow(() -> UserDomainException.notFound("ユーザーが見つかりません"));

    Workout workout = new Workout(request.name(), user);
    workout.replaceSets(toWorkoutSets(request.sets()));

    return workoutRepository.save(workout);
  }

  @Transactional(readOnly = true)
  public List<Workout> getAllWorkoutById(Long id) {
    return workoutRepository.findByUserId(id);
  }

  @Transactional(readOnly = true)
  public Workout getWorkoutById(Long exercisedId) {
    if (exercisedId == null) {
      throw new IllegalArgumentException("IDを指定してください");
    }
    return workoutRepository.findById(exercisedId)
        .orElseThrow(() -> WorkoutDomainException.notFound("Workoutが見つかりません: " + exercisedId));
  }

  @Transactional
  public void deleteWorkout(Long exercisedId, Long userId) {
    Workout workout = findOwnedWorkout(exercisedId, userId);
    workoutRepository.delete(workout);
  }

  @Transactional
  public Workout updateAllDetails(Long exercisedId, Long userId, UpdateWorkoutRequest request) {
    Workout workout = findOwnedWorkout(exercisedId, userId);

    workout.updateAllWorkoutDetails(
        request.name(),
        toWorkoutSets(request.sets()));
    return workoutRepository.save(workout);
  }

  // 使われている箇所が少ない。
  private Workout findOwnedWorkout(Long exercisedId, Long userId) {
    return workoutRepository.findByIdAndUserId(exercisedId, userId)
        .orElseThrow(() -> WorkoutDomainException.notFound("Workout ID " + exercisedId + "は存在していません"));
  }

  private List<WorkoutSet> toWorkoutSets(List<WorkoutSetRequest> requests) {
    return requests.stream()
        .map(r -> new WorkoutSet(r.reps(), r.weights()))
        .toList();
  }
}
