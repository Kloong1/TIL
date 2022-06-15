# 웹 페이지 만들기 - Thymeleaf 적용

본격적으로 컨트롤러와 뷰 템플릿을 개발해보자.

## 컨트롤러 개발

##### BasicItemController.java
```Java
package kloong.itemservice.web.basic;

import ... //생략

@Controller
@RequestMapping("/basic/items")
@RequiredArgsConstructor
public class BasicItemController {

    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "basic/items";
    }

    //테스트용 데이터 추가
    //bean이 생성된 후 DI 이후에 이 코드 실행됨
    @PostConstruct
    public void init() {
        itemRepository.save(new Item("itemA", 10000, 10));
        itemRepository.save(new Item("itemB", 20000, 197));
    }
}
```
- `/basic/items` 로 GET 요청이 들어오면, `ItemRepository` 에서 모든 상품을 조회하여 모델에 담는다. 그리고 뷰 템플릿을 호출한다.
- `@RequiredArgsConstructor`
	- `final` 이 붙은 멤버 변수들의 생성자를 자동으로 만들어준다.
	- 따라서 이 어노테이션을 사용해서 생성자를 만들려면 멤버 변수의 `final` 키워드에 유의해야한다.
	- 이 어노테이션에 의해 만들어진 생성자만 존재하기 때문에, `@Autowired` 를 붙여주지 않아도 Spring이 자동으로 DI를 한다.
- `@PostConstruct`
	- Bean이 생성된 후, DI가 일어난 다음 이 어노테이션이 붙은 메소드가 실행되어서 초기화를 한다.
	- 테스트용 데이터를 넣기 위해서 추가해 놓은 임시 메소드이다.

## 뷰 개발
##### items.html
```HTML
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="utf-8">
    <link th:href="@{/css/bootstrap.min.css}"
            href="../css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container" style="max-width: 600px">
    <div class="py-5 text-center">
        <h2>상품 목록</h2>
    </div>
    <div class="row">
        <div class="col">
            <button class="btn btn-primary float-end"
                    onclick="location.href='addForm.html'"
                    th:onclick="|location.href='@{/basic/items/add}'|"
                    type="button">
                상품 등록</button>
        </div>
    </div>
    <hr class="my-4">
    <div>
        <table class="table">
            <thead>
            <tr>
                <th>ID</th>
                <th>상품명</th>
                <th>가격</th>
                <th>수량</th>
            </tr>
            </thead>
            <tbody>
            <tr th:each="item : ${items}">
                <td><a href="item.html" th:href="@{/basic/items/{itemId}(itemId=${item.id})}" th:text="${item.id}">ID</a></td>
                <td><a href="item.html" th:href="@{|/basic/items/${item.id}|}" th:text="${item.itemName}">상품명</a></td>
                <td th:text="${item.price}">가격</td>
                <td th:text="${item.quantity}">수량</td>
            </tr>
            </tbody>
        </table>
    </div>
</div> <!-- /container -->
</body>
</html>
```

### Thymeleaf 알아보기
#### 타임리프 사용 선언
- `<html xmlns:th="http://www.thymeleaf.org">`

#### 타임리프 핵심 원리
- `th:xxx` 가 붙은 부분은 Server-side에서 렌더링 되고, 기존의 값을 대체한다. `th:xxx` 이 없으면 기존 html의 속성이 그대로 사용된다.
- HTML을 타임리프를 거치지 않고 파일로 직접 열었을 때, `th:xxx` 가 있어도 웹 브라우저는 `th:` 속성을 알지 못하므로 무시한다.
- 따라서 기존의 HTML 파일을 직접 열 수 있도록 유지하는 동시에 뷰 템플릿의 기능도 할 수 있다. 이러한 특징이 Thymeleaf가 natural template라고 불리는 이유이다. Thymeleaf를 사용하면 기존의 화면을 크게 무너트리지 않는다.
- JSP같은 경우에는 HTML의 문법을 따르지 않기 때문에 파일을 직접 열면 렌더링이 의도한대로 되지 않는다.

#### 태그 속성 변경
-  `<link href="value1" th:href="value2">`
- Thymeleaf를 거치게 되면 기존의 속성값인 `href="value1"` 을 `href="value2"` 로 변경한다.
- 만약 기존의 값이 없다면 새로 생성한다.
- Thymeleaf를 거치지 않고 HTML을 그대로 볼 때는 `href` 속성이 사용되고, Thymeleaf를 거치면 `th:href` 의 값이 `href` 로 대치되면서 HTML을 동적으로 변경할 수 있다.
- 대부분의 HTML 속성을 `th:xxx` 로 변경할 수 있다.

#### URL 링크 표현식 - @{...}
- `th:href="@{/css/bootstrap.min.css}"`
- `@{...}` : 타임리프는 URL 링크를 사용하는 경우 `@{...}` 를 사용한다. 이것을 URL 링크 표현식이라 한다.
- URL 링크 표현식을 사용하면 서블릿 컨텍스트를 자동으로 포함한다

#### 상품 등록 폼으로 이동 - 속성 변경, th:onclick
- `th:onclick="|location.href='@{/basic/items/add}'|"`
- 여기에는 리터럴 대체 문법이 사용되었다.

#### 리터럴 대체 - |...|
- `|...|`
- 타임리프에서 문자와 표현식 등은 분리되어 있기 때문에 더해서 사용해야 한다.
	- `<span th:text="'Welcome to our application, ' + ${user.name} + '!'">`
- 하지만 다음과 같이 리터럴 대체 문법을 사용하면, 더하기 없이 편리하게 사용할 수 있다.
	- `<span th:text="|Welcome to our application, ${user.name}!|">`
- 우리가 만든 HTML 파일에서는 다음과 같이 속성을 대치해야 하는데
	- `location.href='/basic/items/add'`
- 리터럴 대체 문법 없이 그냥 사용하면 문자와 표현식을 각각 따로 더해서 사용해야 하므로 다음과 같이 복잡해진다.
	- `th:onclick="'location.href=' + '\'' + @{/basic/items/add} + '\''"`
- 리터럴 대체 문법을 사용하면 다음과 같이 편리하게 사용할 수 있다.
	- `th:onclick="|location.href='@{/basic/items/add}'|"`

#### 반복 출력 - th:each
- `<tr th:each="item : ${items}">`
- 반복은 `th:each` 를 사용한다.
- 이렇게 하면 `Model`에 포함된 `items` 컬렉션 데이터가 `item` 변수에 하나씩 포함되고, 반복문 안에서(태그 안에서) item 변수를 사용할 수 있다.
- 컬렉션의 수 만큼 `<tr>..</tr>` 이 하위 태그를 포함해서 생성된다.

#### 변수 표현식 - ${...}
- `<td th:text="${item.price}">가격</td>`
- `Model`에 포함된 값이나 타임리프 변수로 선언한 값을 조회할 수 있다.
- 프로퍼티 접근법을 사용한다. 즉 `${item.price}` 라고 하면 `item.getPrice()` 가 호출된다.

#### 내용 변경 - th:text
- `<td th:text="${item.price}">가격</td>`
- 태그의 내용을 `th:text` 의 값으로 변경한다.
- 여기서는 `가격` 을 `${item.price}` 의 값으로 변경한다.

#### URL 링크 표현식2 - @{...()}
- `th:href="@{/basic/items/{itemId}(itemId=${item.id})}"`
- URL 링크 표현식을 사용하면 경로를 템플릿처럼 편리하게 사용할 수 있다.
- 경로 변수 뿐만 아니라 쿼리 파라미터도 생성한다.
	- 동일한 이름의 경로 변수가 있으면 그 값으로 들어가고, 나머지는 쿼리 파라미터가 된다.
	- 예: `th:href="@{/basic/items/{itemId}(itemId=${item.id}, query1='test', query2='kloong')}"`
	- 생성 링크: `http://localhost:8080/basic/items/1?query1=test&query2=kloong`

#### URL 링크 간단하게 표현하기 - URL 링크 표현식 & 리터럴 대체
- `th:href="@{|/basic/items/${item.id}|}"`
- 리터럴 대체 문법을 활용해서 URL 링크를 간단하게 표현할 수도 있다.


>**참고: Natural template**
>타임리프는 순수 HTML 파일을 웹 브라우저에서 열어도 내용을 확인할 수 있고, 서버를 통해 뷰 템플릿을 거치면 동적으로 변경된 결과를 확인할 수 있다.
>JSP를 생각해보면, JSP 파일은 웹 브라우저에서 그냥 열면 JSP 소스코드와 HTML이 뒤죽박죽 되어서 정상적인 확인이 불가능하다. 오직 서버를 통해서 JSP를 열어야 한다.
>이렇게 순수 HTML을 그대로 유지하면서 뷰 템플릿도 사용할 수 있는 타임리프의 특징을 natural templates이라 한다.


