package com.example.backend.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    // ── FR-11: Ghi log thời gian thực hiện cho TẤT CẢ các chức năng ──

    @Pointcut("execution(* com.example.backend.controller..*(..))")
    public void controllerLayer() {
    }

    @Pointcut("execution(* com.example.backend.service..*(..))")
    public void serviceLayer() {
    }

    /**
     * Ghi log thời gian thực hiện cho mọi method trong Controller layer.
     */
    @Around("controllerLayer()")
    public Object aroundController(ProceedingJoinPoint joinPoint) throws Throwable {
        return logExecution(joinPoint, "CONTROLLER");
    }

    /**
     * Ghi log thời gian thực hiện cho mọi method trong Service layer.
     */
    @Around("serviceLayer()")
    public Object aroundService(ProceedingJoinPoint joinPoint) throws Throwable {
        return logExecution(joinPoint, "SERVICE");
    }

    // ── Grade-specific advices (giữ nguyên) ──

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

    // ── Helper ──

    private Object logExecution(ProceedingJoinPoint joinPoint, String layer) throws Throwable {
        String method = joinPoint.getSignature().toShortString();
        long start = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - start;
            log.info("[{}] method={} status=SUCCESS durationMs={}", layer, method, duration);
            return result;
        } catch (Throwable ex) {
            long duration = System.currentTimeMillis() - start;
            log.warn("[{}] method={} status=FAILED durationMs={} message={}", layer, method, duration, ex.getMessage());
            throw ex;
        }
    }
}
