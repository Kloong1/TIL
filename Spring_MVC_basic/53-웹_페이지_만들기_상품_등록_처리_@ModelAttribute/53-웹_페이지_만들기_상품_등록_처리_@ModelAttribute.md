# 웹 페이지 만들기 - 상품 등록 처리, @ModelAttribute


이제 상품 등록 폼에서 전달된 데이터로 실제 상품을 등록 처리해보자. 상품 등록 폼은 HTML Form 방식으로 서버에 데이터를 전달한다.

- `content-type: application/x-www-form-urlencoded`
- 메시지 바디에 쿼리 파리미터 형식으로 전달
- `itemName=itemA&price=10000&quantity=10`

## 상품 등록 처리 - @RequestParam
##### addItemVer1 - BasicItemController.java 에 추가
```Java
@PostMapping("/add")
public String addItemVer1(@RequestParam String itemName,
						  @RequestParam int price,
						  @RequestParam Integer quantity,
						  Model model) {

	Item item = new Item(itemName, price, quantity);
	itemRepository.save(item);

	model.addAttribute("item", item);

	return "basic/item";
}
```
- `@RequestParam` 을 활용, 요청 파라미터를 읽어서 item을 등록했다.
- item이 잘 등록되었는지 확인할 수 있게 등록된 item의 상세 정보를 보여주는 `item.html` 뷰 템플릿을 호출한다.


## 상품 등록 처리 - @ModelAttribute
##### addItemVer2 - BasicItemController.java 에 추가
```Java
@PostMapping("/add")
public String addItemVer2(@ModelAttribute Item item, Model model) {
	itemRepository.save(item);
	model.addAttribute("item", item);
	return "basic/item";
}
```
- `@ModelAttribute` 는 `Item` 객체를 생성하고, 요청 파라미터의 값을 프로퍼티 접근법(setter를 활용한 접근)으로 객체에 입력한다.

#### @ModelAttribute - Model에 객체 자동 추가
##### addItemVer2
```Java
@PostMapping("/add")
public String addItemVer2(@ModelAttribute("item") Item item) {
	itemRepository.save(item);
	return "basic/item";
}
```
- `@ModelAttribute` 는 중요한 한가지 기능이 더 있는데, 바로 `Model`에 `@ModelAttribute` 로 지정한 객체를 자동으로 넣어준다.
- 따라서 다른 데이터를 저장하는 게 아닌 이상은 `Model` 파라미터도 넘겨 받을 필요가 없어진다.
- 모델에 데이터를 담을 때는 이름(키)이 필요하다. 이름은 `@ModelAttribute` 에 지정한 `name(value)` 속성을 사용한다.
- 만약 다음과 같이 `@ModelAttribute` 의 `name` 값을 다르게 지정하면 해당 이름으로 모델에 저장된다.
	- `@ModelAttribute("hello") Item item` -> key를 hello 로 지정해서 item 저장
	- `model.addAttribute("hello", item);` 이 코드 동일한 의미를 가진다.

### @ModelAttribute의 name 속성 생략
##### addItemVer3
```Java
@PostMapping("/add")
public String addItemVer3(@ModelAttribute Item item) {
	itemRepository.save(item);
	return "basic/item";
}
```
- `@ModelAttirubte` 의 `name` 속성을 생략해도 알아서 `Model`에 저장해준다.
- 단 주의할 점은, **이름을 생략하면 모델에 저장할 때 클래스 명을 key로 사용한다.**
	- 이 때 클래스의 첫 글자만 소문자로 변경해서 key로 사용한다.
	- 예) `@ModelAttribute HelloData item` -> `helloData` 를 key로 사용하여 모델에 등록

### @ModelAttribute 전체 생략
##### addItemVer4
```Java
@PostMapping("/add")
public String addItemVer4(Item item) {
	itemRepository.save(item);
	return "basic/item";
}
```
- 앞에서 배웠듯이 단순 타입이 아닌 임의의 객체는 `@ModeAttribute` 가 자동으로 적용된다.
- `addItemVer3` 에서 처럼 클래스 명의 첫 글자만 소문자로 바꾼 이름을 key로 사용해서 `Model` 에 자동으로 저장된다.