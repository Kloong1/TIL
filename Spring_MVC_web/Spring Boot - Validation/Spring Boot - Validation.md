# Spring Boot - Validation

## 서론
웹 앱에서 사용자가 HTML Form 등에 정보를 입력해서 서버에 전송을 하는 경우, 사용자가 입력한 정보에 문제가 있는지 서버에서 검증 절차를 거쳐야 한다.

그런데 만약 검증 결과 사용자가 입력한 정보에 문제가 있다고 해서 바로 오류 화면을 띄운다거나 하면, 사용자는 입력한 정보를 처음부터 다시 작성해야 한다.

좋은 웹 앱은 사용자 입력 시 오류가 발생하면, 사용자가 입력한 데이터를 유지한 상태로 어떤 오류가 발생했는지 친절하게 알려주어야 한다.

컨트롤러의 중요한 역할중 하나는 HTTP 요청이 정상인지 검증하는 것이다.

>참고: 클라이언트 검증 & 서버 검증
>클라이언트 검증은 조작이 가능하므로 보안에 취약하다 (JS를 활용한 클라이언트 검증은 Postman 등을 활용하여 쉽게 데이터 조작이 가능하다). 그렇다고 해서 서버에서만 검증 절차를 거치면, 즉각적인 고객 사용성이 떨어진다.
>따라서 클라이언트 검증을 적용하고, 최종적으로 서버 검증을 반드시 거쳐야 한다.

##### 검증 실패 상황 예시
![](스크린샷%202022-08-18%20오후%205.12.16.png)

서버에서 검증을 한 결과 사용자의 입력 정보에 문제가 있는 것이 확인된 경우, 사용자가 입력한 정보(컨트롤러에 넘긴 정보)를 그대로 유지한 채로 입력 화면으로 돌아가 어느 부분에서 검증 오류가 발생했는지 보여줘야 한다.

## BindingResult
스프링에서 제공하는 여러 기능을 사용하지 않고 검증을 하려고 하면 다양한 문제에 부딪히게 된다.

```Java
@PostMapping("/add")
public String addItem(@ModelAttribute Item item,
					  RedirectAttributes redirectAttributes, Model model) {
	//생략					  
}
```

`@ModelAttribute` 로 `Item` 객체를 넘겨 받는 컨트롤러 메소드가 있다.

만약 `Item` 의 `Interger` 멤버 변수인 `price` 를 입력 받는다고 할 때, 사용자가 `백만원` 같이 숫자가 아닌 값을 입력했다고 해보자.

`백만원` 은 일반적인 방법으로는 숫자로 변환이 불가능하므로, 스프링은 `@ModelAttribute Item item` 에 해당 값을 바인딩 시키지 못해 예외가 발생하고, 결국 400 오류가 발생한다.

이 때 `ArgumentResolver` 가 바인딩 자체에 실패했으므로 컨트롤러 메소드는 호출조차 되지 못한다.

하지만 다음과 같이 스프링에서 제공하는 `BindingResult` 를 사용하면 얘기가 달라진다.

```Java
@PostMapping("/add")
public String addItem(@ModelAttribute Item item, BindingResult result
					  RedirectAttributes redirectAttributes, Model model) {
	//생략					  
}
```

 `@ModelAttribute Item item` 에 바인딩 시키지 못한 `백만원` 이라는 값을 `BindingResult` 에 저장한다. 이 때 문제가 생긴 객체와 필드에 대한 정보를 함께 저장한다. ( 여기서는 `@ModelAttribute Item` 과 필드 `price` )

>참고: `BindingResult` 파라미터의 위치
>`BindingResult` 는 반드시 검증할 매개변수 바로 다음에 와야한다. 예를 들어서 `@ModelAttribute Item item` , 바로 다음에 `BindingResult` 가 와야 한다.

`BindingResult` 는 `@ModelAttribute` 처럼 자동으로 모델에 저장되기 때문에, 뷰 템플릿에서 객체의 필드에 접근할 때, 해당 필드의 에러 여부를 확인해서 저장된 에러 정보를 화면에 띄울 수 있다.

### FieldError & ObjectError
`BindingResult` 에 에러에 대한 정보가 저장될 때, `FieldError` 또는 `ObjectError` 객체로 저장된다.

개발자가 다음과 같이 직접 저장할 수 있다.

```Java
if (!StringUtils.hasText(item.getItemName())) {
	bindingResult.addError(
			new FieldError("item", "itemName", "상품 이름은 필수입니다."));
}

if (item.getPrice() * item.getQuantity() < 10000) {
	bindingResult.addError(
		new ObjectError("item", "가격 * 수량은 10,000 이상이어야 합니다.");
}
```

##### FieldError
특정 필드에 관련된 에러 정보는 `FieldError` 객체로 저장하면 된다.

**FieldError 생성자**
```Java
public FieldError(String objectName, String field, String defaultMessage) {}

public FieldError(String objectName, String field,
				  @Nullable Object rejectedValue, boolean bindingFailure,
				  @Nullable String[] codes, @Nullable Object[] arguments,
				  @Nullable String defaultMessage) {}
```

- 첫 번째 생성자
	- `objectName` : 에러가 발생한 객체의 이름
	- `field` : 에러가 발생한 필드 이름
	- `defaultMessage` : default 에러 메시지

- 두 번째 생성자 (더 세밀한 설정 가능)
	-  `objectName` : 에러가 발생한 객체의 이름
	- `field` : 에러가 발생한 필드 이름
	- `rejectedValue` : 검증 오류가 발생한 사용자의 입력
	- `bindingFailure` : 타입 변환 실패 등의 이유로 객체의 필드에 사용자의 입력을 바인딩 하는 것 자체가 실패한 경우 `true`, 아니면 `false`
	- `codes` : 에러 메시지도 스프링의 메시지 기능을 사용할 수 있다 (뒤에서 자세히 설명). 이 때 에러 메시지에 대한 코드를 배열 형태로 여러 개 넘겨서, 배열 원소 순서대로 해당 코드에 대한 에러 메시지가 있으면 해당 메시지를 사용하고, 없으면 다음 코드를 찾아본다.
	- `arguments` : 메시지 기능에서 처럼 에러 메시지에서도 매개변수를 넘겨서 메시지를 만들 수 있다.
	- `defaultMessage` : `codes` 에 해당하는 에러 메시지가 없으면 사용되는 default 에러 메시지

위에서 사용자 입력을 컨트롤러의 `@ModelAttribute` 객체에 바인딩 할 때, 타입 문제 등에 의해 바인딩 자체가 실패하면 사용자의 잘못된 입력 값을 `BindingResult` 에 저장한다고 했다.

이 때 스프링이 하는 작업이 바로 `BindingResult` 에 `FieldError` 객체를 를 생성해서 넣어주는 것이다.

위에서 언급 했던 내용처럼 에러가 난 객체와 필드의 이름과 함께, 사용자의 잘못된 입력 값을 위 파라미터 중 하나인 `rejectedValue` 에 넣어서 `FieldError` 객체를 생성한 뒤 `BindingResult` 에 저장한다.

##### ObjectError
특정 필드가 아닌 여러 필드에 관련된 정보나, 아니면 객체 필드에만 국한된 에러가 아닌 객체 전체에 대한 에러인 경우 `BindingResult` 에 `ObjectError` 객체로 저장하면 된다. 이런 에러를 **글로벌 에러** 라고도 한다.

**ObjectError 생성자**
```Java
public ObjectError(String objectName, String defaultMessage) {}

public ObjectError(String objectName, @Nullable String[] codes,
				   @Nullable Object[] arguments,
				   @Nullable String defaultMessage) {}
```

`ObjectError` 생성자는 `FieldError` 생성자에서 `field` 에 대한 파라미터를 뺀 것과 동일하다.

참고로 `FieldError` 는 `ObjectError` 를 상속받았다.


## Thymeleaf 에서 `BindingResult` 사용
타임리프는 스프링의 `BindingResult` 를 활용해서 검증 오류를 편리하게 화면에 표현하는 기능을 제공한다.

##### `#fields` 
BindingResult` 에 저장되어 있는 검증 오류에 접근할 수 있다.

```HTML
<div th:if="${#fields.hasGlobalErrors()}">
	<p class="field-error" th:each="err : ${#fields.globalErrors()}"
	th:text="${err}">글로벌 오류 메시지</p>
</div>
```

##### `th:errors`
해당 필드에 오류가 있는 경우에 태그를 출력한다. `th:if` 의 편의 버전이다.

```HTML
<div class="field-error" th:errors="*{itemName}">상품명 오류</div>
```

##### `th:field`
`th:field` 는 정상 상황에는 모델 객체의 값을 사용해서 `value` 등의 속성을 렌더링한다. 그런데 `BindingResult` 에 해당 필드에 대한 오류가 존재하면,  `FieldError` 에서 보관한 값인 `rejectedValue`  값을 사용해서 렌더링한다.

위에서 언급 했듯이 타입 오류 등의 문제로 사용자 입력 데이터 바인딩에 실패하면, 스프링은 `FieldError` 를 생성하면서 사용자의 잘못된 입력을 저장하고 해당 오류를 `BindingResult` 에 저장한다.

`BindingError` 는 자동으로 모델에 저장하므로, 타임리프가 에러 여부를 확인하고 사용자의 잘못된 입력을 그대로 유지해서 화면을 렌더링 할 수 있는 것이다.

```HTML
<input type="text" id="itemName" th:field="*{itemName}"
th:errorclass="field-error" class="form-control" placeholder="이름을 입력하세요">
```

##### `th:errorclass`
`th:field` 에서 지정한 필드에 오류가 있으면 class 정보를 추가한다.


## 에러 코드와 에러 메시지 처리
`FieldError` 와 `ObjectError` 생성자에 공통으로 있었던 매개변수인 `@Nullable String[] codes` 와  `@Nullable Object[] arguments` 를 사용하면 에러 메시지도 스프릥의 메시지 기능처럼 처리할 수 있다. 심지어 국제화도 가능하다.

별다른 설정을 하지 않으면 스프링 부트에 의해 메시지 소스가 `messages.properties` 로 지정되므로, 에러 메시지를 따로 관리하기 위해 다음과 같이 설정해주자.

**application.properties**
```
spring.messages.basename=messages,errors
```

이제 `/resources` 경로에 `errors.properties` 를 만들고, 이 파일에서 에러 메시지와 코드를 관리하면 된다. 참고로 메시지와 동일한 방식으로 국제화를 사용할 수 있다 (`errors_en.properties` 등).

**/resources/errors.properties**
```text
required.item.itemName=상품 이름은 필수입니다.
range.item.price=가격은 {0} ~ {1} 까지 허용합니다.
max.item.quantity=수량은 최대 {0} 까지 허용합니다.
totalPriceMin=가격 * 수량의 합은 {0}원 이상이어야 합니다. 현재 값 = {1}
```

이제 다음과 같이 `BindingResult` 에 `FieldError` 나 `ObjectError` 를 저장할 때 에러 코드를 지정해주면,

```Java
bindingResult.addError(new FieldError(
						"item", "price", item.getPrice(), false,
						new String[]{"range.item.price"},
						new Object[]{1000, 1000000}, null));
```

타임리프에서 `range.item.price` 에 해당하는 코드를 메시지 소스에서 찾아서 화면에 출력한다. 이 때 매개변수로 넘어온 `1000` 과 `1000000` 값을 사용해서 메시지를 만들어서 출력한다.

메시지 코드는 하나가 아니라 배열로 여러 값을 전달할 수 있는데, 배열 원소 순서대로 확인 해서 처음 매칭되는 메시지 코드가 사용된다. 즉 우선순위로 이해하면 된다.


### `BindingResult` 의 `rejectValue()` 와  `reject()`
`BindingResult` 에 `FieldError` 와 `ObjectError` 를 직접 생성해서 넣어주는 것은 너무 복잡한 일이다.

객체 이름, 필드 이름, 바인딩 실패 여부, 에러 코드, 메시지 매개변수, default 에러 메시지 등등 수 많은 매개변수를 매번 입력하는 것은 너무 힘들다.

이 때 `BindingResult` 의 `rejectValue()` 와 `reject()` 메서드를 사용하면 매우 편하다.

```Java
void reject(String errorCode);

void reject(String errorCode, String defaultMessage);

void reject(String errorCode, @Nullable Object[] errorArgs,
			@Nullable String defaultMessage);

void rejectValue(@Nullable String field, String errorCode);

void rejectValue(@Nullable String field, String errorCode,
				 String defaultMessage);

void rejectValue(@Nullable String field, String errorCode,
				 @Nullable Object[] errorArgs,
				 @Nullable String defaultMessage);
```

>참고
>`reject()` 와 `rejectValue()` 는 `BindingResult` 가 상속받는 `Errors` 인터페이스에 선언된 메소드이다.

`BindingResult` 는 항상 검증해야 할 매개변수 객체 바로 다음에 위치한다. 즉 검증 객체에 대한 정보를 이미 가지고 있다. 따라서 해당 객체에 대한 정보를 굳이 입력해 줄 필요가 없다.

`reject()` 와 `rejectValue()` 의 선언부를 보면 실제로 객체에 대한 정보를 넘겨받지 않는다. 단지 객체에는 여러 필드가 있기 때문에 `rejectValue()` 가 필드에 대한 정보를 넘겨 받을 뿐이다.

에러 코드도 더 이상 배열 형태로 받지 않는다. 왜냐하면 해당 메소드들이 동작할 때 스프링이 제공하는 `MessageCodesResolver` 가 정해진 규칙에 따라 여러 개의 에러 코드를 만든 뒤, `FieldError` 혹은 `ObjectError` 객체를 생성하며 해당 에러 코드 배열을 넘겨주기 때문이다.

결과적으로 그렇게 만들어진 `FieldError` 혹은 `ObjectError` 객체가 `BindingResulut` 에 저장된다.

#### MessageCodesResolver
주어진 에러 코드로 여러개의 메시지 코드를 생성한다.

`MessageCodesResolver` 는 인터페이스이고, 기본 구현체로 `DefaultMessageCodesResolver` 가 제공된다.

##### DefaultMessageCodesResolver의 기본 메시지 생성 규칙
**객체 오류 (ObjectError, reject())**
```text
객체 오류의 경우 다음 순서로 2가지 생성
	1: code + "." + object name
	2: code
	
예) 오류 코드: required, object name: item
	1: "required.item"
	2: "required"
```

**필드 오류**
```
필드 오류의 경우 다음 순서로 4가지 메시지 코드 생성
	1: code + "." + object name + "." + field
	2: code + "." + field
	3: code + "." + field type
	4: code
	
예) 오류 코드: typeMismatch, object name "user", field "age", field type: int
	1: "typeMismatch.user.age"
	2: "typeMismatch.age"
	3: "typeMismatch.int"
	4: "typeMismatch"
```

### 에러 코드와 메시지 관리
`DefaultMessageCodesResolver` 의 기본 메시지 생성 규칙에 따라서 에러 코드와 메시지를 관리하면, 소스 코드에 손대지 않고도 원하는 곳에 원하는 에러 메시지를 띄울 수 있다.

핵심은 생성된 에러 코드가 우선 순위를 갖고 있다는 것이다. 타임리프에서 메시지를 찾을 때 순서대로 매칭을 해서 먼저 매칭된 메시지를 사용한다는 특징 때문에, 배열의 가장 앞에 있는 에러 코드가 가장 높은 우선 순위를 가진다.

생성된 에러 코드 배열을 살펴보면, 구체적인 것에서 덜 구체적인 순서대로 나열되어 있다는 사실을 알 수 있다.

따라서 이 특징을 이용해서, 크게 중요하지 않은 에러 메시지는 범용성 있는 `requried` 같은 에러 코드로 끝내고, 정말 중요한 메시지는 `required.item.id` 처럼 구체적으로 적어서 사용하는 방식이 더 효과적이다.

**errors.properties**
```text
#==ObjectError==

#Level1
totalPriceMin.item=상품의 가격 * 수량의 합은 {0}원 이상이어야 합니다. 현재 값 = {1}

#Level2 - 생략
totalPriceMin=전체 가격은 {0}원 이상이어야 합니다. 현재 값 = {1}

#==FieldError==

#Level1
required.item.id=상품 id는 필수입니다.

#Level2 - 생략

#Level3
required.java.lang.String = 필수 문자입니다.
required.java.lang.Integer = 필수 숫자입니다.
min.java.lang.Integer = {0} 이상의 숫자를 입력해주세요.

#Level4
required = 필수 값 입니다.
min= {0} 이상이어야 합니다.
range= {0} ~ {1} 범위를 허용합니다.
```

이런 식으로 에러 코드를 레벨을 나누어서 관리하면, 메시지 관리가 매우 편리해지고 일관성 있어진다.


##### 스프링이 만든 에러 코드 처리 - 타입 에러
사용자 입력이 타입 때문에 `@ModelAttribute` 객체에 바인딩이 실패하는 경우, 스프링이 자동으로 `BindinResult` 에 `FieldError` 를 추가해준다.

이 때 해당 `FieldError` 의 메시지 코드를 출력해보면 다음과 같은 형태임을 확인할 수 있다.
- `typeMismatch.item.price`
- `typeMismatch.price`
- `typeMismatch.java.lang.Integer`
- `typeMismatch`

`typeMismatch` 라는 에러 코드와 객체, 필드 정보를 합쳐서 위에서 봤던 에러 코드 생성 규칙에 맞게 4개의 에러 코드를 생성한 것이다.

따라서 이 코드에 맞춰서 에러 메시지를 추가해주면 간단하게 해당 에러 코드를 처리할 수 있다.

## Validator 분리와 등록
역할 분리를 위해 컨트롤러에서 검증 로직을 따로 분리하는 것이 좋다. 별도의 클래스로 분리해보자. 이렇게 분리해두면 다른 곳에서 재사용도 가능해진다.

스프링에서 제공하는 `Validator` 인터페이스를 활용하면 조금 더 편리하게 사용할 수 있다.
```Java
public interface Validator {
	boolean supports(Class<?> clazz);
	void validate(Object target, Errors errors);
}
```
- `supports()` : 대상 클래스가 검증 가능한지 확인
- `validate()` : 실제 검증 메소드 구현. `BindingResult` 는 `Errors` 를 상속받는다.

```Java
public class ItemValidator implements Validator {
	@Override
	public boolean supports(Class<?> clazz) {
		return Item.class.isAssignableFrom(clazz);
	}
	
	@Override
	public void validate(Object target, Errors errors) {
		//검증 로직.
		//검증 결과를 errors에 저장
	}
}
```

`Validator` 를 상속받아서 검증 클래스를 구현하면 다음과 같이 `@Validated` 어노테이션을 적용할 수 있다.

```Java
@Controller
public class HelloController {

	@Autowired
	private final ItemValidator itemValidator;

	@InitBinder
	public void init(WebDataBinder dataBinder) {
		dataBinder.addValidators(itemValidator);
	}
	
	public String addItemV6(@Validated @ModelAttribute Item item,
								BindingResult bindingResult,
								RedirectAttributes redirectAttributes) {
		//검증 코드 생략 가능. itemValiator 를 호출하지 않아도 된다.						
	}
}
```

이렇게 `WebDataBinder` 에 검증기를 추가하면,  해당 컨트롤러에서는 `@Validated` 가 붙은 객체를 대상으로 검증기가 자동으로 적용된다.

여러 검증기를 등록한다면 그 중에 어떤 검증기가 실행되어야 할지 구분이 필요하다. 이때 `supports()` 가 사용된다

이 설정은 해당 컨트롤러에만 영향을 준다. 글로벌 설정은 별도로 해야한다.

##### 검증기 글로벌 설정
```Java
@SpringBootApplication
public class ItemServiceApplication implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(ItemServiceApplication.class, args);
	}
	
	@Override
	public Validator getValidator() {
		return new ItemValidator();
	}
}
```