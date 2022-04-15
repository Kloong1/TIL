# Request scope와 Proxy
이전의 request scope 빈의 생성 시점으로 인한 문제를 Provider가 아닌 Proxy를 사용해서 해결해보자!

```Java
@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class MyLogger { /* 생략 */ }
```

MyLogger의 scope를 지정하는 `@Scope` annotation에서, proxyMode라는 옵션이 존재한다.

- `@Scope` anootation에 `proxyMode = ScopedProxyMode.TARGET_CLASS` 를 추가해주자.
	- 적용 대상이 클래스면 `TARGET_CLASS` 를 선택
	- 적용 대상이 인터페이스면 `INTERFACES` 를 선택

이렇게만 설정하고, LogDemoController와 LogDemoService의 `ObjectProvider<MyLogger>` 를 없앤다. 그리고 기존에 request 빈의 생성 시점 때문에 오류가 발생하던 코드로 다시 코드를 돌려놓는다. 즉 myLogger 빈을 스프링 컨테이너 생성 시점에 DI 받는 코드로 돌려놓는다.

##### LogDemoController.java
```Java
//package, import 생략

@Controller
@RequiredArgsConstructor
public class LogDemoController {

    private final LogDemoService logDemoService;
    private final MyLogger myLogger; //스프링 컨테이너 생성 시점에 DI 받는다.

    @RequestMapping("log-demo")
    @ResponseBody
    public String logDemo(HttpServletRequest request) {
        String requestURL = request.getRequestURL().toString();
        myLogger.setRequestURL(requestURL);

        myLogger.log("controller test");
        logDemoService.logic("testid");

        return "OK";
    }
}
```

##### LogDemoService.java
```Java
//package, import 생략

@Service
@RequiredArgsConstructor
public class LogDemoService {

    private final MyLogger myLogger; //스프링 컨테이너 생성 시점에 DI 받는다.


    public void logic(String id) {
        myLogger.log("service id = " + id);
    }
}
```

이제 어플리케이션을 실행시키면 정상적으로 서버에 올라간다.

마찬가지로 `localhost:8080/log-demo` 로 request를 보내면 로그가 정상적으로 출력되는 것을 확인할 수 있다.

```text
[6882e5c2-7e71-47c5-b43e-0588135e509f] request scope bean created - com.kloong.corebasic1.common.MyLogger@a7ad77c
[6882e5c2-7e71-47c5-b43e-0588135e509f] [http://localhost:8080/log-demo] controller test
[6882e5c2-7e71-47c5-b43e-0588135e509f] [http://localhost:8080/log-demo] service id = testid
[6882e5c2-7e71-47c5-b43e-0588135e509f] request scope bean closed - com.kloong.corebasic1.common.MyLogger@a7ad77c
```

어떻게 오류가 발생하지 않은 것일까? 생기지도 않은 request 빈을 어떻게 주입 받은 것일까?

MyLogger의 `@Scope` annotation에 설정해줬던  `proxyMode = ScopedProxyMode.TARGET_CLASS` 에 비밀이 있다.

이렇게 설정을 하면 MyLogger의 가짜 프록시 클래스를 만들어두고, HTTP request와 상관 없이 가짜 프록시 클래스를 해당 클래스 타입의 DI를 요청하는 다른 빈에 미리 주입해 둘 수 있다.

과연 사실인지 주입된 myLogger 빈을 출력해보자.

```Java
 @RequestMapping("log-demo")
    @ResponseBody
    public String logDemo(HttpServletRequest request) {
        System.out.println("myLogger.getClass() = " + myLogger.getClass());
		//생략...
	}
```

서버를 다시 올리고 HTTP request를 보내면,

```text
myLogger.getClass() = class com.kloong.corebasic1.common.MyLogger$$EnhancerBySpringCGLIB$$7bbc903
```

myLogger의 클래스가 MyLogger가 아닌 CGLIB에 의해 조작된 클래스임을 알 수 있다!

##### 즉 CGLIB 라이브러리가 내 클래스를 상속 받은 가짜 프록시 객체를 만들어서 주입한다는 것!
- `@Scope` 의 `proxyMode = ScopedProxyMode.TARGET_CLASS)` 를 설정하면 스프링 컨테이너는 CGLIB라는 바이트코드를 조작하는 라이브러리를 사용해서, **MyLogger를 상속받은 가짜 프록시 객체를 생성한다.**
- 실제로 myLogger 빈의 클래스를 확인해보면, 내가 작성한 순수한 MyLogger 클래스가 아니라 `MyLogger$$EnhancerBySpringCGLIB` 이라는 클래스라는 것을 확인할 수 있다.
- 따라서 스프링 컨테이너에 등록되는 "myLogger" 빈은 MyLogger 객체가 아니라, 이 가짜 프록시 객체이다.
- `ac.getBean("myLogger", MyLogger.class)` 로 조회해도 **프록시 객체가 조회되는 것을 확인할 수 있다.**
- 그래서 결국엔 DI 되는 빈도 이 가짜 프록시 객체가 된다.

![](Pasted%20image%2020220415222739.png)

##### 가짜 프록시 객체는 요청이 오면 그 때 내부에서 진짜 빈을 요청하는 위임 로직이 들어있다.
- 클라이언트가 `myLogger.logic()` 을 호출하면 사실은 가짜 프록시 객체의 메서드를 호출한 것이다.
- 가짜 프록시 객체는 내부에 진짜 myLogger를 찾는 방법을 알고 있다. 즉 ObjectProvider의 DL 기능을 수행할 수 있다.
- 가짜 프록시 객체는 request scope의 진짜 myLogger를 찾아서, `myLogger.logic()` 를 호출한다.
- 앞 단에서 어떤 조작을 한 뒤 위임하는 역할을 하기 때문에 Proxy라고 부르는 것이다.
- 가짜 프록시 객체는 원본 클래스를 상속 받아서 만들어졌기 때문에 이 객체를 사용하는 클라이언트 입장에서는 사실 원본인지 아닌지도 모르게, 동일하게 사용할 수 있다 (다형성)

#### 동작 방식 정리
- CGLIB라는 라이브러리로 내 클래스를 상속 받은 가짜 프록시 객체를 만들어서 주입한다.
- 이 가짜 프록시 객체는 실제 요청이 오면 그때 내부에서 실제 빈을 요청하는 위임 로직이 들어있다.
- **가짜 프록시 객체는 request scope와는 전혀 관계가 없다.** 그냥 가짜이고, 내부에 단순한 위임 로직만 있다. 마치 싱글톤 처럼 동작한다.

### 특징 정리
- 프록시 객체 덕분에 클라이언트는 마치 싱글톤 빈을 사용하듯이 편리하게 request scope를 사용할 수 있다 (Controller나 Service 코드를 보면 싱글톤 빈을 사용하는 코드와 달라진 것이 없다).
- 사실 Provider를 사용하든, 프록시를 사용하든 상관 없이 **핵심 아이디어는 진짜 객체 조회를 꼭 필요한 시점까지 지연처리 한다는 점이다.**
- 아주 간단한 annotation 설정 변경만으로 원본 객체를 프록시 객체로 대체할 수 있다. 이것이 바로 다형성과 DI 컨테이너가 가진 큰 강점이다.
- 꼭 웹 스코프가 아니어도 프록시를 사용할 수 있다.

### Request scope와 Proxy 사용 주의점
- 프록시를 사용하기 때문에 코드만 보면 마치 싱글톤을 사용하는 것 같지만, 실제로는 HTTP request마다 새로운 빈을 생성하기 때문에 이 사실을 잘 알고 주의해서 사용해야 한다.
- 이런 특별한 scope는 꼭 필요한 곳에만 최소화해서 사용하자, 무분별하게 사용하면 유지보수하기 어려워진다.