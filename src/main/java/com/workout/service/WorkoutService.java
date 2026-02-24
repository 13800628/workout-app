package com.workout.service;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.workout.dto.workouts.WorkoutRequest;
import com.workout.model.User;
import com.workout.model.Workout;
import com.workout.model.WorkoutValidator;
import com.workout.repository.UserRepository;
import com.workout.repository.WorkoutRepository;

import static java.util.Objects.requireNonNull;

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
    WorkoutValidator.validateAll(request.name(), request.reps(), request.sets(), request.weights());

    User user = Optional.ofNullable(request.userId())
                .flatMap(id -> userRepository.findById(requireNonNull(id)))
                .orElse(null);

    if (request.userId() != null && user == null) {
      throw new IllegalArgumentException("見つかりません" + request.userId());
    }
                

    Workout workout = new Workout(
        request.name(),
        request.reps(),
        request.sets(),
        request.weights(),
        user
    );
    return workoutRepository.save(workout);
  }


  // GET/POST/DELETEの実装
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
  
  // booleanか引数なしかは今後検討(仮)
  @Transactional
  public void deletedWorkout(Long id) {
    int deletedCount = workoutRepository.deleteDirectlyById(id);

    if (deletedCount == 0) {
      throw new IllegalArgumentException("Workout ID: " + id + "は存在しません");
    }
  }

  @Transactional
  public Workout updateName(Long id, String name) {
    WorkoutValidator.validateName(name);
    return updateField(id, workout -> workout.setName(name));
  }

  public Workout updateReps(Long id, Integer reps) {
    WorkoutValidator.validateReps(reps);
    return updateField(id, workout -> workout.setReps(reps));
  }
  
  public Workout updateSets(Long id, Integer sets) {
    WorkoutValidator.validateSets(sets);
    return updateField(id, workout -> workout.setSets(sets));
  }
  
  public Workout updateWeights(Long id, Integer weights) {
    WorkoutValidator.validateWeights(weights);
    return updateField(id, workout -> workout.setWeights(weights));
  }

  @Transactional
  public Workout updateAllDetails(Long id, WorkoutRequest request) {
    WorkoutValidator.validateAll(request.name(), request.reps(), request.sets(), request.weights());
    
    Workout workout = getWorkoutById(id);

    workout.updateAllWorkoutDetails(
        request.name(),
        request.reps(),
        request.sets(),
        request.weights()
    );

    return workoutRepository.save(workout);
  }

  /**
   * 更新系共通テンプレ
   */
  private Workout updateField(Long id, Consumer<Workout> updateLogic) {
    Workout workout = getWorkoutById(id);
    Workout saved = workoutRepository.save(workout);
    updateLogic.accept(workout);
    return saved;
  }
}
