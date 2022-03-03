# Spring Bean & Dependency Injection

## 1. Spring bean 등록, 의존 관계 설정
여태까지 개발한 것을 토대로 사용자가 웹을 통해서 서비스를 이용할 수 있게 만들어야 한다. 그러기 위해서는 `MemberController`를 만들고, `MemberController`가 `MemberService`를 통해서 회원 가입, 회원 조회 등의 기능을 수행할 수 있게 해야한다. 이것을 **\"MemberController가 MemberService를 의존한다\"** 고 한다.

#### MemberController.java
```Java
package com.kloong.MemberManaging.controller;  
  
import org.springframework.stereotype.Controller;  
  
@Controller  
public class MemberController  
{  
}
```

`MemberController` 클래스를 만들고 `@Controller` annotation을 붙이면, Spring이 Spring Container에 `MemberController` 객체를 생성한 뒤 Spring bean으로 등록해서 관리한다.

![](Pasted%20image%2020220303012457.png)

위 그림에서 우측 Spring Container 안에 들어있는 초록색 타원이 Spring bean을 의미한다. 맨 처음 Spring이 올라갈 때, Spring이 `@Controller` annotaion이 있는 클래스를 찾아서 Spring container에 Spring bean으로 등록해서 관리하기 때문에 사용자가 다른 작업을 하지 않아도 Spring이 동작할 수 있는 것이다.

#### MemberController.java
```Java
package com.kloong.MemberManaging.controller;  
  
import com.kloong.MemberManaging.service.MemberService;  
import org.springframework.beans.factory.annotation.Autowired;  
import org.springframework.stereotype.Controller;  
  
@Controller  
public class MemberController  
{  
	/*  
	MemberConroller에 @Controller annotaion을 달아서 Spring bean 형태로 관리할 건데
	MemberService를 이렇게 new로 인스턴스 생성을 한 뒤 MemberService의 기능을 사용하면 문제가 생긴다.
	Spring Container에 등록하고 Spring Container로부터 받아 쓰도록 바꿔야 한다.
	MemberController 말고도 다른 Controller에서도 MemberService를 받아 쓸 가능성이 있기 때문에
	인스턴스를 여러개 만들 필요 없이 하나만 만들고 받아 쓰는 것이 더 좋다.
	*/

	//private final MemberService memberService = new MemberService();  

	private final MemberService memberService;  
  
    //MemberController는 @Controller 가 붙어있으므로 Spring이 올라갈 때 Spring Container에  
	//생성됨. 그 때 생성자가 호출되는데, 생성자에 @Autowired 가 붙어있으면
	//memberService를 String Container로부터 가져와서 연결을 시켜준다.
	@Autowired  
	public MemberController(MemberService memberService)
	{
		this.memberService = memberService;  
	}  
}
```

MemberController에서 MemberService 객체를 사용해야 하는데, MemberController는 Spring에 의해 Spring bean으로 관리되고 있다.

이런 경우 MemberSerive를 new 연산자로 새로운 인스턴스를 만들어서 사용하는 것이 아니라, Spring bean 형태의 MemberService를 MemberController에서 끌어다 쓸 수 있게끔 해줘야 한다.

그러기 위해서 생성자를 만들고, `@Autowired` annotation을 달아줘서, Spring이 올라갈 때 이 작업을 알아서 해주게끔 처리한다.

#### MemberService.java
```Java
@Service  
public class MemberService
{  
	private final MemberRepository memberRepository;  

	public MemberService(MemberRepository memberRepository)
	{
		this.memberRepository = memberRepository;  
    }

	//후략...
}
```

MemberService 클래스에 `@Service` annotation을 달아주지 않으면, 실행 했을 때 다음과 같은 에러가 뜬다.

```
***************************
APPLICATION FAILED TO START
***************************

Description:

Parameter 0 of constructor in com.kloong.MemberManaging.controller.MemberController required a bean of type 'com.kloong.MemberManaging.service.MemberService' that could not be found.


Action:

Consider defining a bean of type 'com.kloong.MemberManaging.service.MemberService' in your configuration.
```

Spring에서 `@Autowired`를 보고 MemberSerivce를 MemberController에 연결해 주려고 하는데, MemberService가 아무리 찾아봐도 없다는 의미이다.

이건 당연한 건데, MemberService 클래스에 `@Service` annotation을 달지 않았기 때문에, Spring에서 순수 Java 클래스인 MemberService의 존재를 알지 못하고 Spring bean으로 관리하지 못하는 것이다.

![](Pasted%20image%2020220303015511.png)

MemverService에 `@Service` annotation을 달고, 여기서도 MemberRepository를 이용하기 때문에 마찬가지로 생성자에  `@Autowired` annotation을 달아준다.

\*참고: 생성자가 1개만 있으면 `@Autowired` annotation은 생략 가능하다고 한다.

#### MemberService.java
```Java
@Service  
public class MemberService
{  
	private final MemberRepository memberRepository;  

	@Autowired
	public MemberService(MemberRepository memberRepository)
	{
		this.memberRepository = memberRepository;  
    }

	//후략...
}
```

마지막으로 MemoryMemberReposiory 클래스에 `@Repository` annotation을 달아주면 된다.

#### MemoryMemberRepository.java
```Java
@Repository  
public class MemoryMemberRepository implements MemberRepository  
{  
	private static Map<Long, Member> store = new HashMap<>();  
	private static long sequence = 0L;
}	
```

Controller, Service, Repository (그리고 Domain) 패턴은 매우 정형화된 패턴이기 때문에 (Controller를 통해서 외부 요청을 받고, Service로 비즈니스 로직을 만들고, Repository에 데이터를 저장하는 패턴), 이런 패턴으로 클래스를 작성 후 `@Controller`, `@Service`, `@Repository` annotation을 잘 달아주면 Spring이 올라갈 때 Controller, Service, Repository를 Spring bean 형태로 관리한다.

![](Pasted%20image%2020220303015830.png)

그리고 Spring bean 사이의 연결 과정은 `@Autowired`를 보고 Spring이 연결해준다. 이것을 앞에서 잠시 언급한 **Dependency Injection(DI)** 라고 한다.

## 2. Spring bean을 등록하는 2가지 방법
Spring bean을 등록하는 방법에는 2가지가 있다.
1. Component 스캔과 자동 의존관계 설정
2. 자바 코드로 직접 Spring bean 등록

### 1) Component 스캔과 자동 의존관계 설정
위에서 배운 방식이다.  
`@Controller`, `@Service`, `@Repository` 대신 `@Component` annotation을 달아줘도 된다고 한다. `@Controller`, `@Service`, `@Repository` annotation 내용을 까보면 전부 `@Component` annotation이 포함되어 있음을 확인할 수 있다.

Spring이 올라올 때, `@Component` annotation과 관련 있는 클래스들을 확인해서 Spring bean으로 등록을 한다. 그리고 `@Autowired`가 있으면 Spring bean 사이의 의존관계를 설정해준다.

Spring을 쓰면, 왠만한 객체는 전부 Spring bean으로 등록해서 쓰는게 효율적이라고 한다. 자세한 내용은 후에 서술.

\*참고: Spring은 Spring container에 Spring bean을 등록할 때, 기본으로 싱글톤으로 등록한다 (유일하게 하나만 등록해서 공유한다). 따라서 같은 Spring bean이면 모두 같은 인스턴스다. 필요시 따로 설정을 하면 싱글톤이 아니게 만들 수 있지만, 특별한 경우를 제외하면 대부분 싱글톤을 사용한다.

#### 아무 클래스에 @Component annotation을 달아도 Spring bean으로 등록될까?
##### MemberManagingApplication.java
``` Java
package com.kloong.MemberManaging;  
  
import org.springframework.boot.SpringApplication;  
import org.springframework.boot.autoconfigure.SpringBootApplication;  
  
@SpringBootApplication  
public class MemberManagingApplication {  

	public static void main(String[] args){
		SpringApplication.run(MemberManagingApplication.class, args);  
	}    
}
```

Spring이 Component scan을 하는 범위는 기본적으로 main method가 존재하는 이 클래스의 package인 `com.kloong.MemberManaging` 패키지와 그 하위 패키지들로 설정되어있다.

이 패키지 범위를 벗어난 패키지는 Component scan의 대상이 되지 않는다. 물론 설정을 따로 해주면 이 범위를 변경할 수 있다.

### 2) 자바 코드로 직접 Spring bean 등록
다음 문서에서 서술한다.