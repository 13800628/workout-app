package com.workout.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.workout.dto.workoutdto.AllDetailsRequest;
import com.workout.dto.workoutdto.WorkoutRequest;
import com.workout.model.User;
import com.workout.model.Workout;
import com.workout.repository.UserRepository;
import com.workout.repository.WorkoutRepository;

@Service
public class WorkoutService {
 
  
  private WorkoutRepository workoutRepository;
  private UserRepository userRepository;

  public WorkoutService(UserRepository userRepository, WorkoutRepository workoutRepository) {
    this.userRepository = userRepository;
    this.workoutRepository = workoutRepository;
  }
  @Transactional
  public Workout createWorkout(WorkoutRequest request) {
    User user = null;
    if (request.getUserId() != null) {
     user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("ユーザーが見つかりません: " + request.getUserId()));
    }

    Workout workout = new Workout(
        request.getName(),
        request.getReps(),
        request.getSets(),
        request.getWeights(),
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
    return workoutRepository.findById(id)
           .orElseThrow(() -> new IllegalArgumentException("Workoutが見つかりません: " + id));
  }
  
  // booleanか引数なしかは今後検討(仮)
  @Transactional
  public boolean deletedWorkout(Long id) {
    if (!workoutRepository.existsById(id)) {
      return false;
    }
    workoutRepository.deleteById(id);
    return true;
  }

  @Transactional
  public Workout updateName(Long id, String name) {
    Workout workout = getWorkoutById(id);
    workout.setName(name);
    return workoutRepository.save(workout);
  }

  public Workout updateReps(Long id, Integer reps) {
    Workout workout = getWorkoutById(id);
    workout.setReps(reps);
    return workoutRepository.save(workout);
  }
  
  public Workout updateSets(Long id, Integer sets) {
    Workout workout = getWorkoutById(id);
    workout.setSets(sets);

    return workoutRepository.save(workout);
  }
  
  public Workout updateWeights(Long id, Integer weights) {
    Workout workout = getWorkoutById(id);
    workout.setWeights(weights);
    return workoutRepository.save(workout);
  }

  @Transactional
  public Workout updateAllDetails(Long id, AllDetailsRequest request) {
    Workout workout = getWorkoutById(id);

    workout.updateAllWorkoutDetails(
        request.getName(),
        request.getReps(),
        request.getSets(),
        request.getWeights()
    );

    return workoutRepository.save(workout);
  }
}
