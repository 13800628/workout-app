package com.workout.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.workout.dto.workouts.UpdateWorkoutRequest;
import com.workout.dto.workouts.WorkoutRequest;
import com.workout.exception.user.UserDomainException;
import com.workout.exception.workout.WorkoutDomainException;
import com.workout.model.User;
import com.workout.model.Workout;
import com.workout.repository.UserRepository;
import com.workout.repository.WorkoutRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("WorkoutService ユニットテスト")
/**
 * WorkoutServiceTest
 */
public class WorkoutServiceTest {

  @Mock
  private WorkoutRepository workoutRepository;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private WorkoutService workoutService;

  private User owner;
  private User anothoUser;
  private Workout workout;

  private static final Long OWNER_ID = 1L;
  private static final Long ANOTHER_USER_ID = 2L;
  private static final Long WORKOUT_ID = 100L;

  @BeforeEach
  void setUp() {
    owner = new User("taro", 25, "password");
    ReflectionTestUtils.setField(owner, "id", OWNER_ID);

    anothoUser = new User("hanako", 30, "password2");
    ReflectionTestUtils.setField(anothoUser, "id", ANOTHER_USER_ID);

    workout = new Workout("ベンチプレス", 10, 3, 60, owner);
    ReflectionTestUtils.setField(workout, "id", WORKOUT_ID);
  }

  @Nested
  @DisplayName("createWorkout")
  class CreateWorkout {

    @Test
    @SuppressWarnings("null")
    @DisplayName("正常系: ユーザーが存在すればWorkoutを作成して保存する")
    void createWorkout_success() {
      WorkoutRequest request = new WorkoutRequest("スクワット", 10, 3, 80, OWNER_ID);
      given(userRepository.findById(OWNER_ID)).willReturn(Optional.of(owner));
      given(workoutRepository.save(any(Workout.class))).willAnswer(invocation -> invocation.getArgument(0));

      Workout result = workoutService.createWorkout(request);

      assertThat(result.getName()).isEqualTo("スクワット");
      assertThat(result.getReps()).isEqualTo(10);
      assertThat(result.getSets()).isEqualTo(3);
      assertThat(result.getWeights()).isEqualTo(80);
      assertThat(result.getUser()).isEqualTo(owner);

      ArgumentCaptor<Workout> captor = ArgumentCaptor.forClass(Workout.class);
      verify(workoutRepository).save(captor.capture());
      assertThat(captor.getValue().getUser().getId()).isEqualTo(OWNER_ID);
      verify(userRepository).findById(OWNER_ID);
    }

    @Test
    @DisplayName("異常系: ユーザーが存在しない場合はUserDomainExceptionを投げ、保存しない")
    void createWorkout_userNotFound_throwsUserDomainException() {
      Long nonExixtentUserId = 999L;
      WorkoutRequest request = new WorkoutRequest("スクワット", 10, 3, 80, nonExixtentUserId);
      given(userRepository.findById(nonExixtentUserId)).willReturn(Optional.empty());

      assertThatThrownBy(() -> workoutService.createWorkout(request))
          .isInstanceOf(UserDomainException.class);

      verify(workoutRepository, never()).save(any());
    }
  }
}