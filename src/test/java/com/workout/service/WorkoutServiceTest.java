package com.workout.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import com.workout.controller.AllDetailsRequest;
import com.workout.controller.WorkoutRequest;
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
  private WorkoutService service;


  

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

    AllDetailsRequest request = new AllDetailsRequest();
    request.setName("after");
    request.setReps(10);
    request.setSets(10);
    request.setWeights(100);

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
}
