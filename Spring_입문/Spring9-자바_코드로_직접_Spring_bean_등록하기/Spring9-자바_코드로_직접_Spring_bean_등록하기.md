# 자바 코드로 직접 Spring bean 등록하기

## 1. Annotation을 제거해서 클래스의 Component scan 비활성화
MemberService와 MemoryMemberRepository의 `@Service`, `@Repository`, `@Autowired` annotation을 제거하고, 자바 코드로 직접 Spring bean을 등록하는 법을 알아보자.

이 annotation을 제거하면 Spring이 Component scan을 통해 Spring bean을 자동으로 등록하고 DI를 해주지 않는다.

따라서 `SpringConfig` 클래스를 만든 뒤, 이 클래스에서 Spring에게 어떤 클래스를 Spring bean으로 등록할지, DI는 어떻게 할지 알려준다.

\*참고: 자바 코드 대신 XML 문서로도 Spring bean 등록과 DI를 해줄 수 있지만, 최근에는 잘 사용하지 않는다.


## 2. SpringConfig 클래스 작성
`SpringConfig.java` 파일을 만든다.

![400](스크린샷%202022-03-03%20오후%205.14.10.png)

#### SpringConfig.java
```Java
package com.kloong.MemberManaging;

import com.kloong.MemberManaging.repository.MemberRepository;
import com.kloong.MemberManaging.repository.MemoryMemberRepository;
import com.kloong.MemberManaging.service.MemberService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration annotation을 달아줘야 한다.
@Configuration
public class SpringConfig
{
	//@Bean annotation이 달린 method들이 반환하는 객체가 Spring bean으로 등록된다.
    @Bean
    public MemberService memberService()
    {
		//MemberService는 생성자를 통한 DI가 필요하므로 이렇게 해줘야 한다.
		//코드 자체만 보면 new 연산자를 통해 새로운 MemoryMemberRepository 객체가 연결될 것 같지만
		//이 부분은 Spring이 @Autowired처럼 동작해서 싱글톤의 Spring bean이 연결된다.
        return new MemberService(memberRepository());
    }

    @Bean
    public MemberRepository memberRepository()
    {
        return new MemoryMemberRepository();
    }
}
```

MemberController는 그대로 `@Controller` 와 `@Autowired` annotation을 사용해서 Component scan 방식으로 Spring bean 등록을 하고, 자동으로 DI를 한다.

\*주의: `@Autowired` 를 통한 DI는 MemberConroller, MemberService 등과 같이 Spring이 관리하는 객체에서만 동작한다. Spring bean으로 등록하지 않고 내가 직접 생성한 객체에서는 `@Autowired` 가 동작하지 않는다.

(자세한 건 깊은 내용이라서 설명을 안 하신 것 같지만) Controller의 경우는 어쩔 수 없다고 한다.


## 3. 자바 코드로 직접 Spring bean을 등록하는 이유
실무에서는 주로 정형화된 Controller, Service, Repository 같은 코드는 Component scan 방식을 사용한다. 그러나 정형화 되지 않은 클래스이거나, 상황에 따라 구현 클래스(implements한 클래스)를 변경해야 하면 자바 코드로 직접 Spring bean을 등록한다.

이 예제에서는 아직 DB가 선정되지 않았다는 가상의 시나리오가 있다. 하지만 일단 개발을 해야하기 때문에,  Interface로 MemberRepository를 작성하고 구현 클래스로 MemoryMemberRepository를 작성한 상황이다. 그리고 나중에 DB가 선정되면 MemoryMemberRepository를 그 DB를 사용한 구현 클래스로 바꿔치기 할 것이다.

![](Pasted%20image%2020220303174956.png)

이 때 Spring bean을 자바 코드로 직접 등록을 했다면, 이 바꿔치기 작업을 기존의 동작하는 코드에 전혀 손을 대지 않고 `SpringConfig.java` 만 수정하는 것으로 바꿔치기가 가능하다.

#### 바뀐 SpringConfig.java 예시
```Java
//package, import 생략

@Configuration
public class SpringConfig
{
	//생략

    @Bean
    public MemberRepository memberRepository()
    {
		//MemoryMemberRepository에서 DBMemberRepository로 바뀜
        return new DBMemberRepository();
    }
}
```


## 4. 참고 - DI의 3가지 방식
1. Field injection
2. Setter injection
3. Constructor injection

Constructor injection 방식이 주로 사용된다.

### 1) Field injection
```Java
@Controller
public class MemberController
{
    @Autowired private MemberService memberService;
}
```

좋지 않은 방법이다. Spring이 올라갈 때 Spring bean이 연결되는 작업 외에 다른 작업들을 할 수가 없다.

### 2) Setter injection
```Java
@Controller
public class MemberController
{
    private MemberService memberService;

	@Autowired
	public void setMemberService(MemberService memberService)
	{
		this.memberService = memberService;
	}
}
```

객체 생성이 된 후에 setter가 호출되어서 DI가 일어난다.

이것도 역시 권장되는 방법은 아니다. 의존 관계가 실행중에 동적으로 변하는 경우는 거의 없기 때문에 setter가 맨 처음에 호출된 이후로 다시 호출될 일이 없다. 그런데도 setter가 public으로 열려있으므로 개발자에 의해 잘못 호출되면 문제가 발생할 여지가 생긴다.

### 3) Constructor injection
```Java
@Controller
public class MemberController
{
    private MemberService memberService;

	@Autowired
	public MemberController(MemberService memberService)
	{
		this.memberService = memberService;
	}
}
```

최근에 가장 권장되는 방식. Setter injection에서의 문제를 해결하면서, field injection의 단점이 보완된다.