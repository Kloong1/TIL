# HTTP 요청 메시지 바디 - Plain Text

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

이 중에서 3번째, "HTTP message body에 데이터를 직접 담아서 요청" 하는 경우 데이터를 어떻게 조회할 수 있는지 알아보자.

요청 파라미터와 다르게, HTTP 메시지 바디를 통해 데이터가 직접 넘어오는 경우는 `@RequestParam`, `@ModelAttribute` 를 사용할 수 없다 (물론 HTML Form 형식으로 전달되는 경우 메시지 바디에 데이터가 들어있긴 하지만, 이 경우는 쿼리 파라미터와 동일하게 조회가 가능하다).

이럴 때는 다음과 같은 방법들로 메시지 바디에 담긴 데이터를 조회할 수 있다. 여기서는 데이터 포맷이 단순 텍스트인 경우만 알아볼 것이다.

## Servlet 기술 사용 - HttpServletRequest
##### RequestBodyStringController.java 일부
```Java
@PostMapping("/request-body-string-v1")
public void requestBodyStringVer1(HttpServletRequest request,
								  HttpServletResponse response)
								  throws IOException {
	ServletInputStream inputStream = request.getInputStream();
	String message = StreamUtils.copyToString(inputStream,
											StandardCharsets.UTF_8);

	log.info("message = {}", message);
	response.getWriter().write("OK");
}
```
- `HttpServletRequest` 의 `getInputStream()` 메소드로 메시지 바디를 읽어왔다.
- 실제 로직에는 Spring없이 Servlet만 쓴 것과 동일하다.
- 코드도 길고, 메시지 바디만 읽고 싶은 건데 `HttpServletRequest` 객체를 통채로 받을 필요도 없어 보인다.


## InputStream, OutputStream 매개변수로 받아서 사용
##### RequestBodyStringController.java 일부
```Java
@PostMapping("/request-body-string-v2")
public void requestBodyStringVer2(InputStream inputStream,
								  Writer responseWriter) throws IOException {
	String message =
	StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
	log.info("message = {}", message);
	responseWriter.write("OK");
}
```
- 메시지 바디만 읽으면 되기 때문에 `HttpServletRequest`, `HttpServletResponse` 객체를 통채로 받을 필요가 없다.
- Spring이 지원하는 `@Controller` 파라미터에 `InputStream` / `Reader`, `OutputStream` / `Writer` 가 있기 때문에 위 코드처럼 매개변수로 넘겨 받을 수 있다.


## HttpEntity 사용
##### RequestBodyStringController.java 일부
```Java
@PostMapping("/request-body-string-v3")
public HttpEntity<String> requestBodyStringVer3(HttpEntity<String> httpEntity)
throws IOException {
	log.info("message = {}", httpEntity.getBody());
	return new HttpEntity<>("OK");
}
```
- `@Controller`는 `HttpEntity` 파라미터를 지원한다.
- `HttpEntity`
	- HTTP 헤더와 바디 정보를 편리하게 조회할 수 있다.
		- `getHeader()`, `getBody()` 등...
	- 요청 파라미터를 조회하는 기능(`@ModelAttribute`, `@RequestParam`) 과는 전혀 관계가 없다.
		- 쿼리 파라미터, HTML Form 으로 넘어오는 경우를 제외하면 전부 바디에 있는 데이터를 직접 꺼내야 한다.
	- HTTP 응답 메시지를 작성하는 데도 사용 가능하다.
		- 메시지 바디 정보 입력 가능
		- 헤더 정보 입력 가능
		- View를 조회하는데 쓰이지는 않는다.

#### RequestEntity, ResponseEntity - HttpEntity를 상속
`HttpEntity` 를 상속받은 `RequestEntity`, `ResponseEntity` 로도 동일한 기능을 수행할 수 있다.

```Java
@PostMapping("/request-body-string-v0")
public HttpEntity<String> requestBodyStringVer0(
RequestEntity<String> requestEntity) throws IOException {
	log.info("message = {}", requestEntity.getBody());
	return new ResponseEntity<>("OK", HttpStatus.CREATED);
}
```
- `RequestEntity`
	- 요청에서 사용
	- HTTP Method, url 정보 추가
- `ResponseEntity`
	- 응답에서 사용
	- HTTP 상태 코드 설정 가능

>참고
>스프링MVC 내부에서 HTTP 메시지 바디를 읽어서 문자나 객체로 변환해서 전달해주는데, 이때  `HttpMessageConverter` 를 사용한다. 이후 강의에서 자세히 설명한다.


## @RequestBody 사용
요청 메시지에 대한 다른 정보는 필요 없고 오로지 메시지 바디만 필요하다면 `@RequestBody` 를 사용하면 된다. 이름에서 알 수 있듯이 `@ResponseBody` 의 Request 버전이다.

##### RequestBodyStringController.java 일부
```Java
@ResponseBody
@PostMapping("/request-body-string-v4")
public String requestBodyStringVer4(@RequestBody String messageBody) throws IOException {
	log.info("message = {}", messageBody);
	return "OK";
}
```
- `@RequestBody` 를 사용하면 HTTP 메시지 바디 정보를 편리하게 조회할 수 있다.
- 헤더 정보가 필요하다면 `HttpEntity` 를 사용하거나 `@RequestHeader` 를 사용하면 된다.
- 실무에서 자주 사용한다.


## 정리
- 요청 파라미터 조회
	- `@RequestParam` , `@ModelAttribute`
- HTTP 메시지 바디 조회
	- `@RequestBody`