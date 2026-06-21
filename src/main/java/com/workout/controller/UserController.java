package com.workout.controller;

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
    @Valid @RequestBody UpdateUserRequest request) {
    User updatedUser = userService.updateUser(id, request);
    return ResponseEntity.ok(UserResponse.from(updatedUser));
  }

  // --- D (Delete) - ユーザー削除 ---
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    userService.deleteUser(id);
    return ResponseEntity.noContent().build();
  }

  // password
  @PutMapping("/{id}/password")
  public ResponseEntity<Void> changePassword(
      @PathVariable Long id,
      @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(id, request.oldPassword(), request.newPassword());
        return ResponseEntity.noContent().build();
      }
}
