# Spring 통합 테스트

## 0. 지금까지는...
여태까지 작성했던 테스트 코드는 사실 **Spring과 전혀 관계가 없었다!** 해당 테스트 코드들은 순수 Java 코드가 제대로 동작하는지 확인하기만 했다.

하지만 지금 우리가 테스트 해야되는 기능에는 Spring이 반드시 포함되어 있어야 한다. 실제 동작 시에 Spring이 DB 연결도 해주고 (Datasource 객체를 Spring bean으로 등록해서 관리), 기타 다른 객체들도 Spring bean으로 관리해서 동작하게 끔 만들어져 있기 때문이다.

따라서 테스트 코드에 Spring이 개입할 수 있게 만들어 줘야 한다. Spring이 Spring bean을 만들어서 등록하고, DI도 알아서 하게 만들고, 테스트 할 때 Spring이 올라가서 할 일을 하게 만들어줘야 하는 것이다.

## 1. Spring이 테스트 코드에 개입하게 만드는 방법
Spring container와 테스트 코드를 함께 실행시키는 방법은 아주 간단하다. 테스트 클래스에 `@SpringBootTest` annotation을 달아주면 된다.

##### MemberServiceIntergrationTest.java
```Java
//생략...

@SpringBootTest
class MemberServiceIntegrationTest
{
	//생략...
}
```

또 테스트 method 별로 `@Test` annotation을 달아주고, DI를 위해 `@Autowired` annotation을 사용해야 한다.

##### MemberServiceIntergrationTest.java
```Java
//생략...

@SpringBootTest
class MemberServiceIntegrationTest
{
    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;

    @Test
    void join()
    {
        //생략...
    }

    @Test
    void joinDuplicatedMemberTest()
    {
        //생략...
    }
}
```

테스트 코드이기 때문에 constructor injection을 사용하지 않고 가장 단순한 field injection을 사용했다.

## 2. Spring 통합 테스트와 DB
이 상태로 테스트를 몇 번 진행하면 에러가 발생한다. 왜냐하면 DB에 이전 테스트의 결과가 저장되기 때문이다.

여태까지는 메모리 기반 repository를 사용했기 때문에 `@BeforeEach`와 `@AfterEach` 를 사용해서 각 테스트 method가 독립된 환경에서 실행될 수 있게 했다. 혹은 테스트 method 전체를 한번에 실행시키지 않고 따로 따로 실행시키면 `@BeforeEach`와 `@AfterEach` 없이도 테스트가 가능했다 (물론 실무에서는 테스트 method가 매우 많으므로 그렇게 하지는 않을 것이다).

하지만 DB가 사용된 경우에는 다르다. 이전의 테스트 method 실행 결과가 DB에 저장되기 때문이다. 따라서 `@BeforeEach`와 `@AfterEach` 를 사용해서 DB를 초기화 시키는 쿼리를 실행시켜야 하는데 이는 매우 복잡한 일이다.

그런데 매우 간단한 해결책이 있다. 바로 `@Transactional` annotation을 달아주는 것이다.

##### MemberServiceIntergrationTest.java
```Java
//생략...

@SpringBootTest
@Transactional
class MemberServiceIntegrationTest
{
	//생략...
}
```

`@Transactional` annotation을 달아주면, 각 테스트 method를 실행시킬 때 마다 Spring이 DB에 날렸던 모든 쿼리를 최종적으로 commit 하지 않고 rollback 해버린다. 따라서 테스트 method의 실행 결과가 DB에 적용되지 않게 되고, 각 테스트 method는 독립된 환경에서 실행될 수 있게 된다.