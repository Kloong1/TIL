# Spring Boot - 예외 처리와 오류 페이지

## Servlet의 예외 처리

서블릿은 다음 2가지 방식으로 예외 처리를 지원한다.
1. `Exception` 처리
2. `response.sendError()`

### Exception 처리
웹 애플리케이션은 사용자 요청마다 별도의 쓰레드가 할당되고, 서블릿 컨테이너 안에서 실행된다.

그런데 애플리케이션 실행 중 예외가 발생했는데, 예외를 잡지 못하고 서블릿 밖으로 까지 예외가 전달되면 어떻게 동작할까?

```
WAS(여기까지 예외 전파) <- 필터 <- 서블릿 <- 인터셉터 <- 컨트롤러(예외 발생)
```

스프링 부트에서는 WAS로 Tomcat을 사용하는데, 예외가 WAS 까지 전달될 경우 Tomcat에서 기본으로 제공하는 오류 화면을 볼 수 있다.

WAS까지 예외가 전달된 경우에는 서버 내부에서 처리할 수 없는 오류가 발생한 것으로 판단해서 HTTP 상태 코드 500을 반환하고 다음과 같은 오류 화면을 보여준다.

```
HTTP Status 500 – Internal Server Error
```

### `response.sendError()`

오류가 발생했을 때 `HttpServletResponse` 가 제공하는 `sendError()` 라는 메서드를 사용해도 된다. 이 메소드를 호출한다고 해서 당장 예외가 발생하는 것은 아니고, 서블릿 컨테이너에게 오류가 발생했다는 사실을 전달할 수 있다

다음과 같은 방식으로 사용할 수 있다.
- `response.sendError(HTTP 상태 코드)`
- `response.sendError(HTTP 상태 코드, 오류 메시지)`

다음과 같은 흐름으로 오류 정보가 전달된다.

```
//컨트롤러, 필터, 인터셉터 등에서 response.sendError()를 호출
WAS(sendError 호출 기록 확인) <- 필터 <- 서블릿 <- 인터셉터 <- 컨트롤러
``` 

서블릿 컨테이너는 `response` 객체로 고객에게 응답을 하기 전에 `sendError()` 호출 여부를 확인한다. 만약 호출되었다면 설정한 오류 코드에 맞는 기본 오류 페이지를 보여준다.

### Tomcat 기본 오류 화면 대신 직접 만든 오류 화면 제공하기
서블릿은 `Exception` 이 발생해서 서블릿 밖으로 전달된 상황, 또는 `response.sendError()` 가 호출된 상황 등 각각의 상황에 맞는 오류 화면을 제공할 수 있는 기능을 제공한다.

스프링 부트를 통해서 서블릿 컨테이너를 실행하는 경우, 스프링 부트가 제공하는 기능을
사용해서 상황에 맞는 오류 페이지를 등록하면 된다.

```Java
//package, import 생략

@Component
public class WebServerCustomizer implements
WebServerFactoryCustomizer<ConfigurableWebServerFactory> {

	@Override
	public void customize(ConfigurableWebServerFactory factory) {
		//response.sendError()로 상태 코드를 등록한 경우
		ErrorPage errorPage404 = new ErrorPage(
								HttpStatus.NOT_FOUND, "/error-page/404");
		ErrorPage errorPage500 = new ErrorPage(
					HttpStatus.INTERNAL_SERVER_ERROR, "/error-page/500");

		//WAS까지 Exception이 전달되는 경우 (정확히는 RuntimeException)
		ErrorPage errorPageEx = new ErrorPage(
								RuntimeException.class, "/error-page/500");
								
		factory.addErrorPages(errorPage404, errorPage500, errorPageEx);
	}
}
```

- `response.sendError(404) 호출된 경우` 
	- WAS가 `/error-page/404` 로 서버에 다시 요청
- `response.sendError(500) 호출된 경우` 
	- WAS가 `/error-page/500` 로 서버에 다시 요청
- `RuntimeException` 혹은 그 자식 예외가 발생해서 WAS 까지 전달된 경우 
	- WAS가 `/error-page/500` 로 서버에 다시 요청

WAS가 지정된 URI 로 다시 요청을 보내기 때문에, 해당 요청을 처리할 컨트롤러가 필요하다. 해당 컨트롤러가 요청을 받아서 상황에 맞는 오류 화면을 렌더링하게 구현하면 된다.

```Java
@Controller
public class ErrorPageController {

	@RequestMapping("/error-page/404")
	public String errorPage404(HttpServletRequest request,
										HttpServletResponse response) {
		return "error-page/404";
	}
	
	@RequestMapping("/error-page/500")
	public String errorPage500(HttpServletRequest request,
										HttpServletResponse response) {
		return "error-page/500";
	}
}
```

### 오류 페이지 작동 원리

```
1. 컨트롤러, 필터, 인터셉터 등에서 예외 발생 혹은 response.sendError()를 호출
WAS(예외 전달됨, 혹은 sendError() 호출 확인) <- 필터 <- 서블릿 <- 인터셉터 <- 컨트롤러

2. WAS가 예외 혹은 sendError() 호출 여부를 확인하고 오류 페이지 요청
WAS `/error-page/500` 다시 요청 -> 필터 -> 서블릿 -> 인터셉터 -> 컨트롤러(/error-page/
500) -> View
```

WAS는 예외나 HTTP 상태 코드에 대한 에러 페이지 (정확히는 에러 페이지에 대한 요청 URI) 가 등록되어 있지 않은 경우 기본 오류 화면을 보여주고, 등록되어 있다면 등록된 URI로 다시 요청을 보낸다.

해당 요청에 맞는 컨트롤러가 요청을 받아서 최종적으로 오류 화면을 렌더링하면 사용자는 오류 화면을 볼 수 있게 된다.

**중요한 점은 웹 브라우저(클라이언트)는 서버 내부에서 이런 일이 일어나는지 전혀 모른다는 점이다. 오직 서버 내부에서 오류 페이지를 찾기 위해 추가적인 호출을 한다.**

### WAS 오류 페이지 요청 시 오류 정보 추가
WAS는 오류 페이지를 단순히 다시 요청만 하는 것이 아니라,  발생한 오류에 대한 정보를 `request` 객체에 추가해서 넘겨준다.

미리 정의해 둔 문자열을 key로 사용해서 저장한다. 다음과 같은 key 들이 있다.

- javax.servlet.error.exception : 예외
- javax.servlet.error.exception_type : 예외 타입
- javax.servlet.error.message : 오류 메시지
- javax.servlet.error.request_uri : 클라이언트 요청 URI
- javax.servlet.error.servlet_name : 오류가 발생한 서블릿 이름
- javax.servlet.error.status_code : HTTP 상태 코드

따라서 다음과 같은 방식으로 해당 정보에 접근할 수 있다.
```Java
request.getAttribute(ERROR_EXCEPTION_TYPE)
```

### WAS의 오류 페이지 요청 과정에서 필터 호출 여부
```
1. WAS(여기까지 전파) <- 필터 <- 서블릿 <- 인터셉터 <- 컨트롤러(예외발생)
2. WAS `/error-page/500` 다시 요청 -> 필터 -> 서블릿 -> 인터셉터 -> 컨트롤러(/errorpage/
500) -> View
```

모든 요청은 필터를 거쳐갈 수밖에 없다. 즉 WAS의 오류 페이지 요청도 필터를 거쳐가게 된다.

그런데 일반적인 필터 로직의 경우 (로그인 인증 체크 등) 오류 페이지 호출을 하는 경우에 해당 필터 로직이 동작하는 것은 너무 비효율적이다.

따라서 클라이언트로 부터 발생한 정상 요청인지, 아니면 오류 페이지를 출력하기 위한 내부 요청인지 구분해서 클라이언트의 요청에만 필터가 동작하게 만들어야 한다.

서블릿은 `DispatcherType` 이라는 추가 정보를 제공해서 클라이언트의 정상 요청과 서버 내부의 요청을 구분한다.

##### DispatcherType
요청에 대한 정보가 들어있는 `request` 객체에 `DispatcherType` 정보가 들어있다.

```Java
request.getDispatcherType()
```

반환값에 대한 의미는 다음과 같다.
- `dispatcherType=REQUEST` : 클라이언트의 정상 요청
- `dispatcherType=ERROR` : WAS의 오류 페이지에 대한 요청

다른 값들도 존재한다.

```Java
public enum DispatcherType {
	FORWARD,
	INCLUDE,
	REQUEST,
	ASYNC,
	ERROR
}
```

- `REQUEST` : 클라이언트 요청
- `ERROR` : 오류 요청
- `FORWARD` : MVC에서 배웠던 서블릿에서 다른 서블릿이나 JSP를 호출할 때. `RequestDispatcher.forward(request, response);`
- `INCLUDE` : 서블릿에서 다른 서블릿이나 JSP의 결과를 포함할 때. `RequestDispatcher.include(request, response);`
- `ASYNC` : 서블릿 비동기 호출

##### 필터 등록 시 `DispatcherType` 설정
필터를 등록할 때 해당 필터가 어떤 `DistpatcherType` 의 요청에만 동작을 할지 지정할 수 있다.

```Java
//package, import 생략

@Configuration
public class WebConfig implements WebMvcConfigurer {
	@Bean
	public FilterRegistrationBean logFilter() {
		FilterRegistrationBean<Filter> filterRegistrationBean =
											new FilterRegistrationBean<>();
											
		filterRegistrationBean.setFilter(new LogFilter());
		filterRegistrationBean.setOrder(1);
		filterRegistrationBean.addUrlPatterns("/*");

		//DispatcherType 설정 - REQUEST와 ERROR 에 대해 동작
		filterRegistrationBean.setDispatcherTypes(
						DispatcherType.REQUEST, DispatcherType.ERROR);
		
		return filterRegistrationBean;
	}
}
```

필터를 이렇게 등록하면 일반적인 클라이언트 요청에도 동작하지만 오류 페이지 요청에도 필터가 동작한다.

`DistpatcherType` 에 대한 설정을 따로 하지 않으면 기본값은 `DispatcherType.REQUEST` 이다.

### WAS의 오류 페이지 요청 과정에서 인터셉터 호출 여부
```
1. WAS(여기까지 전파) <- 필터 <- 서블릿 <- 인터셉터 <- 컨트롤러(예외발생)
2. WAS `/error-page/500` 다시 요청 -> 필터 -> 서블릿 -> 인터셉터 -> 컨트롤러(/errorpage/
500) -> View
```

필터와 마찬가지로 오류 페이지 요청 역시 인터셉터를 거쳐갈 수 밖에 없다.

따라서 오류 페이지 요청에 대해서 인터셉터가 동작하지 않게 설정할 필요가 있다.

`DispatcherType` 은 서블릿 기술인데 인터셉터는 스프링 기술이다. 따라서 인터셉터는 `DispatcherType` 에 무관하게 항상 호출된다.

대신 인터셉터는 요청 URI를 세밀하게 조정하기 쉽기 때문에, 인터셉터를 등록할 때 `excludePathPatterns()` 설정을 사용해서 오류 페이지 요청 경로를 제외시키면 된다.

```Java
//package, import 생략
@Configuration
public class WebConfig implements WebMvcConfigurer {
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new LogInterceptor())
				.order(1)
				.addPathPatterns("/**")
				.excludePathPatterns(
					"/css/**", "/*.ico"
					, "/error", "/error-page/**" //오류 페이지 경로
				);
	}
```


## 스프링 부트의 예외 처리와 오류 페이지

### 스프링 부트가 기본으로 제공하는 예외 처리
서블릿 기술만을 가지고 오류 페이지를 보여주는 것은 다음과 같은 복잡한 과정이 필요하다.
- `WebServerCustomizer` 를 만들기
- 예외와 오류 종류에 따라서 `ErrorPage` 를 추가하기
- 오류 페이지 요청 처리용 컨트롤러 만들기

그런데 **스프링 부트는 이런 과정을 모두 기본으로 제공한다.**
- 예외와 HTTP 상태 코드에 대한 `ErrorPage` 를 자동으로 등록해준다.
	- `/error` 경로로 기본 오류 페이지를 설정한다.
		- `new ErrorPage("/error")` : 이런 식으로 상태 코드 혹은 예외를 설정하지 않으면 기본 오류 페이지로 등록된다.
	- 서블릿 밖으로 예외가 전달되거나, `response.sendError()` 가 호출되면 해당 에러나 오류에 대해 `ErrorPage` 가 등록되어 있지 않는 경우 `/error` 경로로 요청을 하게 된다.
- 또 스프링은 `BasicErrorController` 라는 오류 페이지 요청 처리를 담당하는 스프링 컨트롤러를 자동으로 등록한다.
	- `/error` 요청을 매핑해서 처리하는 컨트롤러다.

스프링이 `/error` 를 경로로 하는 기본 `ErrorPage` 등록과 `/error` 에 대한 요청을 처리하는 컨트롤러인 `BasicErrorController` 를 등록해주기 때문에, 개발자는 오류 페이지 뷰만 등록하면 된다.

### 직접 만든 오류 페이지 등록하기
개발자는 원하는 오류 페이지를 보여주기 위해서, 스프링 부트가 기본으로 등록하는 `BasicErrorController` 의 로직에 맞춰서 오류 페이지를 등록하기만 하면 된다. 이미 로직이 전부 구현되어 있기 때문에 아주 편리하다.

뷰 템플릿을 사용해서 동적으로 오류 페이지를 보여줄 수도 있고, 정적 HTML 화면을 보여줄 수도 있다. 필요에 맞는 경로에 오류 페이지 파일을 만들어서 넣어주기만 하면 된다.

##### `BasicErrorController` 가 오류 페이지를 찾는 순서
HTTP 상태 코드 500 오류가 발생한 경우를 예시로 들어서 `BasicErrorController` 가 어떤 순서로 오류 페이지를 찾는지 알아보자.

1. 뷰 템플릿 - `resources/templates/error`
	1. `resources/templates/error/500.html`
	2. `resources/templates/error/5xx.html`
2. 정적 리소스 - `resources/static/error`
	1. `resources/static/error/500.html`
	2. `resources/static/error/5xx.html`
3. 해당하는 오류 페이지가 없을 때 - `error` 라는 이름의 파일을 찾음
	1. `resources/templates/error.html`

`BasicErrorController` 가 위의 순서대로 경로를 탐색해서 HTTP 상태 코드에 해당하는 에러 페이지가 있는지 확인한다 (그렇게 로직이 구현되어 있다). 따라서 개발자는 해당 경로에 HTTP 상태 코드를 이름으로 하는 뷰 파일을 넣어두기만 하면 된다.

뷰 템플릿이 정적 리소스보다 우선순위가 높고, 404, 500처럼 구체적인 상태 코드 이름이 5xx처럼 덜 구체적인 것 보다 우선순위가 높다.

### `BasicErrorController` 코드 훑어보기
```Java
@Controller
@RequestMapping("${server.error.path:${error.path:/error}}")
public class BasicErrorController extends AbstractErrorController {

	@RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
	public ModelAndView errorHtml(HttpServletRequest request, 
									HttpServletResponse response) {
		HttpStatus status = getStatus(request);
		//생략
		response.setStatus(status.value());
		ModelAndView modelAndView =
					resolveErrorView(request, response, status, model);
		return (modelAndView != null) ?
				modelAndView : new ModelAndView("error", model);
	}
}
```
- `@RequestMapping("${server.error.path:${error.path:/error}}")`
	- 따로 설정하지 않으면 스프링 부트가 `/error` 경로에 대해서 `@RequestMapping` 을 한다.
- `HttpStatus status = getStatus(request);`
	- `request` 객체에서 HTTP 상태 코드를 얻는다.
- `ModelAndView modelAndView = resolveErrorView(request, response, status, model)`
	- HTTP 상태 코드에 해당하는 오류 페이지 뷰에 대한 `ModelAndView` 를 반환하는 메소드를 호출한다.

`resolveErrorView()` 메소드의 내부 로직을 따라가다 보면 `DefaultErrorViewResolver` 의 `resolveErrorView()` 메소드가 실행되는 것을 확인할 수 있다.

```Java
public class DefaultErrorViewResolver implements ErrorViewResolver, Ordered {

	@Override
	public ModelAndView resolveErrorView(HttpServletRequest request,
							HttpStatus status, Map<String, Object> model) {
		ModelAndView modelAndView =
							resolve(String.valueOf(status.value()), model);
		//생략
		return modelAndView;
	}

	private ModelAndView resolve(String viewName, Map<String, Object> model) {
		String errorViewName = "error/" + viewName;
		TemplateAvailabilityProvider provider = 
				this.templateAvailabilityProviders.getProvider(
									errorViewName, this.applicationContext);
		if (provider != null) {
			return new ModelAndView(errorViewName, model);
		}
		return resolveResource(errorViewName, model);
	}

	private ModelAndView resolveResource(String viewName,
											Map<String, Object> model) {
		for (String location : this.resources.getStaticLocations()) {
			try {
				Resource resource = 
							this.applicationContext.getResource(location);
				resource = resource.createRelative(viewName + ".html");
				if (resource.exists()) {
					return new ModelAndView(
									new HtmlResourceView(resource), model);
				}
			}
			catch (Exception ex) {
			}
		}
		return null;
	}
}
```
- `resolveErrorView()`
	- `resolve(String.valueOf(status.value()), model)` 를 호출한다.
	- `status` 는 매개변수로 넘어온 HTTP 상태 코드 객체이다.
- `resolve()`
	- `String errorViewName = "error/" + viewName`
		- `viewName` 은 HTTP 상태 코드를 `String` 형태로 바꾼 것이다.
		- 따라서 `errorViewName` 은 `error/500` 같은 문자열이 된다.
		- 이 부분에서 HTTP 상태 코드로 에러 페이지 파일의 이름을 만드는 것이다.
	- `TemplateAvailabilityProvider ...`
		- 이 부분의 코드로 뷰 템플릿 경로에서 에러 페이지 파일을 찾는다.
		- 정확한 동작 과정은 모르지만 아무튼 바로 직전에 만들어진 에러 페이지 파일 이름으로 뷰 템플릿 경로를 먼저 찾아보는 듯 하다.
	- `return resolveResource(errorViewName, model)`
		- 뷰 템플릿 경로에서 에러 페이지를 못 찾으면 이 메소드가 호출된다.
- `resolveResource()`
	- 메소드 내용으로 봤을 때 정적 리소스 경로에서 에러 페이지 파일 이름에 해당하는 뷰 파일을 찾는 것으로 보인다.
	- 즉 전체적으로 보면 뷰 템플릿 경로에서 먼저 찾고, 없으면 정적 리소스 경로에서 찾는 로직이 된다.

### 스프링 부트 오류 페이지 관련 설정
- `server.error.whitelabel.enabled=true` : 오류 페이지를 못 찾을 시 스프링 whitelabel 오류 페이지를 적용한다. `true` 가 기본값이다.
- `server.error.path=/error` : 오류 페이지 요청 경로를 설정한다. 스프링이 자동 등록하는 서블릿 글로벌 오류 페이지 경로와 `BasicErrorController` 오류 컨트롤러 경로에 함께 사용된다.