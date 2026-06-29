package com.mxr.usermanagement.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LatencyMeasurementAspect {
    private final static Logger logger = LoggerFactory.getLogger(LatencyMeasurementAspect.class);
    
    @Pointcut("execution(* com.mxr.usermanagement.services.*.*(..))")
    public void latencyMeasurementPointcut() {}
    
    @Around("latencyMeasurementPointcut()")
    public Object measureLatency(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.nanoTime();
        try {
            Object result = joinPoint.proceed();
            return result;
        } finally {
            long endTime = System.nanoTime();
            logger.info("Method {} in class {} took {} ns", joinPoint.getSignature().getName(), joinPoint.getSignature().getDeclaringTypeName(), endTime - startTime);
        }
        


    }
}