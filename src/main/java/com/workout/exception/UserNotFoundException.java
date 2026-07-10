package com.workout.exception;

// 浅いモジュールになっているので、もっと抽象的な例外クラスを作りこれを統合する方向でいく
public class UserNotFoundException extends RuntimeException {
  
  public UserNotFoundException(String message) {
    super(message);
  }

  public UserNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
