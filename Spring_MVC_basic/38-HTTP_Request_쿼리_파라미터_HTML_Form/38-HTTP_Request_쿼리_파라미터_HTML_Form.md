# HTTP Request - 쿼리 파라미터, HTML Form

Spring에서 쿼리 파라미터와 HTML Form 파라미터를 어떻게 처리할 수 있는지 알아보자.


## 클라이언트가 요청 데이터를 전달하는 방법 3가지
- **GET - 쿼리 파라미터**
	- `/url?username=hello&age=20`
	- 메시지 바디 없이 URL의 쿼리 파라미터에 데이터를 포함해서 전달
	- 검색, 필터, 페이징등에서 많이 사용하는 방식
- **POST - HTML Form**
	- `username=hello&age=20`
	- `content-type: application/x-www-form-urlencoded`
	- 메시지 바디에 쿼리 파리미터 형식으로 전달
	- 회원 가입, 상품 주문 등에서 많이 사용함
- **HTTP message body에 데이터를 직접 담아서 요청**
	- HTTP API에서 주로 사용함
	- JSON, XML, TEXT 등의 포맷을 사용한다
		- 주로 JSON을 사용한다
	- POST, PUT, PATCH


## 요청 파라미터 - 쿼리 파라미터, HTML Form 조회
두 파라미터의 데이터 포맷이 동일하기 때문에 동일한 방법으로 조회할 수 있다.

이 두 타입의 파라미터를 합쳐서 **요청 파라미터(Request parameter)** 라고 한다.

##### GET - 쿼리 파라미터
```
http://localhost:8080/request-param?username=hello&age=20
```

##### POST - HTML Form
```
POST /request-param
...
content-type: application/x-www-form-urlencoded

username=hello&age=20
```

### 1. Servlet 기술 - HttpServletRequest 으로 조회
`HttpServletRequest` 객체의 `getParameter()` 를 사용하면 된다.

##### RequestParamController.java 일부
```Java

@RequestMapping("/request-param-v1")
public void requestParamVer1(HttpServletRequest request,
							 HttpServletResponse response) throws IOException {
	String username = request.getParameter("username");
	int age = Integer.parseInt(request.getParameter("age"));

	log.info("username = {}, age = {}", username, age);
	response.getWriter().write("OK");
}
```

HTML Form 방식 파라미터 조회를 위해 HTML Form을 만들어야 한다. 프로젝트 경로의 `/resources/static` 하위 경로에 리소스를 두면 Spring boot가 자동으로 인식한다.

`/resources/static/basic` 에 `hello-form.html` 을 만들고, `http://localhost:8080/basic/hello-form.html` 로 요청을 하면 된다.

##### hello-form.html
```HTML
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Title</title>
</head>
<body>
	<form action="/request-param-v1" method="post">
		username: <input type="text" name="username" />
		age: <input type="text" name="age" />
		<button type="submit">전송</button>
	</form>
</body>
</html>
```

>참고
>War가 아닌 Jar를 쓰면 `webapp` 경로를 사용할 수 없다. `/resources/static` 하위의 경로에 정적 리소스를 위치시켜야 한다.

### 2. Spring으로 조회 - @RequestParam
스프링이 제공하는 `@RequestParam` 을 사용하면 요청 파라미터를 매우 편리하게 조회할 수 있다.

##### RequestParamController.java 일부
```Java
@ResponseBody //return 값으로 view를 찾지 않고 응답 메시지 바디에 그대로 넣는다.
@RequestMapping("/request-param-v2")
public String requestParamVer2(@RequestParam String username,
							   @RequestParam int age) {
	log.info("username = {}, age = {}", username, age);
	return "OK";
}
```
- `@RequestParam`
	- `@RequestParam`의 `name(value)` 속성 값을 이름으로 가진 파라미터를 조회한다.
	- `@RequestParam("username") String username` : username 파라미터를 조회
	- 변수명과 파라미터 이름을 일치시키면 축약해서 사용 가능하다.
		- `@RequestParam String username` : username 파라미터를 조회
- `@ResponseBody` : `@Controller` 메소드가 String을 반환하는 경우 기본적으로 반환값의 이름을 가진 View를 조회하는데, `@ResponseBody` 를 붙이면 이름 무시하고 HTTP 응답 메시지의 body에 반환값을 그대로 입력해서 반환한다.

#### @RequestParam 생략
파라미터가 String, int 등의 단순 타입이면 `@RequestParam` 을 생략 가능하다.

##### RequestParamController.java 일부
```Java
@ResponseBody
@RequestMapping("/request-param-v4")
public String requestParamVer4(String username, int age) {
	log.info("username = {}, age = {}", username, age);
	return "OK";
}
```

>**주의**
>`@RequestParam` 애노테이션을 생략하면 스프링 MVC는 내부에서 `required=false` 를 적용한다.


#### 반드시 존재해야 하는 파라미터 조건 - required
특정 요청 파라미터가 반드시 존재해야 한다면 `@RequestParam` 의 `required` 속성을 사용하면 된다.

##### RequestParamController.java 일부
```Java
@ResponseBody
@RequestMapping("/request-param-required")
public String requestParamRequired(
		@RequestParam(required = true) String username,
		@RequestParam(required = false) Integer age) {
	log.info("username = {}, age = {}", username, age);
	return "OK";
}
```
- `required` 의 defualt 값은 true(필수)이다.
- `/request-param` 요청
	- `username` 파라미터가 없기 때문에 상태 코드 400을 반환한다.
- `/request-param?username=` 요청
	- 파라미터는 있지만 값이 없는 경우
	- 파라미터가 존재하기는 한다. 따라서 값이 ""(빈 문자열)인 `username`이 들어온다.


##### 주의: primitive 타입 파라미터에 null이 들어오는 경우
- `/request-param-required/username=kloong` 요청이 들어오는 경우
- `age` 는 `required=false` 이어서 문제가 없다? -> NO!
	- 파라미터가 존재하지 않으면 Spring이 파라미터에 해당하는 변수에 null을 넣는다
	- 하지만 primitive 타입에 null을 입력하는 것은 불가능하다 -> 상태 코드 500 반환
	- 따라서 null을 받을 수 있는 Integer 타입으로 변경하거나, `defaultValue` 속성을 사용해야 한다.
- 참고로 `/request-param-required/username=kloong&age=` 이렇게 값만 입력하지 않으면 상태 코드 500이 아닌 400을 반환한다.
	- 빈 문자열을 int로 파싱할 수 없기 때문에 Spring이 클라이언트가 파라미터를 잘못 보냈다고 판단한 것으로 보인다.

#### 파라미터의 Default value
파라미터가 들어오지 않았거나, 들어왔는데 값이 없는 경우 `@RequestParam` 의 `defaultValue` 속성을 사용해서 default 값을 정할 수 있다.

`defaultValue` 속성을 사용했을 때 파라미터가 들어오지 않으면 설정한 값으로 대치되기 때문에, `required` 속성이 의미 없어지게 된다.

##### RequestParamController.java 일부
```Java
@ResponseBody
@RequestMapping("/request-param-default")
public String requestParamDefault(
		@RequestParam(required = true, defaultValue = "guest") String username,
		@RequestParam(required = false, defaultValue = "-1") int age) {
	log.info("username = {}, age = {}", username, age);
	return "OK";
}
```
- `/request-param-default?username=`
	- 위에서 언급했듯이 `defaultValue` 는 파라미터가 존재하지만 값이 없는 경우에도 default 값으로 대치한다.
	- 따라서 이 요청에 대해 `username` 의 값은 guest 가 된다.

#### 파라미터를 Map/MultiValueMap 으로 한 번에 조회
파라미터를 Map 또는 MultiValueMap으로 한 번에 조회할 수 있다.

##### RequestParamController.java 일부
```Java
@ResponseBody
@RequestMapping("/request-param-Map")
public String requestParamMap(@RequestParam Map<String, Object> paramMap) {
	log.info("username = {}, age = {}",
	paramMap.get("username"), paramMap.get("age"));
	return "OK";
}
```

파라미터의 값이 1개인 것이 보장된다면 Map 을 사용해도 되지만, 그렇지 않다면 MultiValueMap 을 사용하자.