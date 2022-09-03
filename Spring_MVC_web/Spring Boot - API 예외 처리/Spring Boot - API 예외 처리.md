# Spring Boot - API 예외 처리
서버가 API 요청을 받아서 응답을 줘야 하는 경우도 있다. 만약 이 때 예외 혹은 오류가 발생한다면 오류 페이지를 보여줘서는 안된다. 이 경우 미리 정해둔 API 스펙에 맞춰서 JSON 형태로 예외나 오류에 대한 정보를 담아 응답을 줘야 한다.

## 스프링 부트 API 예외 처리 - `BasicErrorController`
스프링 부트가 기본으로 등록해주는 `BasicErrorController` 의 코드를 살펴보면 API 예외 처리 기능도 제공하는 것을 확인할 수 있다.

```Java
@RequestMapping(produces = MediaType.TEXT_HTML_VALUE)  
public ModelAndView errorHtml(HttpServletRequest request,
										  HttpServletResponse response) {
	//생략
}

@RequestMapping  
public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {  
   HttpStatus status = getStatus(request);  
   if (status == HttpStatus.NO_CONTENT) {  
      return new ResponseEntity<>(status);  
   }  
   Map<String, Object> body = getErrorAttributes(
				   request, getErrorAttributeOptions(request, MediaType.ALL));  
   return new ResponseEntity<>(body, status);  
}
```
- `errorHtml()` 
	- 오류 페이지를 찾아서 반환해주는 메소드이다. 이전 글에서 확인했다.
	- `@RequestMapping(produces = MediaType.TEXT_HTML_VALUE)` 에 의해 클라이언트 요청의 `Accept` 헤더 값이 `text/html` 인 경우, 즉 일반적인 HTML 오류 페이지를 요구하는 경우 이 메소드가 호출된다.
- `error()`
	- API 예외 처리를 해준다.
	- JSON 형태의 데이터를 담은 HTTP 바디를 만들어서 응답해준다.
	- 다른 속성 없이 `@RequestMapping`  만 되어 있으므로, `/error` 경로로 온 요청중에 `accept` 헤더 값이 `text/html` 인 경우만 제외하고는 전부 이 메소드가 호출된다.

##### 실제 API 예외 처리로 사용하기 어려운 `BasicErrorController`
스프링 부트가 제공하는 `BasicErrorController` 는 HTML 형태의 오류 페이지를 제공하는 경우에는 매우 편리하다. 4xx, 5xx 등 다양한 HTTP 상태 코드에 대해서도 해당하는 경로에 오류 페이지 파일만 위치시키면 된다.

그런데 API 오류 처리는 다른 차원의 이야기이다. API 마다, 또 각각의 컨트롤러나 예외마다 서로 다른 응답 결과를 출력해야 할 수도 있다. 예를 들어서 동일한 예외가 발생하더라도 회원과 관련된 API에서 발생할 때와 상품과 관련된 API에서 발생할 때 응답이 달라질 수 있다.

`BasicErrorController` 가 제공하는 API 예외 처리는 기본적인 오류 정보만 넘겨주기 때문에 실제로 사용하기가 어렵다.

물론 단순하게 생각하면 `BasicErrorController` 를 확장해서 구현하면 상황에 따라서 응답 내용을 변경할 수 있긴 하지만, 스프링 부트에서 제공하는 더 편리한 방법이 있다. 결론부터 말하면 가장 편리한`@ExceptionHandler` 라는 어노테이션을 활용하면 되는데, 스프링 부트가 제공하는 다양한 방법이 있으므로 순서대로 알아보자.

## HandlerExceptionResolver
스프링 MVC는 컨트롤러(핸들러) 밖으로 예외가 던져진 경우 예외를 해결하고, 동작을 새로 정의할 수 있는 방법을 제공한다. 컨트롤러 밖으로 던져진 예외를 해결하고, 동작 방식을 변경하고 싶으면
`HandlerExceptionResolver` 를 사용하면 된다.

![](스크린샷%202022-09-01%20오후%204.01.02.png)

![](스크린샷%202022-09-01%20오후%204.01.13.png)

재밌는 점은 `HandlerExceptionResolver` 가 예외를 잡아서 처리한 뒤 `ModelAndView` 객체를 정상적으로 반환하면, 다시 정상 흐름으로 되돌아가게 된다는 것이다. 예외가 발생한 상황에서 정상 흐름으로 되돌리는 것이 `HandlerExceptionResolver` 의 목적이다.

`HandlerExceptionResolver` 가 반환한 `ModelAndView` 객체로 뷰를 렌더링하고, `afterCompletion()` 까지 호출된다.

참고로 핸들러에서 예외가 발생했을 때 `HandlerExceptionResolver` 가 예외를 잡아서 처리해주더라도 `postHandler()` 는 동작하지 않는다.

```Java
public interface HandlerExceptionResolver {
	ModelAndView resolveException(HttpServletRequest request,
				HttpServletResponse response, Object handler, Exception ex);
}
```
- `Object handler`
	- 예외가 발생한 핸들러에 대한 정보가 담겨있다.
	- 매개변수로 넘어오는 `handler` 객체의 타입은 어떤 핸들러 매핑을 사용하는가에 따라 달라진다. 실제 사용할 때는 `instanceof` 연산자로 객체의 타입을 확인해서 다운 캐스팅 한 뒤 객체에 접근하면 된다.
- `Exception ex`
	- 핸들러에서 발생한 예외 객체이다.

### HandlerExceptionResolver 반환 값에 따른 동작 흐름
- **비어있는 `ModelAndView`**
	- `new ModelAndView()` 처럼 빈 `ModelAndView` 객체를 반환하면, 뷰에 대한 정보가 없으므로 뷰를 렌더링 하지 않고, 정상 흐름으로 서블릿이 리턴된다.
- **뷰가 지정된 `ModelAndView`**
	- `ModelAndView` 에 `View` , `Model` 등의 정보를 지정해서 반환하면 뷰를 렌더링한다.
- **`null`**
	- `null` 을 반환하면, 다음 `HandlerExceptionResolver` 를 찾아서 실행한다. 만약 처리할 수 있는`HandlerExceptionResolver` 가 없으면 기존에 발생한 예외를 서블릿 밖으로 던진다.

### 직접 구현한 HandlerExceptionResolver 등록하기
```Java
@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
		public void extendHandlerExceptionResolvers(
								List<HandlerExceptionResolver> resolvers) {
		resolvers.add(new MyHandlerExceptionResolver());
		resolvers.add(new UserHandlerExceptionResolver());
	}
}
```

### HandlerExceptionResolver 활용하기
이론상으로는 `HandlerExceptionResolver` 를 잘 구현해서 예외를 처리하면 다시 정상 흐름으로 돌리거나, 발생한 오류에 대한 정보를 다양한 형태로 변환해서 클라이언트에게 넘겨줄 수 있다.

- **예외 상태 코드 변환**
	- 발생한 예외를 잡고, `response.sendError(xxx)` 호출로 변경해서 서블릿에서 상태 코드에 따른 오류를 처리하도록 위임한다. 이를 통해 사용자가 직접 정의한 예외도 처리할 수 있다.
	- 이후 WAS는 서블릿 오류 페이지 경로로 내부 호출을 한다. 따로 설정을 해두지 않았다면 스프링 부트가 기본으로 설정한 `/error` 가 호출된다.
- **뷰 템플릿 처리**
	- `ModelAndView` 에 값을 채워서 예외에 따른 새로운 오류 화면 뷰를 렌더링한다.
- **API 응답 처리**
	- `response.getWriter().println()` 처럼 HTTP 응답 바디에 직접 데이터를 넣어줄 수도 있다.
	- 여기에 JSON 형태로 데이터를 넣어주면 API 예외 처리를 할 수 있다.

### HandlerExceptionResolver 의 한계 - API 예외 처리
`HandlerExceptionResolver` 를 구현해서 API 예외 처리가 가능하긴 하다.

하지만 예외 정보를 `response` 객체에 직접 넣어주는 과정이 너무나 복잡하고 불편하다.

또 예외의 종류와 예외가 발생한 컨트롤러를 구분해서 따로 구현하는 일 역시 너무 복잡하다.

따라서 API 예외를 편리하게 처리하기 위해서는 `@ExceptionHandler` 어노테이션을 활용하면 된다.

이 어노테이션에 대해 알아보기 전에 먼저 스프링 부트가 기본으로 등록하는 `HandlerExceptionResolver` 들에 대해서 알아보자. 그 중에서 `@ExceptionHandler` 를 처리해주는 `HandlerExceptionResolver` 가 있다.


## 스프링 부트가 제공하는 HandlerExceptionResolver
스프링 부트가 기본으로 등록하는 `HandlerExceptionResolver` 는 다음과 같다. 스프링 부트는 `HandlerExceptionResolverComposite` 에 다음과 같은 우선순위로 등록한다.

1. `ExceptionHandlerExceptionResolver`
	1. `@ExceptionHandler` 를 처리한다.
	2. API 예외 처리는 대부분 이 기능으로 해결한다.
	3. 우선순위가 가장 높다.
2. `ResponseStatusExceptionResolver`
	1. 예외가 발생하면 해당 예외에 대한 HTTP 상태 코드를 지정한다.
3. `DefaultHandlerExceptionResolver`
	1. 스프링 내부 기본 예외를 처리한다.
	2. 우선 순위가 가장 낮다.

우선 순위가 낮은 순서대로 알아보자.

### DefaultHandlerExceptionResolver
스프링 내부에서 발생하는 예외를 처리한다.

대표적으로 파라미터 바인딩 시점에 타입이 맞지 않으면 내부에서 `TypeMismatchException` 이
발생한다. 해당 예외를 잡아서 처리하지 않으면 서블릿 컨테이너까지 오류가 올라가게 되고, 처리되지 않은 예외가 발생했기 때문에 결과적으로 500 오류가 발생한다.

그런데 파라미터 바인딩이 실패는 대부분 클라이언트가 HTTP 요청 정보를 잘못 호출해서 발생하는 문제이다. HTTP 스펙에서는 이런 경우 상태 코드 400을 사용하도록 되어 있다.

따라서 `DefaultHandlerExceptionResolver` 는 `TypeMismatchException` 이 발생하면 상태 코드 400 오류가 발생하도록 처리해준다.

##### DefaultHandlerExceptionResolver 코드 확인
```Java
public class DefaultHandlerExceptionResolver extends AbstractHandlerExceptionResolver {

	@Override
	@Nullable
	protected ModelAndView doResolveException(
			HttpServletRequest request, HttpServletResponse response, 
								@Nullable Object handler, Exception ex) {

		try {
			if (ex instanceof HttpRequestMethodNotSupportedException) {
				return handleHttpRequestMethodNotSupported(
						(HttpRequestMethodNotSupportedException) ex,
						request, response, handler);
			}
			else if (ex instanceof TypeMismatchException) {
				return handleTypeMismatch(
						(TypeMismatchException) ex, request,
						response, handler);
			}
			//다른 수 많은 스프링 내부 에러에 대한 else if (ex instaneof ...) {}
		}
		catch (Exception handlerEx) {
			//생략
		}
		return null;
	}
	
	protected ModelAndView handleTypeMismatch(TypeMismatchException ex,
			HttpServletRequest request, HttpServletResponse response,
							@Nullable Object handler) throws IOException {

		response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		return new ModelAndView();
	}
}
```

핸들러에서 발생한 예외 객체 `Exception ex` 를 넘겨 받는다.

`instanceof` 로 넘겨받은 예외 객체의 타입을 확인하고, 타입에 맞는 메소드를 호출해서 해당 예외에 대한 HTTP 상태 코드를 설정한다.

예를 들어 `ex` 객체가 `TypeMismatchException` 타입인 경우, `handleTypeMismatch()` 메소드를 호출하는데, 해당 메소드를 보면 `response.sendError(HttpServleResponse.SC_BAD_REQUEST)` 를 호출해서 응답 상태 코드로 400을 지정해주는 것을 확인할 수 있다.