# Request scope와 Provider
앞의 request scope 빈을 사용하는 예제에서, 스프링 컨테이너 생성 시점에서 request scope 빈을 DI 받으려는 순간 request scope 빈이 active 하지 않기 때문에 (request scope 빈은 HTTP reqeust가 들어오는 시점에서 생성되기 때문에) DI에 오류가 생겨서 스프링 컨테이너 생성에 실패하는 것을 확인했다.

이 문제를 해결하기 위해 첫 번째로 Provider를 사용할 수 있다.

스프링 컨테이너가 생기는 시점에 myLogger 빈을 주입 받는 것이 아니라, myLogger를 DL 할 수 있는 `ObjectProvider<MyLogger>` 을 주입 받는다. 이후에 HTTP request로 인해 request 빈이 생성되었을 때, provider를 통해서 해당 request 빈을 찾는 것이다.

##### LogDemoController.java
```Java
//package, import 생략

@Controller
@RequiredArgsConstructor
public class LogDemoController {

    private final LogDemoService logDemoService;
    private final ObjectProvider<MyLogger> myLoggerProvider;

    @RequestMapping("log-demo")
    @ResponseBody
    public String logDemo(HttpServletRequest request) {
        MyLogger myLogger = myLoggerProvider.getObject();
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

    private final ObjectProvider<MyLogger> myLoggerProvider;

    public void logic(String id) {
        MyLogger myLogger = myLoggerProvider.getObject();
        myLogger.log("service id = " + id);
    }
}
```

기존에 스프링 컨테이너 생성 시점에 myLogger 빈을 주입 받으려고 했던 두 클래스가 `ObjectProvider<MyLogger>` 를 사용하도록 만든다.

이제 어플리케이션을 실행시키면 오류 없이 스프링이 동작함을 확인할 수 있다.

`localhost:8080/log-demo` 로 HTTP request를 보내면 LogDemoController에 의해 웹 브라우저 화면에 "OK"가 나타남을 확인할 수 있다.

이제 로그 출력을 살펴보면,

```text
[c0ae5ee5-3b7f-4e13-974d-3130af827c6e] request scope bean created - com.kloong.corebasic1.common.MyLogger@2a6a8e69
[c0ae5ee5-3b7f-4e13-974d-3130af827c6e] [http://localhost:8080/log-demo] controller test
[c0ae5ee5-3b7f-4e13-974d-3130af827c6e] [http://localhost:8080/log-demo] service id = testid
[c0ae5ee5-3b7f-4e13-974d-3130af827c6e] request scope bean closed - com.kloong.corebasic1.common.MyLogger@2a6a8e69
```

myLogger 빈이 생성되고 소멸됨을 확인할 수 있고, UUID로 request를 식별 가능함을 확인할 수 있다. HTTP request를 여러번 보내면 각 request마다 UUID가 달라짐을 볼 수 있다.


## 정리
- ObjectProvider 덕분에 `ObjectProvider.getObject()` 를 호출하는 시점까지 request scope 빈의 생성을 지연할 수 있다.
- `ObjectProvider.getObject()` 를 호출하는 시점에는 HTTP request가 진행중이므로 request scope 빈의 생성이 정상 처리된다.
- request bean이 생성될 때 UUID를 생성해서 초기화하므로, 로그 상에서 각 request를 구분할 수 있게 된다.
- `ObjectProvider.getObject()` 를 LogDemoController, LogDemoService 에서 따로 호출해도, **같은 HTTP request면 동일한 스프링 빈이 반환된다!** 이런 구분하는 기능을 직접 구현하려면 엄청나게 복잡하다! 스프링 짱짱맨!!

이렇게 ObjectProvider를 사용하면 될 것 같은데, Provider 없이도 동작하게 만들 수 없을까...? -> 다음 시간에 계속...