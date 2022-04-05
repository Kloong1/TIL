# Spring의 다양한 설정 방식
스프링 컨테이너는 다양한 형식의 설정 정보를 받아들일 수 있게 유연하게 설계되어 있다.
- Java 코드, XML, Groovy, 심지어 사용자가 만든 임의의 방식까지!

![](Pasted%20image%2020220405183053.png)


## Annotation 기반 Java 코드 설정 사용
지금까지 했던 설정 방식. 최근에는 이 방식을 주로 사용한다.
```Java
new AnnotationConfigApplicationContext(AppConfig.class)
```
`AnnotationConfigApplicationContext` 클래스를 사용하면서 자바 코드로된 설정 정보를 넘기면 된다.


## XML 설정 사용
최근에는 스프링 부트를 많이 사용하면서 XML기반의 설정은 잘 사용하지 않는다.

하지만 아직 많은 레거시 프로젝트 들이 XML로 되어 있고, 또 XML을 사용하면 컴파일 없이 설정 파일의 내용만 수정하면 빈 설정 정보를 변경할 수 있는 장점도 있으므로 한번쯤 배워두는 것도 괜찮다.

`GenericXmlApplicationContext`를 사용하면서 xml 설정 파일을 넘기면 된다.

#### appConfig.xml
```XML
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="memberService" class="hello.core.member.MemberServiceImpl">
        <constructor-arg name="memberRepository" ref="memberRepository" />
    </bean>

    <bean id="memberRepository" class="hello.core.member.MemoryMemberRepository" />

    <bean id="orderService" class="hello.core.order.OrderServiceImpl">
        <constructor-arg name="memberRepository" ref="memberRepository" />
        <constructor-arg name="discountPolicy" ref="discountPolicy" />
    </bean>

    <bean id="discountPolicy" class="hello.core.discount.RateDiscountPolicy" />
</beans>
```

xml 기반의 appConfig.xml 스프링 설정 정보와 자바 코드로 된 AppConfig.java 설정 정보를 비교해보면 포맷만 다를 뿐 거의 비슷하다는 것을 알 수 있다.

실제 테스트 코드도 `AnnotationConfigApplicationContext` 구현체에서 `GenericXmlApplicationContext`로 바뀐 것 말고는 차이점이 없다.

#### XMLAppContextTest.java
```Java
package com.kloong.corebasic1.xml;

//import 생략

import static org.assertj.core.api.Assertions.*;

public class XMLAppContextTest {
    @Test
    void xmlAppContext() {
        ApplicationContext ac = new GenericXmlApplicationContext("appConfig.xml");
        MemberService memberService =
        ac.getBean("memberService", MemberService.class);
        assertThat(memberService).isInstanceOf(MemberService.class);
    }
}

```

xml 기반으로 설정하는 것은 최근에 잘 사용하지 않으므로, 필요하면 스프링 공식 레퍼런스 문서를 확인하자.  
https://spring.io/projects/spring-framework