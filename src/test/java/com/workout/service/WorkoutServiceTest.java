package com.workout.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.workout.dto.workouts.WorkoutRequest;
import com.workout.model.User;
import com.workout.model.Workout;
import com.workout.repository.UserRepository;
import com.workout.repository.WorkoutRepository;
import com.workout.dto.workouts.UpdateWorkoutRequest;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class WorkoutServiceTest {
  @Mock
  private UserRepository userRepository;
  
  @Mock
  private WorkoutRepository workoutRepository;


  @InjectMocks
  private WorkoutService workoutService;

  @Test
  void createWorkout_正常系_Idによって作成() {
    Long userId = 1L;
    WorkoutRequest request = new WorkoutRequest("ベンチプレス", 10, 3, 60, userId);

    User testUser = new User();
    testUser.setId(userId);
    testUser.setUsername("テストユーザー");

    when (userRepository.findById(userId)).thenReturn(Optional.of(testUser));
    when (workoutRepository.save(any(Workout.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Workout result = workoutService.createWorkout(request);

    assertNotNull(result);
    assertEquals("ベンチプレス", result.getName());
    assertEquals(testUser, result.getUser());
    verify(userRepository, times(1)).findById(userId);
  }

  @Test
  void update_名前が更新されて返る() {
    Long id = 1L;
    Workout workout = new Workout();
    workout.setId(id);
    workout.setName("before");
    workout.setReps(1);

    when(workoutRepository.findById(id)).thenReturn(Optional.of(workout));

    // 「何をどう更新するか」をラムダで渡す
    workoutService.update(id, w -> w.setName("after"));
    workoutService.update(id, w -> w.setReps(2));

    assertEquals("after", workout.getName());
    assertEquals(2, workout.getReps());
    verify(workoutRepository, times(2)).findById(id);
  }

  @Test
  void update_IDが存在しない場合は例外を投げる() {
    Long id = 999L;
    when(workoutRepository.findById(id)).thenReturn(Optional.empty());

    // どの項目を更新しようとしてもエラーになることを確認
    assertThrows(IllegalArgumentException.class, () -> workoutService.update(id, w -> w.setName("after")));
  }


  @Test
  void updateAllDetails_全てが更新されて返る() {
    Long id = 1L;
    Workout workout = new Workout();
    workout.setId(id);
    workout.setName("before");
    workout.setReps(1);
    workout.setSets(1);
    workout.setWeights(10);

    UpdateWorkoutRequest request = new UpdateWorkoutRequest("after", 10, 10, 100);

    when(workoutRepository.findById(id)).thenReturn(Optional.of(workout));
    when(workoutRepository.save(any(Workout.class))).thenAnswer(invocation -> invocation.getArgument(0));
 
    Workout result = workoutService.updateAllDetails(id, request);

    assertEquals("after", result.getName());
    assertEquals(10, result.getReps());
    assertEquals(10, result.getSets());
    assertEquals(100, result.getWeights());

    verify(workoutRepository).findById(id);
    verify(workoutRepository).save(workout);
  }

  @Test
  void updateAllDetails_異常系_IDが存在しない場合はRuntimeExceptionを投げる() {
    Long id = 999L;
    UpdateWorkoutRequest request = new UpdateWorkoutRequest("ベンチ", 23, 4, 60);


    when(workoutRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () -> {
      workoutService.updateAllDetails(id, request);
    });

    verify(workoutRepository).findById(id);
    verify(workoutRepository, never()).save(any());
  }
}
