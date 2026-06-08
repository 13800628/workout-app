package com.workout.dto;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(
    @NotBlank(message = "現在のパスワードを入力してください")
    String oldPassword,
    
    @NotBlank(message = "新しいパスワードを入力してください")
    String newPassword
) {}
