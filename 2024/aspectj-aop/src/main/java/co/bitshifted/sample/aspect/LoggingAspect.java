package co.bitshifted.sample.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class LoggingAspect {

    @Around("execution(* *(..))")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("LoggingAspect called..!");
        return joinPoint.proceed();
    }
}
