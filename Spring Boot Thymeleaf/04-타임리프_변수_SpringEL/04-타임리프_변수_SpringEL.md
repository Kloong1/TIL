# 타임리프 - 변수, SpringEL

## 변수 표현식과 SpringEL
- 타임리프에서 변수를 사용할 때는 변수 표현식을 사용한다.
- 변수 표현식: `${...}`
- 이 변수 표현식에서 **SpringEL**이라는 스프링이 제공하는 표현식을 사용할 수 있다.

##### BasicController.java 내용 추가
```Java
package kloong.thymeleaf.basic;

import ... //생략

@Controller
@RequestMapping("/basic")
public class BasicController {

    @GetMapping("/variable")
    public String variable(Model model) {
        User userA = new User("userA", 10);
        User userB = new User("userB", 20);

        List<User> list = new ArrayList<>();
        list.add(userA);
        list.add(userB);

        Map<String, User> map = new HashMap<>();
        map.put("userA", userA);
        map.put("userB", userB);

        model.addAttribute("user", userA);
        model.addAttribute("users", list);
        model.addAttribute("userMap", map);

        return "basic/variable";
    }

    @Data
    static class User {
        private String username;
        private int age;

        public User(String username, int age) {
            this.username = username;
            this.age = age;
        }
    }

}
```
- 모델에 `User` 객체, `List<User>`, `Map<String, User>` 를 추가해서 넘겼다.

##### /resources/templates/basic/variable.html
```HTML
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
<h1>SpringEL 표현식</h1>
<ul>Object
    <li>${user.username} = <span th:text="${user.username}"></span></li>
    <li>${user['username']} = <span th:text="${user['username']}"></span></li>
    <li>${user.getUsername()} = <span th:text="${user.getUsername()}"></span></li>
</ul>
<ul>List
    <li>${users[0].username} = <span th:text="${users[0].username}"></span></li>
    <li>${users[0]['username']} = <span th:text="${users[0]['username']}"></span></li>
    <li>${users[0].getUsername()} = <span th:text="${users[0].getUsername()}"></span></li>
    <li>${users[1].username} = <span th:text="${users[1].username}"></span></li>
    <li>${users[1]['username']} = <span th:text="${users[1]['username']}"></span></li>
    <li>${users[1].getUsername()} = <span th:text="${users[1].getUsername()}"></span></li>
</ul>
<ul>Map
    <li>${userMap['userA'].username} = <span th:text="${userMap['userA'].username}"></span></li>
    <li>${userMap['userA']['username']} = <span th:text="${userMap['userA']['username']}"></span></li>
    <li>${userMap['userA'].getUsername()} = <span th:text="${userMap['userA'].getUsername()}"></span></li>
    <li>${userMap['userB'].username} = <span th:text="${userMap['userB'].username}"></span></li>
    <li>${userMap['userB']['username']} = <span th:text="${userMap['userB']['username']}"></span></li>
    <li>${userMap['userB'].getUsername()} = <span th:text="${userMap['userB'].getUsername()}"></span></li>
</ul>
</body>
</html>
```

#### Spring에서 지원하는 SpringEL 표현식을 사용 가능
##### Object
- `user.username`
	- user의 username을 프로퍼티 방식으로 접근
	- `user.getUsername()` 을 호출해서 값을 받아온다.
- `user['username']`
	- 마찬가지로 `user.getUsername()` 을 호출해서 값을 받아온다.
	- 문자열 형태의 값을 사용하기 때문에 동적으로 값을 가져오는 데 사용할 수 있다.
- `user.getUsername()`
	- user의 `getUsername()` 을 직접 호출

##### List
- `users[0].username`
	- List에서 첫 번째 요소를 찾고, username 프로퍼티 접근
	- `list.get(0).getUsername()`
- `users[0]['username']`
	- 위와 같음
- `users[0].getUsername()`
	- List에서 첫 번째 회원을 찾고 getter 직접 호출

##### Map
- `userMap['userA'].username`
	- Map에서 `userA` key에 해당하는 원소를 찾고, username 프로퍼티 접근
	- `map.get("userA").getUsername()`
- `userMap['userA']['username']` 
	- 위와 같음
- `userMap['userA'].getUsername()`
	- Map에서 `userA` key에 해당하는 원소를 찾아 getter 직접 호출


## 타임리프에서 지역 변수 선언
- `th:with` 를 사용하면 지역 변수를 선언해서 사용할 수 있다.
- 지역 변수는 선언한 태그 안에서만 유효하다.

```HTML
<h1>지역 변수 - (th:with)</h1>
<div th:with="first=${users[0]}">
    <p>처음 사람의 이름은 <span th:text="${first.username}"></span></p>
</div>
```
- `th:with` 로 `first` 라는 지역 변수를 선언했다.
- `first` 는 `users` list의 첫 번째 값을 가진다.
- 즉 `first` 는 `User` 객체를 가리키게 된다.