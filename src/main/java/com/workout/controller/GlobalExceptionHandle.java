package com.workout.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;


@ControllerAdvice
public class GlobalExceptionHandle {
  
  @ExceptionHandler({
    HttpMessageNotReadableException.class,
    MethodArgumentNotValidException.class,
    IllegalArgumentException.class
  })
  public ResponseEntity<ErrorResponse> handleBadRequestException(Exception ex) {
    String errorMessage;

    if (ex instanceof MethodArgumentNotValidException) {
      MethodArgumentNotValidException manv = (MethodArgumentNotValidException) ex;
      if (manv.getBindingResult().getFieldError() != null) {
        errorMessage = "入力値が無効です。: " + manv.getBindingResult().getFieldError().getDefaultMessage();
      } else {
        errorMessage = "入力値が無効です。";
      }
    } else if (ex instanceof HttpMessageNotReadableException) {
      errorMessage = "JSON形式が不正です。";
    } else {
      errorMessage = ex.getMessage();
    }

    ErrorResponse errorResponse = new  ErrorResponse(HttpStatus.BAD_REQUEST, errorMessage);
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
    System.err.println("Unhandled Exception in UserController flow: " + ex.getClass().getName() + ": " + ex.getMessage());

    String errorMessage = "システムエラーが発生しました。再度お試しください。";

    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);
    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
