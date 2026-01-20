package com.workout.controller;

import java.util.stream.Collectors;
import java.util.List;
import java.util.stream.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;


@ControllerAdvice
public class GlobalExceptionHandle {
  
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleBadRequestException(MethodArgumentNotValidException ex) {
    List<String> details = ex.getBindingResult()
          .getFieldErrors()
          .stream()
          .map(error -> error.getField() + ": " + error.getDefaultMessage())
          .collect(Collectors.toList());

    ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST, "入力値が不正です");
    return ResponseEntity.badRequest().body(error);

  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleReadableException(HttpMessageNotReadableException ex) {
    ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST, "JSON形式が正しくありません");
    return ResponseEntity.badRequest().body(error);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException ex) {
    ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    return ResponseEntity.badRequest().body(error);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
    ex.printStackTrace();

    ErrorResponse error = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "システムエラーが発生しました");
    return ResponseEntity.internalServerError().body(error);
  }
}
