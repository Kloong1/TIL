# Spring Boot - Filter & Intercepter

## 서론 - 웹과 관련된 공통 관심사 해결하기
로그인을 한 사용자만 상품 관련된 페이지에 들어갈 수 있어야 한다고 하자.

만약 상품과 관련된 모든 컨트롤러(등록, 수정, 조회 등) 로직에서 공통으로 로그인 여부를 확인해야 한다면? 더 큰 문제는 향후 로그인과 관련된 로직이 변경될 때 생긴다. 작성한 모든 로직을 다 수정해야 한다.

이렇게 애플리케이션 여러 로직에서 공통으로 관심이 있는 있는 것을 **공통 관심사(cross-cutting concern)** 라고 한다. 여기서는 상품 등록, 수정, 삭제, 조회 등등 여러 로직에서 공통으로 인증에 대해서 관심을 가지고 있다.

이러한 공통 관심사는 스프링의 AOP로도 해결할 수 있지만, 웹과 관련된 공통 관심사는 **서블릿 필터** 또는 **스프링 인터셉터** 를 사용하는 것이 좋다.

웹과 관련된 공통 관심사를 처리할 때는 HTTP의 헤더나 URL의 정보들이 필요한데, 서블릿 필터나 스프링 인터셉터는 `HttpServletRequest` 를 매개변수로 제공하기 때문이다.

서블릿 필터와 스프링 인터셉터는 이름 그대로 각각 서블릿과 스프링이 제공하는 기술이다.

스프링 환경에서는 기능이 더 많고 편리한 스프링 인터셉터를 사용하는 것이 좋다. 하지만 서블릿 필터를 사용해야 하는 경우도 있다고 하니 둘 다 알아보자. 스프링에서도 서블릿 필터를 쉽게 사용할 수 있도록 편의 기능을 제공한다.

## Servlet Filter

### Filter의 동작 흐름
```
HTTP 요청 -> WAS-> 필터 -> 서블릿(디스패쳐 서블릿) -> 컨트롤러
```

필터는 서블릿이 지원하는 기능이기 때문에, 스프링으로 진입하기 전(디스패쳐 서블릿이 호출되기 전)에 WAS 에서 동작하게 된다.

필터는 특정 URL 패턴에 적용할 수 있기 때문에, 특정 HTTP 요청에 대해서 원하는 필터를 적용할 수 있다.

만약 `/*` 패턴에 필터를 적용한다면 모든 요청에 대해 적용되는 것이다.

##### 필터 체인
```
HTTP 요청 -> WAS -> 필터1 -> 필터2 -> ... -> 필터N -> 서블릿 -> 컨트롤러
```
필터는 연속된 체인 형태로 구성되는데, 필터의 순서를 선택해서 등록할 수 있다.

만약 여러 필터를 거치는 도중 적절하지 않은 요청이라면 다음과 같이 강제로 `/` 경로로 리다이렉트를 시키는 등의 작업을 할 수 있다.

필터는 서블릿 기술이고, `HttpServletRequest`  객체와 `HttpServletResponse` 객체를 매개변수로 넘겨받기 때문에 이런 동작을 수행할 수 있다.

```
HTTP 요청 -> WAS -> 필터1 -> 필터2 -> (적절하지 않은 요청이라 판단, 디스패처 서블릿 호출 안하고 / 로 리다이렉트)
```


### Filter 인터페이스
```Java
package javax.servlet;  
  
import java.io.IOException;  
  
public interface Filter {  
    default void init(FilterConfig filterConfig)
    throws ServletException {}  
  
    void doFilter(ServletRequest var1, ServletResponse var2,
	    FilterChain var3) throws IOException, ServletException;  
  
    default void destroy() {}  
}
```

- `init()`: 필터 초기화 메서드. 서블릿 컨테이너가 생성될 때 호출된다.
- `doFilter()`: 고객의 요청이 올 때 마다 해당 메서드가 호출된다. 필터의 실제 로직을 구현하면 된다.
- `destroy()`: 필터 종료 메서드. 서블릿 컨테이너가 종료될 때 호출된다.
- `init()` 과 `destroy()` 는 `default` 메소드이기 때문에 반드시 구현할 필요는 없다.

##### Filter 구현 예시
```Java
@Slf4j
public class LogFilter implements Filter {
	@Override
	public void doFilter(ServletRequest request,
						ServletResponse response, FilterChain chain)
						throws IOException, ServletException {
						
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String requestURI = httpRequest.getRequestURI();
		String uuid = UUID.randomUUID().toString();
		
		try {
			log.info("REQUEST [{}][{}]", uuid, requestURI);
			chain.doFilter(request, response);
		} catch (Exception e) {
			throw e;
		} finally {
			log.info("RESPONSE [{}][{}]", uuid, requestURI);
		}
	}
}
```

`Filter` 는 HTTP 요청이 아닌 경우까지 고려해서 만든 인터페이스이기 때문에 `ServletRequest` 와 `ServletResponse` 를 매개변수로 받는다. 따라서 HTTP 요청의 경우 `HttpServletRequest` 와 `HttpServletResponse`  로 다운캐스팅을 해줘야 한다.

또 위에서 언급했듯이 필터는 여려개의 필터가 체인 형태로 동작한다. 따라서 필터의 로직이 모두 끝나면 반드시 `chain.doFilter(request, response)` 메소드를 호출해야 한다. 다른 필터가 없으면 서블릿(디스패쳐 서블릿)을 호출하기 때문에 필터가 하나여도 반드시 호출해야한다. 그렇지 않으면 로직이 필터에서 끝나버린다.

>참고
>필터에는 스프링 인터셉터는 제공하지 않는, 아주 강력한 기능이 있는데, `chain.doFilter(request, response)` 를 호출해서 다음 필터 또는 서블릿을 호출할 때 `request` ,`response` 를 `ServletRequest` , `ServletResponse` 를 구현한 다른 객체로 바꿔치기 할 수 있다.
>잘 사용하는 기능은 아니니 참고만 해두자.


### 필터 등록
필터 인터페이스를 구현하고 등록하면 서블릿 컨테이너가 필터를 싱글톤 객체로 관리한다.

스프링 부트 환경에서는 스프링이 필터를 편리하게 등록할 수 있게 해주는 `FilterRegistrationBean` 을 제공한다.

```Java
@Configuration
public class WebConfig {
	@Bean
	public FilterRegistrationBean logFilter() {
		FilterRegistrationBean<Filter> filterRegistrationBean =
										new FilterRegistrationBean<>();
		filterRegistrationBean.setFilter(new LogFilter());
		filterRegistrationBean.setOrder(1);
		filterRegistrationBean.addUrlPatterns("/members", "");
		return filterRegistrationBean;
	}
}
```

`FilterRegistrationBean` 이 제공하는 메소드로 필터의 순서(필터는 체인으로 동작하기 때문에 순서 설정이 필요)와 필터를 적용할 URL 패턴을 지정할 수 있다(가변길이 매개변수로 한 번에 여러 패턴 적용 가능)



## Spring Interceptor

### Interceptor 특징
```
HTTP 요청 -> WAS -> 필터(체인) -> 서블릿 -> 스프링 인터셉터(체인) -> 컨트롤러
```

스프링 인터셉터는 이름 그대로 서블릿이 아닌 스프링 MVC가 제공하는 기술이다. 서블릿 필터와 스프링 인터셉터 둘 다 웹과 관련된 공통 관심 사항을 처리하지만, 적용되는 순서와 범위, 그리고 사용방법이 다르다.

- 스프링 인터셉터는 디스패쳐 서블릿과 컨트롤러 사이에서 컨트롤러 호출 직전에 호출 된다.
- 스프링 인터셉터는 스프링 MVC가 제공하는 기능이기 때문에 결국 디스패쳐 서블릿 이후에 등장하게 된다
- 스프링 인터셉터는 서블릿 필터의 URL 패턴보다 더 정밀하게 URL 패턴 설정이 가능하다.
- 필터처럼 체인 형태로 동작한다.


### Interceptor 인터페이스
```Java
package org.springframework.web.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;

public interface HandlerInterceptor {

    default boolean preHandle(HttpServletRequest request,
					    HttpServletResponse response, Object handler)
					    throws Exception {
        return true;
    }

    default void postHandle(HttpServletRequest request,
						HttpServletResponse response, Object handler,
						@Nullable ModelAndView modelAndView)
						throws Exception {}

    default void afterCompletion(HttpServletRequest request,
					    HttpServletResponse response, Object handler,
						@Nullable Exception ex) throws Exception {}
}
```

필터는 필터 로직을 수행하는 `doFilter()` 메소드만 제공하는 데 반해, 인터셉터는 필터와 다르게 로직이 실행되는 단계가 세분화되어 있다. 
- `preHandle()` : 핸들러 어댑터 호출 전
- `postHandle()` : 핸들러 어댑터 호출 후 (핸들러 어댑터에 의해 `ModelAndView` 객체가 반환된 이후)
- `afterCompletion()` : 요청 완료 이후 (뷰가 렌더링 된 이후)

또 필터의 경우 매개변수로 `request` , `response` 객체만 제공했지만, 인터셉터는 어떤 컨트롤러가 호출되는지에 대한 정보도 제공한다. 그리고 컨트롤러가 어떤 `modelAndView` 를 반환하는지에 대한 정보도 제공한다.

#### 매개변수 `Object handler`
매개변수로 넘어오는 `handler` 객체의 타입은 어떤 핸들러 매핑을 사용하는가에 따라 달라진다. 따라서 `HandlerInterceptor` 인터페이스에서는 `Object` 형태로 업 캐스팅해서 매개변수로 넘겨주는 것이고, 실제 사용할 때는 `instanceof` 연산자로 객체의 타입을 확인해서 다운 캐스팅 한 뒤 객체에 접근하면 된다.

##### HandlerMethod
스프링을 사용하면 일반적으로 `@Controller` , `@RequestMapping` 을 활용한 핸들러 매핑을 사용하는데, 이 경우 핸들러 객체로 `HandlerMethod` 타입의 객체가 넘어온다

##### ResourceHttpRequestHandler
`@Controller` 가 아니라 정적 리소스가 호출 되는 경우 `ResourceHttpRequestHandler` 객체가 핸들러 객체로 넘어온다.

### Interceptor 호출 흐름

#### Interceptor 정상 흐름
![](스크린샷%202022-08-24%20오후%209.44.31.png)
 - `preHandle()` : 핸들러 어댑터 호출 전에 호출된다.
	 - `preHandle()` 의 반환값이 `true` 이면 다음 인터셉터 혹은 핸들러 어댑터를 호출하고, `false` 이면 더는 진행하지 않는다.
	 - `false` 인 경우 나머지 인터셉터는 물론이고, 핸들러 어댑터도 호출되지 않는다. 그림에서 1번에서 끝이 나버린다.
	 - 3 개의 메소드중 유일하게 반환값이 있다 ( `boolean` )
- `postHandle()` : 핸들러 어댑터 호출 후에 호출된다. 핸들러 어댑터가 `ModelAndView` 객체를 반환하므로 `ModelAndView` 객체를 매개변수로 넘겨받아 접근할 수 있다.
- `afterCompletion()` : 뷰가 렌더링 된 이후에 호출된다.

#### Interceptor 예외 발생 상황 흐름
![](스크린샷%202022-08-24%20오후%209.50.12.png)
 - `preHandle()` : 핸들러 어댑터 호출 전에 호출된다.
- `postHandle()` : 컨트롤러에서 **예외가 발생하면 호출되지 않는다.**
- `afterCompletion()` : **예외가 발생해도 항상 호출된다.** 예외가 발생한 경우 `Exception` 객체를 매개변수로 받아서 예외 정보를 확인하고 처리를 할 수 있다.

**예외가 발생하면 `postHandle()` 은 호출되지 않기 때문에, 예외와 무관하게 공통 처리를 하기 위해서는 `afterCompletion()` 메소드를 사용해야한다.**

#### Interceptor 체인 실행 흐름
3개의 인터셉터 `HandlerInterceptor1`, `HandlerInterceptor2`, `HandlerInterceptor3` 가 각 인터셉터 번호 순서대로 체인 형태로 등록된 상황이라고 하자 ( `HandlerInterceptor1` 이 제일 처음 호출되고, `HandlerInterceptor3` 이 마지막으로 호출됨).

각 인터셉터의 `preHandle()`, `postHandle()`, `afterCompletion()` 메소드의 실행 순서는 어떻게 될까?

3 개의 인터셉터의 각 메소드들은 단순히 자신의 인터셉터 번호와 메소드 이름만 로그로 출력하게끔 구현되어있다. 다음은 `HandlerInterceptor1` 의 코드이다.

```Java
//package, import 생략

@Slf4j
public class HandlerInterceptor1 implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
    HttpServletResponse response, Object handler) throws Exception {
        log.info("Interceptor 1 - preHandle()");
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request,
    HttpServletResponse response, Object handler, ModelAndView modelAndView) 
    throws Exception {
        log.info("Interceptor 1 - postHandle()");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, 
    HttpServletResponse response, Object handler, Exception ex)
    throws Exception {
        log.info("Interceptor 1 - afterCompletion()");
    }
}
```

나머지 두 인터셉터는 위 코드에서 인터셉터 번호만 바뀐다.

3 개의 인터셉터를 번호 순서대로 등록하고 (등록 방법은 아래에서 설명) 컨트롤러를 호출하면 다음과 같이 로그가 찍힌다.

```
Interceptor 1 - preHandle()
Interceptor 2 - preHandle()
Interceptor 3 - preHandle()
Interceptor 3 - postHandle()
Interceptor 2 - postHandle()
Interceptor 1 - postHandle()
Interceptor 3 - afterCompletion()
Interceptor 2 - afterCompletion()
Interceptor 1 - afterCompletion()
```

`preHandle()` 은 등록 순서, 나머지는 역순으로 실행되는 것을 확인할 수 있다.



## Interceptor 등록
`WebMvcConfigurer` 인터페이스가 제공하는 `addInterceptors(InterceptorRegistry registry)` 메소드를 사용해서 인터셉터를 등록할 수 있다.

```Java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor1())
                .order(1)
                .addPathPatterns("/**");

        registry.addInterceptor(new HandlerInterceptor2())
                .order(2)
                .addPathPatterns("/**");

        registry.addInterceptor(new HandlerInterceptor3())
                .order(3)
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**", "/*.ico", "/error");
    }
}
```

- 필터와는 달리 인터셉터는 `addPathPatterns()` 와 `excludePathPattern()` 메소드로 매우 정밀하게 URL 패턴을 지정할 수 있다. 자세한 건 스프링이 제공하는 URL 패턴 문법에 대한 문서를 찾아보자.
