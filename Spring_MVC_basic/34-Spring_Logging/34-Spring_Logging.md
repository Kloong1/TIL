# Spring - logging

운영 시스템에서는 `System.out.println()` 같은 시스템 콘솔을 사용해서 필요한 정보를 출력하지 않고, 별도의 로깅 라이브러리를 사용해서 로그를 출력한다.

참고로 로그 관련 라이브러리도 많고, 깊게 들어가면 끝이 없기 때문에, 여기서는 최소한의 사용 방법만 알아본다.


## Logging 라이브러리
스프링 부트에 스프링 부트 로깅 라이브러리(`spring-boot-starter-logging`)가 포함되어 있다. 스프링 부트 로깅 라이브러리는 기본으로 다음 로깅 라이브러리를 사용한다.

- SLF4J
- Logback

로그 라이브러리에는 Logback, Log4J, Log4J2 등 수많은 라이브러리가 존재하는데, 그것을 통합해서 인터페이스로 제공하는 것이 바로 SLF4J 라이브러리다. 쉽게 이야기해서 SLF4J는 인터페이스이고, 그 구현체로 Logback 같은 로그 라이브러리를 선택하면 된다.

실무에서는 스프링 부트가 기본으로 제공하는 Logback을 대부분 사용한다.


## Log 선언 및 호출
#### Log 선언
- `private Logger log = LoggerFactory.getLogger(getClass());`
- `private static final Logger log = LoggerFactory.getLogger(Xxx.class)`
- `@Slf4j`
	- 롬복에서 지원하는 애노테이션이다.
	- 이 애노테이션을 클래스에 달면, 로깅에 필요한 `Logger` 를 선언해줄 필요가 없다.
	- 즉  `private Logger log = LoggerFactory.getLogger(getClass());` 이 코드를 알아서 만들어준다.

#### Log 호출
- `log.info("hello")`

##### LogTestController.java
```Java
package com.kloong.springmvc.basic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController //@Controller 아니다!
public class LogTestController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @GetMapping("/log-test")
    public String logTest() {
        String name = "Spring";

        System.out.println("name = " + name);

        log.trace("trace log={}", name);
        log.debug("debug log={}", name);
        log.info("info log={}", name);
        log.warn("warn log={}", name);
        log.error("error log={}", name);

        return "OK";
    }
}
```
- `@RestController`
	- `@Controller` 는 메소드의 반환 값이 String 이면 뷰 이름으로 인식한다. 그래서 해당 뷰 이름을 가진 뷰를 찾고, 렌더링을 시도한다.
	- 반면에 `@RestController` 는 반환 값으로 뷰를 찾는 것이 아니라, HTTP 메시지 바디에 바로 입력한다. 따라서 응답 메시지로 반환값인 "OK"를 받을 수 있다. 이는 `@ResponseBody` 와 관련이 있는데, 뒤에서 더 자세히 설명한다.
	- Rest는 RESTful 할때 REST를 의미하는 것이다.

#### 실행 결과
![](스크린샷%202022-05-31%20오후%208.33.44.png)
- Log 포맷
	- 시간 - log 레벨 - 프로세스 ID - 쓰레드 이름 - 클래스 이름 - 로그 메시지
- Log 레벨
	-  **LEVEL: TRACE > DEBUG > INFO > WARN > ERROR**
	- 로그 레벨에 따라 출력 여부를 다르게 할 수 있다.
		- 개발 서버에는 DEBUG 레벨 이하의 로그가 출력되게 설정
		- 실제 운영 서버에는 INFO 레벨 이하의 로그가 출력되게 설정

### 로그 레벨 설정
##### application.properties
```
#프로젝트 전체 로그 레벨 설정(default는 info)
logging.level.root=info

#hello.springmvc 패키지와 그 하위 로그 레벨 설정 (프로젝트 전체 로그 레벨보다 우선순위 높음)
#debug, info, warn, error 레벨의 로그가 전부 출력된다
logging.level.com.kloong.springmvc=debug
```

만약 `logging.level.com.kloong.springmvc=trace` 로 설정하면 모든 레벨의 로그가 출력될 것이다.

### 실무에서 콘솔 출력이 아닌 로깅을 사용해야 하는 이유
- 만약 로깅 라이브러리 대신 `System.out.println()` 으로 로그를 전부 출력한다면?
	- 운영 서버에 배포할 때 해당 출력 코드를 지우지 않으면 로그 폭탄을 맞는다.
	- 그렇다고 로그 출력 코드를 안 쓰면서 개발을 하는건 너무 불편하다.
- 개발 과정에서만 필요한 로그라면 DEBUG 레벨로 로깅을 한다. 출력하는 로그 레벨도 DEBUG로 설정하면 해당 로그를 확인할 수 있다.
	- `log.debug()`
	- `application.properties` 에 `logging.level.com.kloong.springmvc=debug`
- 운영 과정에서 필요한 로그라면 INFO 레벨로 로깅을 한다.
	- `log.info()`
- 실제 배포때는 출력하는 로그 레벨을 INFO로 설정한다.
	- `application.properties` 에 `logging.level.com.kloong.springmvc=info`

### 올바른 로그 사용법
- `log.debug("data="+data)`
	- 로그 출력 레벨을 info로 설정해도 해당 코드에 있는 `"data="+data`가 실제 실행이 되어 버린다.결과적으로 문자 더하기 연산이 발생한다.
- `log.debug("data={}", data)`
	- 로그 출력 레벨을 info로 설정하면 의미없는 연산이 발생하지 않는다.


## 로그 사용의 장점
- 쓰레드 정보, 클래스 이름 같은 부가 정보를 함께 볼 수 있고, 출력 모양을 조정할 수 있다.
- 로그 레벨에 따라 개발 서버에서는 모든 로그를 출력하고, 운영서버에서는 출력하지 않는 등 로그를 상황에 맞게 조절할 수 있다.
- 시스템 아웃 콘솔에만 출력하는 것이 아니라, 파일이나 네트워크 등, 로그를 별도의 위치에 남길 수 있다. 특히 파일로 남길 때는 날짜별 분류 혹은 용량이 커지면 로그를 분할하는 것도 가능하다.
- 성능도 `System.out` 보다 좋다(내부 버퍼링, 멀티 쓰레드 등의 기술을 사용하기 때문). 그래서 실무에서는 꼭 로그를 사용해야 한다.