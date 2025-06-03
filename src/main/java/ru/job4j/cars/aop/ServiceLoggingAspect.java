package ru.job4j.cars.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ServiceLoggingAspect {

    /**
     * Перехватывает все публичные методы всех классов внутри пакета ru.job4j.cars и его подпакетов.
     */
    @Around("execution(public * ru.job4j.cars..*(..))")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();
        Object[] args = joinPoint.getArgs();

        log.info("{}#{} called with args: {}", className, methodName, args);

        try {
            Object result = joinPoint.proceed();
            log.info("{}#{} returned: {}", className, methodName, result);
            return result;
        } catch (Throwable ex) {
            log.error("{}#{} threw exception: {}", className, methodName, ex.getMessage(), ex);
            throw ex;
        }
    }
}
