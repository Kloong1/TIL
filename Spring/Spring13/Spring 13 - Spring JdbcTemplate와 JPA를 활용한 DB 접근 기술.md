# Spring JdbcTemplate와 JPA를 활용한 DB 접근 기술

## 1. JdbcTemplate를 활용한 DB 접근 기술

스프링 JdbcTemplate와 MyBatis 같은 라이브러리를 활용해서, JDBC API만 사용했을 때 발생하는 중복된 코드를 대부분 제거할 수 있다. 하지만 여전히 SQL 쿼리는 직접 작성해야 한다.

순수 JDBC API를 사용했을 떄와 동일한 환경설정에서 사용 가능하다.

## 2. JPA를 활용한 DB 접근 기술
JPA는 순수 JDBC의 반복 코드 문제를 해결해 줌과 동시에, 기본적인 SQL도 JPA가 직접 만들어서 실행해준다. JPA를 사용하게 되면 SQL과 데이터 중심의 설계에서 객체 중심의 설계로 패러다임 전환을 할 수 있다. 

JPA는 표준 interface고, 그 구현체로 hibernate를 주로 사용한다.

JPA는 ORM 기술이다. Object Relational Mapping, 즉 객체와 RDB의 table을 mapping 한다는 의미이다.

JPA를 사용하기 위해서는 JPA 라이브러리가 필요하다.

##### build.gradle
```Java
dependencies {
	implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'
	implementation 'org.springframework.boot:spring-boot-starter-web'
	implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
	runtimeOnly 'com.h2database:h2'
	testImplementation 'org.springframework.boot:spring-boot-starter-test'
}
```

DB에 실제로 저장될 데이터는 `Member` 객체이므로, JPA가 해당 객체를 알아볼 수 있도록 `Member` 클래스에 `@Entity` annotation을 달아준다.

##### Member.java
```Java
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Member
{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //고객이 정하는 id가 아닌 고유값으로써의 id
    private String name;

	//생략...
}
```

멤버 변수 id는 DB에서 직접 생성에서 설정해주는 고유값(인 동시에 Primary key)이므로, 해당 사실을 JPA에 알려줘야 한다. `@Id`와 `@GeneratedValue` annotation을 달아주면 된다.

JPA는 Jdbc나 JdbcTemplate와 다르게 `Datasource`가 아닌 `EntityManager`를 통해 DB에 접근한다. Spring boot가 여러가지 설정 파일들을 보고 자동으로 `EntityManager`를 생성한 뒤 DB와 연결해준다. 우리는 그저 Spring bean으로 관리되고 있는 `EntityManager`를 DI 받으면 된다.

##### JpaMemberRepository.java
```Java
public class JpaMemberRepository implements MemberRepository
{
    private final EntityManager em;

    @Autowired
    public JpaMemberRepository(EntityManager em)
    {
        this.em = em;
    }

	//생략...
}
```

JPA는 간단한 SQL은 알아서 작성해주고, 필요한 경우 JPQL이라는 query를 사용할 수 있다. JPQL은 객체(정확히는 `Entity`)에 대한 query이기 때문에, 여전히 query를 사용함에도 불구하고 SQL과 데이터 중심의 설계에서 객체 중심의 설계로 패러다임을 전환 할 수 있게 해주는 JPA의 특성에 잘 부합한다.

##### JpaMemberRepository.java
```Java
package com.kloong.MemberManaging.repository;

import com.kloong.MemberManaging.domain.Member;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class JpaMemberRepository implements MemberRepository
{
    private final EntityManager em;

    @Autowired
    public JpaMemberRepository(EntityManager em)
    {
        this.em = em;
    }

    @Override
    public Member save(Member member)
    {
        em.persist(member);
        return member;
    }

    @Override
    public Optional<Member> findById(Long id)
    {
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member);
    }

    @Override
    public Optional<Member> findByName(String name)
    {
        List<Member> result = em.createQuery(
		        "select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();

        return result.stream().findAny();
    }

    @Override
    public List<Member> findAll()
    {
        //JPQL. Entity를 대상으로 쿼리를 날리면 SQL로 자동으로 변환됨.
        //객체 자체를 select 하는 모습을 볼 수 있다.
        return em.createQuery("select m from Member m",
			        Member.class).getResultList();
    }
}
```

JPA를 사용하는 경우, 데이터를 저장하거나 변경하는 코드에는 반드시 `@Transactional` annotation이 있어야 한다.

##### MemberService.java
```Java
//생략...

@Transactional
public class MemberService
{
    //생략...

	//데이터를 저장하는 method는 join 하나 뿐이므로 이 method에만 @Transactional annotation을
	//달아줘도 된다.
	//@Transactional
    public Long join(Member member)
    {
        validateDuplicatedMember(member); //중복 회원 검증
        memberRepository.save(member);
        return member.getId();
    }

	//생략...
}
```

마지막으로 MemberRepository를 JpaMemberRepository로 바꿔주기만 하면 된다. 기존의 `Datasource` DI하던 코드를 `EntityManager`를 DI하게 만들어줘야 한다.

##### SpringConfig.java
```Java
//생략...

@Configuration
public class SpringConfig
{
    private EntityManager em;

    @Autowired
    public SpringConfig(EntityManager em)
    {
        this.em = em;
    }

    @Bean
    public MemberService memberService()
    {
        return new MemberService(memberRepository());
    }

    @Bean
    public MemberRepository memberRepository()
    {
        return new JpaMemberRepository(em);
    }
}
```