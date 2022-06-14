# RequestMappingHandlerAdapter 구조

이전 강의에서 배운 `HttpMessageConverter`는 어디서 호출되고 동작하는 것일까?

##### Spring MVC 구조
![](스크린샷%202022-06-04%20오후%2010.40.03.png)

위 그림에서는 찾아볼 수가 없다.

사실 모든 비밀은 애노테이션 기반의 컨트롤러, 즉 `@RequestMapping` 핸들러를 처리하는 핸들러 어댑터 `RequestMappingHandlerAdapter` 에 있다.

## RequestMappingHandlerAdapter 동작 방식
![](스크린샷%202022-06-14%20오후%2010.40.45.png)

### ArgumentResolver(HandlerMethodArgumentResolver)
지금까지 만들었던 컨트롤러들을 잘 생각해보면, 애노테이션 기반의 컨트롤러는 매우 다양한 파라미터를 사용할 수 있었다. `HttpServletRequest` , `Model` 은 물론이고, `@RequestParam` , `@ModelAttribute` , `@RequestBody` , `HttpEntity` 같이 애노테이션 기반의 파라미터나 HTTP 메시지를 처리해야만 얻을 수 있는 파라미터 등도 있었다.

이렇게 파라미터를 유연하게 처리할 수 있는 이유가 바로 `ArgumentResolver` 덕분이다.  `RequestMappingHandlerAdapter` 는 바로 이 `ArgumentResolver` 를 호출해서 컨트롤러(핸들러)가 필요로 하는 다양한 파라미터의 값(객체)을 생성한다. 그리고 이렇게 파리미터의 값이 모두 준비되면 컨트롤러를 호출하면서 값을 넘겨준다.

스프링은 30개가 넘는 `ArgumentResolver` 구햔체를 기본으로 제공한다. 정확히는 `HandlerMethodArgumentResolver` 인데 줄여서 `ArgumentResolver` 라고 부른다.

##### HandlerMethodArgumentResolver.java
```Java
package org.springframework.web.method.support;

import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;

public interface HandlerMethodArgumentResolver {

	boolean supportsParameter(MethodParameter parameter);

	@Nullable
	Object resolveArgument(MethodParameter parameter,
					@Nullable ModelAndViewContainer mavContainer,
					NativeWebRequest webRequest,
					@Nullable WebDataBinderFactory binderFactory)
					throws Exception;
}
```

`ArgumentResolver` 의 `supportsParameter()` 메소드를 호출해서 해당 파라미터를 지원하는지 체크하고, 지원하면 `resolveArgument()` 를 호출해서 실제 객체를 생성한다. 그리고 이렇게 생성된 객체가 컨트롤러 호출시 넘어가는 것이다.

원한다면 직접 이 인터페이스를 확장해서 원하는 `ArgumentResolver` 를 만들 수도 있다. 후에 로그인 처리에서 직접 구현해 볼 것이다.

### ReturnValueHandler(HandlerMethodReturnValueHandler)
`ArgumentResolver` 는 컨트롤러의 파라미터를 처리해주고, `ReturnValueHandler` 는 컨트롤러의 반환값을 변환하고 처리한다. 컨트롤러에서 `String` 으로 뷰 이름을 반환해도 뷰를 찾을 수 있는 이유가 바로 `ReturnValueHandler` 덕분이다.

스프링은 10여개가 넘는 `ReturnValueHandler` 의 구현체를 지원한다.
예) `ModelAndView` , `@ResponseBody` , `HttpEntity` , `String` 등을 처리할 수 있는 `ReturnValueHandler`의 구현체

`HandlerMethodReturnValueHandler` 를 줄여서 `ReturnValueHandler` 라 부른다.


## HttpMessageConverter가 동작하는 위치
![](스크린샷%202022-06-14%20오후%2011.05.58.png)

`HttpMessageConverter`는 바로 `ArgumentResolver` 와 `ReturnValueHandler` 가 사용한다.

#### 요청 - ArgumentResolver
`@RequestBody` 를 처리하는 `ArgumentResolver` 가 있고, `HttpEntity` 를 처리하는 `ArgumentResolver` 가 있다. 이 `ArgumentResolver` 들이 `HttpMessageConverter`를 사용해서 필요한 객체를 생성하는 것이다.

#### 응답 - ReturnValueHandler
`@ResponseBody` 와 `HttpEntity` 를 처리하는 `ReturnValueHandler` 가 있다. 여기에서 `HttpMessageConverter` 를 호출해서 응답 결과를 만든다.

스프링 MVC는 `@RequestBody` `@ResponseBody` 가 있으면 `RequestResponseBodyMethodProcessor`, 
`HttpEntity` 가 있으면 `HttpEntityMethodProcessor` 를 사용한다. 두 클래스는 `ArgumentResolver`, `ReturnValueHandler` 두 역할을 모두 수행한다. 실제로 두 인터페이스를 모두 구현한 것을 확인할 수 있다.

### 확장
스프링은 `HandlerMethodArgumentResolver`, `HandlerMethodReturnValueHandler`, `HttpMessageConverter` 전부 인터페이스로 제공한다. 따라서 필요하다면 언제든지 기능을 확장할 수 있다.

스프링이 필요한 대부분의 기능을 제공하기 때문에 실제 기능을 확장할 일이 많지는 않다. 기능 확장은 `WebMvcConfigurer` 를 상속 받아서 스프링 빈으로 등록하면 된다. 실제 자주 사용하지는 않으니 실제 기능 확장이 필요할 때 `WebMvcConfigurer` 를 검색해보자.

##### WebMvcConfigurer를 통한 확장
```Java
@Bean
public WebMvcConfigurer webMvcConfigurer() {
	return new WebMvcConfigurer() {
		@Override
		public void addArgumentResolvers(List<HandlerMethodArgumentResolver>
		resolvers) {
			//...
		}
		
		@Override
		public void extendMessageConverters(List<HttpMessageConverter<?>>
		converters) {
			//...
		}
		
		@Override
		public void 
		addReturnValueHandlers(List<HandlerMethodReturnValueHandler> handlers) 
		{
			//...
		}
	};
}
```