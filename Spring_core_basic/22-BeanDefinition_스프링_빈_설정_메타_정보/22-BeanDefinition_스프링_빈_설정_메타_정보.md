# BeanDefinition - 스프링 빈 설정 메타 정보
스프링은 Java 코드, XML, Groovy, 심지어 사용자 임의 방식 설정 등 다양한 설정 방식을 지원한다. 어떻게 이런 다양한 설정 형식을 지원할 수 있는 것일까?

**그 중심에는 BeanDefinition 이라는 추상화가 있다.** 쉽게 이야기해서 BeanDefinition을 통해 **역할과 구현을 개념적으로 나눈 것**이다! 

![](Pasted%20image%2020220405192856.png)

- XML을 읽어서 BeanDefinition을 만든다.
- 자바 코드를 읽어서 BeanDefinition을 만든다.
- 사용자가 임의적으로 설정한 방식대로 정보를 읽어서 BeanDefinition을 만든다.

스프링 컨테이너는 설정 방식이 Java 코드인지, XML인지 몰라도 된다. 오직 그 결과로 나온 BeanDefinition만 알면 된다. 즉 역할과 구현을 구분함으로써, 스프링 컨테이너가 BeanDefinition이라는 추상화에만 의존하게 만든 것이다!

BeanDefinition을 빈 설정 메타정보라 한다. Java annotation `@Bean` , XML에서의 `<bean>` 당 각각 하나씩 빈 설정 메타 정보가 생성된다.

그리고 스프링 컨테이너는 이 메타정보를 기반으로 스프링 빈을 생성한다.

## 코드 레벨의 관점
![](Pasted%20image%2020220405193002.png)

- AnnotationConfigApplicationContext는 AnnotatedBeanDefinitionReader를 사용해서 AppConfig.class를 읽고 (Java 코드를 마치 설정 정보처럼 읽는다) BeanDefinition을 생성한다.
- GenericXmlApplicationContext는 XmlBeanDefinitionReader를 사용해서 appConfig.xml 설정정보를 읽고 BeanDefinition을 생성한다.
- 새로운 형식의 설정 정보가 추가되면, XxxBeanDefinitionReader를 만들어서 BeanDefinition을 생성하면 된다.

실제로 AnnotationConfigApplicationContext 클래스를 살펴보면,

#### AnnotationConfigApplicationContext.java
```Java
public class AnnotationConfigApplicationContext extends GenericApplicationContext implements AnnotationConfigRegistry {

	private final AnnotatedBeanDefinitionReader reader;
	
	//생략...
}
```

AnnotatedBeanDefinitionReader가 존재함을 확인할 수 있다.

**정리하자면, 어떤 식으로든 BeanDefinition만 잘 만들어서 스프링 컨테이너에게 전달해주면 된다는 것이다.**


## BeanDefinition
코드를 통해 BeanDefinition에 어떤 정보가 담겨있는지 확인해보자.

#### BeanDefinitionTest.java
```Java
package com.kloong.corebasic1.beandefinition;

//import 생략...

public class BeanDefinitionTest {
    AnnotationConfigApplicationContext ac =
    new AnnotationConfigApplicationContext(AppConfig.class);

    @Test
    @DisplayName("빈 설정 메타정보 확인")
    void findApplicationBean()
    {
        String[] beanDefinitionNames = ac.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            BeanDefinition beanDefinition = ac.getBeanDefinition(beanDefinitionName);

            if (beanDefinition.getRole() == BeanDefinition.ROLE_APPLICATION)
            {
                System.out.println("beanDefinitionName = " + beanDefinitionName +
                        " beanDefinition = " + beanDefinition);
            }
        }
    }
}
```

위 코드를 실행하면 다음과 같은 정보들이 나온다.

- BeanClassName: 생성할 빈의 클래스 명(자바 설정 처럼 팩토리 역할의 빈을 사용하면 없음)
- factoryBeanName: 팩토리 역할의 빈을 사용할 경우 이름, 예) appConfig
- factoryMethodName: 빈을 생성할 팩토리 메서드 지정, 예) memberService
- Scope: 싱글톤(기본값)
- lazyInit: 스프링 컨테이너를 생성할 때 빈을 생성하는 것이 아니라, 실제 빈을 사용할 때 까지 최대한 생성을 지연처리 하는지 여부
- InitMethodName: 빈을 생성하고, 의존관계를 적용한 뒤에 호출되는 초기화 메서드 명
- DestroyMethodName: 빈의 생명주기가 끝나서 제거하기 직전에 호출되는 메서드 명
- Constructor arguments, Properties: 의존관계 주입에서 사용한다. (자바 설정 처럼 팩토리 역할의 빈을 사용하면 없음)

이 빈 메타 정보를 가지고 실제 객체를 생성할 수 있다.

\*참고: `getBeanDefinition()` 메소드는 ApplicationContext 인터페이스에 정의되어 있지 않다. 따라서 다음과 같이 객체를 참조하면 해당 메소드를 사용할 수 없다.

```Java
	ApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);
```

## 정리
- BeanDefinition을 직접 생성해서 스프링 컨테이너에 등록할 수 도 있다. 하지만 실무에서 BeanDefinition을 직접 정의하거나 사용할 일은 거의 없다.
- BeanDefinition에 대해서는 너무 깊이있게 이해하기 보다는, 스프링이 다양한 형태의 설정 정보를 BeanDefinition으로 추상화해서 사용하는 것 정도만 이해하면 된다.
- 가끔 스프링 코드나 스프링 관련 오픈 소스의 코드를 볼 때, BeanDefinition 이라는 것이 보일 때가 있다. 이때 이러한 메커니즘을 떠올리면 된다.