# Spring이란?
## Spring을 구성하는 다양한 프로젝트들
![](스크린샷%202022-03-31%20오후%204.37.14.png)

**Spring Framework와 Spring Boot에 핵심 기능이 들어있다!**

나머지 프로젝트들은 부가적인 기능(그렇다고 중요하지 않다는 것은 아니다)을 구현한 프로젝트들. 위의 프로젝트 외에 더 많은 프로젝트가 있다.

### 1. Spring Framework
Spring의 핵심이 되는 기능들은 다 여기 있다. 최근에는 Spring Boot를 통해서 Spring Framework의 기술들을 보다 편리하게 사용한다.

- 핵심 기술: Spring DI container, AOP, 이벤트, 기타
- 웹 기술: Spring MVC, Spring WebFlux
- 데이터(DB) 접근 기술: Transaction, JDBC, ORM 지원, XML 지원
- 기술 통합: 캐시, 이메일, 원격접근, 스케줄링
- 테스트: Spring 기반 테스트 지원
- 언어: Kotling, Groovy


### 2. Spring Boot
Spring Framework를 편리하게 사용할 수 있도록 지원하는 기술. 최근에는 기본으로 사용한다고 보면 된다.

- 단독으로 실행할 수 있는 Spring application을 쉽게 생성
	- Tomcat 같은 웹 서버를 내장했기 때문에, 별도의 웹 서버를 설치한 뒤 서버에 application을 넣고 띄우는 여러 작업들을 수동으로 하지 않아도 된다.
- 손쉬운 빌드 구성을 위한 starter dependency 제공
	- 기존에는 Spring framework를 쓰기 위해서 라이브러리를 하나 하나 땡겨오는 작업이 필요했는데, Spring Boot를 사용하면 starter 하나를 땡겨오면 알아서 나머지를 다 땡겨와준다.
- (핵심적인) Third-party 라이브러리와의 호환성을 고려해서 알맞는 버전의 라이브러리를 자동으로 구성해 준다.
- 메트릭, 상태 확인, 외부 구성 같은 프로덕션 준비 기능 제공 (모니터링 관련 기능들을 기본적으로 제공해준다는 것 같음)
- 관례에 의한 간결한 설정이 default로 되어 있다. 필요하다면 공식 문서를 찾아서 조금만 수정해주면 된다.

\*주의: Spring Boot는 Spring Framework와 별도로 사용할 수 있는 것이 아니다! Spring의 다양한 프로젝트들을 조금 더 쉽게 사용할 수 있게 도와주는 기술이다.


## Spring이라는 단어의 의미
**Spring이라는 단어는 문맥에 따라 다르게 사용된다 (공식 문서에도 나와있는 내용)**

1. Spring DI container 기술 (Spring의 핵심 기술. Spring bean 관리 등. 가장 좁은 범위의 의미)
2. Spring Framework
3. Spring Boot, Spring Framework 등을 모두 포함한 Spring 생태계 (일반적인 상황에서 쓰이는 의미)


## Spring의 핵심 개념과 핵심 컨셉
핵심 개념과 컨셉이 아주 좋았기 때문에, 거기에 점점 내용이 추가되면서 기술이 탄생하게 되는 것이다. Spring도 3만줄의 코드에서 시작되었다!

이 핵심 개념과 컨셉에 대해서 제대로 이해해야한다! 이 것을 이해 못하고 사용하는 것은 단순히 API 사용 방법을 배우고 쓰는 것 뿐이다.

### Spring의 핵심
Spring은 Java 기반의 framework이다. Java의 가장 큰 특징은 객체 지향 언어라는 것!

**Spring은 객체 지향 언어가 가진 강력한 특징을 살려내는 framework이다!**  
**Spring은 좋은 객체 지향 어플리케이션을 개발할 수 있게 도와주는 framework이다!**

과거 EJB를 사용했을 때, EJB에 종속적으로 개발하면서 객체 지향의 장점을 잃어버리게 되었다.

하지만 Spring이 등장하면서, Spring의 DI container 기술을 통해 객체 지향의 장점을 살릴 수 있게 되었다! 그것이 당시 개발자들이 Spring에 열광한 이유!