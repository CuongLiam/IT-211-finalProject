package com.example.backend.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("execution(* com.example.backend.service.GradeService.gradeSubmission(..))")
    public void gradeSubmissionPointcut() {
    }

    @AfterReturning(pointcut = "gradeSubmissionPointcut()", returning = "result")
    public void afterGradeSuccess(Object result) {
        log.info("[GRADE_SUCCESS] Grade submission completed. result={}", result);
    }

    @AfterThrowing(pointcut = "gradeSubmissionPointcut()", throwing = "ex")
    public void afterGradeFailure(Throwable ex) {
        log.error("[GRADE_FAILURE] Grade submission failed. error={}", ex.getMessage(), ex);
    }

    @Around("execution(* com.example.backend.service.GradeService.*(..))")
    public Object aroundGradeService(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        String method = joinPoint.getSignature().toShortString();

        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - start;
            log.info("[GRADE_TRACE] method={} status=SUCCESS durationMs={}", method, duration);
            return result;
        } catch (Throwable ex) {
            long duration = System.currentTimeMillis() - start;
            log.warn("[GRADE_TRACE] method={} status=FAILED durationMs={} message={}", method, duration, ex.getMessage());
            throw ex;
        }
    }
}
