# Spring - Request Mapping

Request mapping은 요청이 왔을 때 호출될 컨트롤러를 mapping하는 것을 말한다. 지금까지는 단순히 URL로만 mapping을 했지만, URL 외에도 다른 여러 요소를 사용해서 mapping이 가능하다.

## URL 매핑
##### MappingController.java
```Java
package com.kloong.springmvc.basic.requestmapping;

import ... //생략

@RestController
public class MappingController {
    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping("/hello-basic")
    public String helloBasic() {
        log.info("helloBasic");
        return "OK";
    }
}
```
- `@RequestMapping("/hello-basic")`
	- `/hello-basic` URL로 요청이 오면 이 메서드가 실행되도록 매핑한다.
	- 대부분의 애노테이션 속성을 배열 형태로 제공하므로 요청 URL을 여러개 매핑할 수 있다.
		- `@RequestMapping({"/hello-basic", "/hello-go"})`
- `@RestController`
	- `@Controller` 는 메소드의 반환 값이 String 이면 뷰 이름으로 인식한다. 그래서 해당 뷰 이름을 가진 뷰를 찾고, 렌더링을 시도한다.
	- 반면에 `@RestController` 는 반환 값으로 뷰를 찾는 것이 아니라, HTTP 메시지 바디에 바로 입력한다. 따라서 응답 메시지로 반환값인 "OK"를 받을 수 있다. 이는 `@ResponseBody` 와 관련이 있는데, 뒤에서 더 자세히 설명한다.
	- Rest는 RESTful 할때 REST를 의미하는 것이다.

#### 참고
- 다음 두 가지 요청 URL은 서로 다르다. 하지만 스프링은 이 요청 URL들을 동일하게 매핑한다.
	- 요청 URL: `/hello-basic` , `/hello-basic/`
	- 매핑: `hello-basic`


## HTTP Method 지정 매핑
`@RequestMapping` 에 `method` 속성으로 HTTP 메서드를 지정하지 않으면 해당 URL의 요청에 대해서는 HTTP 메서드와 무관하게 컨트롤러가 호출된다.

설계에 따라서 동일한 URL에 대한 요청이더라도 HTTP 요청 메서드가 다른 경우 다른 컨트롤러가 호출되어야 할 수 있기 때문에, Spring에서는 HTTP 메서드를 지정해서 매핑할 수 있게 지원한다.

```Java
@RequestMapping(value = "/hello-basic", method = RequestMethod.GET)
```

만약 이렇게 매핑한 후 `/hello-basic` 에 POST 요청을 하면, 스프링 MVC는 HTTP 405 상태 코드 (Method Not Allowed)를 반환한다. Spring이 JSON 형태로 에러 메세지를 만들어서 함께 보내준다.

Spring은 HTTP Method 매핑에 대해서 더 편리한 애노테이션을 제공한다.
```Java
@GetMapping
@PostMapping
@PutMapping
@DeleteMapping
@PatchMapping
...
```

위의 축약형 애노테이션을 사용하는 것이 더 직관적이다. 애노테이션 코드를 보면 내부에서 `@RequestMapping` 의 `method` 를 지정해서 사용하는 것을 확인할 수 있다

##### GetMapping.java 일부
```Java
@RequestMapping(method = RequestMethod.GET)
public @interface GetMapping { }
```


## PathVariable(경로 변수)로 매핑
```Java
/**
 * PathVaraible 사용
 * 요청이 /mapping/kloong 이런 식으로 오는 경우(요청 URL 자체에 값이 있는 느낌)
 * 변수명이 URL의 {} 안에 있는 내용과 동일하면 다음과 같이 축약 가능
 * @PathVariable("userId") String userId -> @PathVariable String userId
 */
@GetMapping("/mapping/{userId}")
public String mappingPath(@PathVariable("userId") String data) {
	log.info("mappingPath userId={}", data);
	return "OK";
}
```

- 최근 HTTP API는 리소스 경로에 식별자를 넣는 스타일, 즉 PathVariable 방식을 선호한다.
	- `/mapping/userA`, `/users/132`
- `@RequestMapping` 은 URL 경로를 템플릿화 (위 코드에서 `/{userId}` 같은 것을 말하는 것임) 할 수 있다.
- 이 때 `@PathVariable` 을 사용하면, 템플릿화 한 부분과 매칭되는 부분을 편리하게 조회할 수 있다.
- URL 경로에 `{userId}` 같은 변수가 존재해서 PathVariable 인 듯 하다.
- `@PathVariable` 의 이름과 매개변수 이름이 같으면 축약해서 쓸 수 있다.
	- `@PathVariable("userId") String userId` -> `@PathVariable String userId`

### PathVariable 다중 매핑
```Java
//PathVariable 다중 매핑
@GetMapping("/mapping/users/{userId}/orders/{orderId}")
public String mappingPath2(@PathVariable String userId,
						   @PathVariable Long orderId) {
	log.info("mappingPath2 userId={}, orderId={}", userId, orderId);
	return "OK";
}
```


## 특정 쿼리 파라미터 조건 매핑
```Java
/**
* 파라미터로 추가 매핑
* params="mode",
* params="!mode"
* params="mode=debug"
* params="mode!=debug" (! = )
* params = {"mode=debug","data=good"}
*/
@GetMapping(value = "/mapping-param", params = "mode=debug")
public String mappingParam() {
	log.info("mappingParam");
	return "OK";
}
```
- 특정 파라미터의 존재 여부를 조건으로 매핑할 수 있다.
- 위 메서드는 `/mapping-param?mode=debug` 요청에 매핑한 것이다. 파라미터 이름과 값이 모두 조건과 맞아야 한다.
- 파라미터의 이름, 값 등 다양하게 조건을 줄 수 있다.
- 실제로 잘 쓰이진 않는다.


## 특정 HTTP 헤더 조건 매핑
```Java
/**
* 특정 헤더로 추가 매핑
* headers="mode",
* headers="!mode"
* headers="mode=debug"
* headers="mode!=debug" (! = )
*/
@GetMapping(value = "/mapping-header", headers = "mode=debug")
public String mappingHeader() {
	log.info("mappingHeader");
	return "OK";
}
```
- 쿼리 파라미터 매핑과 비슷하지만, 파라미터 대신 HTTP 헤더를 사용한다.


## Consume 조건 매핑 - Content-Type 헤더
```Java
/**
* Content-Type 헤더 기반 추가 매핑 Media Type
* consumes="application/json"
* consumes="!application/json"
* consumes="application/*"
* consumes="*\/*"
* MediaType.APPLICATION_JSON_VALUE
*/
@PostMapping(value = "/mapping-consume", consumes = "application/json")
public String mappingConsumes() {
	log.info("mappingConsumes");
	return "OK";
}
```
- HTTP 요청의 `Content-Type` 헤더의 값, 즉 미디어 타입으로 매핑이 가능하다.
- HTTP 헤더 매핑에서 `@PostMapping(value = "/mapping-header", headers = "Content-Type=application/json")` 하는 것과 다르다.
	- Spring에서 내부적으로 처리하는 것이 있기 때문에, `Content-Type` 헤더를 조건으로 매핑하려면 반드시 `consume` 을 쓰자.
- 만약 `Content-Type` 헤더의 값이 조건과 맞지 않으면 HTTP 415 상태 코드(Unsupported Media Type)를 반환한다.


## Produce 조건 매핑 - Accept 헤더
```Java
/**
* Accept 헤더 기반 Media Type
* produces = "text/html"
* produces = "!text/html"
* produces = "text/*"
* produces = "*\/*"
*/
@PostMapping(value = "/mapping-produce", produces = "text/html")
public String mappingProduces() {
	log.info("mappingProduces");
	return "OK";
}
```
- `consume` 처럼 미디어 타입에 대한 조건이지만 주체가 다르다.
	- `produce` 는 클라이언트가 받기를 원하는 미디어 타입, 즉 `Accept` 헤더에 대한 조건이다.
	- `consume` 은 서버가 받기를 원하는 미디어 타입에 대한 조건이었다.
- 만약 `Accept` 헤더의 값이 조건과 맞지 않으면 HTTP 406 상태 코드(Not Acceptable)를 반환한다.