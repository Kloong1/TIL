# @Configuration과 바이트코드 조작
스프링 컨테이너는 **싱글톤 레지스트리(싱글톤 객체를 생성하고 관리하는 역할)다.** 따라서 스프링 빈이 싱글톤이 되도록 보장해주어야 한다.

그런데 아무리 스프링이라고 해도, **이미 작성되어 있는 자바 코드를 조작하기는 어렵다.** AppConfig의 자바 코드를 보면 `new MemoryMemberRepository()`가 분명 3번 호출되어야 하는 것이 맞다.

그래서 스프링은 **클래스의 바이트코드를 조작하는 라이브러리를 사용한다.**

모든 비밀은 `@Configuration`에 담겨있다.

다음 테스트 코드의 출력을 확인해보자.

```Java
//package, import 생략

public class ConfigurationSingletonTest {

    @Test
    void configurationDeep() {
        ApplicationContext ac =
        new AnnotationConfigApplicationContext(AppConfig.class);

        AppConfig bean = ac.getBean(AppConfig.class);

		//bean.getClass() = class com.kloong.corebasic1.AppConfig 가 출력될 것 같은데...
        System.out.println("bean.getClass() = " + bean.getClass());
    }
}
```

 `AnnotationConfigApplicationContext` 에 파라미터로 넘긴 값도 스프링 빈으로 등록된다. 따라서 AppConfig도 스프링 빈이 된다. 위 코드는 AppConfig 스프링 빈을 조회하는 코드이다.

위 테스트 메소드를 실행시키면 "bean.getClass() = class com.kloong.corebasic1.AppConfig"가 출력될 것이라는 예상과 다르게 다음과 같은 출력이 나타난다.

```
bean.getClass() = class com.kloong.corebasic1.AppConfig$$EnhancerBySpringCGLIB$$7d542337
```

AppConfig 클래스 뒤에 뭔가 이상한게 추가가 되어있는 것을 확인할 수 있다.

이는 내가 작성한 AppConfig 클래스가 아니라, **스프링이 바이트 코드 조작으로 만들어낸 클래스이다!**

스프링이 CGLIB라는 바이트코드 조작 라이브러리를 사용해서 **AppConfig 클래스를 상속받은 임의의 다른 클래스**를 만들고, **그 다른 클래스를 스프링 빈으로 등록한 것이다!**

![](Pasted%20image%2020220406181043.png)
appConfig 스프링 빈은 실제로 AppConfig의 객체가 아니라, AppConfig 클래스를 상속받은 임의의 다른 클래스의 객체이다.

스프링은 그 임의의 다른 클래스가 바로 싱글톤이 보장되도록 해준다. 아마도 다음과 같이 바이트 코드를 조작해서 작성되어 있을 것이다.(실제로는 CGLIB의 내부 기술을 사용하는데 매우 복잡하다.)

#### AppConfig@CGLIB의 대략적인 예상 코드
```Java
@Bean
public MemberRepository memberRepository() {

	if (memoryMemberRepository가 이미 스프링 컨테이너에 등록되어 있으면?) {
		return 스프링 컨테이너에서 찾아서 반환;
	}
	else { //스프링 컨테이너에 없으면
	기존 로직을 호출해서 MemoryMemberRepository를 생성하고 스프링 컨테이너에 등록
	기존 로직이란 AppConfig에 작성되어있는 코드를 의미함
		return 등록한 객체를 반환
	}
}
```

CGLIB에 의해 조작되어 만들어진 임의의 클래스가 이런 원리로 동작하기 때문에 싱글톤이 보장되는 것이다.

AppConfig@CGLIB이 AppConfig의 자손 타입이기 때문에 AppConfig 타입으로 조회가 가능하다.


## @Configuration을 적용하지 않고 @Bean만 적용한다면?
`@Configuration` 을 붙이면 바이트코드를 조작하는 CGLIB 기술을 사용해서 싱글톤을 보장한다. 그러면 `@Configuration` 을 없애고 `@Bean` 만 적용하면 어떻게 될까?

#### ConfigurationSingletonTest.java
```Java
//package, import 생략

public class ConfigurationSingletonTest {

    @Test
    void configurationDeep() {
        ApplicationContext ac =
        new AnnotationConfigApplicationContext(AppConfig.class);

        AppConfig bean = ac.getBean(AppConfig.class);

        System.out.println("bean.getClass() = " + bean.getClass());
    }
}
```

다음과 같은 출력이 나타난다 (일부 내용 생략)

```
call AppConfig.memberRepository
call AppConfig.memberService
call AppConfig.memberRepository
call AppConfig.orderService
call AppConfig.memberRepository
bean.getClass() = class com.kloong.corebasic1.AppConfig
```

즉 정상적으로 스프링 빈으로 등록되긴 하지만, CGLIB에 의해 조작된 임의의 클래스의 객체가 등록되는 것이 아니라 순수한 클래스의 객체가 등록되는 것을 확인할 수 있다.

또 이 출력 결과를 통해서 memberRepository() 메소드가 총 3번 호출된 것을 알 수 있다.

1번은 @Bean에 의해 스프링 컨테이너에 등록하기 위해서이고, 2번은 각각 memberService() 메소드와 orderService() 메소드에서 memberRepository()를 호출한 것이다.

조작되지 않은 AppConfig의 순수 자바 코드가 실행됨으로 인해서 **싱글톤이 깨진 것이다!**

```Java
//package, import 생략

public class ConfigurationSingletonTest {
    @Test
    void configurationTest() {
        ApplicationContext ac =
        new AnnotationConfigApplicationContext(AppConfig.class);

        MemberServiceImpl memberService =
        ac.getBean("memberService", MemberServiceImpl.class);
        OrderServiceImpl orderService =
        ac.getBean("orderService", OrderServiceImpl.class);
        MemberRepository memberRepository =
        ac.getBean("memberRepository", MemberRepository.class);

        MemberRepository memberRepository1 = memberService.getMemberRepository();
        MemberRepository memberRepository2 = orderService.getMemberRepository();

        System.out.println("memberRepository = " + memberRepository);
        System.out.println(
        "memberService -> memberRepository = " + memberRepository1);
        System.out.println(
        "orderService -> memberRepository = " + memberRepository2);

        Assertions.assertThat(memberRepository).isSameAs(memberRepository1);
        Assertions.assertThat(memberRepository).isSameAs(memberRepository2);
        Assertions.assertThat(memberRepository1).isSameAs(memberRepository2);
    }
}
```

MemberRepository의 객체가 서로 같은 객체인지 테스트 코드를 통해 확인해보면 당연히 실패한다.

그리고 문제가 하나 더 있다. 

#### AppConfig.java
```Java
//package, import 생략

//@Configuration
public class AppConfig {

	//스프링에 의해 bean으로 관리되는 객체 생성
    @Bean
    public MemberRepository memberRepository() {
        System.out.println("call AppConfig.memberRepository");
        return new MemoryMemberRepository();
    }

    @Bean
    public DiscountPolicy discountPolicy() {
        return new RateDiscountPolicy();
    }

    @Bean
    public MemberService memberService() {
        System.out.println("call AppConfig.memberService");
        return new MemberServiceImpl(memberRepository()); //일반 객체 주입
    }

    @Bean
    public OrderService orderService() {
        System.out.println("call AppConfig.orderService");
        return new OrderServiceImpl(memberRepository(), discountPolicy());
        //일반 객체 주입
    }

}
```

`new MemberServiceImpl(memberRepository())` 과  
`new OrderServiceImpl(memberRepository(), discountPolicy())`  에서
주입되는 MemoryMemberRepository 객체는 빈으로 관리되는 객체가 아니다!

그냥 클라이언트 코드에서 객체를 생성해서 넣는 것과 동일하다.

이건 그냥 당연한 얘기다. CGLIB에 의해 조작된 AppConfig를 통해 주입되는 객체는, 스프링 컨테이너에 해당 객체가 존재하면 찾아서 반환해줬다. 이 것 때문에 싱글톤도 보장이 되는 것이였다.

하지만 현재 AppConfig는 순수 클래스인 상태이기 때문에, 순수 자바 코드를 호출한다. 따라서 스프링 빈을 넘겨주지 않고 스프링이 관리하지 않는 그냥 객체를 새로 생성해서 넘겨주는 것이다.


## 정리
- `@Bean`만 사용해도 스프링 빈으로 등록되지만, 싱글톤을 보장하지 않는다.
	- 정확히는 memberRepository() 처럼 **의존관계 주입이 필요해서 메서드를 직접 호출할 때 싱글톤을 보장하지 않는다.**

크게 고민할 것이 없다. 스프링 설정 정보는 항상 `@Configuration` 을 사용하자.