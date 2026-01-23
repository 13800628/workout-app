package com.workout.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.workout.model.User;
import com.workout.repository.UserRepository;

@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;

  public User registerUser(String username, Integer age) {
    User user = new User(username, age);
    return userRepository.save(user);
  }

  // getAllUsers getUserById updateUser deleteUser の実装
  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  public User getUserById(Long id) {
    return userRepository.findById(id)
           .orElseThrow(() -> new IllegalArgumentException("ID: " + id));
    // サービス層でエラーの場合も知っておく
  }

  @Transactional
  public User updateUser(Long id, String username, Integer age) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("ID: " + id));

    if (username == null || username.isEmpty()) {
      throw new IllegalArgumentException("名前がnullです");
    }

    if (age == null) {
      throw new IllegalArgumentException("年齢がnullです");
    }
    user.setUsername(username);
    user.setAge(age);
    return userRepository.save(user);
  }

  public void deleteUser(Long id) {

    if (!userRepository.existsById(id)) {
      throw new IllegalArgumentException("ID: " + id + "は存在しません");
    }

    userRepository.deleteById(id);
  }
}