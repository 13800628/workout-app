package com.workout.exception;

import java.util.Objects;

import org.springframework.http.HttpStatus;

import io.micrometer.common.lang.NonNull;
import jakarta.annotation.Nonnull;

// 継承されるベースとなる抽象化された例外クラス
public abstract class DomainException extends RuntimeException {
  
  private final @NonNull HttpStatus status;

  protected DomainException(String message, HttpStatus status) {
    super(message);
    this.status = Objects.requireNonNull(status, "status must not be null");
  }

  protected DomainException(String message, HttpStatus status, Throwable cause) {
    super(message, cause);
    this.status = Objects.requireNonNull(status, "status must not be null");
  }

  public @Nonnull HttpStatus getStatus() {
    return status;
  }
}
