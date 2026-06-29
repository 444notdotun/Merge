package com.mxr.usermanagement.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mxr.usermanagement.data.dto.requests.PerformanceResponseDto;


@Service

public class PerformanceSevice {
    private static final Logger logger = LoggerFactory.getLogger(PerformanceSevice.class);

    public PerformanceResponseDto simulateOperations(int delayMs){
        String message = "Operation completed successfully";
        if(delayMs > 5000){
            message = "Time taken is above 5000ms";
            throw new IllegalArgumentException(message);
        }
        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException e) {
            logger.error("Thread interrupted", e);
            Thread.currentThread().interrupt();
        }
        return mapToPerformanceDTO(delayMs, message);
        
    }

    public PerformanceResponseDto fastOperation(){
        int delayMs = 0;
        String message = "Fast operation completed successfully";
        return mapToPerformanceDTO(delayMs, message);
    }

    
    private PerformanceResponseDto mapToPerformanceDTO(int delayMs, String message){
        PerformanceResponseDto response = PerformanceResponseDto.builder()
        .delay(delayMs)
        .message(message)
        .timeGenerated(java.time.LocalDateTime.now())
        .build();
        return response;
    }
}
