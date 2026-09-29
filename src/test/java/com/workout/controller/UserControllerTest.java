package com.workout.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
import org.springframework.security.core.Authentication;

import com.workout.config.CustomUserDetails;
import com.workout.dto.ChangePasswordRequest;
import com.workout.dto.UpdateUserRequest;
import com.workout.dto.UserRequest;
import com.workout.dto.UserResponse;
import com.workout.exception.user.UserDomainException;
import com.workout.model.User;
import com.workout.service.UserService;


@ExtendWith(MockitoExtension.class)
@DisplayName("UserControllerテスト")
public  class UserControllerTest {

  @Mock 
  private UserService userService;

  @Mock 
  private Authentication authentication;

  @Mock 
  private CustomUserDetails customUserDetails;

  @InjectMocks 
  private UserController userController;

  private User existingUser;

  @BeforeEach 
  void setUp() {
    existingUser = new User("taro", 25, "encodedPassword");
    existingUser.setId(1L);
  }

  private void stubAuthenticatedUser() {
    given(authentication.getPrincipal()).willReturn(customUserDetails);
    given(customUserDetails.getUserId()).willReturn(1L);
  }

  @Nested 
  @DisplayName("registerUser")
  class Register {

    @Test 
    @DisplayName("正常系: Serviceを呼び出し、201 CreateとUserResponseを返す")
    void registerUser_Success() {
      UserRequest request = new UserRequest("hanako", 20, "plainPassword");
      User createdUser = new User("hanako", 20, "encodedPassword");
      createdUser.setId(2L);

      given(userService.registerUser(request)).willReturn(createdUser);

      ResponseEntity<UserResponse> response = userController.registerUser(request);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
      assertThat(response.getBody()).isEqualTo(UserResponse.from(createdUser));
      verify(userService).registerUser(request);
    }

    @Test 
    @DisplayName("異常系: Serviceが例外を投げた場合、そのまま")
    void  registerUser_serviceThrows() {
      UserRequest request = new UserRequest("hanako", 20, "plainPassword");
      given(userService.registerUser(request))
        .willThrow(new IllegalArgumentException("不正な入力です"));

      assertThatThrownBy(() -> userController.registerUser(request))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("不正な入力です");
    }
  }

  @Nested 
  @DisplayName ("getMe")
  class GetMe{
    
    @Test
    @DisplayName("正常系: 認証情報からuserIdを取得し、自分の情報を200で返す")
    void getMe_success() {
      stubAuthenticatedUser();
      given(userService.getUserById(1L)).willReturn(existingUser);

      ResponseEntity<UserResponse> response = userController.getMe(authentication);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isEqualTo(UserResponse.from(existingUser));
      verify(userService).getUserById(1L);
    }

    @Test 
    @DisplayName ("異常系: 存在しないuserIdの場合、UserDomainException")
    void getMe_userNotFound() {
      stubAuthenticatedUser();
      given(userService.getUserById(1L))
          .willThrow(UserDomainException.notFound("ユーザーが見つかりません"));

      assertThatThrownBy(() -> userController.getMe(authentication))
       .isInstanceOf(UserDomainException.class)
       .extracting(ex -> ((UserDomainException) ex).getStatus())
       .isEqualTo(HttpStatus.NOT_FOUND);
    }
  }

  @Nested 
  @DisplayName ("updateMe")
  class UpdateMe {

    @Test 
    @DisplayName ("正常系: 認証情報のuserIdでServiceを呼び出し、更新後の情報を200で返す")
    void updateMe_success() {
      stubAuthenticatedUser();
      UpdateUserRequest request = new UpdateUserRequest("taro-updated", 26);
      User updatedUser = new User("taro-updated", 26, "encodedPassword");
      updatedUser.setId(1L);

      given(userService.updateUser(1L, request)).willReturn(updatedUser);

      ResponseEntity<UserResponse> response = userController.updateMe(request, authentication);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isEqualTo(UserResponse.from(updatedUser));
      verify(userService).updateUser(1L, request);
    }

    @Test 
    @DisplayName ("異常系: Serviceが例外投げた場合そのまま")
    void updateMe_serviceThrows() {
      stubAuthenticatedUser();
      UpdateUserRequest request = new UpdateUserRequest("taro-updated", -1);

      given(userService.updateUser(1L, request))
       .willThrow(new IllegalArgumentException("年齢は0以上で入力してください"));

      assertThatThrownBy(() -> userController.updateMe(request, authentication))
       .isInstanceOf(IllegalArgumentException.class)
       .hasMessage("年齢は0以上で入力してください");
    }
  }

  @Nested 
  @DisplayName ("changePassword")
  class ChangePassword {

    @Test 
    @DisplayName ("正常系: 認証情報とuserIdと旧/新パスワードで204")
    void changePassword_success() {
      stubAuthenticatedUser();
      ChangePasswordRequest request = new ChangePasswordRequest("oldPassword", "newPassword");

      ResponseEntity<Void> response = userController.changePassword(request, authentication);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
      assertThat(response.getBody()).isNull();
      verify(userService).changePassword(1L, "oldPassword", "newPassword");
    }

    @Test 
    @DisplayName ("異常系: 現在のパスワードが誤ってる場合、IllegalArgumentException")
    void changePassword_wrongOldPassword() {
      stubAuthenticatedUser();
      ChangePasswordRequest request = new ChangePasswordRequest("wrongPassword", "newPassword");

      org.mockito.BDDMockito
        .willThrow(new IllegalArgumentException("現在のパスワードが正しくありません"))
        .given(userService)
        .changePassword(1L, "wrongPassword", "newPassword");

      assertThatThrownBy(() -> userController.changePassword(request, authentication))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("現在のパスワードが正しくありません");
    }

    @Test 
    @DisplayName ("異常系: 存在しないuserIdの場合、UserDomainException")
    void changePassword_userNotFound() {
      stubAuthenticatedUser();
      ChangePasswordRequest request = new ChangePasswordRequest("old", "new");

      org.mockito.BDDMockito
       .willThrow(UserDomainException.notFound("ユーザーが見つかりません: 1"))
       .given(userService)
       .changePassword(1L, "old", "new");

      assertThatThrownBy(() -> userController.changePassword(request, authentication))
       .isInstanceOf(UserDomainException.class);
    }
  }


  @Nested 
  @DisplayName ("deleteMe")
  class DeleteMe {

    @Test 
    @DisplayName ("正常系: 認証情報のuserIdでserviceを呼び204")
    void deleteMe_success() {
      stubAuthenticatedUser();

      ResponseEntity<Void> response = userController.deleteMe(authentication);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
      assertThat(response.getBody()).isNull();
      verify(userService, times(1)).deleteUser(1L);
    }

    @Test 
    @DisplayName ("異常系: 存在しないuserIdの場合、UserDomainException")
    void deleteMe_userNotFound() {
      stubAuthenticatedUser();

      org.mockito.BDDMockito
       .willThrow(UserDomainException.notFound("ID: 1は存在しません"))
       .given(userService)
       .deleteUser(1L);

      assertThatThrownBy(() -> userController.deleteMe(authentication))
       .isInstanceOf(UserDomainException.class)
       .hasMessage("ID: 1は存在しません")
       .extracting(ex -> ((UserDomainException) ex).getStatus())
       .isEqualTo(HttpStatus.NOT_FOUND);
    }
  }
}