# Spring Boot - HandlerMethodArgumentResolver로 로그인 처리하기

## 서론 - 어노테이션으로 로그인 여부 확인하기
```Java
@GetMapping("/")
public String homeLogin(
@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false)
Member loginMember, Model model) {
	//세션에 회원 데이터가 없으면 home
	if (loginMember == null) {
		return "home";
	}
	//세션이 유지되면 로그인으로 이동
	model.addAttribute("member", loginMember);
	return "loginHome";
}
```

위 코드처럼 `@SessionAttribute` 어노테이션으로 세션에 저장되어 있는 값을 꺼내와서 로그인 여부를 확인할 수 있다.

그런데 `HandlerMethodArgumentResolver` 를 구현해서 등록하면 다음과 같은 방식으로 더 간단하게 로그인 여부를 확인할 수 있다.

```Java
@GetMapping("/")
public String homeLogin(@Login Member loginMember, Model model) {
	//세션에 회원 데이터가 없으면 home
	if (loginMember == null) {
		return "home";
	}
	//세션이 유지되면 로그인으로 이동
	model.addAttribute("member", loginMember);
	return "loginHome";
}
```

컨트롤러에 `@Login` 어노테이션이 있는 매개변수가 있으면 직접 구현한 `HandlerMethodArgumentReslover` 가 동작해서 자동으로 세션에 있는 로그인 회원 객체를 찾아서 넘겨주고, 로그인 되어있지 않은 경우 `null` 을 넘겨주도록 개발하면 된다.


## HandlerMethodArgumentResolver 인터페이스
```Java
public interface HandlerMethodArgumentResolver {

	boolean supportsParameter(MethodParameter parameter);

	@Nullable
	Object resolveArgument(MethodParameter parameter,
	@Nullable ModelAndViewContainer mavContainer,
	NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory)
	throws Exception;
}
```
- `supportsParameter()` : 이 메소드의 매개변수로 컨트롤러의 특정 파라미터 정보(타입, 어노테이션 등)가 넘어온다. 그 정보를 보고 해당 파라미터를 처리할 수 있다면 `true` 를, 아니면 `false` 를 반환하도록 구현하면 된다.
- `resolveArgument()` : `supportsParameter()` 가 `true` 를 반환하면, 이 메소드가 컨트롤러에 필요한 파라미터를 생성해서 반환해준다. 스프링 MVC는 컨트롤러의 메서드를 호출하면서 이 메소드에서 반환된 객체를 파라미터로 전달한다.

스프링은 수많은 `HandlerMethodArgumentResolver` 구현체를 기본적으로 등록해둔다. 그래서 별다른 설정 없이 컨트롤러 메소드에 다양한 파라미터를 사용할 수 있는 것이다.


## HandlerMethodAnotationResolver 구현하기
먼저 직접 구현한 `HandlerMethodAnotationResolver` 의 대상이 될 어노테이션을 만들어보자.

```Java
package hello.login.web.argumentresolver;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Login {}
```

- `@Target(ElementType.PARAMETER)` : 파라미터에만 사용
- `@Retention(RetentionPolicy.RUNTIME)` : 리플렉션 등을 활용할 수 있도록 런타임까지 애노테이션 정보가 남아있음

이제 컨트롤러에서 `@Login` 어노테이션이 붙은 매개변수를 처리해주는 `HandlerMethodAnotaionResolver` 를 구현해보자.

```Java
//package, import 생략

public class LoginMemberArgumentResolver implements
HandlerMethodArgumentResolver {
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
	
		boolean hasLoginAnnotation =
				parameter.hasParameterAnnotation(Login.class);
					
		boolean hasMemberType =
				Member.class.isAssignableFrom(parameter.getParameterType());
				
		return hasLoginAnnotation && hasMemberType;
	}
	
	@Override
	public Object resolveArgument(MethodParameter parameter,
		ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
		WebDataBinderFactory binderFactory) throws Exception {
		
		HttpServletRequest request =
				(HttpServletRequest) webRequest.getNativeRequest();
				
		HttpSession session = request.getSession(false);
		
		if (session == null) {
			return null;
		}
		return session.getAttribute(SessionConst.LOGIN_MEMBER);
	}
}
```
- `supportsParameter()` : 파라미터에 `@Login` 어노테이션이 있으면서 파라미터 타입이 `Member` 이면 이 `ArgumentResolver` 가 사용된다.
- `resolveArgument()` : 세션에 있는 로그인 회원 정보인 `Member` 객체를 찾아서 반환해준다. 로그인 되어있지 않다면 `null`을 반환한다.  스프링 MVC는 컨트롤러의 메서드를 호출하면서 이 메소드에서 반환된 `Member` 객체를 파라미터로 전달한다.


## HandlerMethodArgumentResolver 등록
```Java
@Configuration
public class WebConfig implements WebMvcConfigurer {
	@Override
	public void addArgumentResolvers(
						List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(new LoginMemberArgumentResolver());
	}
}
```


## 정리
`HandlerMethodArgumentResolver` 를 활용하면 로그인 처리 외에도 공통 작업이 필요할 때 컨트롤러를 더욱 편리하게 사용할 수 있다.
