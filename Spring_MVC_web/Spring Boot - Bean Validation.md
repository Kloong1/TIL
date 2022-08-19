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

`spring-boot-starter-validation` 의존관계를 추가하면 스프링 부트가 자동으로 Bean validator를 인지하고 스프링에 통합한다.

스프링 부트는 Bean validator를 자동으로 글로벌 validator로 등록한다. 이렇게 글로벌 validator가 적용되어 있기 때문에 검증이 필요한 객체에 `@Valid` , `@Validated` 만 적용하면 된다.

검증 오류가 발생하면 `FieldError` , `ObjectError` 를 생성해서 `BindingResult` 에 담아준다.


## Bean Validator 검증 과정

#### 검증 순서
1. `@Validated @ModelAttribute` 가 적용된 객체의 각각의 필드에 타입 변환 시도
	1. 성공하면 다음으로
	2. 실패하면 `typeMismatch` 에러 코드로 `FieldError` 추가
2. 타입 변환에 성공한 필드에 bean validator 적용

**바인딩에 성공한 필드에 대해서만 Bean validation이 적용된다.** 생각해보면 그냥 당연한 소리다.


## Bean Validation 에러 코드 & 에러 메시지
Bean Validation 이 기본으로 제공하는 오류 메시지 말고 직접 작성한 오류 메시지를 사용할 수 있다.

Bean Validation을 적용하고 검증 오류가 발생하는 입력을 서버에 보낸 뒤 BindingResult 에 등록된 검증 오류를 살펴보면 다음과 같은 메시지 코드를 확인할 수 있다.

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


