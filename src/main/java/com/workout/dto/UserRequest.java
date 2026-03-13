package com.workout.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record UserRequest(

  /* 
  @NotNull(message = "ユーザーIDは必須です")
  Long userId,
  */

  @NotBlank(message = "ユーザー名は必須です")
  String username,

  @Positive(message = "年齢は1歳以上で入力してください")
  Integer age
) {}
