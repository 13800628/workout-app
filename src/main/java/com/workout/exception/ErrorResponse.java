package com.workout.exception;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ErrorResponse {
    // HTTPステータスコード (e.g., 400, 500)
    private final int status;
    // エラー詳細メッセージ
    private final String message;
    // エラー発生時刻
    @JsonFormat(pattern = "yyy-MM-dd HH:mm:ss")
    private final LocalDateTime timestamp;

    private List<String> details;

    public ErrorResponse(HttpStatus status, String message) {
        this.status = status.value();
        this.message = message;
        // JSTで現在時刻を設定
        this.timestamp = LocalDateTime.now(ZoneId.of("Asia/Tokyo"));
    }

    public ErrorResponse(HttpStatus status, String message, List<String> details) {
        this(status, message);
        this.details = details;
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

    public List<String> getDetails() {
        return details;
    }
}
