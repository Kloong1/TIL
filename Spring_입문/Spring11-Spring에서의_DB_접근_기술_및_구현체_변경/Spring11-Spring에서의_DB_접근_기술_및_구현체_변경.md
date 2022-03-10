# Spring에서의 DB 접근 기술 및 구현체 변경
이 예제에서는 단순한 교육용 DB인 H2 DB를 사용한다.

## 1. Spring에서의 DB 접근 기술의 발달
20년 전 쯤에는 순수 **JDBC**로 DB에 접근했다. 물론 매우 어려운 작업이었다.

그래서 Spring에서 **JDBCTemplete**을 제공해줬고, JDBCTemplete을 사용하면 SQL query를 편리하게 날릴 수 있다.

하지만 query를 직접 짜는 것도 힘든 작업이기 때문에, **JPA**라는 기술을 사용해서 DB에 접근할 수 있게 되었다.

이 JPA도 이제는 매우 오래된 기술이기 때문에, 최근에는 **Spring Data JPA** 라는, JPA를 한 번 감싸서 JPA를 더 편리하게 사용할 수 있는 기술을 사용한다.

## 2. 순수 JDBC

### 환경 설정

#### 1) build.gradle 파일에 jdbc, h2 db 관련 라이브러리 추가

#### build.gradle
```Java
plugins {
	id 'org.springframework.boot' version '2.6.4'
	id 'io.spring.dependency-management' version '1.0.11.RELEASE'
	id 'java'
}

group = 'com.kloong'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '11'

repositories {
	mavenCentral()
}

dependencies {
	implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'
	implementation 'org.springframework.boot:spring-boot-starter-web'
	implementation 'org.springframework.boot:spring-boot-starter-jdbc'
	runtimeOnly 'com.h2database:h2'
	testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

tasks.named('test') {
	useJUnitPlatform()
}

```

#### 2) Spring Boot에 DB 연결 설정 추가

#### application.properties
```Java
spring.datasource.url=jdbc:h2:tcp://localhost/~/Spring/MemberManaging
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
```

### JdbcMemberRepository.java 작성
코드가 매우 길어서 코드는 생략

### 구현체를 MemoryMemberRepository에서 JdbcMemberRepository로 변경

다른 코드를 건드릴 필요 없이, `SpringConfig.java` 만 수정하면 된다!

#### SpringConfig.java
```Java
package com.kloong.MemberManaging;

import com.kloong.MemberManaging.repository.JdbcMemberRepository;
import com.kloong.MemberManaging.repository.MemberRepository;
import com.kloong.MemberManaging.repository.MemoryMemberRepository;
import com.kloong.MemberManaging.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class SpringConfig
{
    private DataSource dataSource;

    @Autowired
    public SpringConfig(DataSource dataSource)
    {
        this.dataSource = dataSource;
    }

    @Bean
    public MemberService memberService()
    {
        return new MemberService(memberRepository());
    }

    @Bean
    public MemberRepository memberRepository()
    {
        return new JdbcMemberRepository(dataSource);
        //return new MemoryMemberRepository();
    }
}

```

\*참고: DataSource는 DB connection을 획득할 때 사용하는 객체다. Spring Boot는 DB connection 정보(`application.properties` 파일에 입력해둔 정보)를 바탕으로 DataSource를 생성하고 Spring bean으로 만들어둔다. 그래서 이런 코드를 통해 DI를 쉽게 받을 수 있다.

![](Pasted%20image%2020220303230244.png)

![](Pasted%20image%2020220303230254.png)

Spring의 DI를 사용하면 기존 코드를 전혀 손대지 않고, 설정만으로 구현 클래스를 변경할 수 있다. Spring을 통해서 편리하게 이런 객체지향적인 설계를 할 수 있다!

이 예제는 간단한 경우지만, 만약 MemberRepository를 의존하는 Service가 많은 경우, 직접 동작하는 코드를 건드리려면 많은 코드를 건드려야만 한다. 하지만 Spring을 통해 위와 같은 방식으로 설계를 하면, assembling을 담당하는 코드 하나만 수정하면 Spring이 DI를 알아서 해주기 때문에 아주 편리하다!

이는 객체 지향 설계의 SOLID 원칙 중 하나인 **OCP(Open-Closed Principle)** 를 잘 지켰다고 볼 수 있다. OCP는 **확장에는 Open, 수정/변경에는 Closed** 라는 원칙인데, 이 원칙을 지키면서 객체 지향의 다형성을 잘 활용하면 코드 수정을 최소화하면서 기능을 확장할 수 있다.