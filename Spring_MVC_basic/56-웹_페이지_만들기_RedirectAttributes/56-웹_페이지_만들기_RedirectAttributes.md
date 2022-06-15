# 웹 페이지 만들기 - RedirectAttributes

상품을 저장하고 상품 상세 화면으로 리다이렉트 한 것 까지는 좋았다. 그런데 고객 입장에서 저장이 잘 된 것인지 안 된 것인지 확신이 들지 않는다는 피드백이 있었다. 그래서 저장이 되었으면 상품 상세 화면에 "저장되었습니다"라는 메시지를 보여달라는 요구사항이 추가되었다. 간단하게 해결해보자.

## 컨트롤러 개발
##### addItemVer6 - BasicItemController.java 일부
```Java
@PostMapping("/add")
public String addItemVer6(Item item, RedirectAttributes redirectAttributes) {
	Item savedItem = itemRepository.save(item);
	redirectAttributes.addAttribute("itemId", savedItem.getId()); //redirect 경로로 치환됨
	redirectAttributes.addAttribute("status", true); //쿼리 파라미터로 사용됨
	return "redirect:/basic/items/{itemId}";
}
```
- `RedirectAttributes`
	- 리다이렉트를 할 때 경로 변수를 사용할 수 있게 해주고, 요청에 쿼리 파라미터도 붙여준다.
	- `return "redirect:/basic/items/{itemId}"`
		- `redirectAttributes.addAttribute("itemId", savedItem.getId());` 에 의해 `{itemId}` 가 저장된 item의 id로 치환된다.
		- `redirectAttributes.addAttribute("status", true);` 에서 `status` 가 경로 변수가 아니므로 쿼리 파라미터로 사용된다.
		- 따라서 redirect 요청은 `http://localhost:8080/basic/items/1?status=true` 가 된다.
	- 자동으로 URL 인코딩도 해주기 때문에 편리하다.


## 뷰 템플릿에 상품 등록 성공 메시지 추가
`addItemVer6` 에 의해 `/basic/items/{itemId}` 으로 redirect 하기 때문에 최종적으로 `item.html` 뷰 템플릿이 호출된다. 해당 뷰 템플릿에서 `?status=true` 쿼리 파라미터가 있으면 상품 등록 성공 메시지가 출력되게 만들어보자.

##### item.html
```HTML
<div class="py-5 text-center">
	<h2>상품 상세</h2>
</div>
<h2 th:if="${param.status}" th:text="'상품 등록 성공'"></h2>
```
- `th:if`
	- 조건이 참이면 실행된다.
- `${param.status}`
	- 타임리프에서 쿼리 파라미터를 편리하게 조회하는 기능
	- 원래는 컨트롤러에서 `Model`에 쿼리 파라미터의 값을 저장한 뒤, 뷰 템플릿에서 `Model`에 저장된 값을 꺼내서 써야 한다.
	- 그런데 쿼리 파라미터는 자주 사용되기 때문에, 타임리프에서 직접 조회가 가능하도록 지원한다.
- 상품 등록을 한 후 리다이렉트를 통해 `item.html` 이 호출되면 `상품 등록 성공` 메시지가 출력되지만, 상품 목록에서 상품 상세 정보 페이지를 직접 조회하면 해당 메시지가 호출되지 않는 것을 확인할 수 있다.