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

왜냐하면 `DefaultHandlerExceptionResolver` 가 핸들러 어댑터에서 발생한 `TypeMismatchException` 이 예외를 잡아서 상태 코드 400 오류가 발생하도록 처리해주기 때문이다.

`DefualtHandlerExceptionResolver` 의 코드를 확인해서 어떻게 스프링 내부 예외를 처리해주는지 확인해보자.

#### DefaultHandlerExceptionResolver 코드 확인
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

먼저 `DefualtHandlerExceptionResolver` 가 `AbstractHandlerExceptionResolver` 를 상속받고 있는 것을 확인할 수 있는데, `AbstractHandlerExceptionResolver` 는 `HandlerExceptionResolver` 를 구현하고 있다.

`AbstractHandlerExceptionResolver` 가 구현한  `HandlerExceptionResolver` 의 `resolveException()` 메소드를 보면 `doResolverException()` 메소드를 호출하는 코드를 확인할 수 있다.

`DefaultHandlerExceptionResolver` 의 `doResolveException()` 코드를 보면 핸들러에서 발생한 예외 객체 `Exception ex` 를 넘겨 받는다.

`instanceof` 로 넘겨받은 예외 객체의 타입을 확인하고, 타입에 맞는 메소드를 호출해서 해당 예외에 대한 HTTP 상태 코드를 설정한다.

예를 들어 `ex` 객체가 `TypeMismatchException` 타입인 경우, `handleTypeMismatch()` 메소드를 호출하는데, 해당 메소드를 보면 `response.sendError(HttpServleResponse.SC_BAD_REQUEST)` 를 호출해서 응답 상태 코드로 400을 지정해주는 것을 확인할 수 있다.

이러한 이유로 `TypeMismatchException` 예외가 발생해도 응답의 HTTP 상태 코드가 500이 아닌 400이 되는 것이다.

### ResponseStatusExceptionResolver
`@ResponseStatus` 어노테이션이 있는 예외 혹은 `ResponseStatusException` 예외를 처리해서 HTTP 상태 코드를 지정해주는 역할을 한다.

#### @ResponseStatus 어노테이션이 있는 예외
예외 클래스에 `@ResponseStatus` 어노테이션을 적용하면, 핸들러에서 해당 예외가 발생했을 때 `ResponseStatusExceptionResolver` 가 해당 예외를 잡아서 HTTP 상태 코드를 지정한다.

```Java
@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "잘못된 요청 오류")
public class BadRequestException extends RuntimeException {
}
```

예를 들어 핸들러에서 위의 `BadRequestException` 이 발생하면 HTTP 상태 코드는 400 Bad request 가 된다.

`@ResponseStatus` 의 `reason` 속성값을 에러 메시지로 담아준다. `reason` 값에 메시지 코드를 넣으면 코드에 해당하는 메시지를 `MessageSource` 에서 찾아주는 기능도 제공한다.

#### ResponseStatusException 예외
`@ResponseStatus` 는 개발자가 직접 변경할 수 없는 예외 클래스에는 적용할 수 없다 (라이브러리에서 제공하는 예외 등). 또 어노테이션을 사용하기 때문에 조건에 따라 동적으로 변경하는 것도 어렵다.

이때는 `ResponseStatusException` 예외를 사용해서 처리하면 된다.

```Java
@GetMapping("/api/response-status-ex2")
public String responseStatusEx2() {
	throw new ResponseStatusException(HttpStatus.NOT_FOUND, "error.bad",
											new IllegalArgumentException());
}
```

HTTP 상태 코드를 지정하고 싶은 예외를 `ResponseStatusException` 으로 한 번 감싸주는 것이다. `@ResponseStatus` 와 마찬가지로 메시지도 적용할 수 있다.

핸들러에서 `ResponseStatusExeptionResolver` 가 발생하면 `ResponseStatusExceptionResolver` 가 동작해서 지정해둔 HTTP 상태 코드를 응답 상태 코드로 설정하고, 에러 메시지도 넣어준다.

#### ResponseStatusExceptionResolver 코드 확인
```Java
public class ResponseStatusExceptionResolver
	extends AbstractHandlerExceptionResolver implements MessageSourceAware {

	@Override
	@Nullable
	protected ModelAndView doResolveException(
			HttpServletRequest request, HttpServletResponse response, 
			@Nullable Object handler, Exception ex) {

		try {
			if (ex instanceof ResponseStatusException) {
				return resolveResponseStatusException(
					(ResponseStatusException) ex, request, response, handler);
			}

			ResponseStatus status = 
							AnnotatedElementUtils.findMergedAnnotation(
							ex.getClass(), ResponseStatus.class);
							
			if (status != null) {
				return resolveResponseStatus(
								status, request, response, handler, ex);
			}
			//생략
		}
		catch (Exception resolveEx) {
			//생략...
		}
		return null;
	}

	protected ModelAndView applyStatusAndReason(
		int statusCode, @Nullable String reason, HttpServletResponse response)
		throws IOException {

		if (!StringUtils.hasLength(reason)) {
			response.sendError(statusCode);
		}
		
		else {
			String resolvedReason = (this.messageSource != null ?
					this.messageSource.getMessage(
					reason, null, reason, LocaleContextHolder.getLocale()) :
					reason);
					
			response.sendError(statusCode, resolvedReason);
		}
		return new ModelAndView();
	}
```

`ResponseStatusExceptionResolver` 역시 `AbstractHandlerExceptionResolver` 를 상속받고 있기 때문에 `HandlerExceptionResolver` 가 구현한 `resolveException()` 에 의해 `doResolveException()` 메소드가 호출된다.

핸들러에서 발생한 `Exception ex` 의 타입이 `ResponseStatusException` 이면 `resolveResponseStatusException()` 메소드를 호출한다.

또는 해당 예외 클래스에 `@ResponseStatus` 어노테이션이 붙어있는 경우 `resolveResponseStatus()` 메소드를 호출한다.

그렇게 호출된 두 메소드 모두 `applyStatusAndReason()` 메소드를 호출하게 되는데, 이 메소드에서 `response.sendError()` 로 HTTP 상태 코드를 지정하고, 에러 메시지도 설정해주는 것을 확인할 수 있다.

### ExceptionHandlerExceptionResolver
스프링은 `ExceptionHandlerExceptionResolver` 를 기본으로 제공하고, 기본으로 제공하는
`HandlerExceptionResolver` 중에 우선순위도 가장 높다. 실무에서 API 예외 처리는 대부분 이 기능을 사용한다.

### @ExceptionHandler
```Java
@Controller
public class HelloController {

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(IllegalArgumentException.class)
	public String illegalExHandle(IllegalArgumentException e) {
		return "error/400";
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler({UserException.class, TypeMismatchException.class})
	public String multiExHandle(Exception e) {
		return "error/400";
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(Exception.class)
	public String allExHandle(Exception e) {
		return "error/400";
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler
	public String exHandleThis(RuntimeException e) {
		return "error/500";
	}

	//컨트롤러 메소드들...
}
```

`@ExceptionHandler` 어노테이션으로 해당 컨트롤러에서 처리하고 싶은 예외를 지정해주면 된다.

컨트롤러에서 예외가 발생하면 예외에 해당하는 `@ExceptionHandler` 메소드가 호출된다.

참고로 `@ExceptionHandler` 로 지정한 예외는 그 자식 예외에도 적용된다. 하지만 스프링에서는 더 자세한 설정이 우선순위를 가진다. 따라서 위 코드에서 `Exception.class` 를 처리하는 `@ExceptionHandler` 메소드인 `allExHandle()` 이 있지만, `IllegalArgumentException` 이 발생하면 `illegalExHandle()` 메소드가 더 높은 우선순위를 가져서 해당 메소드가 실행된다.

또 `multiExHandle()` 처럼 여러 예외를 한번에 처리할 수도 있다.

그리고 `exHandleThis()` 메소드처럼 `@ExceptionHandler` 에 예외 클래스 속성을 생략할 수도 있는데, 생략하면 메서드의 파라미터로 지정된 예외를 처리한다.

### @ExceptionHandler 로 API 예외 처리
위 코드처럼 `String` 으로 뷰 이름을 반환하는 `@ExceptionHandler` 메소드를 사용할 수도 있지만, 사실 이렇게 사용할 거면 굳이 `@ExceptionHandler` 를 사용할 이유가 없다.

`@ExceptionHandler` 는 API 예외 처리에 매우 유용하다. 다음 에시 코드를 살펴보자.

**ErrorResult.java**
```Java
@Data
@AllArgsConstructor
public class ErrorResult {
	private String code;
	private String message;
}
```

**HelloController.java**
```Java
@RestController
public class HelloController {

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(IllegalArgumentException.class)
	public ErrorResult illegalExHandle(IllegalArgumentException e) {
		return new ErrorResult("BAD", e.getMessage());
	}
	
	@ExceptionHandler
	public ResponseEntity<ErrorResult> userExHandle(UserException e) {
		ErrorResult errorResult = new ErrorResult("USER-EX", e.getMessage());
		return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
	}
	
	//컨트롤러 메소드들...
}
```

메소드의 반환형이 직접 작성한 클래스인 `ErrorResult` 이다.

컨트롤러에 `@RestController` 어노테이션이 있으므로, 모든 메소드에 `@ResponseBody` 가 적용된다. 따라서 `HttpMessageConverter` 가 동작해서 `ErrorResult` 객체를 JSON 형태로 변환해서 응답 메시지 바디에 넣어준다.

또 컨트롤러에서 발생한 예외에 대해서 응답 HTTP 상태 코드를 동적으로 변경할 필요가 없다면 `@ResponseStatus` 어노테이션을 사용하면 되고, 동적 변경이 필요하다면 `ResponseEntity<>` 반환형을 사용하면 된다.

이렇게 스프링이 제공하는 기능들을 간편하게 이용할 수 있기 때문에, `HandlerExceptionResolver` 를 직접 구현해서 `response` 객체에 직접 응답값을 넣어주는 것과는 비교할 수 없을 정도로 편리하다. 

그리고 `@ExceptionHandler` 를 사용하면 동일한 예외가 발생해도, 컨트롤러마다 다른 예외 처리 방식을 적용하기가 매우 쉽다.

##### 참고: @ExcpetionHandler 의 파라미터와 반환형
`@ExceptionHandler` 에는 마치 스프링 컨트롤러 메소드처럼 다양한 파라미터와 반환형을 지정할
수 있다. 다음 공식 메뉴얼을 참고하자.

링크: https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-annexceptionhandler-args

#### @ExceptionHandler API 예외 처리 흐름
1. 컨트롤러에서 `IllegalArgumentException` 예외가 발생했지만 해당 예외를 처리하지 못하고 예외는 컨트롤러 밖으로 던져진다.
2. 예외가 발생했으로 `HandlerExceptionResolver` 가 작동한다. 스프링이 기본 등록해둔 `HanlderExceptionResolver` 중 가장 우선순위가 높은 `ExceptionHandlerExceptionResolver` 가 실행된다.
3. `ExceptionHandlerExceptionResolver` 는 해당 컨트롤러에 `IllegalArgumentException` 을 처리할 수 있는 `@ExceptionHandler` 메소드가 있는지 확인한다.
4. `illegalExHandle()` 를 실행한다. `@RestController` 이므로 `illegalExHandle()` 에도`@ResponseBody` 가 적용된다. 따라서 `HttpMessageConverter` 가 동작해서 `ErrorResult` 객체가 JSON으로 변환되어 메시지 바디에 들어간다.
5. `@ResponseStatus(HttpStatus.BAD_REQUEST)` 를 지정했으므로 HTTP 상태 코드 400으로 응답한다.

## @ControllerAdvice - 예외 처리 코드 분리
`@ExceptionHandler` 를 사용해서 컨트롤러에서 발생하는 예외를 편리하게 처리할 수 있게 되었지만, 정상 코드와 예외 처리 코드가 하나의 컨트롤러에 섞여 있다는 문제가 발생한다.

이 때 `@ControllerAdvice` 또는 `@RestControllerAdvice` 를 사용하면 예외 처리 코드를 분리해낼 수 있다.

`@ControllerAdvice` 는 대상으로 지정한 여러 컨트롤러에 `@ExceptionHandler` , `@InitBinder` 기능을 부여해주는 역할을 한다.

`@RestControllerAdvice` 는 `@ControllerAdvice` 에 `@ResponseBody` 가 추가되어 있는 것 뿐이다 (`@Controller` , `@RestController` 의 차이와 동일).

```Java
@RestControllerAdvice(assignableTypes = HelloControlle.class)
public class HelloControllerAdvice {

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(IllegalArgumentException.class)
	public ErrorResult illegalExHandle(IllegalArgumentException e) {
		return new ErrorResult("BAD", e.getMessage());
	}
	
	@ExceptionHandler
	public ResponseEntity<ErrorResult> userExHandle(UserException e) {
		ErrorResult errorResult = new ErrorResult("USER-EX", e.getMessage());
		return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
	}
}
```

기존 컨트롤러에서 `@ExceptionHandler` 코드를 지우고 위 클래스를 작성한 뒤 `@RestControllerAdvice` 어노테이션을 달아주기만 하면 된다.

#### @ControllerAdvice 대상 컨트롤러 지정 방법
`@ControllerAdvice` 에 대상을 지정하지 않으면 모든 컨트롤러에 적용된다 (글로벌 적용).

다음과 같은 방법으로 대상 컨트롤러를 지정할 수 있다.

```Java
// Target all Controllers annotated with @RestController
@ControllerAdvice(annotations = RestController.class)
public class ExampleAdvice1 {}

// Target all Controllers within specific packages
@ControllerAdvice("org.example.controllers")
public class ExampleAdvice2 {}

// Target all Controllers assignable to specific classes
@ControllerAdvice(assignableTypes =
				  {ControllerInterface.class, AbstractController.class})
public class ExampleAdvice3 {}
```
