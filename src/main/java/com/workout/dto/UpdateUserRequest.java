package com.workout.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record UpdateUserRequest(
  @NotBlank(message = "ユーザー名は必須です")
  String username,

  @Positive
  Integer age
) {}
