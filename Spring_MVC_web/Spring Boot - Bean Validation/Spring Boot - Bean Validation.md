# Spring Boot - Bean Validation

## Bean Validation이란?
Bean validation은 특정한 구현체가 아니라 **Bean Validation 2.0(JSR-380)**이라는 기술 표준이다.

쉽게 이야기해서 검증 애노테이션과 여러 인터페이스의 모음이다. 마치 JPA가 표준 기술이고 그 구현체로 하이버네이트가 있는 것과 같다.

일반적으로 하이버네이트 Validator 구현체를 사용한다.

>참고: 하이버네이트 Validator 관련 링크
>공식 사이트: http://hibernate.org/validator/
>도큐먼트에서 검증 어노테이션을 찾아서 사용하면 된다. 왠만한 검증 로직은 다 이미 구현되어있다.

특정 필드에 대한 검증 로직들을 잘 살펴보면 매우 일반적인 로직이 대부분이다. 빈 값인지 체크하거나, 길이 혹은 범위를 체크하거나 하는 등의 로직이 반복적으로 수행된다.

Bean validation을 사용하면 이런 반복적이고 일반적인 검증 로직을 어노테이션 하나로 처리할 수 있다.

**Bean validation 적용 예시**
```Java
public class Item {

	private Long id;
	
	@NotBlank
	private String itemName;
	
	@NotNull
	@Range(min = 1000, max = 1000000)
	private Integer price;
	
	@NotNull
	@Max(9999)
	private Integer quantity;
	
	//...
}
```

원래라면 따로 validator를 구현해서 각 필드의 값을 직접 확인하는 코드를 구현했어야 했는데, bean validation을 적용한 순간 간단한 필드 검증 로직을 어노테이션 몇 개로 처리할 수 있게 되었다.


## Bean Validation 의존관계 추가 & 스프링 통합

**build.gradle**
```
implementation 'org.springframework.boot:spring-boot-starter-validation'
```

위 의존관계를 추가하면 다음 라이브러리가 추가된다.

- **Jakarta Bean Validation**
	- `jakarta.validation-api` : Bean Validation 인터페이스
	- `hibernate-validator` : 구현체

>참고
>검증 어노테이션을 사용하다 보면 `javax.validation` 패키지에 있는 어노테이션이 있고, `org.hibernate.validator` 에 있는 어노테이션도 있다.
>`javax.validation` 은 구현체에 관계없이 제공되는 표준 인터페이스이고, `org.hibernate.validator` 로 시작하면 하이버네이트 validator 구현체를 사용할 때만 제공되는 검증 기능이다. 실무에서 대부분 하이버네이트 validator를 사용하므로 자유롭게 사용해도 된다.

`spring-boot-starter-validation` 의존관계를 추가하면 스프링 부트가 자동으로 bean validator를 인지하고 스프링에 통합한다.

스프링 부트는 bean validator를 자동으로 글로벌 validator로 등록한다. 이렇게 글로벌 validator가 적용되어 있기 때문에 검증이 필요한 객체에 `@Valid` , `@Validated` 만 적용하면 된다.

검증 오류가 발생하면 `FieldError` , `ObjectError` 를 생성해서 `BindingResult` 에 담아준다.


## Bean Validator 검증 과정

#### 검증 순서
1. `@Validated @ModelAttribute` 가 적용된 객체의 각각의 필드에 타입 변환 시도
	1. 성공하면 다음으로
	2. 실패하면 `typeMismatch` 에러 코드로 `FieldError` 추가
2. 타입 변환에 성공한 필드에 bean validator 적용

**바인딩에 성공한 필드에 대해서만 Bean validation이 적용된다.** 생각해보면 그냥 당연한 소리다.


## Bean Validation 에러 코드 & 에러 메시지
Bean Validation이 기본으로 제공하는 오류 메시지 말고 직접 작성한 오류 메시지를 사용할 수 있다.

Bean Validation을 적용하고 검증 오류가 발생하는 입력을 서버에 보낸 뒤 `BindingResult` 에 등록된 검증 오류를 살펴보면 다음과 같은 메시지 코드를 확인할 수 있다.

- **`@NotBlank`**
	- `NotBlank.item.itemName`
	- `NotBlank.itemName`
	- `NotBlank.java.lang.String`
	- `NotBlank`

즉 Bean validator가 검증 오류가 발생하면 검증 어노테이션 이름 (여기서는 `NotBlank`) 을 에러 코드로 사용해서 `FieldError` 를 생성하고 `BindingResult` 에 저장하는 것이다.

이 과정에서 `MessageCodesResolver` 가 내부적으로 `NotBlank` 라는 에러 코드를 기반으로 위의 4개의 메시지 코드를 만들어낸 것이다.

따라서 검증 어노테이션과 검증 대상 객체, 필드 이름을 조합해서 에러 메시지를 등록하면 에러 메시지를 커스터마이징 할 수 있다.

##### BeanValidation 메시지 찾는 순서
1. 생성된 메시지 코드 순서대로 메시지 소스에서 메시지 찾기
2. 애노테이션의 `message` 속성 사용 `@NotBlank(message = "공백! {0}")`
3. 라이브러리가 제공하는 default 에러 메시지 사용

#### 에러 메시지 매개변수
Bean validation 에러 메시지에서도 매개변수를 사용할 수 있다.

**errors.properties**
```
NotBlank={0} 공백X
Range={0}, {2} ~ {1} 허용
Max={0}, 최대 {1}
```

`{0}` 은 필드명이고, `{1}` , `{2}` ... 는 각 애노테이션 마다 다르다. 도큐먼트를 확인해보자.


## Bean Validation 오브젝트 오류
Bean Validation에서 특정 필드( `FieldError` )가 아닌 해당 오브젝트 관련 오류( `ObjectError` )를 처리하려면 `@ScriptAssert()` 를 사용하면 된다.

하지만 실무에서는 검증 기능이 해당 객체의 범위를 넘어서는 경우들도 종종 등장하는데 (DB에 접근한다던지 등), 이런 경우 `@ScriptAssert()` 를 사용하기 어렵다.

따라서 오브젝트 오류의 경우 검증 코드를 직접 작성하는 것을 권장한다.


## Bean Validation 적용 대상 설정 - Groups

```Java
public class Item {

	@NotNull
	private Long id;
	
	@NotBlank
	private String itemName;
	
	@NotNull
	@Range(min = 1000, max = 1000000)
	private Integer price;
	
	@NotNull
	@Max(9999)
	private Integer quantity;
	
	//...
}
```

컨트롤러에서 `@Validated @ModelAttribute Item` 객체에 대해서 위와 같은 검증 어노테이션으로 bean validation을 한다고 하자.

그런데 만약 사용자로부터 새로운 `Item` 정보를 입력 받아서 저장할 때는 `id` 필드를 비어있는 채로 넘겨 받아서 서버에서 `id` 값을 부여하고, 수정할 때는 반드시 기존의 `id` 값을 넘겨 받아야 하는 ( `@NotNull` 을 적용해야 하는) 상황이라면 어떻게 해야 할까?

```Java
@PostMapping("/add")
public String addItem(@Validated @ModelAttribute Item item, 생략) {
	//생략
}

@PostMapping("/{itemId}/edit")
public String edit(@PathVariable Long itemId,
				   @Validated @ModelAttribute Item item, 생략) {
	//생략
}
```

Bean validation을 이용하기 때문에 저장과 수정 컨트롤러 메소드에서 `@Validated` 를 동일하게 적용했다.

문제는 두 경우의 검증 로직이 다른 상황이라는 것이다.

**이런 상황에서 bean validation의 Groups 기능을 사용할 수 있다.**

Groups 기능을 적용하기 위해 저장과 수정에 대한 더미 클래스 (인터페이스) 두 개를 만든다.

```Java
package hello.itemservice.domain.item;
public interface SaveCheck {}
```

```Java
package hello.itemservice.domain.item;
public interface EditCheck {}
```

그리고 더미 클래스를 사용해서 `Item` 클래스의 검증 어노테이션에 `groups` 속성을 적용한다.

```Java
@Data
public class Item {
	@NotNull(groups = EditCheck.class) //수정시에만 적용
	private Long id;
	
	@NotBlank(groups = {SaveCheck.class, EditCheck.class})
	private String itemName;
	
	@NotNull(groups = {SaveCheck.class, EditCheck.class})
	@Range(min = 1000, max = 1000000,
			groups = {SaveCheck.class, EditCheck.class})
	private Integer price;
	
	@NotNull(groups = {SaveCheck.class, EditCheck.class})
	@Max(value = 9999, groups = SaveCheck.class) //등록시에만 적용
	private Integer quantity;
}
```

그리고 컨트롤러 메소드의 `@Validated` 어노테이션을 다음과 같이 수정한다.

```Java
@PostMapping("/add")
public String addItem(@Validated(SaveCheck.class) @ModelAttribute Item item,
													  생략) {
	//생략
}

@PostMapping("/{itemId}/edit")
public String edit(@PathVariable Long itemId,
				   @Validated(EditCheck.class) @ModelAttribute Item item, 생략) {
	//생략
}
```

이렇게 하면 `Item` 의 검증 어노테이션의 `groups` 속성값인 더미 클래스를 `value` 로 가지는 `@Validated` 어노테이션에만 해당 검증 로직이 적용된다.

간단히 말하면 `id` 에 적용된 `@NotNull` 검증 로직은 수정 컨트롤러 메소드의 `Item` 객체에만 적용된다는 것이다. 나머지 필드와 검증 어노테이션에도 동일한 방식으로 `groups` 기능이 적용된다.


## 도메인 객체와 DTO 객체 분리
실무에서는 bean validation의 groups 기능을 잘 사용하지 않는다. 왜냐하면 사용자가 전달하는 데이터가 실제 도메인 객체의 필드와 일치하지 않는 경우가 대부분이기 때문이다.

그래서 보통 사용자 입력을 도메인 객체에 직접 바인딩하는 것이 아니라, 별도의 DTO 객체에 바인딩해서 컨트롤러에 전달한다.

## Bean Validation 과 `@RequestBody`

`@Valid` , `@Validated` 는 `@ModelAttribute` 뿐만 아니라 `@RequestBody` 로 전달받는 객체에도 적용할 수 있다.

>참고: `@ModelAttribute` 와 `@RequestBody`
>`@ModelAttribute` 는 HTTP 요청 파라미터(URL 쿼리 스트링, HTML Form)를 다룰 때 사용한다. `@RequestBody` 는 HTTP Body의 데이터를 객체로 변환할 때 사용한다. 주로 API JSON 요청을 다룰 때 사용한다.

```Java
@PostMapping("/add")
public Object addItem(@Validated @RequestBody ItemSaveForm form,
						BindingResult bindingResult) {
	//생략
}
```

**HTTP Request 메시지 바디 예시 - 성공**
```
POST http://localhost:8080/validation/api/items/add
{"itemName":"hello", "price":1000, "quantity": 10}
```

JSON 형태의 HTTP 메시지 바디가 `HttpMessageConverter` 에 의해 `ItemSaveForm` 객체에 바인딩 되고, `@Validated` 에 의해 bean validator가 동작해서 검증 로직을 수행한다.

JSON 형태의 API 요청의 경우 다음과 같이 3가지 경우를 구분해서 생각해야 한다.
- 올바른 JSON 형태의 데이터: 객체 바인딩 성공. 검증 로직 수행.
- 올바르지 않은 JSON 형태의 데이터: JSON을 객체로 바인딩 하는 것 자체가 실패. 검증 로직 수행하지 못하고 예외 발생.
- 검증 오류를 유발하는 데이터: JSON을 객체로 바인딩하는 것은 성공했고, 검증에서 실패함.

다음과 같은 요청은 타입에 의한 변환 문제 때문에 `HttpMessageConverter` 가 객체 변환에 실패하고, 검증 로직이 아예 수행되지 않고 예외가 터진다.

**HTTP Request 메시지 바디 예시 - 객체 변환 실패**
```
POST http://localhost:8080/validation/api/items/add
{"itemName":"hello", "price":"A", "quantity": 10}
```

`@ModelAttribute` 는 필드 단위로 정교하게 바인딩이 적용된다. 특정 필드가 바인딩 되지 않아도 나머지 필드는 정상 바인딩 되고, bean validator를 사용한 검증도 적용할 수 있다.

`@RequestBody` 는 `HttpMessageConverter` 가 JSON 데이터를 객체로 변경하는데 실패하면 이후
단계 자체가 진행되지 않고 **예외가 발생한다.** 컨트롤러도 호출되지 않고, bean validator도 적용할 수 없다.