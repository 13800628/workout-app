package com.workout.dto;

import com.workout.model.User;

public record UserResponse(
  Long id, 
  String username,
  Integer age
) {
  
  public static UserResponse from(User user) {
    if (user == null) return null;
    return new UserResponse(
      user.getId(),
      user.getUsername(),
      user.getAge()
    );
  }
}
