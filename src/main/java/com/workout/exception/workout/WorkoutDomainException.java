package com.workout.exception.workout;

import org.springframework.http.HttpStatus;

import com.workout.exception.DomainException;

public class WorkoutDomainException extends DomainException {

  private WorkoutDomainException(String message, HttpStatus status) {
    super(message, status);
  }

  private WorkoutDomainException(String message, HttpStatus status, Throwable cause) {
    super(message, status, cause);
  }

  public static WorkoutDomainException notFound(String message) {
    return new WorkoutDomainException(message, HttpStatus.NOT_FOUND);
  }

  public static WorkoutDomainException notFound(String message, Throwable cause) {
    return new WorkoutDomainException(message, HttpStatus.NOT_FOUND, cause);
  }

  public static WorkoutDomainException invalid(String message) {
    return new WorkoutDomainException(message, HttpStatus.BAD_REQUEST);
  }

  public static WorkoutDomainException conflict(String message) {
    return new WorkoutDomainException(message, HttpStatus.CONFLICT);
  }
}
