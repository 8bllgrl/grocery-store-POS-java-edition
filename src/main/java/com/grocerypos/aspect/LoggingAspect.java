package com.grocerypos.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Aspect
public class LoggingAspect {

    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    @Pointcut("execution(* com.grocerypos..*.*(..)) " +
            "&& !execution(* com.grocerypos..*Clock*.*(..)) " +
            "&& !execution(* com.grocerypos..*Tick*.*(..)) " +
            "&& !execution(* com.grocerypos..*.*Property(..)) " +
            "&& !execution(* com.grocerypos..*.get*(..)) " +
            "&& !execution(* com.grocerypos..*.set*(..)) " +
            "&& !within(com.grocerypos..TopBarController) " +
            // 3. Silence UI rendering internals, cell factories, and update hooks
            "&& !execution(* com.grocerypos..*.updateItem(..)) " +
            "&& !execution(* com.grocerypos..*.*lambda$buildCartItemCellFactory*(..))")
    public void applicationMethods() {}

    // 2. Clean, single-line @Around advice
    @Around("applicationMethods()")
    public Object logMethodLifecycle(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
            System.out.printf("[%s] -- [%s.%s] EXCEPTION: %s%n",
                    timestamp, className, methodName, throwable.getClass().getSimpleName());
            throw throwable;
        }

        long duration = System.currentTimeMillis() - startTime;
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Class<?> returnType = signature.getReturnType();

        if (returnType == void.class) {
            System.out.printf("[%s] -- [%s.%s] Took %dms | void%n",
                    timestamp, className, methodName, duration);
        } else {
            System.out.printf("[%s] -- [%s.%s] Took %dms | Return: %s%n",
                    timestamp, className, methodName, duration, result);
        }

        return result;
    }
}