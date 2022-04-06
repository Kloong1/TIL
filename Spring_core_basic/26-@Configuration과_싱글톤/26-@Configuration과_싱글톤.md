# @Configuration과 싱글톤

#### AppConfig.java
```Java
//package, import 생략

@Configuration
public class AppConfig {

    @Bean
    public MemberRepository memberRepository() {
        return new MemoryMemberRepository();
    }

    @Bean
    public DiscountPolicy discountPolicy() {
        return new RateDiscountPolicy();
    }

    @Bean
    public MemberService memberService() {
        return new MemberServiceImpl(memberRepository()); //
    }

    @Bean
    public OrderService orderService() {
        return new OrderServiceImpl(memberRepository(), discountPolicy());
    }

}
```

AppConfig를 살펴보면 뭔가 이상한 점이 있다.

분명히 스프링 컨테이너는 싱글톤 패턴으로 객체를 관리한다고 했는데,  AppConfig의 코드만 보면 `memberService()`와 `orderService()`에서 MemoryMemberRepository의 객체가 각각 한 번씩, 총 두번 생성되는 것처럼 보인다.

결과적으로 각각 다른 2개의 MemoryMemberRepository가 생성되면서 싱글톤이 깨지는 것 처럼 보인다. 스프링이 아무리 엄청난 프레임워크라고 해도, `@Bean` annotation이 붙어 있는 코드가 저렇게 쓰여져 있기 때문에 저 코드를 그대로 실행시킬 수 밖에 없다. 프레임워크지 마법은 아니니까...

한번 테스트 코드로 MemoryMemberRepository의 객체가 1개인지 2개인지 확인해보자.

MemoryMemberRepository 객체를 주입받는 MemberServiceImpl과 OrderServiceImpl에 테스트용 코드를 임시로 추가해보자.

#### MemberServiceImpl.java
```Java
package com.kloong.corebasic1.member;

public class MemberServiceImpl implements MemberService{

    //DIP 만족. 인터페이스에만 의존한다. 어떤 구현체가 들어올지 모른다.
    private final MemberRepository memberRepository;

    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

	//로직 코드 생략

    //테스트 용도 코드
    public MemberRepository getMemberRepository() {
        return memberRepository;
    }
}
```

#### OrderServiceImpl.java
```Java
package com.kloong.corebasic1.order;

//import 생략

public class OrderServiceImpl implements OrderService{

    private final MemberRepository memberRepository;
    private final DiscountPolicy discountPolicy;

    public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy discountPolicy) {
        this.memberRepository = memberRepository;
        this.discountPolicy = discountPolicy;
    }

	//로직 코드 생략

    //테스트 용도 코드
    public MemberRepository getMemberRepository() {
        return memberRepository;
    }
}
```

이제 테스트 코드로 확인을 해보자.

#### ConfigurationSingletonTest.java
```Java
//package, import 생략

public class ConfigurationSingletonTest {
    @Test
    void configurationTest() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);

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

테스트를 해보니, MemberServiceImpl이 주입 받은 MemberRepository 객체와 OrderServiceImpl이 주입 받은 MemberRepository 객체가 **서로 동일하다!**

분명 AppConfig 코드를 보면 `new MemoryMemberRepository()` 코드가 2번 실행되는 것처럼 보이는데 어떻게 된 일일까?

스프링이 AppConfig의 메소드 중 일부를 호출을 하지 않는 것일까? 로그를 찍어서 확인해보자.

#### AppConfig.java
```Java
//package, import 생략

@Configuration
public class AppConfig {

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
        return new MemberServiceImpl(memberRepository());
    }

    @Bean
    public OrderService orderService() {
        System.out.println("call AppConfig.orderService");
        return new OrderServiceImpl(memberRepository(), discountPolicy());
    }
}
```

`@Bean`이 붙어있는 메소드의 실행 순서는 보장되지 않지만, 아무튼 스프링 컨테이너가 `@Bean` 메소드를 전부 한 번씩은 실행시키기 때문에 위 코드에 의하면 "call AppConfig.memberRepository" 로그가 적어도 3번은 출력될 것이라고 예측할 수 있다.

**그런데 짜잔!**

위에서 작성해 둔 ConfigurationSingletonTest 코드를 실행시켜보면 "call AppConfig.memberRepository" 로그가 **단 한 번만 출력되는 것을 확인할 수 있다.**

"call AppConfig.memberRepository", "call AppConfig.memberService", "call AppConfig.orderService" 이 3개의 로그가 각각 한 번씩만 출력된다.

스프링이 어떻게 하는지는 잘 모르겠지만, 아무튼 싱글톤을 보장해준다는 사실을 확인할 수 있다. 어떻게 하는지는 다음 시간에 계속...





