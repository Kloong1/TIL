# HttpMessageConverter

HTTP API의 경우처럼 JSON 데이터를 HTTP 메시지 바디에서 직접 읽거나 쓰는 경우 HTTP 메시지 컨버터를 사용하면 편리하다.

##### @ResponseBody 간단 동작 원리
![](스크린샷%202022-06-03%20오후%2010.25.36.png)
- `@ResponseBody` 를 적용
	- HTTP 메시지 바디에 문자 내용을 직접 반환한다
	- 뷰 템플릿을 사용하지 않기 때문에 `viewResolver` 대신에 `HttpMessageConverter` 가 동작한다.
	- 기본 문자열 처리 클래스: `StringHttpMessageConverter`
	- 기본 객체 처리 클래스: `MappingJackson2HttpMessageConverter`
	- 이외에도 byte 처리 등 기능에 맞춰서 여러 종류의 `HttpMessageConverter`가 기본으로 등록되어 있다.

> 참고
> HTTP 응답의 경우 클라이언트의 HTTP Accept 해더와 서버의 컨트롤러 반환 타입 정보 둘을 조합해서 `HttpMessageConverter` 가 선택된다.


#### Spring MVC는 다음의 경우에 HttpMessageConverter를 적용한다
- HTTP 요청: `@RequestBody` 또는 `HttpEntity`, `RequestEntity` 를 사용하는 경우
- HTTP 응답: `@ResponseBody` 또는  `HttpEntity`, `ResponseEntity`  를 사용하는 경우


## HttpMessageConverter 인터페이스
HTTP 메시지 컨버터에는 `StringHttpMessageConverter`, `MappingJackson2HttpMessageConverter` 등 기능에 따른 다양한 구현체가 있다.

이 구현체들의 부모가 되는 `HttpMessageConverter` 인터페이스에 대해 알아보자.

##### org.springframework.http.converter.HttpMessageConverter
```Java
package org.springframework.http.converter;

public interface HttpMessageConverter<T> {
	boolean canRead(Class<?> clazz, @Nullable MediaType mediaType);
	boolean canWrite(Class<?> clazz, @Nullable MediaType mediaType);
	
	List<MediaType> getSupportedMediaTypes();
	
	T read(Class<? extends T> clazz, HttpInputMessage inputMessage)
		throws IOException, HttpMessageNotReadableException;
	
	void write(T t, @Nullable MediaType contentType,
	HttpOutputMessage outputMessage)
	throws IOException, HttpMessageNotWritableException;
}
```
- HTTP 메시지 컨버터는 HTTP 요청, HTTP 응답 두 경우 모두에 사용된다.
- 즉 양방향이다. 읽어서 변환해주거나, 변환해서 써주는 작업 모두를 수행한다.
- `canRead()` , `canWrite()`
	-  메시지 컨버터가 해당 클래스, 미디어 타입을 지원하는지 체크하는 기능
- `read()` , `write()`
	-  메시지 컨버터를 통해서 메시지를 읽고 쓰는 기능


## Spring Boot 기본 등록 메시지 컨버터
```
0 = ByteArrayHttpMessageConverter
1 = StringHttpMessageConverter
2 = MappingJackson2HttpMessageConverter
... (일부 생략)
```
- Spring Boot는 다양한 메시지 컨버터를 기본으로 제공한다.
- 대상 클래스 타입과 미디어 타입 둘을 체크해서 어떤 메시지 컨버터를 사용할지 결정한다.
	- 미디어 타입
		- HTTP 요청의 경우: `Content-Type` 헤더
		- HTTP 응답의 경우: `Accept` 헤더
- 만약 만족하지 않으면 다음 메시지 컨버터로 우선순위가 넘어간다.

#### 주요 메시지 컨버터
- `ByteArrayHttpMessageConverter`
	- `byte[]` 데이터를 처리한다
	- 클래스 타입: `byte[]`
	- 미디어 타입: `*/*`
	- 요청 예시
		- `@RequestBody byte[] data`
		- 사실상 HTTP 메시지 바디를 아무런 변환 없이 그대로 읽어오는 것이다. 그렇기 때문에 모든 미디어 타입에서 사용 가능하다.
	- 응답 예시
		- `@ResponseBody` 
		- `return byte[]`
		- `Content-Type: application/octet-stream`
- `StringHttpMessageConverter` 
	- `String` 으로 데이터를 처리한다.
	- 클래스 타입: `String`
	- 미디어 타입: `*/*`
	- 요청 예시
		- `@RequestBody String data`
		- `byte[]` 형태의 HTTP 메시지 바디를 String으로 변환하는 것이다. 따라서 모든 미디어 타입에서 사용 가능하다.
	- 응답 예시
		- `@ResponseBody`
		- `return "hello"`
		- `Content-Type: text/plain`
- `MappingJackson2HttpMessageConverter`
	- JSON 타입 데이터를 처리한다.
	- `Content-Type: application/json`
	- 클래스 타입: 객체 또는 `HashMap` 
	- 미디어 타입: `application/json` 관련 (이거 말고도 다른 헤더 값이 존재하기 때문에 "관련"임)
	- 요청 예시
		- `@RequestBody HelloData data`
	- 응답 예시
		- `@ResponseBody`
		- `return new HelloData()`
		- `Content-Type: application/json` 관련


## HTTP 요청 데이터 읽기 예시
- HTTP 요청이 왔는데, 컨트롤러에서 `@RequestBody` 또는 `HttpEntity` 파라미터를 사용한다.
-  `HttpMessageConverter` 가 동작한다.
- 기본 등록된 `HttpMessageConverter`의 구현체를 우선순위대로 탐색하면서, 요청 메시지를 읽을 수 있는지 확인하기 위해 `canRead()` 를 호출한다. 읽을 수 있으면 해당 메시지 컨버터를 사용하고, 아니면 다음 우선순위의 메시지 컨버터를 확인한다.
	- 대상 클래스 타입을 지원하는가?
		- 예) `@RequestBody` 파라미터의 대상 클래스 확인 (`byte[]`, `String`, `HelloData` 등)
	- HTTP 요청 데이터의 미디어 타입을 지원하는가?
		- `Content-Type` 헤더 확인 (`text/plain`, `application/json`, `*/*` 등)
	- `canRead()` 조건을 만족하면 `read()` 를 호출해서 객체를 생성해서 반환한다 (컨트롤러의 파라미터로 넘겨준다).


## HTTP 응답 데이터 쓰기 예시
- 컨트롤러에서 `@ResponseBody` 를 사용하거나, `HttpEntity` 타입으로 반환한다.
-  `HttpMessageConverter` 가 동작한다.
- 기본 등록된 `HttpMessageConverter`의 구현체를 우선순위대로 탐색하면서, 컨트롤러가 반환하는 타입을 HTTP 응답 메시지 바디에 write 할 수 있는지 확인하기 위해 `canWrite()` 를 호출한다. 쓸 수 있으면 해당 메시지 컨버터를 사용하고, 아니면 다음 우선순위의 메시지 컨버터를 확인한다.
	- 대상 클래스 타입을 지원하는가?
		- `@ResponseBody`를 사용하는 경우 컨트롤러의 반환형 확인 (`byte[]`, `String`, `HelloData` 등)
		- `HttpEntity` 를 사용하는 경우 제네릭 변수 확인 (`HttpEntity<HelloData>`, `HttpEntity<String>` 등)
	- 미디어 타입 확인: HTTP 요청의 `Accept` 를 지원하는가?
		- 만약 `@RequestMapping`의 `produces` 속성이 있으면 해당 속성에 맞춰야한다.
		- `Accept` 헤더 확인 (`text/plain`, `application/json`, `*/*` 등)
	- `canWrite()` 조건을 만족하면 `write()` 를 호출해서 HTTP 응답 메시지 바디에 데이터를 입력한다.


## 예제
### 1번 요청
```
content-type: application/json
@RequestMapping
void hello(@RequetsBody String data) {}
```
1. HTTP 요청이 왔는데, 컨트롤러에서 `@RequestBody` 또는 `HttpEntity` 파라미터를 사용한다.
2. 기본 등록된 `HttpMessageConverter`의 구현체를 우선순위대로 탐색하면서, 요청 메시지를 읽을 수 있는지 확인하기 위해 `canRead()` 를 호출한다.
3. 우선 순위가 가장 높은 `ByteArrayHttpMessageConverter` 가 처리 가능한지 확인한다.
4. 대상 클래스가 `String` 이므로 넘어간다.
5. 다음 우선순위인 `StringHttpMessageConverter` 가 처리 가능한지 확인한다.
6. 대상 클래스가 `String`이고, `StringHttpMessageConverter` 는 `*/*` 미디어 타입에 대해 처리 가능하므로 `StringHttpMessageConverter` 가 선택된다.

### 2번 요청
```
content-type: application/json
@RequestMapping
void hello(@RequetsBody HelloData data) {}
```
1. HTTP 요청이 왔는데, 컨트롤러에서 `@RequestBody` 또는 `HttpEntity` 파라미터를 사용한다.
2. 기본 등록된 `HttpMessageConverter`의 구현체를 우선순위대로 탐색하면서, 요청 메시지를 읽을 수 있는지 확인하기 위해 `canRead()` 를 호출한다.
3. 우선 순위가 가장 높은 `ByteArrayHttpMessageConverter` 가 처리 가능한지 확인한다.
4. 대상 클래스가 `HelloData` 이므로 넘어간다.
5. 다음 우선순위인 `StringHttpMessageConverter` 가 처리 가능한지 확인한다. 마찬가지로 대상 클래스가 `HelloData` 이므로 넘어간다.
6. 다음 우선순위인 `MappingJackson2HttpMessageConverter` 가 처리 가능한지 확인한다.
7. 대상 클래스가 `HelloData` 이니까 처리 가능하고, `application/json` 미디어 타입도 처리 가능하므로 선택된다.

### 3번 요청 - 불가능한 케이스
```
content-type: text/html
@RequestMapping
void hello(@RequetsBody HelloData data) {}
```
- 우선순위에 따라 `MappingJackson2HttpMessageConverter` 까지 확인하는데, 미디어 타입이 `text/html` 이므로 처리 불가능하다.