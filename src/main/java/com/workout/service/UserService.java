package com.workout.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.workout.dto.UserRequest;
import com.workout.model.User;
import com.workout.repository.UserRepository;

@Service
public class UserService {
  private UserRepository userRepository;
  private PasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional
  public User registerUser(UserRequest request) {
    User user = new User(
      request.username(),
      request.age(),
      passwordEncoder.encode(request.password())
    );
    return userRepository.save(user);
  }

  // getAllUsers getUserById updateUser deleteUser の実装
  @Transactional(readOnly = true)
  public Page<User> getAllUsers(int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    return userRepository.findAll(pageable);
  }

  // テスト用、ビルドの整合性のため
  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  @Transactional(readOnly = true)
  public User getUserById(Long id) {
    if (id == null) {
      throw new IllegalArgumentException("IDを指定してください");
    }
    
    return userRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("IDが見つかりません: " + id));
  }

  @Transactional
  public User updateUser(Long id, UserRequest request) {
    User user = getUserById(id);

    user.setUsername(request.username());
    user.setAge(request.age());

    return user;
  }

  @Transactional
  public void deleteUser(Long id) {
    int deletedCount = userRepository.deleteDirectlyById(id);

    if (deletedCount == 0) {
      throw new IllegalArgumentException("ID: " + id + "は存在しません");
    }
  }

  // password
  @Transactional
  public void changePassword(Long id, String oldPassword, String newPassword) {
    User user = getUserById(id);

    if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
      throw new IllegalArgumentException("現在のパスワードが正しくありません");
    }

    user.setPassword(passwordEncoder.encode(newPassword));
    userRepository.save(user);
  }
}