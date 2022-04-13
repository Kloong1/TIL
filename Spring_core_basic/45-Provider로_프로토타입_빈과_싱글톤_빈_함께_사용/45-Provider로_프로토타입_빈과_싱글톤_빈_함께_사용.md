# Provider로 프로토타입 빈과 싱글톤 빈을 함께 사용하기
싱글톤 빈과 프로토타입 빈을 함께 사용할 때, 어떻게 하면 사용할 때 마다 새로운 프로토타입 빈을 생성할 수 있을까?


## 사용할 때 마다 스프링 컨테이너에 요청
가장 단순(무식)한 방법은 싱글톤 빈이 프로토타입을 사용할 때 마다 스프링 컨테이너에 새로 요청하는 것이다.

이를 위해서 싱글톤 빈이 ApplicationContext 객체를 주입받아서, 사용 시마다 ApplicationContext를 통해 프로토타입 빈을 조회해야 한다.

```Java
static class ClientBean {

	@Autowired
	private ApplicationContext ac;
	
	public int logic() {
		PrototypeBean prototypeBean = ac.getBean(PrototypeBean.class);
		prototypeBean.addCount();
		return prototypeBean.getCount();
	}

	/* 생략 */
}
```

- 실행해보면 `ac.getBean()` 을 통해서 항상 새로운 프로토타입 빈이 생성되는 것을 확인할 수 있다.
- 이렇게 의존관계를 외부에서 주입(DI) 받는게 아니라 이렇게 직접 필요한 의존관계를 찾는 것을 **Dependency Lookup(DL. 의존관계 조회/탐색)** 이라고 한다.
- 그런데 이렇게 스프링의 ApplicationContext 전체를 주입받게 되면, 스프링 컨테이너에 종속적인 코드가되고, 단위 테스트도 어려워진다.

여기서 필요한 기능은 지정한 프로토타입 빈을 컨테이너에서 대신 찾아주는, DL 정도의 기능만 제공하는 무언가가 있으면 된다. 너무 많은 기능을 가지고 있는 ApplicationContext를 통채로 가지고 있을 필요가 전혀 없다.


## ObjectFactory, ObjectProvider
지정한 빈을 컨테이너에서 대신 찾아주는 DL 서비스를 제공하는 것이 바로 ObjectProvider 다. 참고로
과거에는 ObjectFactory가 있었는데, 여기에 편의 기능을 추가해서 ObjectProvider가 만들어졌다.

##### SingletonWithPrototypeTest1.java
```Java
//package, import 생략

public class SingletonWithPrototypeTest1 {

    @Test
    void singletonClientUsePrototype() {
        ConfigurableApplicationContext ac =
        new AnnotationConfigApplicationContext(
        ClientBean.class, PrototypeBean.class);

        ClientBean clientBean1 = ac.getBean(ClientBean.class);
        int count1 = clientBean1.logic();
        Assertions.assertThat(count1).isEqualTo(1);

        ClientBean clientBean2 = ac.getBean(ClientBean.class);
        int count2 = clientBean2.logic();
        Assertions.assertThat(count2).isEqualTo(1);

        ac.close();
    }

    @Scope("singleton")
    static class ClientBean {

        private final ObjectProvider<PrototypeBean> prototypeBeanProvider;

        public ClientBean(ObjectProvider<PrototypeBean> prototypeBeanProvider) {
            this.prototypeBeanProvider = prototypeBeanProvider;
        }

        public int logic() {
            System.out.println("ClientBean.logic");
            PrototypeBean prototypeBean = prototypeBeanProvider.getObject();
            prototypeBean.addCount();
            return prototypeBean.getCount();
        }

		/* 생략 */
    }

    @Scope("prototype")
    static class PrototypeBean { /* 생략 */ }
}
```

`ObjectProvider<PrototypeBean>` 타입의 빈을 스프링으로부터 주입받은 뒤, 이 빈을 사용해서 prototypeBean을 탐색하여 사용한다. 이 빈은 스프링이 내부적으로 만들어서 주입해주는 빈이다.

테스트 코드를 실행시켜보면,

```text
ClientBean.init
ClientBean.logic
PrototypeBean.init com.kloong.corebasic1.scope.SingletonWithPrototypeTest1$PrototypeBean@c05fddc
ClientBean.logic
PrototypeBean.init com.kloong.corebasic1.scope.SingletonWithPrototypeTest1$PrototypeBean@13bc8645
23:18:27.953 [main] DEBUG org.springframework.context.annotation.AnnotationConfigApplicationContext - Closing org.springframework.context.annotation.AnnotationConfigApplicationContext@635eaaf1, started on Wed Apr 13 23:18:27 KST 2022
ClientBean.destroy
```

`logic()`이 호출될 떄 마다 prototypeBean이 새롭게 생성되는 것을 확인할 수 있다.

- ObjectProvider의 `getObject()` 를 호출하면 내부에서는 스프링 컨테이너를 통해 해당 빈을 찾아서 반환하는 DL 기능을 수행한다. 이 때 PrototypeBean은 프로토타입 빈이므로, DL을 하는 순간 빈을 생성해서 반환해준다.
- 스프링이 제공하는 기능을 사용하지만, 기능이 단순하므로 단위테스트를 만들거나 mock 코드를 만들기는 훨씬 쉬워진다.
- ObjectProvider는 여기서 필요한 DL 정도의 기능만 제공한다.

위 코드에서 ObjectProvider를 ObjectFactory로 바꿔도 정상 동작한다. ObjectFactory 인터페이스에는 `getObject()` 메서드만 존재하고, 이 인터페이스를 상속받아서 추가적인 편의 기능을 제공하는 인터페이스가 `ObjectProvider` 이다.

- **ObjectFactory**: 기능이 단순하다. 별도의 라이브러리 필요 없다. 스프링에 의존적이다.
- **ObjectProvider**: ObjectFactory 상속한다. 옵션, 스트림 처리 등 편의 기능이 많다. 별도의 라이브러리 필요 없다. 스프링에 의존적이다.

둘 다 스프링에 의존적임을 알 수 있다.


## JSR-330 Provider
ObjectFactory와 ObjectProvider는 모두 스프링에 의존적이다. 스프링에 비의존적으로 DL을 할 수 있는 방법이 있다.

바로 `javax.inject.Provider` 라는 JSR-330 자바 표준을 사용하는 방법이다 (JSR-xxx는 자바 표준을 의미한다). 컨테이너에서 DL을 해주는 provider의 개념을 자바 진영에서 표준화 한 것이다.

이 방법을 사용하려면 `javax.inject:javax.inject:1` 라이브러리를 gradle에 추가해야 한다. 이 부분이 단점이라고 하면 단점이 될 수도 있다.

##### build.gradle
```Java
dependencies {
	implementation 'org.springframework.boot:spring-boot-starter'

	//lombok 라이브러리 추가 시작
	compileOnly 'org.projectlombok:lombok'
	annotationProcessor 'org.projectlombok:lombok'

	testCompileOnly 'org.projectlombok:lombok'
	testAnnotationProcessor 'org.projectlombok:lombok'
	//lombok 라이브러리 추가 끝

	//javax.inject.Provider
	implementation 'javax.inject:javax.inject:1'

	testImplementation 'org.springframework.boot:spring-boot-starter-test'
}
```

Provider의 코드를 살펴보면 아주 간단하다.

#### javax.inject.Provider.java
```Java
package javax.inject;
public interface Provider<T> {
	T get();
}
```

Provider를 사용하게끔 코드를 바꿔보자.

```Java
//package, import 생략
import javax.inject.Provider;

public class SingletonWithPrototypeTest1 {

	/* 생략 */

    @Scope("singleton")
    static class ClientBean {

		//ObjectProvider에서 Provider로 바뀜
        private final Provider<PrototypeBean> prototypeBeanProvider;

        public ClientBean(Provider<PrototypeBean> prototypeBeanProvider) {
            this.prototypeBeanProvider = prototypeBeanProvider;
        }

        @PostConstruct
        public void init() {
            System.out.println("ClientBean.init");
        }

        @PreDestroy
        public void destroy() {
            System.out.println("ClientBean.destroy");
        }

        public int logic() {
            System.out.println("ClientBean.logic");
            //getObject()가 아닌 get()
            PrototypeBean prototypeBean = prototypeBeanProvider.get();
            prototypeBean.addCount();
            return prototypeBean.getCount();
        }
    }

    @Scope("prototype")
    static class PrototypeBean {
        private int count = 0;

        public void addCount() {
            count++;
        }

        public int getCount() {
            return count;
        }

        @PostConstruct
        public void init() {
            System.out.println("PrototypeBean.init " + this);
        }

        @PreDestroy
        public void destroy() {
            System.out.println("PrototypeBean.destroy " + this);
        }
    }
}
```

`ObjectProvider<PrototypeBean>` 을 `Provider<PrototypeBean>` 으로만 바꾸고, `getObject()`를 `get()` 으로만 바꿨다.

- 실행해보면 `get()` 을 통해서 항상 새로운 프로토타입 빈이 생성되는 것을 확인할 수 있다.
- Provider 의 `get()` 을 호출하면 내부에서는 스프링 컨테이너를 통해 해당 빈을 찾아서 반환하는 **DL**을 수행한다.
- 자바 표준이고, 기능이 단순하므로 단위테스트를 만들거나 mock 코드를 만들기는 훨씬 쉬워진다.
- Provider 는 지금 딱 필요한 DL 정도의 기능만 제공한다.

#### Provider 특징
- `get()` 메서드 하나로 기능이 매우 단순하다.
- 별도의 라이브러리가 필요하다는 단점 아닌 단점이 있다.
- 자바 표준이므로 스프링이 아닌 다른 컨테이너에서도 사용할 수 있다.


## 정리
그러면 프로토타입 빈을 언제 사용할까? -> 매번 사용할 때 마다 의존관계 주입이 완료된 새로운 객체가 필요하면 사용하면 된다.

그런데 실무에서 웹 애플리케이션을 개발해보면, 싱글톤 빈으로 대부분의 문제를 해결할 수 있기 때문에 프로토타입 빈을 직접적으로 사용하는 일은 매우 드물다.

참고로 ObjectProvider, JSR-330 Provider등은 프로토타입 뿐만 아니라 DL이 필요한 경우는 언제든지
사용할 수 있다.

>참고: 스프링이 제공하는 메서드에 @Lookup 애노테이션을 사용하는 방법도 있지만, 이전 방법들로 충분하고, 고려해야할 내용도 많아서 생략하겠다

>참고: 실무에서 자바 표준인 JSR-330 Provider를 사용할 것인지, 아니면 스프링이 제공하는 ObjectProvider를 사용할 것인지 고민이 될 것이다.
>
>ObjectProvider는 DL을 위한 편의 기능을 많이 제공해주고, 스프링 외에 별도의 의존관계 추가가 필요 없기 때문에 편리하다. 만약 (정말 그럴일은 거의 없겠지만) 코드를 스프링이 아닌 다른 컨테이너에서도 사용할 수 있어야 한다면 JSR-330 Provider를 사용해야한다.
>
>스프링을 사용하다 보면 이 기능 뿐만 아니라 다른 기능들도 자바 표준과 스프링이 제공하는 기능이 겹칠때가 많이 있다. 대부분 스프링이 더 다양하고 편리한 기능을 제공해주기 때문에, 특별히 다른 컨테이너를 사용할 일이 없다면, 스프링이 제공하는 기능을 사용하면 된다.