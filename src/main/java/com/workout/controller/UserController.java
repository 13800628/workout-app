package com.workout.controller;



import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

  // 自分のIDを取り出すヘルパー関数
  private Long currentUserId(Authentication auth) {
    CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
    return principal.getUserId();
  }

  // 未認証でも可能(登録)
  @PostMapping
  public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserRequest request) {
    User user = userService.registerUser(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.from(user));
  }

  // Read 自分の情報のみ
  @GetMapping("/me")
  public ResponseEntity<UserResponse> getMe(Authentication auth) {
    User user = userService.getUserById(currentUserId(auth));
    return ResponseEntity.ok(UserResponse.from(user));
  }

  // --- U (Update) 自分の情報のみ
  @PutMapping("/me")
  public ResponseEntity<UserResponse> updateMe(
    @Valid
    @RequestBody UpdateUserRequest request,
    Authentication auth) {
    User updatedUser = userService.updateUser(currentUserId(auth), request);
    return ResponseEntity.ok(UserResponse.from(updatedUser));
  }

  // --- D (Delete) 自分のみ削除
  @DeleteMapping("/me")
  public ResponseEntity<Void> deleteMe(Authentication auth) {
    userService.deleteUser(currentUserId(auth));
    return ResponseEntity.noContent().build();
  }

  // password 自分のみ
  @PutMapping("/me/password")
  public ResponseEntity<Void> changePassword(
      @Valid @RequestBody ChangePasswordRequest request, Authentication auth) {
        userService.changePassword(currentUserId(auth), request.oldPassword(), request.newPassword());
        return ResponseEntity.noContent().build();
      }
}
