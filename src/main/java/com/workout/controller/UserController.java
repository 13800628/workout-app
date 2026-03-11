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
import com.workout.model.User;
import com.workout.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping
  public ResponseEntity<User> registerUser(@Valid @RequestBody UserRequest request) {
    User user = userService.registerUser(request.username(), request.age());
    return ResponseEntity.status(HttpStatus.CREATED).body(user);
  }

  @GetMapping
  public ResponseEntity<Page<User>> getAllUsers(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    return ResponseEntity.ok(userService.getAllUsers(page, size));
  }

  @GetMapping("/{id}")
  public ResponseEntity<User> getUserById(@Valid @PathVariable("id") Long id) {
    User user = userService.getUserById(id);
    if (user == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(userService.getUserById(id));
  }

  // --- U (Update) - ユーザー情報更新 ---
  // UserRequestを再利用して、ユーザー名と年齢を更新すると想定
  @PutMapping("/{id}")
  public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
    User updatedUser = userService.updateUser(id, request.username(), request.age());
    return ResponseEntity.ok(updatedUser);
  }

  // --- D (Delete) - ユーザー削除 ---
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    userService.deleteUser(id);
    return ResponseEntity.noContent().build();
  }
}
