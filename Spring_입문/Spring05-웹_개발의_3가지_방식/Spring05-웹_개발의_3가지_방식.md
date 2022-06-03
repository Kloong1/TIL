# 웹 개발의 3가지 방식

웹 개발을 하는 방식(웹 페이지를 제공하는 방식) 에는 크게 3가지 방법이 있다.
 1. 정적 컨텐츠
 2. MVC와 템플릿 엔진
 3. API

## 1. 정적 컨텐츠 (Static Contents)
서버에서 다른 동작 없이 html 파일을 그대로 웹브라우저로 넘겨주는 방식.

Spring Boot에서는 이 방식을 기본적으로 지원하고 있다.
`src/main/resources/static` 폴더에 `<파일 이름>.html` 을 만들고
웹브라우저에서 `localhost:8080/<파일 이름>.html`로 요청을 보내면 해당 html 파일이 열린다.

![](Pasted%20image%2020220301164834.png)
 1. 웹 브라우저에서 `localhost:8080/hello-static.html` 로 요청을 보낸다.
 2. 해당 요청을 내장 Tomcat 서버가 받아서 Spring에게 넘겨준다.
 3. Spring에서는 Controller가 먼저 우선순위를 가지기 때문에, `hello-static` 관련 컨트롤러가 있는지 확인한다.
 4. 위 예시에서는 컨트롤러가 없기 때문에, `resources/static/`  에서 `hello-static.html`이 있는지 찾는다.
 5. 찾은 파일을 웹 브라우저에게 넘겨준다.


\*위의 설명은 디테일 한 것이 대부분 생략되어 있다. 깊게 들어가면 더 많은 내용이 있지만, Spring Boot 동작 방식의 큰 그림 정도로 생각하고 받아들이면 된다.

## 2. MVC와 템플릿 엔진
MVC: Model, View, Controller

가장 많이 쓰는 방식. JSP, PHP 등도 템플릿 엔진이다.
서버에서 무언가 프로그래밍을 해서 html 파일을 동적으로 바꿔서 넘겨준다.
이 작업을 하기 위해서 Model, View, Controller와 템플릿 엔진이 필요하다.

하지만 JSP, PHP 에서는 Controller를 쓰지 않고, View에 모든 작업을 다 했다.
이것을 **Model 1** 방식이라고 한다.

View에서는 화면을 그리는 데 모든 역량을 집중해야 한다.
Controller와 Model은 비즈니스 로직, 내부 처리에 집중을 해야한다.

가장 기본적인 MVC 예제를 보자. 앞에서 잠깐 다뤘던 내용과 크게 다르지 않다.

`src/main/java/<package 이름>/controller` 패키지에 HelloController.java가 있다 

![](스크린샷%202022-03-01%20오후%206.24.31.png)

`HelloController.java` 의 내용

![](스크린샷%202022-03-01%20오후%206.24.15.png)

`hello` method는 앞에서 다룬 예제에서 만든 method.

`helloMVC` method를 추가로 작성했다. `@RequestParam("name")` 은 웹 브라우저로부터 `name` parameter를 넘겨받는다는 의미.

웹 브라우저에서

```
localhost:8080/hello-mvc?name=yohkim
```

이렇게 요청을 보내면, 컨트롤러의 `GetMapping("hello-mvc)` annotaion에 의해 helloMVC method가 호출이 된다. 이 때 "yohkim" 이라는 값이 `name` parameter에 담겨서 helloMVC method에 전달되고, 해당 매개변수의 값인 "yohkim"이 `String name` 에 저장된다.

`hello-template.html` 의 내용

![](스크린샷%202022-03-01%20오후%206.32.03.png)

Controller에서 "hello-template"를 반환하므로, `viewResolver` 가 `hello-template.html` 을 찾는다. 해당 html 파일에서 `${name}` 으로 model에 있는 name attribute의 값을 가져다 쓴다.

name attribute에는 웹 브라우저에서 넘겨준 매개변수 값인 yohkim이 저장되어 있으므로  
안녕하세요! yohkim 이 화면에 나타난다.

![](Pasted%20image%2020220301183526.png)


### \*웹브라우저에서 "name" 매개변수를 넘겨주지 않으면 어떻게 될까?

```
localhost:8080/hello-mvc
```

웹브라우저에서 이렇게만 요청을 보내면

![](스크린샷%202022-03-01%20오후%206.39.15.png)

위와 같은 에러 페이지가 나타난다. Controller에서 "name" 매개변수를 받아서 처리해 줘야 하는데 웹브라우저한테서 해당 매개변수가 전달되지 않았기 때문이다.

Spring Boot의 log를 살펴보면 (IDE에서 실행시키면 계속 찍히는 그 log)

```
2022-03-01 18:39:09.208  WARN 10038 --- [nio-8080-exec-9] .w.s.m.s.DefaultHandlerExceptionResolver : Resolved [org.springframework.web.bind.MissingServletRequestParameterException: Required request parameter 'name' for method parameter type String is not present]
```

대충 "name" 매개변수가 없다는 log 메세지가 뜬다.  
해당 매개변수가 없어도 대충 유도리있게 처리해주지 않는 이유는

![](스크린샷%202022-03-01%20오후%206.43.53.png)

`requiered`가 default로 `true` 로 되어있기 때문.

![](스크린샷%202022-03-01%20오후%206.45.48.png)

`requiered = false` 로 바꿔주고 매개변수 없이  `localhost:8080/hello-mvc` 요청을 보내면

![](스크린샷%202022-03-01%20오후%206.47.11.png)

에러 페이지 대신 위와 같은 페이지가 열린다.

## 3. API
서버에서 html을 넘겨주는 것이 아니라, JSON 형태로 클라이언트에게 넘겨준다.
그러면 클라이언트에서 JSON을 파싱해서 원하는 정보를 사용하면 된다.

혹은 서버끼리 통신할 때는 html 파일 형태를 사용할 필요가 없으므로 이 방식을 사용한다.

컨트롤러가 view에 데이터를 넘겨주거나 view를 그리는 작업을 하지 않고, 데이터를 HTTP 메세지의 body에 담아서 (HTML의 body 태그가 아님!) 그대로 클라이언트(혹은 요청을 보낸 다른 서버)에게 보낸다.

![](스크린샷%202022-03-01%20오후%208.45.32.png)

위 사진에서 `@ResponseBody` annotation이 의미하는 것은, view를 그리는 작업을 할 필요 없이 이 method에서 return하는 어떤 값(위 method에서는 String이지만, 다른 객체가 와도 된다)을 HTTP 메세지의 body에 담아서 그대로 보내라는 뜻이다.

웹브라우저에서 `localhost:8080/hello-string?name=yohkim` 요청을 보내면

![](스크린샷%202022-03-01%20오후%208.49.33.png)

이런 페이지가 뜨고, 해당 페이지의 소스를 확인하면 html 태그가 전혀 없고 "hello yohkim" 이라는 plain text만 존재하는 것을 확인할 수 있다.

그런데 실제로는 위와 같이 String을 넘겨주는 방식은 잘 사용하지 않고, 객체(에 담긴 정보)를 JSON 형태로 변환해서 해당 정보를 넘겨주는 방식을 주로 사용한다.

![](스크린샷%202022-03-01%20오후%208.52.11.png)

`helloAPI` method는 `Hello` 객체를 반환하는 method이다.   `Hello` 객체에는 `name` 이라는 String 변수가 존재한다.

웹브라우저로부터 `localhost:8080/hello-string?name=yohkim` 요청이 들어오면, `helloAPI` method가 호출된다. 이 method는 매개변수로 받은 "yohkim"을 가지고 `hello` 인스턴스를 만든 뒤, 이 인스턴스를 반환한다.

![](스크린샷%202022-03-01%20오후%208.55.43.png)

웹브라우저에서 결과를 확인하면 위와 같은 JSON 형태의 데이터를 볼 수 있다. html 태그는 존재하지 않고 오로지 JSON 데이터만 있음을 확인할 수 있다.

`hello` 인스턴스를 반환했는데 웹브라우저가 JSON 데이터를 받은 이유는, Spring이 해당 객체를 JSON으로 변환해서 웹브라우저에게 넘겨주기 때문이다.

동작 과정은 아래와 같다.

![](Pasted%20image%2020220301210339.png)

 1. 웹브라우저가 `localhost:8080/hello-api?name=yohkim` 요청을 보낸다.
 2. 내장 Tomcat 서버가 Spring으로 `hello-api` 에 대한 요청이 왔다고 넘겨준다.
 3. `GetMapping("hello-api")` 에 의해 `helloAPI` method가 호출되고, method가 `Hello` 객체를 반환한다.
 4. `@ResponseBody` annotation에 의해 Spring은 view를 찾지 않고 (`viewResolver`가 동작하지 않는다)  `HttpMessageConverter` 를 호출한다.
 5. `HttpMessageConverter` 에는 String을 처리하는 `StringHttpMessageConverter`, 객체를 처리하는 `MappingJackson2HttpMessageConverter` 등이 기본으로 등록되어 있다. 여기서는 `Hello` 객체를 반환하므로 `MappingJackson2HttpMessageConverter` 가 호출되어 `Hello` 객체를 JSON으로 변환하여 ({name : "yohkim"}) 클라이언트에게 넘겨준다.

\*`MappingJackson2HttpMessageConverter` 에서 Jackson은 객체를 JSON 형태로 변환해주는 라이브러리이다. Jackson 말고도 구글에서 만든 GSON이 있다.

\*\*최근에는 JSON을 사용하는 것이 일반적이고, Spring에서도 기본적으로 JSON으로 변환해서 데이터를 넘겨주게끔 되어 있다. 하지만 레거시 프로젝트의 경우 간혹 XML을 사용하는 경우가 있는데, 이런 경우에는 설정을 해주면 JSON대신 XML 형태로 변환을 해서 넘겨줄 수 있다고 한다. 아니면 HttpMessageConverter를 직접 만들어서 원하는 형태로 데이터를 넘겨줄 수 있다고 하는데, 실무에서는 거의 건드리지 않는다고 한다.

\*\*\*`HttpMessageConverter` 는 클라이언트 request의 HTTP Accept 헤더와 서버의 컨트롤러의 반환 타입 (method의 반환 타입) 정보 등을 조합해서 선택된다. 예를 들어 HTTP Accept 헤더에 XML만 받을 수 있다고 되어있으면, XML로 변환하는 converter가 선택된다.
