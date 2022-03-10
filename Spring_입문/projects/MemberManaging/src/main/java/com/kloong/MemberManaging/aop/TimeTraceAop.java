package com.kloong.MemberManaging.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class TimeTraceAop
{
    //@Around annotation을 통해 공통 관심 사항을 적용할 target을 지정해줄 수 있다.
    //여기서는 MemberManaging 패키지 이하에는 전부 적용한다는 뜻.
    //패키지명, 클래스명, 매개변수 타입 등 조건을 직접 지정해줄 수 있다.
    @Around("execution(* com.kloong.MemberManaging..*(..))")
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable
    {
        long start = System.currentTimeMillis();
        System.out.println("START: " + joinPoint.toString());
        try {
            return joinPoint.proceed();
        } finally {
            long finish = System.currentTimeMillis();
            long timeMs = finish - start;
            System.out.println("END: " + joinPoint.toString() + " " + timeMs + "ms");
        }
    }
}