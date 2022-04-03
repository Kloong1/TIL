## AppConfig 리팩토링

현재 AppConfig를 보면 **중복**이 존재하고, **역할**에 따른 **구현**의 관계가 한 눈에 보이지 않는다.

#### AppConfig.java (리팩토링 이전)
```Java
package com.kloong.corebasic1;

//import 생략

public class AppConfig {

    public MemberService memberService() {
        return new MemberServiceImpl(new MemoryMemberRepository());
    }

    public OrderService orderService() {
        return new OrderServiceImpl(
        new MemoryMemberRepository(), new FixDiscountPolicy());
    }
}

```

AppConfig가 애플리케이션 전체 구조를 구성(설정)해 주는 역할을 하는데, 코드에서 이 그림이 잘 보이지 않는다. 따라서 리팩토링을 해주자.

#### AppConfig를 통해 한 눈에 보였으면 하는 그림
![](Pasted%20image%2020220403223101.png)

#### AppConfig.java (리팩토링 이후)
```Java
package com.kloong.corebasic1;

//import 생략

public class AppConfig {

    private MemberRepository memberRepository() {
        return new MemoryMemberRepository();
    }

    private DiscountPolicy discountPolicy() {
        return new FixDiscountPolicy();
    }

    public MemberService memberService() {
        return new MemberServiceImpl(memberRepository());
    }

    public OrderService orderService() {
        return new OrderServiceImpl(memberRepository(), discountPolicy());
    }

}
```

AppConfig.java만 보면 역할과 그 역할에 대한 구현 클래스가 한눈에 들어온다. 애플리케이션 전체 구성이 어떻게 되어있는지 빠르게 파악할 수 있다.

또 `new MemoryMemberRepository()` 코드의 중복이 제거되었다. 만약 구현체를 변경할 일이 생기면 중복된 코드를 여러개 변경할 필요가 없다.