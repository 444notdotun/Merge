package com.mxr.usermanagement.data.dto.requests;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceResponseDto {
    int delay;
    String message;
    LocalDateTime timeGenerated;
}
