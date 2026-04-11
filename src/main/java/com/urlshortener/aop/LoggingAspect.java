package com.urlshortener.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("execution(* com.urlshortener.service..*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {

        long start = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().toShortString();
        log.info("➡️ Entering: {} with args: {}", methodName, joinPoint.getArgs());
        try {
            Object result = joinPoint.proceed();
            long timeTaken = System.currentTimeMillis() - start;
            log.info("✅ Exiting: {} | Time: {} ms | Result: {}", methodName, timeTaken, result);
            return result;
        } catch (Exception e) {
            log.error("❌ Exception in: {} | Message: {}", methodName, e.getMessage());
            throw e;
        }
    }
}
