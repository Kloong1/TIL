# AOP

## 1. AOP가 필요한 상황

#### 만약 모든 method의 호출 시간을 측정하고 싶다면?

모든 method에 시간을 측정하는 코드를 추가해야한다. 이것 자체로도 너무 오래 걸리는 작업이다. 그런데 여기에 다음과 같은 추가적인 문제점이 발생한다.

 1. 시간을 측정하는 로직과 핵심 비즈니스의 로직이 섞여서 유지보수가 어렵다.
 2. 시간을 측정하는 로직을 변경할 때 모든 로직을 찾아가면서 변경해야한다.
 3. 위 문제를 해결하기 위해서 시간을 측정하는 로직을 별도의 공통 로직으로 만드는 것 역시 매우 어려운 작업이다.

회원 가입, 회원 조회등 비즈니스 로직에 관련된 핵심 관심 사항과, 시간을 측정하는 로직인 공통 관심 사항이 얽히면서 문제가 생긴다.

하지만 여기에 AOP를 도입한다면?

## 2. AOP(Aspect Oriented Programming) 적용하기
AOP(Aspect Oriented Programming). 관점 지향 프로그래밍.

공통 관심 사항(Cross-cutting concern)과 핵심 관심 사항(Core concern)을 분리할 수 있다.

![](Pasted%20image%2020220310201848.png)

##### TimeTraceAop.java
```Java
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
```

`@Aspect` annotation을 달아주면 해당 클래스를 AOP로 사용할 수 있다.

AOP로 사용할 객체 역시 Spring bean으로 등록해줘야 하기 때문에 (실제 동작은 Spring container 위에서 하기 때문에 당연하다) `@Component` annotation을 달아줬다.

AOP는 Service, Repository같은 정형화 된 component가 아니기 때문에, `SpringConfig.java` 에서 코드로 Spring bean으로 직접 등록하는 것이 좋다 (하지만 이 예제에서는 `@Around`로 타겟팅되는 클래스에 TimeTraceApp 클래스가 포함되어 버려서 bean 순환 참조 문제가 발생한다. 이를 해결하기 위해서 `@Around` 의 조건에서 TimeTraceAop를 제외시키면 되는데, 그러면 조건이 복잡해지니까 그냥 `@Component`를 사용했다.)

`@Around` annoatation으로 AOP가 적용될 타겟을 지정할 수 있다.

AOP를 통해서 기존의 여러 문제들을 해결할 수 있게 되었다.

 1. 회원 가입, 회원 조회 등의 핵심 관심 사항과 시간을 측정하는 공통 관심 사항을 분리했다.
 2. 시간을 측정하는 로직을 별도의 공통 로직으로 만들었다. 따라서,
	 1. 핵심 관심 사항을 깔끔하게 유지할 수 있다.
	 2. 변경이 필요하면 이 로직만 변경하면 된다.
 3. 원하는 적용 대상을 선택할 수 있다.

## 3. Spring에서 AOP의 동작 방식
AOP의 동작 방식에는 여러가지 방식이 있지만, Spring에서는 Proxy를 통해 AOP가 동작한다.

##### AOP 적용 전 의존 관계
![](Pasted%20image%2020220310205725.png)
MemberController bean이 MemberService bean을 의존하고 있다.

##### AOP 적용 후 의존 관계
![](Pasted%20image%2020220310205746.png)
AOP가 적용된 클래스의 경우, **Proxy**라고 하는 **가짜** 객체가 만들어져서 Spring에 의해 관리된다.

Spring이 올라오면서 Spring bean이 등록되고 DI를 하는 과정에서, 가짜 Spring bean이 등록되고, 이 bean이 DI가 된다. 따라서 MemberController에서 의존하고 있는 MemberService를 호출하면, 이 가짜 Spring bean이 동작을 하고, 가짜 Spring bean에서 내부적으로 joinPoint.proceed()를 하면 그제서야 진짜 Spring bean이 동작하게된다.

전체적인 그림을 살펴보면 다음과 같다.

##### AOP 적용 전
![](Pasted%20image%2020220310210246.png)

##### AOP 적용 후
![](Pasted%20image%2020220310210309.png)

실제로 Proxy가 DI 되었는지 코드로 확인해보면,

##### MemberController.java
```Java
//생략...

@Controller
public class MemberController
{
    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService)
    {
        this.memberService = memberService;
        System.out.println("memberService = " + memberService.getClass());
    }

	//생략...
}
```

실행시켜보면 `com.kloong.MemberManaging.service.MemberService` 가 아니라 아래와 같은 결과가 찍힌다.

```
memberService = class com.kloong.MemberManaging.service.MemberService$$EnhancerBySpringCGLIB$$9a9e2221
```

MemberService를 복제해서 코드를 조작한 새로운 클래스, 즉 Proxy가 DI되었음을 확인할 수 있다.

\*참고: DI 기법을 활용했기 때문에 간단하게 Proxy 방식의 AOP를 적용할 수 있음을 알 수 있다. DI 과정에서 실제 객체가 아닌 가짜 객체만 injection 하면 되기 때문이다. 이것 역시 DI의 장점 중 하나이다.


## 4. 마치며...
AOP를 활용하면 공통 관심 사항과 핵심 관심 사항을 분리해서 코드를 관리할 수 있고, 원하는 대상에 공통 관심 사항을 적용할 수 있다.

위에서 예시로 든 시간 측정 외에도 매우 다양한 상황에서 활용이 가능하므로 AOP를 아주 강력한 도구로 사용할 수 있다.