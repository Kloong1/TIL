# Spring Data JPA를 활용한 DB 접근 기술

## 1. Spring Data JPA의 강점
스프링 부트와 JPA만 사용해도 개발 생산성이 매우 증가하고, 개발해야할 코드도 확연히 줄어든다. 그런데 **Spring Data JPA**를 사용하면, 기존의 한계를 넘어 Repository의 구현 클래스 없이 인터페이스 만으로 개발을 완료할 수 있다. 그리고 반복적으로 개발해온 기본적인 CRUD 기능도 Spring Data JPA가 모두 제공한다.

실무에서 RDB를 사용한다면, JPA와 Spring Data JPA는 이제 선택이 아닌 필수이다.

\*주의: Spring Data JPA는 JPA를 편리하게 사용하도록 도와주는 기술이다. 따라서 JPA를 먼저 학습한 후에 스프링 데이터 JPA를 학습해야 한다.


## 2. Spring Data JPA 적용하기

##### SpringDataJpaMemberRepository.java
```Java
package com.kloong.MemberManaging.repository;

import com.kloong.MemberManaging.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataJpaMemberRepository extends JpaRepository<Member, Long>, MemberRepository
{
    @Override
    Optional<Member> findByName(String name);
}
```

Class가 아닌 interface를 작성한 뒤, `JpaRepository<,>` interface와 기존에 작성해둔 MemberRepository interface를 extends하면 끝이다.

`JpaRepository<,>`를 extends 할 때는 DB에 저장할 Entity와, 해당 Entity의 ID type을 넣어주면 된다.

이렇게만 작성한 뒤 `SpringConfig.java` 만 수정해주면 끝난다.

##### SpringConfig.java
```Java
package com.kloong.MemberManaging;

import com.kloong.MemberManaging.repository.*;
import com.kloong.MemberManaging.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

@Configuration
public class SpringConfig
{
    private final MemberRepository memberRepository;

    @Autowired
    public SpringConfig(MemberRepository memberRepository)
    {
        this.memberRepository = memberRepository;
    }

    @Bean
    public MemberService memberService()
    {
        return new MemberService(memberRepository);
    }
}
```


## 3. 이 코드가 동작할 수 있는 이유

##### SpringDataJpaMemberRepository.java
```Java
//생략...

public interface SpringDataJpaMemberRepository extends JpaRepository<Member, Long>, MemberRepository
{
    @Override
    Optional<Member> findByName(String name);
}
```

Spring이 `JpaRepository<,>`를 extends한 **interface의 구현체를 알아서 생성한뒤 그 객체를 Spring bean으로 등록해서 관리한다!!**

![](Pasted%20image%2020220310184629.png)

`JpaRepository` 에는 위 그림처럼 기본적인 CRUD를 위한 공통적인 method들이 존재한다.

MemberRepository의 method인 save, findById, findAll 등의 method가 구현체에 알아서 구현이 된다는 의미이다.

하지만 공통적이지 않은 method(예를 들어 이 예제에서는 findByName method. 이름이 존재하지 않거나, name 대신 username을 사용할 수도 있고, 경우는 매우 많음)는 Spring에게 그 존재를 알려줘야 한다.

method의 이름을 짓는 규칙만 맞춰주면, Spring이 method 이름을 보고 해당 method를 구현해준다.

##### SpringDataJpaMemberRepository.java
```Java
//생략...

public interface SpringDataJpaMemberRepository extends JpaRepository<Member, Long>, MemberRepository
{
	//findBy 뒤에 Name이 있으므로, 이 method는 다음과 같은 JPQL을 사용하게끔 구현된다
	//select m from member m where m.name = ?
    @Override
    Optional<Member> findByName(String name);

	//아래 method는 그냥 예시. 이런 것도 가능하다. 명명 규칙만 맞추면 된다.
	Optional<Member> findByNameAndId(String name, Long id);
}
```

즉 개발자가 할 일은 이름 짓는 규칙에 맞춰서 method 이름만 잘 지어주면 끝나는 것이다. 따라서 기본적인 CRUD를 구현하는 데 드는 시간이 획기적으로 줄어든다!

\*참고: 실무에서는 JPA와 Spring Data JPA를 기본으로 사용하고, 복잡한 동적 쿼리는 Querydsl이라는 라이브러리를 사용하면 된다. Querydsl을 사용하면 쿼리도 자바 코드로 안전하게 작성할 수 있고, 동적 쿼리도 편리하게 작성할 수 있다. 이 조합으로 해결하기 어려운 쿼리는 JPA가 제공하는 네이티브 쿼리를 사용하거나, 앞서 학습한 스프링 JdbcTemplate를 사용하면 된다.