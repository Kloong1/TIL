# 타임리프 - URL 링크
타임리프에서 URL을 생성할 때는 `@{...}` 문법을 사용하면 된다.

##### BasicController.java 내용 추가
```Java
package kloong.thymeleaf.basic;

import ... //생략

@Controller
@RequestMapping("/basic")
public class BasicController {

    @GetMapping("link")
    public String link(Model model) {
        model.addAttribute("param1", "data1");
        model.addAttribute("param2", "data2");
        return "basic/link";
    }
}
```

##### /resources/templates/basic/link.html
```HTML
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
<h1>URL 링크</h1>
<ul>
    <li><a th:href="@{/hello}">basic url</a></li>
    <li><a th:href="@{/hello(param1=${param1}, param2=${param2})}">hello query param</a></li>
    <li><a th:href="@{/hello/{param1}/{param2}(param1=${param1}, param2=${param2})}">path variable</a></li>
    <li><a th:href="@{/hello/{param1}(param1=${param1}, param2=${param2})}">path variable + query parameter</a></li>
</ul>
</body>
</html>
```

##### 단순한 URL
- `@{/hello}` -> `/hello`

##### 쿼리 파라미터
- `@{/hello(param1=${param1}, param2=${param2})}`
	- -> `/hello?param1=data1&param2=data2`
	- `(...)` 에 있는 부분은 쿼리 파라미터로 처리된다.

##### 경로 변수
- `@{/hello/{param1}/{param2}(param1=${param1}, param2=${param2})}`
	- -> `/hello/data1/data2`
	- URL 경로에 변수가 있으면 `(...)` 안에 경로 변수와 이름이 같은 변수는 경로 변수로 치환된다.

##### 경로 변수 + 쿼리 파라미터
- `@{/hello/{param1}(param1=${param1}, param2=${param2})}`
	- -> `/hello/data1?param2=data2`
	- 경로 변수와 쿼리 파라미터를 함께 사용할 수 있다.
	- URL 경로에 이름이 같은 변수가 있으면 경로 변수로 처리되고, 없으면 쿼리 파라미터로 처리된다.

##### 상대경로, 절대경로
- `/hello` : 절대 경로
- `hello` : 상대 경로
- 참고: https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html#link-urls
