package com.workout.service;

import java.util.Optional;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

  public Optional<User> getUserById(Long id) {
      return userRepository.findById(id);
  }

  public User updateUser(Long id, String username, Integer age) {
      Optional<User> userOpt = userRepository.findById(id);
      if (userOpt.isPresent()) {
          User user = userOpt.get();
          user.setUsername(username);
          user.setAge(age);
          return userRepository.save(user);
      }
      return null;
  }

  public void deleteUser(Long id) {
      userRepository.deleteById(id);
  }
}