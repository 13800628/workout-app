package com.workout.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

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
import com.workout.dto.workouts.WorkoutSetRequest;
import com.workout.model.WorkoutSet;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
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

    workout = new Workout("ベンチプレス", owner);
    workout.replaceSets(List.of(new WorkoutSet(10, 60), new WorkoutSet(10, 55), new WorkoutSet(8, 50)));
    ReflectionTestUtils.setField(workout, "id", WORKOUT_ID);
  }

  @Nested
  @DisplayName("createWorkout")
  class CreateWorkout {

    @Test
    @SuppressWarnings("null")
    @DisplayName("正常系: ユーザーが存在すればセット付きWorkoutを作成して保存する")
    void createWorkout_success() {
      WorkoutRequest request = new WorkoutRequest("スクワット",
          List.of(new WorkoutSetRequest(10, 80), new WorkoutSetRequest(8, 70)));
      given(userRepository.findById(OWNER_ID)).willReturn(Optional.of(owner));
      given(workoutRepository.save(any(Workout.class))).willAnswer(invocation -> invocation.getArgument(0));

      Workout result = workoutService.createWorkout(OWNER_ID, request);

      assertThat(result.getName()).isEqualTo("スクワット");
      assertThat(result.getUser()).isEqualTo(owner);
      assertThat(result.getSets()).hasSize(2);
      assertThat(result.getSets().get(0).getSetOrder()).isEqualTo(1);
      assertThat(result.getSets().get(0).getReps()).isEqualTo(10);
      assertThat(result.getSets().get(0).getWeights()).isEqualTo(80);
      assertThat(result.getSets().get(1).getSetOrder()).isEqualTo(2);
      assertThat(result.getSets().get(1).getReps()).isEqualTo(8);
      assertThat(result.getSets().get(1).getWeights()).isEqualTo(70);

      ArgumentCaptor<Workout> captor = ArgumentCaptor.forClass(Workout.class);
      verify(workoutRepository).save(captor.capture());
      assertThat(captor.getValue().getUser().getId()).isEqualTo(OWNER_ID);
      verify(userRepository).findById(OWNER_ID);
    }

    @Test
    @DisplayName("異常系: ユーザーが存在しない場合はUserDomainExceptionを投げ、保存しない")
    void createWorkout_userNotFound_throwsUserDomainException() {
      Long nonExixtentUserId = 999l;
      WorkoutRequest request = new WorkoutRequest("スクワット",
          List.of(new WorkoutSetRequest(10, 80)));
      given(userRepository.findById(nonExixtentUserId)).willReturn(Optional.empty());

      assertThatThrownBy(() -> workoutService.createWorkout(nonExixtentUserId, request))
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

      Workout workout2 = new Workout("デッドリフト", owner);
      workout2.replaceSets(List.of(new WorkoutSet(8, 100)));
      ReflectionTestUtils.setField(workout2, "id", 101L);
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

  @Nested
  @DisplayName("deleteWorkout")
  class DeleteWorkout {

    @Test
    @DisplayName("正常系: 所有者本人が削除すれば取得したWorkoutをdeleteする")
    void deleteWorkout_success() {
      given(workoutRepository.findByIdAndUserId(WORKOUT_ID, OWNER_ID)).willReturn(Optional.of(workout));

      assertThatCode(() -> workoutService.deleteWorkout(WORKOUT_ID, OWNER_ID))
          .doesNotThrowAnyException();

      verify(workoutRepository).delete(workout);
    }

    @Test
    @DisplayName("異常系: IDが存在しない場合はWorkoutDomainExceptionを投げ、削除しない")
    void deleteWorkout_notFound_throwsWorkoutDomainException() {
      Long nonExistentId = 999L;
      given(workoutRepository.findByIdAndUserId(nonExistentId, OWNER_ID)).willReturn(Optional.empty());

      assertThatThrownBy(() -> workoutService.deleteWorkout(nonExistentId, OWNER_ID))
          .isInstanceOf(WorkoutDomainException.class);

      verify(workoutRepository, never()).delete(any());
    }

    @Test
    @DisplayName("異常系(BOLA): 所有者と異なるuserIdで削除しようとすると例外で、削除しない")
    void deleteWorkout_wrongOwner_throwsWorkoutDomainException() {
      given(workoutRepository.findByIdAndUserId(WORKOUT_ID, ANOTHER_USER_ID)).willReturn(Optional.empty());

      assertThatThrownBy(() -> workoutService.deleteWorkout(WORKOUT_ID, ANOTHER_USER_ID))
          .isInstanceOf(WorkoutDomainException.class);

      verify(workoutRepository, never()).delete(any());
    }
  }

  @Nested
  @DisplayName("updateAllDetails")
  class UpdateAllDetails {

    @Test
    @DisplayName("正常系: 存在するWorkoutを更新して保存する")
    void updateAllDetails_success() {
      UpdateWorkoutRequest request = new UpdateWorkoutRequest("懸垂",
          List.of(new WorkoutSetRequest(12, 0), new WorkoutSetRequest(10, 0)));
      given(workoutRepository.findByIdAndUserId(WORKOUT_ID, OWNER_ID)).willReturn(Optional.of(workout));
      given(workoutRepository.save(any(Workout.class))).willAnswer(invocation -> invocation.getArgument(0));

      Workout result = workoutService.updateAllDetails(WORKOUT_ID, OWNER_ID, request);

      assertThat(result.getName()).isEqualTo("懸垂");
      assertThat(result.getSets()).hasSize(2);
      assertThat(result.getSets().get(0).getReps()).isEqualTo(12);
      assertThat(result.getSets().get(0).getWeights()).isEqualTo(0);
      verify(workoutRepository).save(workout);
    }

    @Test
    @DisplayName("全置換: 旧セットは残らず、setOrderが1から振り直される")
    void updateAllDetails_replacesSets_andReordersFromOne() {
      // 元は3セット(60/55/50kg)。2セット(40/35kg)で置き換える
      UpdateWorkoutRequest request = new UpdateWorkoutRequest("ベンチプレス",
          List.of(new WorkoutSetRequest(5, 40), new WorkoutSetRequest(5, 35)));
      given(workoutRepository.findByIdAndUserId(WORKOUT_ID, OWNER_ID)).willReturn(Optional.of(workout));
      given(workoutRepository.save(any(Workout.class))).willAnswer(invocation -> invocation.getArgument(0));

      Workout result = workoutService.updateAllDetails(WORKOUT_ID, OWNER_ID, request);

      assertThat(result.getSets()).hasSize(2);
      assertThat(result.getSets()).extracting(WorkoutSet::getSetOrder).containsExactly(1, 2);
      assertThat(result.getSets()).extracting(WorkoutSet::getWeights).containsExactly(40, 35);
    }

    @Test
    @DisplayName("異常系: 存在しないIDの場合はWorkoutDomainExceptionを投げ保存しない")
    void updateAllDetails_notFound_throwsWorkoutDomainException() {
      Long nonExistentId = 999L;
      UpdateWorkoutRequest request = new UpdateWorkoutRequest("懸垂",
          List.of(new WorkoutSetRequest(12, 0)));
      given(workoutRepository.findByIdAndUserId(nonExistentId, OWNER_ID)).willReturn(Optional.empty());

      assertThatThrownBy(() -> workoutService.updateAllDetails(nonExistentId, OWNER_ID, request))
          .isInstanceOf(WorkoutDomainException.class);

      verify(workoutRepository, never()).save(any());
    }

    @Test
    @DisplayName("異常系: セットのrepsが負の場合は例外")
    void updateAllDetails_negativeReps_throwsIllegalArgumentException() {
      UpdateWorkoutRequest request = new UpdateWorkoutRequest("懸垂",
          List.of(new WorkoutSetRequest(-1, 0)));
      given(workoutRepository.findByIdAndUserId(WORKOUT_ID, OWNER_ID)).willReturn(Optional.of(workout));

      assertThatThrownBy(() -> workoutService.updateAllDetails(WORKOUT_ID, OWNER_ID, request))
          .isInstanceOf(IllegalArgumentException.class);

      verify(workoutRepository, never()).save(any());
    }

    @Test
    @DisplayName("境界値: セットのweightsが-1の場合は例外(旧仕様の-1許容は解消)")
    void updateAllDetails_negativeWeights_throwsIllegalArgumentException() {
      UpdateWorkoutRequest request = new UpdateWorkoutRequest("懸垂",
          List.of(new WorkoutSetRequest(10, -1)));
      given(workoutRepository.findByIdAndUserId(WORKOUT_ID, OWNER_ID)).willReturn(Optional.of(workout));

      assertThatThrownBy(() -> workoutService.updateAllDetails(WORKOUT_ID, OWNER_ID, request))
          .isInstanceOf(IllegalArgumentException.class);

      verify(workoutRepository, never()).save(any());
    }

    @Test
    @DisplayName("異常系(BOLA): 所有者と異なるユーザーが更新しようとするとWorkoutDomainExceptionを投げる")
    void updateAllDetails_nonOwner_throwsWorkoutDomainException() {
      UpdateWorkoutRequest request = new UpdateWorkoutRequest("不正な更新",
          List.of(new WorkoutSetRequest(99, 99)));
      given(workoutRepository.findByIdAndUserId(WORKOUT_ID, ANOTHER_USER_ID)).willReturn(Optional.empty());

      assertThatThrownBy(() -> workoutService.updateAllDetails(WORKOUT_ID, ANOTHER_USER_ID, request))
          .isInstanceOf(WorkoutDomainException.class);

      verify(workoutRepository, never()).save(any());
    }
  }
}