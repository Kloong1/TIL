# Lombok과 최신 트렌드
스프링 프레임워크를 사용해서 개발을 해 보면, 대부분의 의존관계가 불변이다. 따라서 final 키워드와 함께 constructor injection을 사용하게 된다.

그런데 생성자를 만들고, 주입 받은 값을 대입하는 코드도 만드는 것은 field injection에 비해 확실히 귀찮은 일이다. 조금 더 편하게 하는 방법은 없을까?

다음 코드를 최적화해보자.

```Java
@Component
public class OrderServiceImpl implements OrderService{

    private final MemberRepository memberRepository;
    private final DiscountPolicy discountPolicy;

    @Autowired
    public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy discountPolicy) {
        this.memberRepository = memberRepository;
        this.discountPolicy = discountPolicy;
    }
}
```

일단 생성자가 1개만 있으면 `@Autowired` 를 생략할 수 있다.

```Java
@Component
public class OrderServiceImpl implements OrderService{

    private final MemberRepository memberRepository;
    private final DiscountPolicy discountPolicy;

    public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy discountPolicy) {
        this.memberRepository = memberRepository;
        this.discountPolicy = discountPolicy;
    }
}
```

이제 이 코드에 Lombok을 적용해보자.

## Lombok 라이브러리 추가 및 환경 설정

#### 1. build.gradle 수정
```gradle
plugins {
	id 'org.springframework.boot' version '2.6.6'
	id 'io.spring.dependency-management' version '1.0.11.RELEASE'
	id 'java'
}

group = 'com.kloong'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '11'

//lombok 설정 추가 시작
configurations {
	compileOnly {
		extendsFrom annotationProcessor
	}
}
//lombok 설정 추가 끝

repositories {
	mavenCentral()
}

dependencies {
	implementation 'org.springframework.boot:spring-boot-starter'

	//lombok 라이브러리 추가 시작
	compileOnly 'org.projectlombok:lombok'
	annotationProcessor 'org.projectlombok:lombok'

	testCompileOnly 'org.projectlombok:lombok'
	testAnnotationProcessor 'org.projectlombok:lombok'
	//lombok 라이브러리 추가 끝

	testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

tasks.named('test') {
	useJUnitPlatform()
}
```

https://start.spring.io/ 로 프로젝트를 만들 때 Lombok 라이브러리를 미리 추가하는 방법도 가능하다.

### 2.Enable annotation processing
Preferences -> Annotation Processors 검색 -> Enable annotation processing 체크 -> 재시작

### 3. 테스트
#### LombokTest.java
```Java
package com.kloong.corebasic1;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LombokTest {

    private String name;

    public static void main(String[] args) {
        LombokTest lombokTest = new LombokTest();

        lombokTest.setName("kim");
        String name = lombokTest.getName();
        System.out.println("name = " + name);
    }
}
```

실행시켜보면 다음과 같이 출력된다.
```text
name = kim
```

getter와 setter를 구현하지 않았는데도, 컴파일도 잘 되고 동작도 잘 한다. Lombok이 annotation processing을 통해서 getter와 setter를 자동으로 만들어주는 것이다. 실무에서 매우 많이 사용한다.

생성자에 관련된 annotation도 지원된다. `@NoArgsConstructor`, `@AllArgsConstructor`, `@RequiredArgsConstructor` 등이 있다.


## Lombok을 사용해 코드 최적화
#### OrderServiceImpl.java
```Java
@Component
public class OrderServiceImpl implements OrderService{

    private final MemberRepository memberRepository;
    private final DiscountPolicy discountPolicy;

    public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy discountPolicy) {
        this.memberRepository = memberRepository;
        this.discountPolicy = discountPolicy;
    }
}
```

위의 코드에 Lombok을 적용해서 코드를 최적화 해보자.

#### OrderServiceImpl.java
```Java
//package, import 생략

@Component
@RequiredArgsConstructor //Lombok 적용
public class OrderServiceImpl implements OrderService{

    private final MemberRepository memberRepository;
    private final DiscountPolicy discountPolicy;
}
```

`@RequiredArgsConstructor` 를 사용하면, final이 붙은 필드에 대해 Lombok이 생성자를 자동으로 만들어준다. final이 붙으면 생성자를 통해서만 외부에서 초기화가 가능하기 때문이다.

이 최종결과 코드와 이전의 코드는 완전히 동일하다. 롬복이 자바의 애노테이션 프로세서라는 기능을 이용해서 컴파일 시점에 생성자 코드를 자동으로 생성해준다. 실제 class 를 열어보면 생성자 코드가 존재함을 확인할 수 있다.

Lombok을 적용하자 코드가 매우 간단해짐을 확인할 수 있다. 나중에 final이 붙은 필드를 추가하더라도 생성자를 수정할 필요가 없으니 더더욱 편리하다.


## 정리
최근에는 생성자를 딱 1개 두고, `@Autowired` 를 생략하는 방법을 주로 사용한다. 그런데 여기에 Lombok 라이브러리의 `@RequiredArgsConstructor` 를 사용하면, 생성자조차 작성하지 않고도 기능을 동일하게 사용할 수 있다.