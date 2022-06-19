# 타임리프 - text, utext

타임리프의 가장 기본 기능인 텍스트를 출력하는 기능 먼저 알아보자.

## `th:text` 와 `[[...]]`
타임리프는 기본적으로 HTML 테그의 속성에 기능을 정의해서 동작한다.

#### `th:text`
- HTML의 콘텐츠(content)에 데이터를 출력할 때는 다음과 같이 `th:text` 를 사용하면 된다.
- `<span th:text="${data}">`
 
#### `[[...]]`
- HTML 태그의 속성이 아니라 데이터가 실제로 표현 될 HTML 콘텐츠의 위치에 코드를 작성하고 싶으면 다음과 같이 `[[...]]` 를 사용하면 된다.
- `<span>[[${data}]]</span>`

##### BasicController.java
```Java
package kloong.thymeleaf.basic;  
  
import ... //생략
  
@Controller  
@RequestMapping("/basic")  
public class BasicController {  
  
    @GetMapping("/text-basic")  
    public String textBasic(Model model) {  
        model.addAttribute("data", "Hello Thymeleaf!");  
        return "basic/text-basic";  
    }  
}
```

##### /resources/templates/basic/text-basic.html
```HTML
<!DOCTYPE html>  
<html xmlns:th="http://www.thymeleaf.org">  
<head>  
    <meta charset="UTF-8">  
    <title>Thymeleaf text basic</title>  
</head>  
<body>  

<h1>컨텐츠에 데이터 출력하기</h1>  
  
<ul>  
    <li>th:text 사용 - <span th:text="${data}"></span></li>  
    <li>컨텐츠 안에서 직접 출력하기 - <span>[[${data}]]</span></li>  
</ul>  
  
</body>  
</html>
```

### HTML Entity와 Escape
HTML 문서는 `<` , `>` 같은 특수 문자를 사용한 태그를 기반으로 이루어져있다. 따라서 뷰 템플릿으로 HTML 화면을 생성할 때, 출력하는 데이터에 이러한 특수 문자가 있다면 주의해야 한다. 앞에서 만든 예제의 데이터를 다음과 같이 변경해서 실행해보자.

##### 변경 전
`model.addAttribute("data", "Hello Thymeleaf!");`

##### 변경 후
`model.addAttribute("data", "Hello <b>Thymeleaf!</b>");`

##### 개발자의 의도
"Thymleleaf!" 라는 문자열을 `<b>` HTML 태그로 bold 체로 바꿔서 출력해야지!

##### 현실 - 웹 브라우저 실행 결과
- 웹 브라우저에 렌더링 된 문자열: `Hello <b>Thymeleaf!</b>`
- 웹 브라우저 소스 보기: `Hello &lt;b&gt;Tyhmeleaf!&lt;/b&gt;`

#### HTML Entity
- HTML은 `<`, `>` 과 같은 특수문자를 사용한 HTML 태그로 이루어져 있다.
- 따라서 웹 브라우저는 `<` 를 HTML 태그의 시작으로 인식한다.
- 만약 개발자가 화면에 `<` 문자를 출력하고 싶은데, 웹 브라우저가 `<` 를 항상 HTML 태그로 인식해버리면 문제가 생긴다.
- 따라서 `<` 나 `>` 같은 특수문자를 웹 브라우저가 plain text로 인식시킬 방법이 필요하다.
- 이 때 HTML Entity를 사용하면 된다.
- `<` 를 `&lt;` 로, `>` 는 `&gt;` 로 표현하기로 약속을 해 두고, 해당 문자열이 들어오면 웹 브라우저는 다음과 같이 동작한다.
	- `&lt;` -> 화면에 `<` 출력
	- `&gt;` -> 화면에 `>` 출력

#### Escape
- HTML에서 사용되는 특수 문자를 HTML Entity로 변경하는 것을 Escape 라고 한다.
- 타임리프가 제공하는 `th:text`와 `[[...]]` 는 기본적으로 Escape를 제공한다.


## `th:utext`와 `[(...)]`

### Unescape
- `th:text`와 `[[...]]` 는 자동으로 Escape를 지원한다.
- Escape를 사용하지 않으려면 다음의 기능으로 대체해서 사용하면 된다.
	- `th:text` -> `th:utext`
	- `[[...]]` -> `[(...)]`

##### BasicController.java 일부
```Java
package kloong.thymeleaf.basic;  
  
import ... //생략
  
@Controller  
@RequestMapping("/basic")  
public class BasicController {  
  
    @GetMapping("/text-unescaped")  
    public String textUnescaped(Model model) {  
        model.addAttribute("data", "Hello <b>Thymeleaf!</b>");  
        return "basic/text-unescaped";  
    }  
}
```

##### /resources/templates/basic/text-unescaped.html
```HTML
<!DOCTYPE html>  
<html xmlns:th="http://www.thymeleaf.org">  
<head>  
    <meta charset="UTF-8">  
    <title>Title</title>  
</head>  
<body>  
<h1>text vs utext</h1>  
<ul>  
    <li>th:text = <span th:text="${data}"></span></li>  
    <li>th:utext = <span th:utext="${data}"></span></li>  
</ul>  
<h1><span th:inline="none">[[...]] vs [(...)]</span></h1>  
<ul>  
    <li><span th:inline="none">[[...]] = </span>[[${data}]]</li>  
    <li><span th:inline="none">[(...)] = </span>[(${data})]</li>  
</ul>  
</body>  
</html>
```
- `th:inline="none"`
	- 타임리프는 `[[...]]` 를 해석하기 때문에, 화면에 `[[...]]` 라는 문자열을 그대로 보여줄 수 없다.
	- 이 기능을 사용하면 태그 안의 내용은 타임리프가 해석하지 않는다.

#### 실행 결과
![](스크린샷%202022-06-19%20오후%205.48.47.png)
`<b>` 태그를 사용한 bold체가 적용된 것을 확인할 수 있다.

##### 소스 보기
```HTML
<li>th:text = <span>Hello &lt;b&gt;Thymeleaf!&lt;/b&gt;</span></li>
<li>th:utext = <span>Hello <b>Thymeleaf!</b></span></li>

<!-- 중략 -->

<li><span>[[...]] = </span>Hello &lt;b&gt;Thymeleaf!&lt;/b&gt;</li>
<li><span>[(...)] = </span>Hello <b>Thymeleaf!</b></li>
```


## Escape의 중요성
실제 서비스를 개발하다 보면 escape를 사용하지 않아서 HTML 문서가 깨져 정상 렌더링 되지 않는  문제가 발생한다. Escape를 기본으로 하고, 꼭 필요한 때만 unescape를 사용하자.