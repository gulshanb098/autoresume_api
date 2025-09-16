package com.autoresume.autoresume_api.exception;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.autoresume.autoresume_api.dto.response.APIResponse;

import io.swagger.v3.oas.annotations.Hidden;

@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(FileTypeNotAllowedException.class)
        public ResponseEntity<APIResponse<Void>> handleFileTypeNotAllowed(FileTypeNotAllowedException ex) {
                return ResponseEntity.badRequest().body(
                                APIResponse.<Void>builder()
                                                .success(false)
                                                .error(ex.getMessage())
                                                .timestamp(LocalDateTime.now())
                                                .build());
        }

        public ResponseEntity<?> handleResourceNotFound(ResourceNotFoundException ex) {
                return ResponseEntity.badRequest().body(
                                APIResponse.<Void>builder()
                                                .success(false)
                                                .error("ResourceNotFound: " + ex.getMessage())
                                                .timestamp(LocalDateTime.now())
                                                .build());
        }

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<?> handleAccessDenied(AccessDeniedException ex) {
                return ResponseEntity.status(403).body(
                                APIResponse.<Void>builder()
                                                .success(false)
                                                .error("Forbidden: " + ex.getMessage())
                                                .timestamp(LocalDateTime.now())
                                                .build());
        }

        @ExceptionHandler(RuntimeException.class)
        public ResponseEntity<APIResponse<Void>> handleRuntime(RuntimeException ex) {
                return ResponseEntity.status(500).body(
                                APIResponse.<Void>builder()
                                                .success(false)
                                                .error("Internal Server Error: " + ex.getMessage())
                                                .timestamp(LocalDateTime.now())
                                                .build());
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<APIResponse<Void>> handleGeneric(Exception ex) {
                return ResponseEntity.status(500).body(
                                APIResponse.<Void>builder()
                                                .success(false)
                                                .error("Unexpected error: " + ex.getMessage())
                                                .timestamp(LocalDateTime.now())
                                                .build());
        }
}
