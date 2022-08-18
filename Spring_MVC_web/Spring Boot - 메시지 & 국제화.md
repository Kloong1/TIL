# Spring Boot - 메시지 & 국제화

## 서론
스프링에는 웹 앱 화면에 보이는 문구, 단어 등을 한 곳에서 통합해서 관리할 수 있는 메시지 기능이 있다. 예를 들어 100개의 html 파일 중 상품 정보 관련 HTML Form 안에 있는 label 의 "이름" 이라는 단어만 "상품명" 으로 고치고 싶다면 어떻게 해야 할까?

Replace 기능으로 "이름" 이라는 단어를 "상품명" 으로 고치려고 하니 다른 원하지 않는 파일에 있는 단어나, "회원 이름" 같은 단어도 영향을 받아서 문제가 생길 것이다.

하지만 스프링이 제공하는 메시지 기능을 이용해서 상품 정보 관련 HTML Form 의 label 에 있는 "이름" 이라는 단어만 따로 관리하고 있었다면, 그 단어를 "상품명" 으로만 고치면 모든 문제가 해결 된다.

그리고 이 메시지 기능을 확장한 것이 국제화 기능이다. 단어나 문구를 한 곳에서 통합해서 관리하고 있기 때문에, 해당 단어/문구를 다양한 언어로 번역해 놓기만 하면 스프링이 HTTP 헤더를 보고 요청에 맞는 언어로 제공해준다.


## 1. 메시지

### 스프링 메시지 기능 설정
스프링에서 메시지 관리 기능을 사용하려면 스프링이 제공하는 `MessageSource` 객체를 스프링 빈으로 등록하면 된다.

스프링 부트를 사용하면 스프링 부트가 `MessageSource` 객체를 스프링 빈으로 자동 등록해준다.

>참고
>`MessageSource` 는 인터페이스이다. 따라서 직접 스프링 빈으로 등록하려면 구현체인 `ResourceBundleMessageSource` 를 스프링 빈으로 등록하면 된다.


### 메시지 소스 설정
스프링 부트 환경에서 `MessageSource` 를 스프링 빈으로 등록하지 않고 별도의 설정을 하지 않으면 메시지 소스 이름이 `messages` 로 설정된다.

따라서 `/resources` 경로에 `messages.properties` 라는 파일만 만들고 해당 파일에서 메시지를 관리하면 메시지 기능을 사용할 수 있다.

만약 국제화 기능을 사용하고 싶으면 메시지 소스 이름에 원하는 국가의 약어를 추가해서 파일을 만들면 된다. `messages_en.properties`, `messages_ko.properties` 이런 식으로 하면 된다.

스프링 부트에서 메시지 소스를 설정하고 싶으면 다음과 같이 하면 된다.

**application.properties**
```text
spring.messages.basename=messages,config.i18n.messages
```

위와 같이 여러 메시지 소스를 설정할 수도 있다.


### 메시지 소스 파일 만들기 예시
스프링 부트에서 따로 설정을 안 했다고 가정한 상태라고 가정하고 메시지 소스 파일을 만들어보자.

`/resources` 경로에 `messages.properties` 파일을 만든다.

**/resources/messages.properties**
```text
hello=안녕
hello.name=안녕 {0}
```

`{0}` : 메시지를 사용할 때 파라미터를 적용할 수 있다. `{0}` 은 첫 번째 파라미터, `{1}` 은 두 번째 파라미터... 이런 방식이다.

국제화를 적용하려면 메시지 소스 이름 끝에 원하는 국가의 약어를 추가한 파일만 만들어주면 된다.

**/resources/messages_en.properties**
```text
hello=hello
hello.name=hello {0}
```

### 스프링 메시지 기능 사용 - JAVA 코드
스프링이 제공하는 `MessageSource` 인터페이스를 보면, JAVA 코드로 메시지 소스에서 메시지를 읽어오는 기능을 제공하는 것을 알 수 있다.

```JAVA
public interface MessageSource {
	String getMessage(String code, @Nullable Object[] args,
					@Nullable String defaultMessage, Locale locale);
	
	String getMessage(String code, @Nullable Object[] args, Locale locale)
	throws NoSuchMessageException;

	//생략...
}
```

`defaultMessage` 가 없는 메소드는 `code` 에 해당하는 메시지가 없는 경우 `NoSuchMessageException` 이 발생한다.

스프링 부트에서 `MessageSources` 를 자동으로 스프링 빈으로 등록하기 때문에, 다음과 같이 사용하면 된다.

```Java
@SpringBootTest
public class MessageSourceTest {
	@Autowired
	MessageSource ms;
	
	@Test
	void helloMessage() {
		String result =
			ms.getMessage("hello.name", new Object[] {"Spring"}, 
							Locale.ENGLISH);
		assertThat(result).isEqualTo("hello Spring");
	}
}
```

`Object[]` 를 만들어서 파라미터를 넘겨줄 수 있다.

##### 국제화 기능 적용 (Locale에 해당하는 메시지 소스 파일 선택)
메소드에 파라미터로 전달한 Locale 을 기반으로 메시지 소스 파일을 선택한다.
- `null` 인 경우 `messages.properties`
- Locale이 `en_US` 의 경우 `messages_en_US` -> `messages_en` ->  `messages` 순서로 찾는다.
- 가장 비슷한 순서대로 찾고, 없으면 default 메시지 소스를 이용한다고 보면 된다.

### 스프링 메시지 기능 사용 - Thymeleaf
스프링에서 밀어주는 Thymeleaf 를 사용하고 있다면 타임리프의 메시지 표현식 `#{...}` 으로 아주 간편하게 메시지 기능을 사용할 수 있다.

**렌더링 전**
```HTML
<div th:text="#{hello}"></div>
```

**렌더링 후**
```HTML
<div>안녕</div>
```

파라미터는 다음과 같이 사용할 수 있다.

**렌더링 전**
```HTML
<div th:text="#{hello.name(${member.name})}"></div>
```

**렌더링 후**
```HTML
<div>안녕 홍길동</div>
```


## 2. 국제화
사실 앞에서 한 내용으로 국제화 설정은 이미 끝났다. 메시지 소스를 설정하고, 국가에 해당하는 메시지 파일도 추가하고, Thymeleaf 를 활용하여 실제로 적용까지 했기 때문.

Spring boot & Thymeleaf 환경에서는 html 파일에 국제화가 필요한 부분마다 메시지 표현식 `#{...}` 을 적용한 뒤 국가에 맞는 메시지 파일만 추가해주면 끝난다.

### 스프링의 국제화 메시지 소스 선택
`MessageSource` 소스에서 보았듯이 메시지 기능은 `Locale` 정보를 알아야 언어를 선택할 수 있다.

스프링은 언어 선택시 기본으로 HTTP 요청의 `Accept-Language` 헤더 값을 사용한다.

##### LocaleResolver
스프링은 `Locale` 선택 방식을 변경할 수 있도록 `LocaleResolver` 라는 인터페이스를 제공하는데, 스프링 부트는 기본으로 `Accept-Language`  헤더 값을 활용하는 `AcceptHeaderLocaleResolver` 를 사용한다.

##### LocaleResolver 변경
만약 `Locale` 선택 방식을 변경하려면 `LocaleResolver` 의 구현체를 변경하면 된다. 예를 들어서 고객이 직접 `Locale` 을 선택하도록 하는 것이다. 관련해서 `LocaleResolver` 를 검색하면 수 많은 예제가 나오니 참고하자.