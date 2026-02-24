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

@ExtendWith(MockitoExtension.class)
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
  void updateName_名前が更新されて返る() {
    Long id = 1L;
    Workout workout = new Workout();
    workout.setId(id);
    workout.setName("before");

    when(workoutRepository.findById(id)).thenReturn(Optional.of(workout));
    when(workoutRepository.save(any(Workout.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Workout result = workoutService.updateName(id, "after");

    assertEquals("after", result.getName());
    verify(workoutRepository).findById(id);
    verify(workoutRepository).save(workout);
  }

  @Test
  void updateName_異常系_IDが存在しない場合は例外を投げる() {
    Long id = 999L;

    when(workoutRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () -> {
      workoutService.updateName(id, "after");
    });

    verify(workoutRepository, never()).save(any());
  }

  @Test
  void updateReps_回数が更新されて返る() {
    Long id = 1L;
    Workout workout = new Workout();
    workout.setId(id);
    workout.setReps(1);

    when(workoutRepository.findById(id)).thenReturn(Optional.of(workout));
    when(workoutRepository.save(any(Workout.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Workout result = workoutService.updateReps(id, 2);

    assertEquals(2, result.getReps());
    verify(workoutRepository).findById(id);
    verify(workoutRepository).save(workout);
  }

  @Test
  void updateSets_セット数が更新されて返る() {
    Long id = 1L;
    Workout workout = new Workout();
    workout.setId(id);
    workout.setSets(3);

    when(workoutRepository.findById(id)).thenReturn(Optional.of(workout));
    when(workoutRepository.save(any(Workout.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Workout result = workoutService.updateSets(id, 5);

    assertEquals(5, result.getSets());
    verify(workoutRepository).findById(id);
    verify(workoutRepository).save(workout);
  }

  @Test
  void updateWeights_重量が更新されて返る() {
    Long id = 1L;
    Workout workout = new Workout();
    workout.setId(id);
    workout.setWeights(80);

    when(workoutRepository.findById(id)).thenReturn(Optional.of(workout));
    when(workoutRepository.save(any(Workout.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Workout result = workoutService.updateWeights(id, 100);

    assertEquals(100, result.getWeights());
    verify(workoutRepository).findById(id);
    verify(workoutRepository).save(workout);
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

    WorkoutRequest request = new WorkoutRequest("after", 10, 10, 100, 1L);

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
    WorkoutRequest request = new WorkoutRequest("ベンチ", 23, 4, 60, 999L);


    when(workoutRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () -> {
      workoutService.updateAllDetails(id, request);
    });

    verify(workoutRepository).findById(id);
    verify(workoutRepository, never()).save(any());
  }

  @Test
  void updateAllDetails_異常系_回数の項目がnullの場合() {
    WorkoutRequest request = new WorkoutRequest("ベンチ", null, 3, 3, 1L);
    
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
        workoutService.updateAllDetails(1L, request);
    });

    assertEquals("回数は0回以上にしてください", exception.getMessage());
  }
}
