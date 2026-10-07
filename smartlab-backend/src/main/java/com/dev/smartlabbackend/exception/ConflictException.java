package com.dev.smartlabbackend.exception;

import org.springframework.http.HttpStatus;

/**
 * 409 Conflict - dữ liệu xung đột với trạng thái hiện tại (trùng unique, đang được tham chiếu...).
 */
public class ConflictException extends AppException {
    public ConflictException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
