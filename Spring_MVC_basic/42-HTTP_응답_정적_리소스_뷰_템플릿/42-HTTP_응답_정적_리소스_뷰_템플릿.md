# HTTP 응답 - 정적 리소스, 뷰 템플릿

## Spring에서 응답 데이터를 만드는 방법 3가지
- **정적 리소스**
	- HTML, css, js 등을 제공하는 경우
	- 파일을 그대로 전달한다
- **뷰 템플릿**
	- 동적인 HTML을 제공하는 경우
- **HTTP 메시지 바디**
	- 데이터를 전달해야 하는 경우
	- HTTP API에서 많이 쓰임


## 정적 리소스
- 스프링 부트는 클래스패스 하위의 다음 디렉토리에 위치하는 정적 리소스를 제공한다.
	- `/static`
	- `/public`
	- `/resources`
	- `/META-INF/resources`

`src/main/resources` 는 리소스를 보관하는 곳인 동시에 클래스패스의 시작 경로이다. 따라서 이 경로 하위에, 위의 4개의 디렉토리 중 하나를 만들어서 그 곳에 리소스를 위치시키면 정적 리소스를 제공할 수 있다.

#### 예시
- 리소스 경로: `src/main/resources/static/html/hello.html`
- 해당 리소스 요청: `http://localhost:8080/html/hello.html`


## 뷰 템플릿
뷰 템플릿을 거쳐서 HTML이 생성되고, 뷰가 응답을 만들어서 전달한다.

일반적으로 HTML을 동적으로 생성하는 용도로 사용하지만, 다른 종류도 동적으로 생성 가능하다. 뷰 템플릿이 만들 수 있는 것이라면 상관 없다.

#### 뷰 템플릿 경로
`src/main/resources/templates`

#### 뷰 템플릿 생성
`src/main/resources/templates/response` 에 `hello.html` 을 만들어보자.

##### hello.html
```HTML
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
	<meta charset="UTF-8">
	<title>Title</title>
</head>
<body>
<p th:text="${data}">empty</p>
</body>
</html>
```


### 뷰 템플릿을 호출하는 컨트롤러
##### ResponseViewController.java
```Java
package com.kloong.springmvc.basic.response;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ResponseViewController {

    @RequestMapping("/response-view-v1")
    public ModelAndView responseViewVer1() {
        ModelAndView mav = new ModelAndView("response/hello");
        mav.addObject("data", "hello v1!");
        return mav;
    }

    @RequestMapping("/response-view-v2")
    public String responseViewVer2(Model model) {
        model.addAttribute("data", "hello v2!");
        return "/response/hello";
    }

    @RequestMapping("/response/hello")
    public void responseViewVer3(Model model) {
        model.addAttribute("data", "hello v3!");
    }
}
```

#### ModelAndView 반환 - Ver. 1
- Model 데이터와 뷰의 논리 이름을 가지고 있는 ModelAndView 객체를 반환한다.
- 뷰 리졸버가 ModelAndView 객체에 있는 뷰의 논리 이름으로 뷰를 찾고, 뷰가 렌더링된다.

#### String 반환 - Ver. 2
- 메소드 레벨에 `@ResponseBody` 가 없고, 클래스 레벨에 `@Controller` 가 있다면, 뷰 리졸버가 반환된 문자열에 해당하는 논리 이름을 가진 뷰를 찾고, 뷰가 렌더링된다.
- `/response/hello` 가 반환되기 때문에 `/templates/response/hello.html` 뷰가 렌더링된다.

#### Void 반환 - Ver. 3
- `@Controller` 를 사용하고, `HttpServletResponse` , `OutputStream` /`Writer` 같은 HTTP 메시지 바디를 처리하는 파라미터가 없으면 요청 URL을 참고해서 논리 뷰 이름으로 사용한다.
	- 요청 URL: `/response/hello`
	- 호출: `/templates/response/hello.html`
- **이 방식은 명시적이지 못하고, URL과 뷰 논리 이름이 일치하는 경우도 거의 없기 때문에 권장되지 않는다**

### Spring Boot에서 Thymeleaf 설정
`org.springframework.boot:spring-boot-starter-thymeleaf` 라이브러리를 추가하면 스프링 부트가 `ThymeleafViewResolver` 와 필요한 스프링 빈들을 자동으로 등록한다. 처음에 프로젝트 만들 때 이미 라이브러리를 추가해뒀다.

##### build.gradle
```
dependencies {
	implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'
	...
}
```

그리고 다음과 같이 기본 설정을 한다. default 값이기 때문에 실제로 `application.properties`에 다음 설정값이 존재하진 않는다. 설정을 바꿔야 한다면 이런 식으로 바꾸면 된다는 예시를 든 것이다.

##### application.properties
```
spring.thymeleaf.prefix=classpath:/templates/
spring.thymeleaf.suffix=.html
```

`classpath` 는 `/src/main/resources` 를 의미한다.