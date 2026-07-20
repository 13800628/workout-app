package com.workout.service;
// ここからテスト一つ一つ書いていく

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.refEq;
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
  }
}