# Spring Boot - Session & Cookie

## 로그인 처리하기 - 세션

Stateless 한 HTTP의 특성상, 로그인 같은 기능을 구현하기 위해서는 서버와 클라이언트 사이의 연결을 유지할 수 있는 방법이 필요하다.

그렇다고 클라이언트의 로그인 상태를 유지하기 위해 모든 HTTP request 마다 ID와 비밀번호를 전달받을 수는 없는 노릇이다. 이는 보안상으로도 문제가 될 수 있다.

따라서 보안상의 문제를 해결하기 위해서는 클라이언트의 중요한 정보를 모두 서버에 저장해야 한다. 그리고 클라이언트와 서버는 추정 불가능(Brute-forcing이 불가능)한 임의의 식별자 값으로 연결해야 한다. 이 식별자로는 값 자체만으로는 어떠한 의미도 가지지 않는 UUID를 사용할 수 있다.

**이렇게 서버에 중요한 정보를 보관하고 연결을 유지하는 방법을 세션이라 한다.**

## 세션 동작 방식
![](스크린샷%202022-08-22%20오후%209.36.07.png)

##### 클라이언트와 서버는 결국 쿠키로 연결이 되어야 한다.
- 서버는 세션 ID를 쿠키에 담아서 클라이언트에게 전달한다.
- 클라이언트는 쿠키 저장소에 해당 쿠키(세션 ID)를 보관한다.
- 여기서 중요한 포인트는 회원과 관련된 어떠한 정보도 전달하지 않는다는 것이다.
- 오직 추정 불가능한 세션 ID만 쿠키를 통해 전달한다.

![](스크린샷%202022-08-22%20오후%209.38.43.png)


## Servlet HTTP Session
세션이라는 개념은 대부분의 웹 애플리케이션에 필요한 것이다. 그래서 Servlet은 세션을 위해 `HttpSession` 이라는 기능을 제공한다.

서블릿을 통해 `HttpSession` 을 생성하면 `JSESSIONID` 라는 이름을 가진 쿠키를 생성한다. 이 쿠키를 클라이언트의 식별자로 사용하는 것이다.

```
쿠키 예시
Cookie: JSESSIONID=5B78E23B513F50164D6FDD8C97B0AD05
```

```Java
@PostMapping("/login")
public String login(@Valid @ModelAttribute LoginForm form,
					BindingResult bindingResult, HttpServletRequest request) {
	Member loginMember =
			loginService.login(form.getLoginId(), form.getPassword());

	if (loginMember == null) {
		return "login/loginForm";
	}
	
	//로그인 성공 처리
	//세션이 있으면 있는 세션 반환, 없으면 신규 세션 생성
	HttpSession session = request.getSession();
	
	//세션에 로그인 회원 정보 보관
	session.setAttribute("loginMember", loginMember);
	
	return "redirect:/";
}
```

- **`request.getSession()`**
	- `HttpServletRequest` 객체가 제공하는 `HttpSession` 을 생성하는 메소드이다.
	- `request.getSession(true)`
		- 세션이 이미 존재하면 기존 세션을 반환한다. HTTP Request 헤더의 쿠키 중 `JSESSIONID` 라는 쿠키의 값이 이미 존재하는지 확인하면 된다.
		- 세션이 없으면 새로운 세션을 생성해서 반환한다.
		- `true` 가 default 값이다.
	- `request.getSession(false)`
		- 세션이 이미 존재하면 기존 세션을 반환한다.
		- 세션이 없으면 새로운 세션을 생성하지 않고  `null` 을 반환한다.

- **`session.setAttribute("loginMember", loginMember)`**
	- 하나의 세션에 Key-value 형식으로 여러 값을 저장할 수 있다.


#### 세션으로 클라이언트의 로그인 여부 확인
`request.getSession(false)` 는 기존 세션이 없으면 `null` 을 반환하므로 이 메소드를 활용해서 로그인 여부를 판단할 수 있다.

```Java
@GetMapping("/")
public String homeLogin(HttpServletRequest request, Model model) {
	//세션이 없으면 home
	HttpSession session = request.getSession(false);
	if (session == null) {
		return "home";
	}
	
	Member loginMember =
				(Member) session.getAttribute("loginMember");
	//세션에 회원 데이터가 없으면 home
	if (loginMember == null) {
		return "home";
	}
	
	//세션이 유지되면 로그인으로 이동
	model.addAttribute("member", loginMember);
	return "loginHome";
}
```

#### 세션 로그아웃
`HttpSession` 객체의 `invalidate()` 메소드로 세션을 제거할 수 있다.

```Java
@PostMapping("/logout")
public String logout(HttpServletRequest request) {
	HttpSession session = request.getSession(false);
	if (session != null) {
		session.invalidate();
	}
	return "redirect:/";
}
```


## Spring과 Session

### `@SessionAttribute`
스프링은 세션을 더 편리하게 사용할 수 있도록 `@SessionAttribute` 을 지원한다.

클라이언트의 로그인 여부를 확인하기 위해서 컨트롤러마다 다음과 같은 코드를 계속 추가하기는 너무 귀찮다. 스프링에서 제공하는 `@SessionAttribute` 로 이런 공통적인 기능 구현을 쉽게 해결할 수 있다.

```Java
@GetMapping("/")
public String homeLogin(HttpServletRequest request, Model model) {
	//세션이 없으면 home
	HttpSession session = request.getSession(false);
	if (session == null) {
		return "home";
	}
	
	Member loginMember =
				(Member) session.getAttribute("loginMember");
	//세션에 회원 데이터가 없으면 home
	if (loginMember == null) {
		return "home";
	}
	
	//세션이 유지되면 로그인으로 이동
	model.addAttribute("member", loginMember);
	return "loginHome";
}
```

위 코드에 `@SessionAttribute` 를 적용하면 다음과 같이 코드가 간단해진다.

```Java
@GetMapping("/")
public String homeLoginV3Spring(
@SessionAttribute(name = "loginMember", required = false) Member loginMember,
															Model model) {
	//세션에 회원 데이터가 없으면 home
	if (loginMember == null) {
		return "home";
	}
	
	//세션이 유지되면 로그인으로 이동
	model.addAttribute("member", loginMember);
	return "loginHome";
}
```

마치 `@RequestParam` 처럼 간단하게 사용할 수 있다.

HTTP Request가 들어오면, 헤더에서 쿠키를 통해 세션을 찾고, 해당 세션에 저장되어 있는 `name` 속성 값을 key로 가지는 데이터(객체)를 찾아와준다. 이런 번거로운 과정을 스프링이 내부적으로 처리해준다.

`required` 값이 `false` 이기 때문에 해당 데이터가 존재하지 않으면 (혹은 세션이 존재하지 않으면) 객체의 값은 `null` 로 초기화된다.


## 세션 타임아웃 설정
개발자가 로그아웃 기능을 잘 개발해서 사용자가 로그아웃을 하면  `session.invalidate()` 가 정상 호출되고, 세션이 제거된다고 하자.

그런데 대부분의 사용자는 직접 로그아웃을 하지 않고 그냥 웹 브라우저를 종료한다.

문제는 HTTP는 **비연결성(Connectionless)** 이므로 서버 입장에서는 해당 사용자가 웹 브라우저를 종료한 것인지 아닌지를 인식할 수 없다. 따라서 서버에서 세션을 언제 삭제해야 하는지 판단하기가 어렵다.

이 경우 남아있는 세션을 제거하지 않으면 다음과 같은 문제가 발생할 수 있다.
- 세션 식별자로 사용되는 쿠키( `JSESSIONID` )를 탈취 당했을 경우, 로그인한 사용자가 웹 브라우저를 종료하고 오랜 시간이 지나도 해당 쿠키로 악의적인 요청을 할 수 있다.
- 세션은 기본적으로 메모리에 생성된다. 세션을 제거하지 않으면 언젠간 메모리가 꽉 찰 것이다.

#### 세션의 종료 시점
단순하게 세션 생성 시점으로 n 분 뒤에 세션을 제거한다고 하면, 사용자가 웹 앱의 서비스를 계속 이용중이라고 하더라도 사용자는 n 분마다 다시 로그인을 해야한다.

따라서 사용자가 서버에 가장 최근에 요청한 시간을 기준으로 n 분 뒤에 세션을 제거하는 방식을 사용하면 된다. 즉 사용자의 요청 시간을 기준으로 세션 종료 시점을 계속 업데이트 하는 것이다.

`HttpSession` 이 이 방식을 사용해서 세션을 관리한다.

#### 스프링 부트 세션 타임아웃 설정
스프링 부트 환경에서 다음과 같은 방법으로 세션 타임아웃 시간을 글로벌 설정 할 수 있다. 단위는 초 이지만, 글로벌 설정은 분 단위, 즉 60의 배수로 설정해야 한다. 참고로 기본값은 1800초(30분)이다.

**application.properties**
```
server.servlet.session.timeout=60
```

**특정 세션의 타임아웃 설정**
```Java
session.setMaxInactiveInterval(1800); //1800초
```