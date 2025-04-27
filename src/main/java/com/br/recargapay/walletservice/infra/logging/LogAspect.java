package com.br.recargapay.walletservice.infra.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Component
public class LogAspect {

    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controller() {}

    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void useCase() {}

    @Around("controller() || useCase()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch stopwatch = new StopWatch();
        stopwatch.start();

        logger.info("➡️ Start: {}.{}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());

        try {
            Object result = joinPoint.proceed();
            stopwatch.stop();
            logger.info("✅ End: {}.{} ({} ms)", joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(), stopwatch.getTotalTimeMillis());
            return result;
        } catch (Throwable ex) {
            stopwatch.stop();
            logger.error("❌ Error in: {}.{} ({} ms) - {}", joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(), stopwatch.getTotalTimeMillis(), ex.getMessage());
            throw ex;
        }
    }
}