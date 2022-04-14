# Request scope 예제

## 1. 웹 환경 설정, 라이브러리 추가
웹 스코프는 웹 환경에서만 동작하므로, 웹 환경 관련 라이브러리를 추가해보자.

##### build.gradle
```gradle
dependencies {
	//web 라이브러리 추가
	implementation 'org.springframework.boot:spring-boot-starter-web'

	//생략
}
```

프로젝트의 External library를 살펴보면 Tomcat 라이브러리와, Spring web 관련 라이브러리가 추가된 것을 볼 수 있다.

![](스크린샷%202022-04-14%20오후%203.51.23.png)

이제 프로젝트를 생성할 때 만들어져 있었던 `@SpringBootApplication` 이 붙어있는 클래스의 main 메서드를 실행하면,

##### Corebasic1Application.java
```Java
package com.kloong.corebasic1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Corebasic1Application {

    public static void main(String[] args) {
        SpringApplication.run(Corebasic1Application.class, args);
    }

}
```

출력에 이전과는 다른 로그가 남아있는 것을 확인 할 수 있다.

```text
2022-04-14 15:51:58.061  INFO 1601 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
```

Tomcat 서버가 8080 포트로 띄워졌다는 로그를 확인할 수 있다. 실제로 `http://localhost:8080` 으로 접속하면 오류 페이지가 뜨는 것을 확인할 수 있다.

>참고: spring-boot-starter-web 라이브러리를 추가하면 스프링 부트는 내장 톰켓 서버를 활용해서 웹 서버와 스프링을 함께 실행시킨다.

>참고: 스프링 부트는 웹 라이브러리가 없으면 우리가 지금까지 학습한AnnotationConfigApplicationContext을 기반으로 애플리케이션을 구동한다. 웹 라이브러리가추가되면 웹과 관련된 추가 설정과 환경들이 필요하므로AnnotationConfigServletWebServerApplicationContext를 기반으로 애플리케이션을 구동한다.


## 2. Request scope 예제 개발
웹 어플리케이션에 동시에 여러 HTTP 요청이 오면, 로그를 남길 때 정확히 어떤 요청이 남긴 로그인지 구분하기 어렵다.

이럴때 사용하기 딱 좋은것이 바로 request 스코프이다.

다음과 같이 로그가 남도록 request 스코프를 활용해서 추가 기능을 개발해보자.

```text
[d06b992f...] request scope bean create
[d06b992f...][http://localhost:8080/log-demo] controller test
[d06b992f...][http://localhost:8080/log-demo] service id = testId
[d06b992f...] request scope bean close
```

- 로그 포맷: `[UUID][request URL]{message}`
- UUID를 사용해서 각 로그가 어떤 HTTP 요청에 대한 로그인지 구분할 수 있다.
- requestURL 정보도 추가로 넣어서 어떤 URL을 요청해서 남은 로그인지 확인하자.

>참고: UUID란?
>Universally Unique Identifier(범용 고유 식별자)로, 네트워크 상의 개체들을 식별하기 위한 고유한 ID 부여 표준 체계이다. 고유성을 완벽하게 보장할 수는 없지만, 확률상으로는 중복될 가능성이 없다고 보면 된다.

HTTP request가 오면, 위의 포맷에 맞게 로그를 남기는 동작을 하는 MyLogger 클래스를 작성해보자.

##### MyLogger.java
```Java
//package, import 생략
import java.util.UUID;

@Component
@Scope(value = "request") //request scope. value는 생략 가능하다.
public class MyLogger {

    private String uuid;
    private String requestURL;

    public void setRequestURL(String requestURL) {
        this.requestURL = requestURL;
    }

    public void log(String message) {
        System.out.printf("[%s] [%s] %s\n", uuid, requestURL, message);
    }

    @PostConstruct
    public void init() {
        uuid = UUID.randomUUID().toString(); //UUID 할당
        System.out.println(
        "[" + uuid + "] request scope bean created - " + this);
    }

    @PreDestroy
    public void close() {
        System.out.println(
        "[" + uuid + "] request scope bean closed - " + this);
    }
}
```

- `@Scope(value = "request")` 를 사용해서 request 스코프로 지정했다. 이제 **이 빈은 HTTP 요청 당 하나씩 생성되고, HTTP 요청이 끝나는 시점에 소멸된다.**
- 이 빈이 생성되는 시점에 스프링이 `@PostConstruct` 초기화 메서드를 호출해서 uuid를 생성한 뒤 저장해둔다. 이 빈은 HTTP 요청 당 하나씩 생성되므로, **uuid를 저장해두면 다른 HTTP 요청과 구분할 수 있다.**
- 이 빈이 소멸되는 시점에 스프링이 `@PreDestroy` 를 호출해서 종료 메시지를 남긴다.
- requestURL은 이 빈이 생성되는 시점에는 알 수 없으므로, 외부에서 setter로 입력 받는다.

이제 클라이언트의 요청을 처리할 controller 클래스를 작성해보자.

##### LogDemoController.java
```Java
//package, import 생략
import javax.servlet.http.HttpServletRequest;

@Controller
@RequiredArgsConstructor
public class LogDemoController {

    private final LogDemoService logDemoService;
    private final MyLogger myLogger;

	//http://localhost:8080/log-demo 에 대한 request를 이 메소드로 mapping
    @RequestMapping("log-demo")
    //이 메서드가 return하는 String을 response 메세지의 body에 그대로 넣어서 반환
    @ResponseBody
    //HTTPServletRequest에 request에 대한 정보가 들어있음. 자바 표준.
    public String logDemo(HttpServletRequest request) {
        String requestURL = request.getRequestURL().toString();
        myLogger.setRequestURL(requestURL);

        myLogger.log("controller test");
        logDemoService.logic("testid");

        return "OK";
    }
}
```

- HttpServletRequest를 통해서 요청 URL에 대한 정보를 받는다.
- requestURL 값 = http://localhost:8080/log-demo
- 이렇게 받은 requestURL을 myLogger에 저장해둔다. myLogger는 HTTP 요청 당 생성되므로, 다른 HTTP 요청 때문에 값이 변경되는 걱정은 하지 않아도 된다.
- 컨트롤러에서 controller test라는 로그를 남긴다.
- 컨트롤러에서 LogDemoService의 로직을 실행시킨다. 파라미터로 임의의 user id인 "testid"를 넘긴다.

##### LogDemoService.java
```Java
//package, import 생략

@Service
@RequiredArgsConstructor
public class LogDemoService {

    private final MyLogger myLogger;

    public void logic(String id) {
        myLogger.log("service id = " + id);
    }
}
```

- 비즈니스 로직이 있는 서비스 계층에서도 로그를 출력한다. 예제이므로 실제 로직은 없고 그냥 로그만 출력한다.
- 여기서 중요한점이 있다. request scope를 사용하지 않고 파라미터로 request에 관련된 모든 정보 (uuid, request url 등)를 서비스 계층에 넘긴다면, 파라미터가 많아서 지저분해진다. 더 큰 문제는 requestURL 같은 웹과 관련된 정보가 웹과 관련없는 서비스 계층까지 넘어가게 된다. **웹과 관련된 부분은 컨트롤러까지만 사용해야 한다.** 서비스 계층은 웹 기술에 종속되지 않고, 가급적 순수하게 유지하는 것이 유지보수 관점에서 좋다.
- request scope의 MyLogger 덕분에 uuid나 request URL을 파라미터로 넘기지 않고, MyLogger의 멤버변수에 저장해서 코드와 계층을 깔끔하게 유지할 수 있다.


이제 실행을 시켜보면, **오류가 발생한다!**

로그를 살펴보면,

```text
Caused by: org.springframework.beans.factory.support.ScopeNotActiveException: Error creating bean with name 'myLogger': Scope 'request' is not active for the current thread; consider defining a scoped proxy for this bean if you intend to refer to it from a singleton; 
```

이런 내용이 있다.

대충 해석하면 myLogger 빈이 request scope인데, 현재 activate 되어있지 않았다는 것이다.

스프링 애플리케이션을 실행하고 스프링 컨테이너가 생성되는 시점에서, 싱글톤 빈은 생성해서 주입이 가능하지만 **request 스코프 빈은 당연하게도 아직 생성되지 않는다. 이 빈은 실제 고객의 요청이 와야 생성할 수 있다!**

##### LogDemoController.java
```Java
@Controller
@RequiredArgsConstructor
public class LogDemoController {

    private final LogDemoService logDemoService;
    private final MyLogger myLogger;

	/* 생략 */
}
```

이 코드를 보면 스프링 컨테이너가 LogDemoController에 DI를 해야하는데, myLogger 빈이 request 빈이기 때문에 아직 생성되지 않아서 DI가 불가능하기 때문이다.

스프링 컨테이너가 올라가는 시점에서는 HTTP request가 올 수가 없다. 즉 에러 로그의 Scope 'request'가 not active 하다는 것이 이런 의미이다.

결국엔 이 myLogger 빈의 주입을 요청햐는 단계를, 스프링 컨테이너 생성 시점이 아닌 HTTP request가 오는 그 시점으로 미뤄야 한다.

이를 해결하기 위해서 **ObjectProvider**를 사용하면 된다! 다음 장에 계속...