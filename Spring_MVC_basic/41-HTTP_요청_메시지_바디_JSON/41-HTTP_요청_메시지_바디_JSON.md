# HTTP 요청 메시지 바디 - JSON

HTTP API에서 주로 사용하는 JSON 포맷의 데이터를 바디에서 조회하는 방법을 알아보자.


## Servlet 기술 사용
##### RequestBodyJsonController.java 일부
```Java
private ObjectMapper objectMapper = new ObjectMapper();

@PostMapping("/request-body-json-v1")
public void requestBodyJsonVer1(HttpServletRequest request, HttpServletResponse response) throws IOException {

	ServletInputStream inputStream = request.getInputStream();
	String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);

	log.info("messageBody = {}", messageBody);
	HelloData helloData = objectMapper.readValue(messageBody, HelloData.class);
	log.info("helloData = {}", helloData);

	response.getWriter().write("OK");
}
```
- `HttpServletRequest` 의 `getInputStream()` 를 통해 직접 HTTP 메시지 바디에서 데이터를 읽어온다.
- 문자로 된 JSON 데이터를 Jackson 라이브러리가 지원하는 `ObjectMapper` 를 사용해서 자바 객체로 변환한다.


## @RequestBody 사용
##### RequestBodyJsonController.java 일부
```Java
@ResponseBody
@PostMapping("/request-body-json-v2")
public String requestBodyJsonVer2(@RequestBody String messageBody) throws IOException {
	log.info("messageBody = {}", messageBody);
	HelloData helloData = objectMapper.readValue(messageBody, HelloData.class);
	log.info("helloData = {}", helloData);

	return "OK";
}
```
- Ver.1 에서 `@RequestBody` 를 적용한 것 뿐이다. 새로운 내용은 없다.
- String 형태로 받아서 `ObjectMapper` 를 사용하여 자바 객체로 변환하는 과정이 불편하다.


## @RequestBody 사용 - 원하는 타입의 파라미터
`@RequestBody` 와 함께 원하는 타입의 파라미터를 사용하면 `ObjectMapper` 로 객체 변환을 해줄 필요가 없다.
##### RequestBodyJsonController.java 일부
```Java
@ResponseBody
@PostMapping("/request-body-json-v3")
public String requestBodyJsonVer3(@RequestBody HelloData helloData) throws IOException {
	log.info("helloData = {}", helloData);
	return "OK";
}
```

#### @RequestBody 객체 파라미터
- `@RequestBody HelloData helloData` 처럼 편리하게 사용할 수 있다.
- `@RequestBody` 와 직접 만든 객체를 함께 사용하면 된다.
- `HttpEntity<원하는 타입>` , `@RequestBody 타입 변수명` 를 사용하면, **HTTP 메시지 컨버터가 HTTP 메시지 바디의 내용을 우리가 원하는 문자나 객체 등으로 변환해준다.**
- HTTP 메시지 컨버터는 문자열 뿐만 아니라 JSON도 객체로 변환해준다. 즉 Ver2에서 `ObjectMapper` 로 했던 작업을 대신 처리해준다.
	- HTTP 헤더에서 `content-type: application/json` 을 확인
	- `HttpMessageConverter` 가 내부에서 `MappingJackson2HttpMessageConverter` 를 실행시킴
	- 사실상 `ObjectMapper`와 변환 로직은 동일하다.

#### @RequestBody는 생략 불가능!
- `@RequestBody HelloData helloData` 에서 `@RequestBody` 를 생략하면 `@ModelAttribute` 가 적용된다!
- 스프링은 `@ModelAttribute`, `@RequestParam` 과 애노테이션을 생략시 다음과 같은 규칙을 적용한다.
	- String , int , Integer 같은 단순 타입 -> `@RequestParam`
	- 나머지 타입 (argument resolver 로 지정하지 않은 타입)-> `@ModelAttribute`
- 따라서  `@RequestBody` 를 임의의 타입과 함께 사용했을 때, `@ReqeustBody` 를 생략하면 HTTP 메시지 바디가 아니라 요청 파라미터를 처리하게 된다.

>주의
>JSON 데이터를 바디에 담은 HTTP 요청시에 `content-type` 이 `application/json` 인지 꼭 확인해야 한다. 그래야 JSON을 처리할 수 있는 HTTP 메시지 컨버터가 실행된다.


## HttpEntity 사용
##### RequestBodyJsonController.java 일부
```Java
@ResponseBody
@PostMapping("/request-body-json-v4")
public String requestBodyJsonVer4(HttpEntity<HelloData> helloData) throws IOException {
	log.info("helloData = {}", helloData.getBody());
	return "OK";
}
```
- `getBody()` 로 변환된 객체를 꺼내올 수 있다.


## 객체를 JSON으로 자동 변환하여 응답
`@ResponseBody` 와 함께 임의의 타입의 객체를 반환하면, `HttpMessageConvertor` 가 JSON으로 변환해서 메시지 바디에 넣어준다. 물론 이 경우에도 `HttpEntity` 를 사용할 수 있다.

##### RequestBodyJsonController.java 일부
```Java
@ResponseBody
@PostMapping("/request-body-json-v5")
public HelloData requestBodyJsonVer5(@RequestBody HelloData helloData) throws IOException {
	log.info("helloData = {}", helloData);
	return helloData;
}
```

##### RequestBodyJsonController.java 일부
```Java
@ResponseBody
@PostMapping("/request-body-json-v6")
public HttpEntity<HelloData> requestBodyJsonVer6(@RequestBody HelloData helloData) throws IOException {
	log.info("helloData = {}", helloData);
	return new HttpEntity<>(helloData);
}
```

두 경우 다 요청을 보내면 JSON 포맷의 응답 메시지가 온다.