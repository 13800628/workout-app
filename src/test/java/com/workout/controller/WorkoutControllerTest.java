package com.workout.controller;


import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.springframework.http.MediaType;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workout.config.SecurityConfig;
import com.workout.dto.workouts.AllDetailsRequest;
import com.workout.dto.workouts.WorkoutRequest;
import com.workout.model.Workout;
import com.workout.service.WorkoutService;

@WebMvcTest(WorkoutController.class)
@Import(SecurityConfig.class)
class WorkoutControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private WorkoutService workoutService;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void createWorkout_正常系_201を返す() throws Exception {
    WorkoutRequest request = new WorkoutRequest();
    request.setName("ベンチ");
    request.setReps(10);
    request.setSets(3);
    request.setWeights(100);

    Workout savedWorkout = new Workout();
    savedWorkout.setId(1L);
    when(workoutService.createWorkout(any(WorkoutRequest.class))).thenReturn(savedWorkout);

    mockMvc.perform(post("/api/workouts/create")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andDo(print())
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.id").value(1));
  }

  @Test
  void getAllUsers_正常系_リストと200を返す() throws Exception {
    Long userId = 1L;
    List<Workout> workouts = List.of(new Workout(), new Workout());

    when(workoutService.getAllWorkoutById(userId)).thenReturn(workouts);

    mockMvc.perform(get("/api/workouts/{id}", userId))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2));
  }

  @Test
  void updateAllDetails_正常系_詳細を一括更新して200を返す() throws Exception {
    Long id = 1L;
    AllDetailsRequest request = new AllDetailsRequest();
    request.setName("スクワット");
    request.setReps(10);
    request.setSets(3);
    request.setWeights(100);

    Workout updated = new Workout();
    updated.setId(id);

    when(workoutService.updateAllDetails(eq(id), any(AllDetailsRequest.class))).thenReturn(updated);

    mockMvc.perform(put("/api/workouts/{id}/details", id)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  void deletedWorkout_正常系_204を返す() throws Exception {
    Long id = 1L;

    mockMvc.perform(delete("/api/workouts/{id}", id))
        .andExpect(status().isNoContent());

    verify(workoutService, times(1)).deletedWorkout(id);
  }
}
