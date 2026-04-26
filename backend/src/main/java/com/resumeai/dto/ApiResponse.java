package com.resumeai.dto;

public record ApiResponse<T>(boolean success, String message, T data) {
}
