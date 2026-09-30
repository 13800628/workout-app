package com.workout.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.checkerframework.checker.units.qual.g;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.configurers.HttpsRedirectConfigurer;
import org.springframework.security.core.Authentication;

import com.workout.config.CustomUserDetails;
import com.workout.dto.workouts.UpdateWorkoutRequest;
import com.workout.dto.workouts.WorkoutRequest;
import com.workout.dto.workouts.WorkoutResponse;
import com.workout.exception.user.UserDomainException;
import com.workout.exception.workout.WorkoutDomainException;
import com.workout.model.User;
import com.workout.model.Workout;
import com.workout.service.WorkoutService;

@ExtendWith (MockitoExtension.class)
@DisplayName ("WorkoutControllerのテスト")
public  class WorkoutControllerTest {

  @Mock 
  private WorkoutService workoutService;

  @Mock 
  private Authentication authentication;

  @Mock 
  private CustomUserDetails customUserDetails;

  @InjectMocks 
  private WorkoutController workoutController;

  private User owner;
  private Workout existingWorkout;

  @BeforeEach 
  void setUp() {
    given(authentication.getPrincipal()).willReturn(customUserDetails);
    given(customUserDetails.getUserId()).willReturn(1L);

    owner = new User("taro", 25, "encodedPassword");
    owner.setId(1L);

    existingWorkout = new Workout("ベンチプレス", 10, 3, 60, owner);
    existingWorkout.setId(100L);
  }

  @Nested 
  @DisplayName ("createWorkout")
  class CreateWorkout {

    @Test 
    @DisplayName ("正常系: 認証済みuserIdでserviceを呼び204")
    void createWorkout_success() {
      WorkoutRequest request = new WorkoutRequest("ベンチプレス", 10, 3, 60);
      given(workoutService.createWorkout(1L, request)).willReturn(existingWorkout);

      ResponseEntity<WorkoutResponse> response = workoutController.createWorkout(request, authentication);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
      assertThat(response.getBody()).isEqualTo(WorkoutResponse.from(existingWorkout));
      verify(workoutService).createWorkout(1L, request);
    }

    @Test 
    @DisplayName ("異常系: 紐づくUserが存在しない場合、UserDomainException")
    void createWorkout_userNotFound() {
      WorkoutRequest request = new WorkoutRequest("ベンチプレス", 10, 3, 60);
      given(workoutService.createWorkout(1L, request))
       .willThrow(UserDomainException.notFound("ユーザーが見つかりません"));

      assertThatThrownBy(() -> workoutController.createWorkout(request, authentication))
        .isInstanceOf(UserDomainException.class)
        .extracting(ex -> ((UserDomainException) ex).getStatus())
        .isEqualTo(HttpStatus.NOT_FOUND);
    }
  }

  @Nested 
  @DisplayName ("getMyWorkouts")
  class GetWorkouts {

    @Test 
    @DisplayName ("正常系: 認証済みuserIdに紐づくワークアウト一覧を200で返す")
    void getMyWorkouts_success() {
      Workout secondWorkout = new Workout("スクワット", 8, 4, 80, owner);
      secondWorkout.setId(101L);

      given(workoutService.getAllWorkoutById(1L))
       .willReturn(List.of(existingWorkout, secondWorkout));

      ResponseEntity<List<WorkoutResponse>> response = workoutController.getMyWorkouts(authentication);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody())
       .containsExactly(WorkoutResponse.from(existingWorkout), WorkoutResponse.from(secondWorkout));
      verify(workoutService).getAllWorkoutById(1L);
    }

    @Test 
    @DisplayName ("正常系: ワークアウトが存在しない場合は空リストを返す")
    void getMyWorkouts_empty() {
      given(workoutService.getAllWorkoutById(1L)).willReturn(List.of());

      ResponseEntity<List<WorkoutResponse>> response = workoutController.getMyWorkouts(authentication);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isEmpty();
      verify(workoutService).getAllWorkoutById(1L);
    }

    @Test 
    @DisplayName ("異常系: Servicehが例外を投げた場合そのまま")
    void getMyWorkouts_serviceThrows() {
      given(workoutService.getAllWorkoutById(1L))
       .willThrow(UserDomainException.notFound("ユーザーが見つかりません: 1"));

      assertThatThrownBy(() -> workoutController.getMyWorkouts(authentication))
       .isInstanceOf(UserDomainException.class);
    }
  }

  @Nested 
  @DisplayName ("updateDetails")
  class UpdateDetails {

    @Test 
    @DisplayName ("正常系: workoutIdと認証済みuserIdでserviceを呼ぶ")
    void updateDetails_success() {
      UpdateWorkoutRequest request =  new UpdateWorkoutRequest("ベンチプレス2", 12, 4, 6);
      Workout updatedWorkout = new Workout("ベンチプレス2", 12, 4, 6, owner);
      updatedWorkout.setId(100L);

      given(workoutService.updateAllDetails(100L, 1L, request)).willReturn(updatedWorkout);

      ResponseEntity<WorkoutResponse> response = workoutController.updateDetails(100L, request, authentication);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isEqualTo(WorkoutResponse.from(updatedWorkout));
      verify(workoutService).updateAllDetails(100L, 1L, request);
    }

    @Test 
    @DisplayName ("異常系(BOLA対策): 所有者が一致しない場合、WorkoutDomainException")
    void updateDetails_notOwner() {
      UpdateWorkoutRequest request = new UpdateWorkoutRequest("改ざん", 1, 1, 1);
      given(workoutService.updateAllDetails(999L, 1L, request))
       .willThrow(WorkoutDomainException.notFound("ワークアウトが見つかりません: 999"));

      assertThatThrownBy(() -> workoutController.updateDetails(999L, request, authentication))
       .isInstanceOf(WorkoutDomainException.class)
       .extracting(ex -> ((WorkoutDomainException) ex).getStatus())
       .isEqualTo(HttpStatus.NOT_FOUND);

      verify(workoutService).updateAllDetails(999L, 1L, request);
    }

    @Test 
    @DisplayName ("異常系: 存在しないWorkoutIdの場合、WorkoutDomainException")
    void updateDetails_notFound() {
      UpdateWorkoutRequest request = new UpdateWorkoutRequest("ベンチプレス2", 12, 4, 6);
      given(workoutService.updateAllDetails(999L, 1L, request))
       .willThrow(WorkoutDomainException.notFound("ワークアウトが見つかりません: 999"));
      
      assertThatThrownBy(() -> workoutController.updateDetails(999L, request, authentication))
       .isInstanceOf(WorkoutDomainException.class)
       .hasMessage("ワークアウトが見つかりません: 999");
    }

    @Test 
    @DisplayName ("異常系: 入力値不正の場合、IllegalArgumentException")
    void updateDetails_invalidInput() {
      UpdateWorkoutRequest request = new UpdateWorkoutRequest("ベンチプレス", -1, 4, 6);
      given(workoutService.updateAllDetails(100L, 1L, request))
       .willThrow(new IllegalArgumentException("回数は0以上にしてください"));

      assertThatThrownBy(() -> workoutController.updateDetails(100L, request, authentication))
       .isInstanceOf(IllegalArgumentException.class)
       .hasMessage("回数は0以上にしてください");
    }
  }

  @Nested 
  @DisplayName ("deleteWorkout")
  class DeleteWorkout {

    @Test 
    @DisplayName ("正常系: workoutIdと認証済みuserIdでServiceを呼ぶ")
    void deleteWorkout_success() {
      ResponseEntity<Void> response = workoutController.deleteWorkout(100L, authentication);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
      assertThat(response.getBody()).isNull();
      verify(workoutService, times(1)).deleteWorkout(100L, 1L);
    }

    @Test 
    @DisplayName ("異常系(BOLA対策): 所有者が一致しないか存在しない場合、WorkoutDomainException")
    void deleteWorkout_notOwnerOrNotFound() {
      willThrow(WorkoutDomainException.notFound("ワークアウトが見つかりません: 999"))
      .given(workoutService)
      .deleteWorkout(999L, 1L);

    assertThatThrownBy(() -> workoutController.deleteWorkout(999L, authentication))
     .isInstanceOf(WorkoutDomainException.class)
     .hasMessage("ワークアウトが見つかりません: 999")
     .extracting(ex -> ((WorkoutDomainException) ex).getStatus())
     .isEqualTo(HttpStatus.NOT_FOUND);

    verify(workoutService).deleteWorkout(999L, 1L);
    }
  }
}