package com.mxr.usermanagement.controller;

import org.springframework.web.bind.annotation.RestController;

import com.mxr.usermanagement.Service.PerformanceSevice;
import com.mxr.usermanagement.data.dto.requests.PerformanceResponseDto;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/v1/performance")

public class PerformanceController {
    private final PerformanceSevice performanceSevice;
    
    public PerformanceController(PerformanceSevice performanceSevice){
        this.performanceSevice = performanceSevice;
    }

    @GetMapping("/simulate")
    public ResponseEntity<PerformanceResponseDto> simulate(@RequestParam int delayMs) {
        if(delayMs < 0) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        PerformanceResponseDto response = performanceSevice.simulateOperations(delayMs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @GetMapping("/fast")
    public ResponseEntity<PerformanceResponseDto> fastOperation() {
        PerformanceResponseDto response = performanceSevice.fastOperation();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
}
