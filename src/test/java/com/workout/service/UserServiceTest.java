package com.workout.service;
// ここからテスト一つ一つ書いていく

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.AdditionalAnswers.returnsFirstArg;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.workout.dto.UpdateUserRequest;
import com.workout.dto.UserRequest;
import com.workout.exception.user.UserDomainException;
import com.workout.model.User;
import com.workout.repository.UserRepository;


/**
 * UserServiceTest
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceのテスト")
public class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private UserService userService;

  private User existingUser;

  @BeforeEach
  void setUp() {
    existingUser = new User("taro", 25, "encodedPassword");
    existingUser.setId(1L);
  }

  @Nested
  @DisplayName("registerUser")
  class Register {

    @Test
    @SuppressWarnings("null")
    @DisplayName("正常系: パスワードをエンコードしてユーザー登録")
    void registerUser_success() {
      UserRequest request = new UserRequest("hanako", 20, "plainPassword");
      given(passwordEncoder.encode("plainPassword")).willReturn("encodedPassword");
      given(userRepository.save(any(User.class))).will(returnsFirstArg());

      User result = userService.registerUser(request);

      ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
      verify(userRepository).save(captor.capture());
      User savedUser = captor.getValue();

      assertThat(result.getUsername()).isEqualTo("hanako");
      assertThat(result.getAge()).isEqualTo(20);
      assertThat(savedUser.getPassword()).isEqualTo("encodedPassword");
      verify(passwordEncoder).encode("plainPassword");
    }
  }

  @Nested
  @SuppressWarnings("null")
  @DisplayName("getAlUsers(page, size)")
  class GetAllUsersPaged {

    @Test
    @DisplayName("正常系: 指定したページ、サイズでユーザー一覧を取得")
    void getAllUsers_paged_success() {
      User user2 = new User("jiro", 30, "password2");
      user2.setId(2L);
      Page<User> page = new PageImpl<>(List.of(existingUser, user2));
      given(userRepository.findAll(any(Pageable.class))).willReturn(page);

      Page<User> result = userService.getAllUsers(0, 10);

      assertThat(result.getContent()).hasSize(2);
      assertThat(result.getContent()).containsExactly(existingUser, user2);
      verify(userRepository).findAll(PageRequest.of(0, 10));
    }
  }

  // ここからテストを追記していく
  @Test
  @DisplayName("異常系: 存在しないユーザー名の場合はUserDomainException(404)を投げる")
  void getUserByUsername_notFound() {
    given(userRepository.findByUsername("unknown")).willReturn(Optional.empty());

    assertThatThrownBy(() -> userService.getUserByUsername("unknown"))
        .isInstanceOf(UserDomainException.class)
        .hasMessage("ユーザーが見つかりません: unknown")
        .extracting(ex -> ((UserDomainException) ex).getStatus())
        .isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Nested
  @DisplayName("updateUser")
  class UpdateUser {

    @Test
    @SuppressWarnings("null")
    @DisplayName("正常系: プロフィールを更新して保存")
    void updateUser_success() {

      UpdateUserRequest request = new UpdateUserRequest("taro-updated", 26);
      given(userRepository.findById(1L)).willReturn(Optional.of(existingUser));
      given(userRepository.save(any(User.class)))
          .willAnswer(invocation -> invocation.getArgument(0));

      User result = userService.updateUser(1L, request);

      assertThat(result.getUsername()).isEqualTo("taro-updated");
      assertThat(result.getAge()).isEqualTo(26);
      verify(userRepository).save(existingUser);
    }

    @Test
    @SuppressWarnings("null")
    @DisplayName("異常系: 存在しないIDの場合はUserDomainExceptionを投げ、保存しない")
    void updateUser_notFound() {
      UpdateUserRequest request = new UpdateUserRequest("taro-updated", 26);
      given(userRepository.findById(99L)).willReturn(Optional.empty());

      assertThatThrownBy(() -> userService.updateUser(99L, request))
          .isInstanceOf(UserDomainException.class);

      then(userRepository).should(never()).save(any());
    }

    @Test
    @SuppressWarnings("null")
    @DisplayName("異常系: 年齢が負の数の場合はIllegalArgumentExceptionを投げる")
    void updateUser_negativeAge() {
      UpdateUserRequest request = new UpdateUserRequest("taro-updated", -1);
      given(userRepository.findById(1L)).willReturn(Optional.of(existingUser));

      assertThatThrownBy(() -> userService.updateUser(1L, request))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("年齢は0以上で入力してください");

      then(userRepository).should(never()).save(any());
    }
  }

  @Nested
  @DisplayName("changePassword")
  class ChangePassword {

    @Test
    @SuppressWarnings("null")
    @DisplayName("正常系: 現在のパスワードが一致すれば新パスワードにエンコードして保存する")
    void changePassword_success() {
      given(userRepository.findById(1L)).willReturn(Optional.of(existingUser));
      given(passwordEncoder.matches("oldPassword", "encodedPassword")).willReturn(true);
      given(passwordEncoder.encode("newPassword")).willReturn("encodedNewPassword");

      userService.changePassword(1L, "oldPassword", "newPassword");

      assertThat(existingUser.getPassword()).isEqualTo("encodedNewPassword");
      verify(userRepository).save(existingUser);
    }

    @Test
    @SuppressWarnings("null")
    @DisplayName("現在のパスワードが一致しない場合はIllegalArgumentExceptionを投げ、保存しない")
    void changePassword_wrongOldPassword() {
      given(userRepository.findById(1L)).willReturn(Optional.of(existingUser));
      given(passwordEncoder.matches("wrongPassword", "encodedPassword")).willReturn(false);

      assertThatThrownBy(() -> userService.changePassword(1L, "wrongPassword", "newPassword"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("現在のパスワードが正しくありません");

      then(passwordEncoder).should(times(1)).matches("wrongPassword", "encodedPassword");

      then(passwordEncoder).should(never()).encode(anyString());
      then(userRepository).should(never()).save(any());
    }

    @Test
    @DisplayName("異常系: 存在しないIDの場合はUserDomainExceptionを投げる")
    void changePassword_userNotFound() {
      given(userRepository.findById(99L)).willReturn(Optional.empty());

      assertThatThrownBy(() -> userService.changePassword(99L, "old", "new"))
        .isInstanceOf(UserDomainException.class);

      then(passwordEncoder).should(never()).matches(anyString(), anyString());
    }
  }

  @Nested
  @DisplayName("deleteUser")
  class DeleteUser {

    @Test
    @DisplayName("正常系: 削除件数が1ならば正常終了する")
    void deleteUser_success() {
      given(userRepository.deleteDirectlyById(1L)).willReturn(1);

      userService.deleteUser(1L);
      verify(userRepository, times(1)).deleteDirectlyById(1L);
    }

    @Test
    @DisplayName("異常系: 削除件数が0件ならばUseDomainException(404)を投げる")
    void deleteUser_notFound() {
      given(userRepository.deleteDirectlyById(99L)).willReturn(0);

      assertThatThrownBy(() -> userService.deleteUser(99L))
          .isInstanceOf(UserDomainException.class)
          .hasMessage("ID: 99は存在しません")
          .extracting(ex -> ((UserDomainException) ex).getStatus())
          .isEqualTo(HttpStatus.NOT_FOUND);
    }
  }
}