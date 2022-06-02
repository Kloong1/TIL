# HTTP 요청 파라미터 - @ModelAttribute

실제 개발을 하면 객체를 만들고, 요청 파라미터를 받아서 그 객체에 파라미터 값을 넣어주어야 한다.  만약 `@RequestParam` 만 가지고 개발을 해야 한다면 다음과 같이 코드를 작성할 것이다.

```Java
public void helloMethod(@RequestParam String username,
						@RequestParam int age) {
	HelloData data = new HelloData();
	data.setUsername(username);
	data.setAge(age);
}
```

그런데 스프링은 위의 과정을 완전히 자동화 해주는 `@ModelAttribute` 를 제공한다!


## HelloData - 요청 파라미터를 바인딩 받을 객체

먼저 요청 파라미터를 바인딩 받을 객체를 만들자.

##### HelloData.java
```Java
package com.kloong.springmvc.basic;

import lombok.Data;

@Data
public class HelloData {
    private String username;
    private int age;
}
```
- Lombok의 `@Data`
	- `@Getter` , `@Setter` , `@ToString` , `@EqualsAndHashCode` , `@RequiredArgsConstructor` 를 자동으로 적용해준다.


## @ModelAttribute 적용
이제 `@ModelAttribute` 를 사용해서 요청 파라미터를 객체로 한 번에 받아보자.

##### RequestParamController.java 일부
```Java
@ResponseBody
@RequestMapping("/model-attribute-v1")
public String modelAttributeVer1(@ModelAttribute HelloData helloData) {
	log.info("helloData = {}", helloData);
	return "OK";
}
```
- `http://localhost:8080/model-attribute-v1?username=kloong&age=20` 요청을 보내면?
- `HelloData` 객체가 알아서 생성되고, 값도 잘 들어온다!
	- 출력: `helloData = HelloData(username=kloong, age=20)`

##### Spring MVC는 위 코드에 대해서 다음과 같이 동작한다.
- `HelloData` 객체를 생성한다.
- 요청 파라미터의 이름으로 `HelloData` 객체의 프로퍼티를 찾는다. 그리고 해당 프로퍼티의 setter를 호출해서 파라미터의 값을 입력(바인딩) 한다.
- 예) 파라미터 이름이 `username` 이면 `setUsername()` 메서드를 찾아서 호출하면서 값을 입력한다

>참고: 프로퍼티
>객체에 `getUsername()`, `setUsername()` 메서드가 있으면, 이 객체는 `username` 이라는 프로퍼티를 가지고 있는 것이다. `username` 프로퍼티의 값을 변경하면 `setUsername()` 이 호출되고, 조회하면 `getUsername()` 이 호출된다.

##### 바인딩 오류(BindException)
`?age=twenty` 파라미터 처럼 숫자가 들어가야 할 곳에 문자를 넣으면 BindException 이 발생한다. 이런 바인딩 오류를 처리하는 방법은 이후에 검증 부분에서 다룬다.


## @ModelAttribute 생략
`@RequestParam` 을 생략 가능했던 것처럼 `@ModelAttribute` 도 생략 가능하다.

##### RequestParamController.java 일부
```Java
@ResponseBody
@RequestMapping("/model-attribute-v2")
public String modelAttributeVer2(HelloData helloData) {
    log.info("helloData = {}", helloData);
    return "OK";
}
```
- 그런데 `@RequestParam` 과 `@ModelAttribute` 모두 생략 가능하면, Spring은 무엇이 생략된 건지 어떻게 알 수 있을까?
- Spring은 `@RequestParam` 또는 `@ModelAttribute` 를 생략할 시 다음과 같은 규칙을 적용한다.
	- String , int , Integer 같은 단순 타입 -> `@RequestParam` 적용
	- 나머지 타입 (argument resolver로 지정되어있지 않은 타입)-> `@ModelAttribute` 적용

>참고
>argument resolver는 뒤에서 학습한다.

