package com.workout.controller;


import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.workout.dto.UserRequest;
import com.workout.dto.UserResponse;
import com.workout.model.User;
import com.workout.service.UserService;
import com.workout.config.CustomUserDetails;
import com.workout.dto.ChangePasswordRequest;
import com.workout.dto.UpdateUserRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  // 今後はサービス層のみにする(現状はまず動くためにして同じような実装をサービス層に移植する)
  private void assertSelf(Long targetId, Authentication auth) {
    CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
    if (!principal.getUserId().equals(targetId)) {
      throw new AccessDeniedException("自分以外のユーザー情報は操作できません");
    }
  }


  @PostMapping
  public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserRequest request) {
    User user = userService.registerUser(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.from(user));
  }

  @GetMapping
  public ResponseEntity<Page<UserResponse>> getAllUsers(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    Page<UserResponse> users = userService.getAllUsers(page, size)
        .map(UserResponse::from);
    return ResponseEntity.ok(users);
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserResponse> getUserById(@PathVariable("id") Long id) {
    User user = userService.getUserById(id);
    return ResponseEntity.ok(UserResponse.from(user));
  }

  // --- U (Update) - ユーザー情報更新 ---
  @PutMapping("/{id}")
  public ResponseEntity<UserResponse> updateUser(
    @PathVariable Long id,
    @Valid @RequestBody UpdateUserRequest request, Authentication auth) {
      assertSelf(id, auth);
    User updatedUser = userService.updateUser(id, request);
    return ResponseEntity.ok(UserResponse.from(updatedUser));
  }

  // --- D (Delete) - ユーザー削除 ---
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long id, Authentication auth) {
    assertSelf(id, auth);
    userService.deleteUser(id);
    return ResponseEntity.noContent().build();
  }

  // password
  @PutMapping("/{id}/password")
  public ResponseEntity<Void> changePassword(
      @PathVariable Long id,
      @Valid @RequestBody ChangePasswordRequest request, Authentication auth) {
        assertSelf(id, auth);
        userService.changePassword(id, request.oldPassword(), request.newPassword());
        return ResponseEntity.noContent().build();
      }
}
