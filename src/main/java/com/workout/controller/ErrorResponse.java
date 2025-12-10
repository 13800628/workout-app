package com.workout.controller;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.http.HttpStatus;

public class ErrorResponse {
  // HTTPステータスコード (e.g., 400, 500)
    private final int status;
    // エラー詳細メッセージ
    private final String message;
    // エラー発生時刻
    private final LocalDateTime timestamp; 

    public ErrorResponse(HttpStatus status, String message) {
        this.status = status.value();
        this.message = message;
        // JSTで現在時刻を設定
        this.timestamp = LocalDateTime.now(ZoneId.of("Asia/Tokyo")); 
    }

    // Getter methods (JSONシリアライズに必要)
    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
