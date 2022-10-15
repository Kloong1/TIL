# Optional

## 0. 시작하기에 앞서
이 글은 책 "자바의 정석" 과 다음 3개의 링크를 참고해서 요약해 쓰는 글이다. 더 자세한 정보를 얻고 싶다면 "자바의 정석"과 다음 링크를 읽어보는 것을 추천한다(내 글보다 훨씬 좋다!).

[자바8 Optional 1부: 빠져나올 수 없는 null 처리의 늪](https://www.daleseo.com/java8-optional-before/)
[자바8 Optional 2부: null을 대하는 새로운 방법](https://www.daleseo.com/java8-optional-after/)
[자바8 Optional 3부: Optional을 Optional답게](https://www.daleseo.com/java8-optional-effective/)


## 1. `java.lang.Optional<T>`
`Optional<T>` 클래스는 "`null` 일 수도 있는 객체" 를 감싸는 일종의 래퍼 클래스이다.

Java의 고질적인 문제 중 하나는 바로 `null` 처리이다. Java를 잘 몰라도  `NullPointerException` 의 악명은 익히 들어봤을 것이다. NPE의 가장 큰 문제는 컴파일 타임에 잡아내기 쉽지 않아서, 런타임에 예상치 못한 곳에서 갑자기 튀어나온다는 것이다.

다음 코드를 살펴보자.

```Java
@Getter
public class Order {
	private Long id;
	private Member member;
}

@Getter
public class Member {
	private Long id;
	private Address address;
}

@Getter
public class Address {
	private String street;
	private String city;
}
```

주어진 데이터 모델에서 "주문"한 "회원"의 "도시" 를 알아내는 메소드인 `getCityOfMemberOfOrder()` 가 필요하다고 해보자.

```Java
public String getCityOfMemberOfOrder(Order order) {
	return order.getMember().getAddress().getCity();
}
```

Java를 써 본 사람이라면 위 코드가 얼마나 위험한 코드인지 쉽게 알 수 있을 것이다. `Order` 객체, `getMember()` 로 얻은 `Member` 객체, `getAddress()` 로 얻은 `Address` 객체, 이렇게 세 객체 중 하나라도 `null` 이라면 바로 `NullPointerException` 이 발생한다.

문제는 단순히 `Order` 객체를 넘겨받은 입장인 `getCityOfMemberOfOrder()` 메소드는 위 객체들이 `null` 인지 아닌지 알 방법이 없다. 따라서 `NullPointerException` 을 예방하려면 다음과 같이 `null` 체크를 해야한다.

```Java
public String getCityOfMemberOfOrder(Order order) {
	if (order != null) {
		Member member = order.getMember();
		if (member != null) {
			Address address = member.getAddress();
			if (address != null) {
				return address.getCity();
			}
		}
	}
	return null;
}
```

그 놈의 `null` 이 도대체 뭐길래, 아주 단순한 로직이 `null` 체크 코드 때문에 이렇게 끔찍하게 변해버렸다. `if` 문이 너무 많아져서 코드를 읽기가 어렵고,  핵심 로직을 한 눈에 파악하기도 힘들다.

또 `if` 문을 모두 통과하지 못하면 위 메소드는 또 다시 `null` 을 반환해 버린다. 그러면 이 메소드를 호출하는 쪽 역시 같은 방식으로 `null` 체크를 해야만 한다. 만약 `null` 체크를 잊어버린다면? 런타임에서 NPE 가 발생할 확률이 높아지는 것이다.

따라서 완벽한 NPE 예방을 위해서는 내가 호출하려는 메소드가 `null` 을 반환하는지의 여부도 하나 하나 확인을 해야만 한다. 정말 끔찍한 일이 아닐 수 없다.

하지만 Java 8 부터 `Optional` 이 등장하게 되면서 고질적인 `null` 처리 문제가 어느정도 해소되었다. `Optional` 사용법을 알아보면서 `Optional` 이 이 문제를 어떻게 해결할 수 있는지 확인해보자.

## 2. Optional 기본 사용법 - Optional이 제공하는 메소드

### 1) Optional 객체 생성하기

#### i) Optional.empty()
비어있는 `Optional` 객체를 반환한다. 이 객체는 `Optional` 내부적으로 미리 생성해놓은 싱글톤 인스턴스이다.

```Java
public final class Optional<T> {

    private static final Optional<?> EMPTY =
								    new Optional<>(null);
								    
    private final T value;
    
    private Optional(T value) {
        this.value = value;
    }

    public static<T> Optional<T> empty() {
        @SuppressWarnings("unchecked")
        Optional<T> t = (Optional<T>) EMPTY;
        return t;
    }
    
    //생략...
}
```

```Java
Optional<Member> emptyMember = Optional.empty();
```

#### ii) Optional.of()
`null` 이 아닌 객체를 담고 있는 `Optional` 객체를 생성한다. 메소드의 인자로 `null` 이 넘어올 경우 NPE를 던지기 때문에 주의해서 사용해야 한다.

```Java
public static <T> Optional<T> of(T value) {
	return new Optional<>(Objects.requireNonNull(value));
}
//Objects.requireNonNull(value) 에서 value == null 이면 NPE 발생
```

```Java
Optional<Member> member = Optional.of(aMember);
```

#### iii) Optional.ofNullable()
`null` 인지 아닌지 확신할 수 없는 객체를 담고 있는 `Optional` 객체를 생성한다. `null` 이 넘어올 경우 `Optional.empty()` 와 동일하게 비어 있는 싱글톤 `Optional` 객체를 반환한다.

```Java
public static <T> Optional<T> ofNullable(T value) {
	return value == null ? (Optional<T>) EMPTY
						 : new Optional<>(value);
```

```Java
Map<Long, Member> memberMap = new HashMap<>();
Optional<Member> member = 
					Optional.ofNullable(memberMap.get(10L));
```

### 2) Optional 객체에서 값 꺼내오기
맨 처음에 언급했듯이 `Optional<T>` 클래스는 "`null` 일 수도 있는 객체" 를 감싸는 일종의 래퍼 클래스이다.

따라서 메소드의 반환형이 `Optional<T>` 라면 메소드를 호출하는 쪽에서는 "이 메소드가 반환하는 객체가 `null` 일 수도 있다는" 사실을 바로 알 수 있게 되고, `null` 체크를 한 뒤 객체에 접근을 시도할 것이다.

그렇다면 `Optional` 객체가 담고 있는 객체를 어떻게 꺼내올 수 있을까?

`Optional` 은 `null` 일 수도 있는 객체를 담고 있기 때문에, 담고 있는 객체가 `null` 일 경우를 대비해서 해당 객체에 접근하는 다양한 방식의 메소드를 제공한다.

#### i) get()
객체를 반환한다.  객체가 `null` 이면 `NoSuchElementException` 을 던진다. 따라서 객체가 `null` 이 아니라고 확신할 때만 이 메소드를 사용해야 한다.

```Java
public T get() {
	if (value == null) {
		throw new NoSuchElementException("No value present");
	}
	return value;
}
```

#### ii) orElse()
객체가 `null` 이 아니면 반환하고, `null` 이면 인자로 받은 객체를 반환한다.

```Java
public T orElse(T other) {
	return value != null ? value : other;
}
```

#### iii) orElseGet()
`orElse()` 의 람다식 버전. 객체가 `null` 일 경우 인자로 넘겨받은 람다식의 실행 결과값을 반환한다.

```Java
public T orElseGet(Supplier<? extends T> supplier) {
	return value != null ? value : supplier.get();
}
```

#### iV) orElseThrow()
객체가 `null` 인 경우 인자로 넘겨받은 `Exception` 객체를 반환하는 람다식을 실행해서 예외를 던진다.
 
```Java
public <X extends Throwable> T orElseThrow(
			Supplier<? extends X> exceptionSupplier) throws X {
	if (value != null) {
		return value;
	} else {
		throw exceptionSupplier.get();
	}
}
```

## 3. Optional 의 잘못된 사용
이제 `Optional` 이 제공하는 다양한 메소드들도 알았으니 한 번 앞에서 예시로 들었던 `getCityOfMemberOfOrder()` 메소드의 `null` 체크 문제에 `Optional` 을 적용해보자.

`Optional` 의 `get()` 메소드를 쓰려면 `Optional` 객체가 담고 있는 객체가 `null` 인지 확인을 해야하는 데, 이 때 `isPresent()` 메소드를 사용할 수 있다.

```Java
public boolean isPresent() {
	return value != null;
}
```

```Java
if (optionalMember.isPresent()) {
	return optionalMember.get();
}
```

이제 `getCityOfMemberOfOrder()` 에 `Optional` 을 적용해보자.

```Java
public Optional<String> getCityOfMemberFromOrder(Order order) {
	Optional<Order> optionalOrder = Optional.ofNullable(order);
	if (optionalOrder.isPresent()) {
		Optional<Member> optionalMember =
						Optional.ofNullable(maybeOrder.get());
		if (optionalMember.isPresent()) {
			Optional<Address> optionalAddress =
						Optional.ofNullable(maybeMember.get());
			if (optionalAddress.isPresent()) {
				Address address = optionalAddress.get();
				Optinal<String> optionalCity =
						Optional.ofNullable(address.getCity());
				if (optionalCity.isPresent()) {
					return optionalCity.get();
				}
			}
		}
	}
	return Optional.empty();
}
```

와! 이런 끔찍한 코드가 탄생해버렸다! 분명 `Optional` 을 사용하면 `null` 체크 문제를 해결할 수 있다고 했는데 이게 무슨 일일까?

바로 `Optional` 을 제대로 사용하지 못한 결과다. `Optional` 을 제대로 사용하기 위해서는 함수형 프로그래밍의 관점을 가지고, `Optional` 을 마치 한 개의 원소를 가진 `Stream` 처럼 바라봐야 한다.

## 4. Optional 제대로 사용하기 - 함수형 프로그래밍
`Optional` 은 "이 값은 `null` 일 수도 있다"는 사실을 알려주는 래퍼 클래스의 역할만 하는 것이 아니다. `Optional` 은 개발자에게 함수형 프로그래밍을 가능하게 해준다.

`Optional` 은 인자로 람다식을 받는 `map()`, `filter()` 등의 메소드들을 지원한다. 어디서 많이 보지 않았나?

맞다. 바로 `Stream` 에서 본 그 친구들과 동일하다.

위에서 언급했듯이 `Optional` 을 제대로 사용하려면 한 개의 원소를 가진 `Stream` 취급을 해야 한다. 

`null` 체크는 `Optional` 내부에서 처리하도록 맡기고 (마치 `Stream` 에서 반복은 내부에서 알아서 하도록 맡긴 것 처럼), 개발자는 `Optional` 객체가 갖고 있는 객체로 "무엇을 해야 할 지" 에만 집중해서 코드를 작성하면 된다.

예시 코드를 보면 바로 이해가 갈 것이다. `Optional` 을 "제대로" 사용해서 `getCityOfMemberOfOrder()` 메소드의 문제를 해결해보자.

```Java
public Optional<String> getCityOfMemberOfOrder(Order order) {
	return Optional.ofNullable(order)
					.map(Order::getMember)
					.map(Member::getAddress)
					.map(Address::getCity)
					.orElse(Optional.empty());
}
```

코드가 엄청나게 간결해졌다. 그리고 핵심 로직을 한 눈에 파악할 수 있게 되었다.

`Stream` 처럼 이런 코드가 가능한 이유는 `Optional` 의 `map()` 을 살펴보면 알 수 있다.

```Java
public <U> Optional<U> map(
					Function<? super T, ? extends U> mapper) {
	Objects.requireNonNull(mapper);
	if (!isPresent()) {
		return empty();
	} else {
		return Optional.ofNullable(mapper.apply(value));
	}
}
```

반환형이 `Optional` 이기 때문에 `map()` 을 연속적으로 적용할 수 있다.

그리고 가장 마지막에 `orElse()`, `orElseGet()`, `orElseThrow()` 등의 메소드를 사용해서 값이 존재 하면 그 값을 반환하고 없으면 다른 동작을 하면 된다.

이렇게 함수형 프로그래밍의 관점으로 `Optional` 을 사용해야 `Optional` 을 제대로 사용할 수 있다. 그래서 [Lambda Expression](https://kloong.tistory.com/entry/Java-%EB%9E%8C%EB%8B%A4%EC%8B%9DLambda-Expression?category=1255092), [Stream](https://kloong.tistory.com/entry/Java-Stream) 에 이어서 마지막으로 이 글을 작성하는 것이다.

더 자세한 내용과 예제 코드는 아래 링크를 확인하길 바란다. 사실상 저 링크 첨부한 게 이 글의 본체이기 때문에 왠만하면 저 글을 읽는 것이 좋다 ㅎㅎ....
 
#### 참고 - 링크 3개 꼭 읽어보시길!
[자바8 Optional 1부: 빠져나올 수 없는 null 처리의 늪](https://www.daleseo.com/java8-optional-before/)
[자바8 Optional 2부: null을 대하는 새로운 방법](https://www.daleseo.com/java8-optional-after/)
[자바8 Optional 3부: Optional을 Optional답게](https://www.daleseo.com/java8-optional-effective/)

남궁 성, 『자바의 정석』, 도우출판(2016)
[자바의 정석](https://book.interpark.com/product/BookDisplay.do?_method=detail&sc.shopNo=0000400000&sc.prdNo=249927409&utm_source=google&utm_medium=cpc&utm_campaign=book_domestic_majorbook_s_20210617_pc_cpc_paidsearch&utm_content=consider_34&utm_term=%EC%9E%90%EB%B0%94%EC%9D%98%EC%A0%95%EC%84%9D&utm_term=%EC%9E%90%EB%B0%94%EC%9D%98%EC%A0%95%EC%84%9D&gclid=CjwKCAjwqJSaBhBUEiwAg5W9p_zJ8fuTlad1BC4fy9Y2CZ4crFX5zON47142QInLYr-MzGapRF-NrxoCOmIQAvD_BwE)