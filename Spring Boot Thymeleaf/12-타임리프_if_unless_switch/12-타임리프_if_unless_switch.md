# 타임리프 - if, unless, switch

타임리프는 조건식으로 `if` 와 `unless` (`if`의 반대)를 지원한다.

##### BasicController.java 내용 추가
```Java
package kloong.thymeleaf.basic;

import ... //생략

@Controller
@RequestMapping("/basic")
public class BasicController {

    @GetMapping("condition")
    public String condition(Model model) {
        addUsers(model);
        return "basic/condition";
    }
    
    private void addUsers(Model model) {
        List<User> list = new ArrayList<>();
        list.add(new User("userA", 10));
        list.add(new User("userB", 20));
        list.add(new User("userC", 30));

        model.addAttribute("users", list);
    }
}
```

##### /resources/templates/basic/condition.html
```HTML
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
<h1>if, unless</h1>
<table border="1">
    <tr>
        <th>count</th>
        <th>username</th>
        <th>age</th>
    </tr>
    <tr th:each="user, userStat : ${users}">
        <td th:text="${userStat.count}">count</td>
        <td th:text="${user.username}">username</td>
        <td>
            <span th:text="${user.age}">age</span>
            <span th:text="'미성년자'" th:if="${user.age lt 20}"></span>
            <span th:text="'미성년자'" th:unless="${user.age ge 20}"></span>
        </td>
    </tr>
</table>
<h1>switch</h1>
<table border="1">
    <tr>
        <th>count</th>
        <th>username</th>
        <th>age</th>
    </tr>
    <tr th:each="user, userStat : ${users}">
        <td th:text="${userStat.count}">count</td>
        <td th:text="${user.username}">username</td>
        <td th:switch="${user.age}">
            <span th:case="10">10살</span>
            <span th:case="20">20살</span>
            <span th:case="*">기타</span>
        </td>
    </tr>
</table>
</body>
</html>
```

### `if` & `unless`
- `if` 는 조건식의 값이 `false` 이면 **태그 자체를 렌더링하지 않는다.**
	- `<span th:text="'미성년자'" th:if="${user.age lt 20}"></span>`
	- 만약 `user.age` 가 20 미만인 경우 위 태그 전체가 렌더링 되지 않고 사라진다.
- `unless` 는 `if` 의 반대이다. 조건식의 값이 `true` 이면 **태그 자체를 렌더링하지 않는다.**
	- `<span th:text="'미성년자'" th:unless="${user.age ge 20}"></span>`
	- 만약 `user.age` 가 20 이상인 경우 위 태그 전체가 렌더링 되지 않고 사라진다.

### `switch` - `case`
- Java의 Switch-case와 동일하다.
- `*` 은 `default` 를 의미한다.
- `if`, `unless` 와 마찬가지로 조건을 만족하는 `case` 외의 태그는 전부 렌더링되지 않고 사라진다.

>**참고**
>`th:if="${user.age lt 20}"` 이렇게도 되고, `th:if="${user.age} lt 20"` 이렇게도 된다.