package com.workout.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.workout.model.User;
import com.workout.repository.UserRepository;

@Service
public class UserService {
  private UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Transactional
  public User registerUser(String username, Integer age) {
    validateUserData(username, age);
    User user = new User(username, age);
    return userRepository.save(user);
  }

  // getAllUsers getUserById updateUser deleteUser の実装
  @Transactional(readOnly = true)
  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  @Transactional(readOnly = true)
  public User getUserById(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("IDが見つかりません: " + id));
  }

  @Transactional
  public User updateUser(Long id, String username, Integer age) {
    User user = getUserById(id);

    validateUserData(username, age);

    user.setUsername(username);
    user.setAge(age);
    return userRepository.save(user);
  }

  @Transactional
  public void deleteUser(Long id) {

    if (!userRepository.existsById(id)) {
      throw new IllegalArgumentException("ID: " + id + "は存在しません");
    }

    userRepository.deleteById(id);
  }

  private void validateUserData(String username, Integer age) {
    if (username == null || username.isBlank()) {
      throw new IllegalArgumentException("ユーザー名は必須です（空文字・空白不可）");
    }
    if (age == null || age < 0) {
      throw new IllegalArgumentException("年齢は0歳以上で指定してください");
    }
  }
}