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

  @Nested
  @DisplayName("getAllWorkoutById")
  class GetAllWorkoutById {

    @Test
    @DisplayName("正常系: ユーザーに紐づくWokout一覧を返す")
    void getAllWorkoutById_returnsList() {

    Workout workout2 = new Workout("デッドリフト", 8, 3, 100, owner);
    ReflectionTestUtils.setField(workout2, "id", 101L);
    given(workoutRepository.findByUserId(OWNER_ID)).willReturn(List.of(workout, workout2));

    List<Workout> result = workoutService.getAllWorkoutById(OWNER_ID);

    assertThat(result).hasSize(2).containsExactly(workout, workout2);
    verify(workoutRepository).findByUserId(OWNER_ID);
    }

    @Test
    @DisplayName("Workoutが一件もない場合は空リストを返す")
    void getAllWorkoutById_returnsEmptyList_whenNoWorkout() {
      given(workoutRepository.findByUserId(ANOTHER_USER_ID)).willReturn(List.of());

      List<Workout> result = workoutService.getAllWorkoutById(ANOTHER_USER_ID);

      assertThat(result).isEmpty();
    }
  }

  // 後々のテスト追加
  @Nested
  @DisplayName("getWorkoutById")
  class GetWorkoutById {

    @Test
    @DisplayName("正常系: 存在するIDならWorkoutを返す")
    void getWorkoutById_success() {
      given(workoutRepository.findById(WORKOUT_ID)).willReturn(Optional.of(workout));

      Workout result = workoutService.getWorkoutById(WORKOUT_ID);
      assertThat(result).isEqualTo(workout);
    }

    @Test
    @DisplayName("異常系: 存在しないIDならWorkoutDomainExceptionを投げる")
    void getWorkoutById_notFound_throwsWorkoutDomainException() {
      Long nonExistentId = 999L;
      given(workoutRepository.findById(nonExistentId)).willReturn(Optional.empty());

      assertThatThrownBy(() -> workoutService.getWorkoutById(nonExistentId))
        .isInstanceOf(WorkoutDomainException.class)
        .hasMessageContaining(String.valueOf(nonExistentId));
    }

    @Test
    @DisplayName("IDがnullの場合はIllegalArgumentExceptionを投げ、保存しない")
    void getWorkoutById_nullId_throwsIllegalArgumentException() {
      assertThatThrownBy(() -> workoutService.getWorkoutById(null))
        .isInstanceOf(IllegalArgumentException.class);
      
        verify(workoutRepository, never()).findById(any());
    }
  }

  @Nested
  @DisplayName("deleteWorkout")
  class DeleteWorkout {

    @Test
    @DisplayName("正常系: 所有者本人が削除すればdeleteCount=1で例外は投げない")
    void deleteWorkout_success() {
      given(workoutRepository.deleteDirectlyByIdAndUserId(WORKOUT_ID, OWNER_ID));

      assertThatCode(() -> workoutService.deleteWorkout(WORKOUT_ID, OWNER_ID));

      verify(workoutRepository).deleteDirectlyByIdAndUserId(WORKOUT_ID, OWNER_ID);
    }

    @Test
    @DisplayName("異常系: IDが存在しない場合はWorkoutDomainExceptionを投げる")
    void deleteWorkout_notFound_throwsWorkoutDomainException() {
      Long nonExistentId = 999L;
      given(workoutRepository.deleteDirectlyByIdAndUserId(nonExistentId, OWNER_ID)).willReturn(0);

      assertThatThrownBy(() -> workoutService.deleteWorkout(nonExistentId, OWNER_ID))
          .isInstanceOf(WorkoutDomainException.class);
    }


    @Test
    @DisplayName("所有者と異なるuserIdで削除しようとするとdeletedCount=0となり例外")
    void deleteWorkout_wrongOwner_throwsWorkoutDomainException() {
      given(workoutRepository.deleteDirectlyByIdAndUserId(WORKOUT_ID, ANOTHER_USER_ID)).willReturn(0);

      assertThatThrownBy(() -> workoutService.deleteWorkout(WORKOUT_ID, ANOTHER_USER_ID))
        .isInstanceOf(WorkoutDomainException.class);

      verify(workoutRepository).deleteDirectlyByIdAndUserId(WORKOUT_ID, ANOTHER_USER_ID);
      verify(workoutRepository, never()).deleteDirectlyByIdAndUserId(WORKOUT_ID, OWNER_ID);
    }
  }

  @Nested
  @DisplayName("updateAllDetails")
  class UpdateAllDetails {

    @Test
    @DisplayName("正常系: 存在するWorkoutを更新して保存する")
    void updateAllDetails_success() {
      UpdateWorkoutRequest request = new UpdateWorkoutRequest("懸垂", 12, 4, 0);
      given(workoutRepository.findById(WORKOUT_ID)).willReturn(Optional.of(workout));
      given(workoutRepository.save(any(Workout.class))).willAnswer(invocation -> invocation.getArgument(0));

      Workout result = workoutService.updateAllDetails(WORKOUT_ID, request);

      assertThat(result.getName()).isEqualTo("懸垂");
      assertThat(result.getReps()).isEqualTo(12);
      assertThat(result.getSets()).isEqualTo(4);
      assertThat(result.getWeights()).isEqualTo(0);
      verify(workoutRepository).save(workout);
    }

    @Test
    @DisplayName("異常系: 存在しないIDの場合はWorkoutDomainExceptionを投げ保存しない")
    void updateAllDetails_notFound_throwsWorkoutDomainException() {
      Long nonExistentId = 999L;
      UpdateWorkoutRequest request = new UpdateWorkoutRequest("懸垂", 12, 4, 0);
      given(workoutRepository.findById(nonExistentId)).willReturn(Optional.empty());

      assertThatThrownBy(() -> workoutService.updateAllDetails(nonExistentId, request))
        .isInstanceOf(WorkoutDomainException.class);

      verify(workoutRepository, never()).save(any());
    }

    @Test
    @DisplayName("異常系: 更新値が不正(repsなどがnull)な場合は例外")
    void updateAllDetails_invalidValues_throwsIllegalArgumentException() {
      UpdateWorkoutRequest request = new UpdateWorkoutRequest("懸垂", -1, 4, 0);
      given(workoutRepository.findById(WORKOUT_ID)).willReturn(Optional.of(workout));

      assertThatThrownBy(() -> workoutService.updateAllDetails(WORKOUT_ID, request))
        .isInstanceOf(IllegalArgumentException.class);

        verify(workoutRepository, never()).save(any());
    }
  }
}