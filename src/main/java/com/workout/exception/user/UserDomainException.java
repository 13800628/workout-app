package com.workout.exception.user;

import org.springframework.http.HttpStatus;

import com.workout.exception.DomainException;

public class UserDomainException extends DomainException {
  
  private UserDomainException(String message, HttpStatus status) {
    super(message, status);
  }

  private UserDomainException(String message, HttpStatus status, Throwable cause) {
    super(message, status, cause);
  }

  public static UserDomainException notFound(String message) {
    return new UserDomainException(message, HttpStatus.NOT_FOUND);
  }

  public static UserDomainException notFound(String message, Throwable cause) {
    return new UserDomainException(message, HttpStatus.NOT_FOUND, cause);
  }

  public static UserDomainException invalid(String message) {
    return new UserDomainException(message, HttpStatus.BAD_REQUEST);
  }

  public static UserDomainException conflict(String message) {
    return new UserDomainException(message, HttpStatus.CONFLICT);
  }
}
