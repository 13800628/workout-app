package com.workout.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.workout.model.User;
import com.workout.service.UserService;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

  @Autowired
  private UserService userService;

  @PostMapping("/register")
  public ResponseEntity<User> registerUser(@RequestBody UserRequest request) {
    User user = userService.registerUser(request.getUserName(), request.getAge());
    return ResponseEntity.status(HttpStatus.CREATED).body(user);
  }

  // --- R (Read) - 全ユーザー取得 ---
  @GetMapping
  public ResponseEntity<List<User>> getAllUsers() {
    List<User> users = userService.getAllUsers();
    return ResponseEntity.ok(users);
  }

  @GetMapping("/{id}")
  public ResponseEntity<User> getUserBy(@PathVariable("id") Long id) {
    User user = userService.getUserById(id); // コントローラーは成功だけ知っておく
        
    return ResponseEntity.ok(user);
  }

  // --- U (Update) - ユーザー情報更新 ---
  // UserRequestを再利用して、ユーザー名と年齢を更新すると想定
  @PutMapping("/{id}")
  public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody UserRequest request) {
    // UserServiceで更新処理を行い、更新後のUserオブジェクトを返す
    User updatedUser = userService.updateUser(id, request.getUserName(), request.getAge());

    // ユーザーが見つからない場合はUserService側でResourceNotFoundExceptionをスローさせても良い
    return ResponseEntity.ok(updatedUser);
  }

  // --- D (Delete) - ユーザー削除 ---
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    userService.deleteUser(id);
    // 削除成功後はコンテンツなし (204 No Content) を返すのが一般的
    return ResponseEntity.noContent().build();
    // return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
