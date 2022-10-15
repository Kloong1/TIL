# Stream

## 1. Stream 이란 무엇인가?
스트림은 단어 그대로 **데이터의 흐름**이다.

JDK 1.8 에서 스트림이 등장하기 전에는, 많은 양의 데이터를 다룰 때 `Collection` 이나 배열에 데이터를 담고, `for` 문이나 `Iterator` 를 사용해서 각각의 데이터에 접근했다.

하지만 `for` 와 `Iterator` 를 사용해 접근해서 데이터를 처리하는 코드는 너무 길고, "반복을 위한 코드" 때문에 "데이터를 처리 하는 핵심 로직"을 한 눈에 확인하기 어렵다는 문제점이 있다.

또 `Collection` 과 `Iterator` 같은 인터페이스로 데이터를 다루는 방법이 추상화 되어 있긴 하지만, 여전히 각각의 컬렉션 클래스에는 같은 기능의 메소드들이 중복해서 정의되어 있다. 예를 들어 `List` 를 정렬할 때는 `Collection.sort()` 를 사용해야 하고, 배열을 정렬할 때는 `Arrays.sort()` 를 사용해야 한다.

그런데 `Stream` 의 등장으로 이러한 문제들을 어느 정도 해결할 수 있게 되었다. 스트림은 각기 다른 데이터 소스를 데이터의 흐름으로 추상화하고, 데이터를 다루는데 자주 사용하는 메서드들을 정의해 놓았다. 따라서 스트림을 사용하면 데이터 소스가 달라도 동일한 방식으로 편리하게 데이터를 처리할 수 있게 된다.

스트림 이전의 데이터 처리 방식에 별 문제를 느끼지 못했을 수도 있지만, 함수형 프로그래밍의 관점으로 스트림과 스트림이 제공하는 메소드들, 그리고 람다식을 잘 조합해서 사용한다면 더 간결하고 명확한 코드를 작성할 수 있다.

`String[]` 과 `List<String>` 을 정렬해서 출력하는 예시를 살펴보자.

```Java
/* 주어진 String 배열과 String 리스트 */
String[] strArr = new String[] {"c", "b", "a"};
List<String> strList = Arrays.asList(strArr);

/* 정렬 */
Arrays.sort(strArr);
Collections.sort(strList);

/* 출력 */
for (String str : strArr)
	System.out.println(str);

for (String str : strList)
	System.out.println(str);
```

별 문제 없어보이기는 한다. 그런데 위 코드에 스트림과 람다식을 적용한다면?

```Java
/* Stream<String> 반환 */
Stream<String> strStream1 = Arrays.stream(strArr);
Stream<String> strStream2 = strList.stream();

/* 정렬 및 출력*/
strStream1.sorted().forEach(System.out::println);
strStream2.sorted().forEach(System.out::println);
```

주어진 두 객체는 `Stirng[]` 과 `List<String>` 으로 타입이 달랐는데, 스트림을 적용하자 `Stream<String>` 의 같은 타입으로 데이터를 다룰 수 있게 되었다.

타입이 같다는 것은 데이터에 접근하는 방식도 동일하다는 것을 의미한다. 따라서 정렬과 출력 코드를 보면 변수명 말고는 완전히 동일한 것을 확인할 수 있다. 이렇게 되면 코드의 재사용성도 높아진다. 스트림 객체를 넘겨받는 메소드만 구현한 뒤, 데이터 소스를 스트림으로 변환해서 넘기기만 하면 되기 때문이다.

위 코드를 더 줄이면 다음과 같다.

```Java
Arrays.stream(strArr)
		.sorted()
		.forEach(System.out::println);
		
strList.stream()
		.sorted()
		.forEach(System.out::println);
```

스트림 객체를 참조하는 변수를 따로 만들지 않고, 바로 정렬하고 출력했다.

>참고: 굳이 언급 안해도 잘 알겠지만, 스트림과 람다식을 사용한 데이터 처리가 `for`나 `Iterator` 를 사용한 방식의 상위 호환이라는 의미는 아니다. 모든 기술은 적재적소에 활용해야 빛을 발하는 법이다. `for` 나 `Iterator` 가 더 좋은 선택인 경우도 많이 있을 것이다.

## 2. Stream 의 특징
스트림은 다음과 같은 특징을 갖고 있다. 이 특징들을 잘 이해해야 스트림을 언제 어디서 써야하는 지 알 수 있을 것이다.

#### 1) 스트림은 데이터 소스를 변경하지 않는다.
스트림은 데이터 소스로부터 데이터를 읽기만 할 뿐, 데이터 소스를 변경하지 않는다. 앞에서 봤던 정렬하고 출력하는 코드를 실행시키면 정렬된 데이터들이 출력된다. 하지만 데이터 소스인 `strArr` 과 `strList` 는 정렬되지 않는다 (궁금하면 직접 확인해보자).

이는 함수형 프로그래밍에서 데이터의 불변성(immutable)을 만족시킨다.

필요하다면 정렬된 결과를 새로운 컬렉션이나 배열에 담아서 반환할 수도 있다. 이 부분은 뒤에서 자세히 설명할 것이다.

```Java
List<String> sortedStrList = strList.stream()
									.sorted()
									.collect(Collectors.toList());
```

#### 2) 스트림은 일회용이다.
스트림은 `Iterator` 처럼 컬렉션의 요소를 모두 읽고 나면 다시 사용할 수 없다. 필요하다면 스트림을 다시 생성해서 데이터에 접근해야 한다.

```Java
strStream1.sorted().forEach(System.out::println);
strStream1.sorted().forEach(System.out::println); //에러!!!
```

#### 3) 스트림은 내부 반복으로 데이터를 처리한다.
스트림으로 작성한 코드가 더 간결한 이유는 "반복을 위한 코드" 가 스트림 내부에 숨겨져 있기 때문이다. 개발자는 스트림의 데이터에 "무엇을 할 지" 만 선언하면 된다. "어떻게 반복할지" 는 스트림이 알아서 해 준다. 따라서 스트림으로 작성된 코드를 보면 데이터를 처리하는 핵심적인 로직을 쉽게 파악할 수 있다.

#### 4) 중간 연산과 최종 연산
위에서도 언급했지만 스트림이 제공하는 다양한 메소드(연산)를 통해서 복잡한 작업들을 간단하게 처리할 수 있다.

스트림이 제공하는 연산은 **중간 연산**과 **최종 연산**으로 분류할 수 있다.

- **중간 연산**
	- 연산의 결과(메소드의 반환값)가 스트림인 연산이다.
	- 따라서 중간 연산의 결과에 다시 중간 연산을 연쇄적으로 수행할 수 있다.
- **최종 연산**
	- 연산 결과(메소드의 반환값)가 스트림이 아니다.
	- 스트림의 요소를 소모하면서 연산을 수행하므로, 마지막에 단 한번만 연산이 가능하다.
	- 최종 연산을 마치면 스트림이 닫힌다.

다음 예시 코드를 보면 중간 연산과 최종 연산에 대해서 감을 잡을 수 있을 것이다.

```Java
Integer[] integerArr = {5, 5, 3, 9, 2, 11};

Arrays.stream(integerArr) // Stream<Integer>
		.filter(n -> n < 10) // 중간 연산을 연쇄적으로 적용
		.distinct()
		.sorted()
		.limit(3)
		.forEach(System.out::println); // 최종 연산

/* 출력: 2\n3\n5 */
```

`Stream<Integer>` 스트림에 스트림이 제공하는 중간 연산 메소드인 `fiter()`, `distint()`, `sorted()`, `limit()` 를 연쇄적으로 적용한다. 해당 메소드의 반환형은 `Stream` 이기 때문에 이러한 형태의 코드가 가능해진다.

마지막에 적용된 연산 `forEach()` 는 최종 연산이다. 스트림의 요소를 소모하면서 연산이 일어나기 때문에 중간 연산이 모두 끝난 마지막에 단 한번만 적용할 수 있다.

>참고: 지금 당장은 해당 연산들이 정확히 무슨 역할을 하는지 알 필요는 없다. 람다식을 알고, 함수형 프로그래밍을 접해본 적이 있다면 대충 무슨 동작을 하는지 감이 올 것이다. 스트림에서 10 미만의 숫자만 거른 뒤(filter), 중복되는 요소를 제거하고(distinct), 정렬한 뒤(sorted), 앞에서부터 3개의 요소만 추출해서(limit), 각 요소를 순서대로 출력한다(forEach). 

#### 5) 지연된 연산(Lazy Evaluation)
스트림은 최종 연산이 수행되기 전까지 중간 연산이 수행되지 않는다.

```Java
Arrays.stream(integerArr) // Stream<Integer>
		.filter(n -> n < 10) // 중간 연산을 연쇄적으로 적용
		.distinct()
		.sorted()
		.limit(3)
		.forEach(System.out::println); // 최종 연산
```

즉 이 코드에서 `distinct()` 나 `sorted()` 중간 연산을 호출해도 그 즉시 연산이 수행되는 것이 아니다. 중간 연산을 호출하는 것은 단지 어떤 연산이 수행되어야 하는지를 지정해 주는 것 뿐이다. 최종 연산을 수행할 때 지정해 둔 중간 연산들이 수행되고, 그 결과가 최종 연산에서 소모된다.

지연된 연산이 없다면 스트림은 매우 비효율적인 반복을 수행하게 된다. N 번의 중간 연산이 지연된 연산 없이 호출 즉시 실행된다면, 항상 모든 데이터에 N 번 반복해서 접근해야 할 것이다.

하지만 스트림은 지연된 연산을 통해 최종 연산이 일어날 때 까지 연산을 지연시킬 수 있게 되고, 따라서 최종 연산에서 실제로 연산이 수행 될 때 최적화를 해서 반복 횟수를 줄일 수 있게 된다.

자세한 내용은 다음 링크를 참조하길 바란다. 이 내용을 자세히 설명하기에는 지면이 너무나 부족하다 (사실 나도 잘 모르는 부분이기도 하다).

[Lazy Evaluation이란?](https://dororongju.tistory.com/137)
[스트림: 지연 연산과 최적화](https://bugoverdose.github.io/development/stream-lazy-evaluation/)

#### 6) 간편한 병렬 처리
스트림은 내부적으로 병렬 처리를 지원한다. 개발자는 그저 `parallel()` 메소드만 호출하면 된다.

```Java
int sum = intList.stream()
				.parallel()
				.sum();
```

>참고: 병렬로 처리되지 않게 하려면 `sequential()` 을 호출하면 된다. 하지만 스트림은 기본적으로 병렬 처리를 하지 않으므로 병렬 처리를 취소할 때(`parallel()` 호출을 취소할 때)만 사용한다. `paralell()` 과 `sequential()` 은 새로운 스트림을 생성하지 않고 단지 스트림의 속성을 변경하기만 한다.

## 3. 스트림 만들기
스트림으로 작업을 하려면 일단 스트림을 만들어야 한다. 스트림을 생성하는 방법은 매우 다양한데, 가장 기본적으로 `Collection` 이나 배열을 통해 만드는 방법만 알아보자.

>참고: 특정 범위의 수, 난수, 파일 등으로도 스트림을 만들 수 있다. 필요한 경우 검색해보거나 책을 찾아보면 되겠다. 글쓴이도 책에서 일부만 발췌해서 글을 쓰고 있다 ㅎㅎ...

#### 1) Collection
`Collection` 인터페이스에 `stream()` 이 정의되어 있다. 이 메소드는 위에서 계속 봐서 알겠지만 컬렉션으로 스트림을 만드는 것이다. `Collection` 의 자손인 `List`, `Set` 을 구현한 모든 컬렉션 클래스는 `stream()` 을 구현하고 있기 때문에 간편하게 스트림을 생성할 수 있다.

```Java
List<Integer> list = Arrays.asList(1, 2, 3);
Stream<Integer> stream = list.stream();
```

#### 2) 배열
배열로 스트림을 생성하는 메서드는 `Stream` 과 `Arrays` 에 스태틱 메소드로 정의되어 있다.

```Java
Integer[] arr = {1, 2, 3};
Stream<Integer> stream = Arrays.stream(arr);
```

## 4. 스트림의 중간 연산
스트림은 많은 종류의 중간 연산을 제공한다. 각각의 중간 연산을 하나 하나 설명하려고 이 글을 쓴 건 아니긴 한데, 그렇다고 해서 이 부분을 설명 안하면 도대체 스트림을 어떻게 쓰는 건지 감이 잡히지 않을 것이다.

그래서 간단히 몇개만 설명하고, 나머지는 잘 정리된 링크를 첨부하겠다. 아니면 책을 참고해도 좋을 것이다.

[스트림 중간 연산](https://jamie95.tistory.com/54)

#### 1) filter() - 조건에 맞지 않는 스트림 요소 걸러내기
주어진 조건에 맞지 않는 요소를 걸러낸다.

```Java
Stream<T> filter(Predicate<? super T> predicate)
```

`Predicate` 를 인자로 받는 것을 확인할 수 있다. 즉 람다식과 조합해서 다음과 같이 사용할 수 있다.

```Java
Integer[] integerArr = {5, 3, 9, 2, 11};

Arrays.stream(integerArr)
		.filter(n -> n < 10) //10 이상의 수는 거른다
		.filter(n -> n % 2 == 1) //짝수는 거른다
		.forEach(System.out::println); //요소 출력

/* 출력: 5\n3\n9\n */
```

#### 2) distinct() - 중복된 요소 제거
중복된 요소를 제거한다.

```Java
Stream<T> distinct()
```

```Java
Integer[] integerArr = {1, 1, 2, 2, 3, 3, 4, 4, 5, 5};

Arrays.stream(integerArr)
		.distinct()
		.forEach(System.out::print);

/* 출력: 12345 */
```

#### 3) skip(), limit() - 스트림 자르기
스트림의 일부를 잘라낼 때 사용한다. `skip(n)` 은 처음 n개의 요소를 건너 뛰고, `limit(n)` 는 뒤의 스트림의 개수를 n개로 제한한다.

```Java
Stream<T> skip(long n)
Stream<T> limit(long maxSize)
```

```Java
Integer[] integerArr = {1, 2, 3, 4, 5, 6, 7, 8};

Arrays.stream(integerArr)
		.skip(2) //2개 건너 뜀
		.limit(3) //요소 개수 3개로 제한
		.forEach(System.out::print); //3 번째 요소부터 3개의 요소 출력

/* 출력: 345 */
```

#### 4) sorted() - 스트림 정렬
스트림을 정렬한다.

```Java
Stream<T> sorted()
Stream<T> sorted(Compartor<? super T> comparator)
```

`Comparator` 를 지정하지 않으면 스트림 요소의 `comapreTo()`, 그러니까 `Comparable` 인터페이스를 구현한 메소드를 기준으로 정렬한다. `List` 정렬하는 것과 크게 다르지 않다.

#### 5) map() - 스트림 요소 변환
스트림의 각 요소에 저장된 값 중에서 원하는 필드만 뽑아내거나, 요소를 특정 형태로 변환해야 할 때 사용할 수 있다.

```Java
Stream<R> map(Function<? super T, ? extends R> mapper)
```

예를 들어 `String` 스트림의 각 요소를 `Integer` 로 바꾸려면 다음과 같이 하면 된다.

```Java
String[] strArr = {"1", "2", "3"};

Arrays.stream(strArr)
		.map(str -> Integer.parseInt(str))
		.forEach(System.out::print);

/* 출력: 123 */
```

위 코드에서 `map()` 에 넘겨준 람다식은 `str -> Integer.parseInt(str)` 이므로, 스트림 타입이 `map()` 연산에 의해 `Stream<String>` 이 `Stream<Integer>` 로 변환되었다고 볼 수 있다.

## 5. 스트림의 최종 연산
최종 연산은 스트림의 연산을 소모해서 결과를 만들어낸다. 그래서 최종 연산 후에는 스트림이 닫히게 되고, 해당 스트림을 다시 사용할 수 없게 된다.

최종 연산도 종류가 매우 다양한데, 중간 연산 설명과 마찬가지로 몇 가지만 소개하고 자세한 내용은 링크로 대체하겠다. 귀찮아서 그런 것도 크지만, 하나 하나 설명하기에는 책이나 공식 도큐먼트를 옮겨다 쓰는 것과 별반 다르지 않게 느껴져서 그렇다. 이미 잘 설명된 글을 참고하는 것이 훨씬 빠르다.

[스트림의 최종 연산](http://www.tcpschool.com/java/java_stream_terminal)

#### 1) forEach()
앞에서 계속 사용했던 `forEach()` 최종 연산이다. 반환 타입이 `void` 라서 스트림의 요소를 출력하는 용도로 많이 사용된다.

```Java
void forEach(Consumer<? super T> action)
```

#### 2) reduce()
스트림의 요소를 줄여나가면서 연산을 수행하고, 연산의 최종 결과를 반환한다. 스트림의 처음 두 요소를 가지고 연산을 한 뒤, 그 연산의 결과를 가지고 스트림의 다음 요소와 연산한다. 이 것을 계속 반복하면 스트림의 요소가 하나씩 줄어서 최종 연산 결과만 남게 된다.

```Java
Optional<T> reduce(BinaryOperator<T> accumulator)
```

스트림이 비어있을 수도 있으므로 반환형이 `Optional<T>` 이다.

연산 결과의 초기값을 갖는 `reduce()` 도 있는데, 이 메서드는 초기값과 스트림의 첫 번째 요소로 연산을 시작한다. 스트림이 비어있어도 초기값이 존재하기 때문에 반환형이 `Optional<T>` 가 아니라 `T` 이다.

```Java
T reduce(T identity, BinaryOperator<T> accumulator)
//identity는 초기값
```

다음과 같이 사용할 수 있다.

```Java
int count = integerStream.reduce(0, cnt -> cnt + 1);
int sum = integerStream.reduce(0, (n1, n2) -> n1 + n2);
```

#### 3) collect()
스트림의 요소를 수집한다. 단순히 컬렉션과 배열로 반환받을 수도 있고, 개수를 세거나 평균을 낼 수도 있고, 특정 기준으로 그룹화 할 수도 있다.

여기서는 간단하게 컬렉션과 배열로 반환 받는 것만 설명하겠다.

`collect()` 에는 어떻게 요소를 수집할 것인지 매개변수로 넘겨줘야 하는데, 컬렉션과 배열로 요소 수집을 할 경우 다음 코드처럼 `Collectors` 클래스의 스태틱 메소드를 사용하면 매우 편하다. 

```Java
List<String> strStream.collect(Collectors.toList());
List<String> strStream.collect(Collectors.toArray(new String[]));
```


#### 참고
남궁 성, 『자바의 정석』, 도우출판(2016)
[자바의 정석](https://book.interpark.com/product/BookDisplay.do?_method=detail&sc.shopNo=0000400000&sc.prdNo=249927409&utm_source=google&utm_medium=cpc&utm_campaign=book_domestic_majorbook_s_20210617_pc_cpc_paidsearch&utm_content=consider_34&utm_term=%EC%9E%90%EB%B0%94%EC%9D%98%EC%A0%95%EC%84%9D&utm_term=%EC%9E%90%EB%B0%94%EC%9D%98%EC%A0%95%EC%84%9D&gclid=CjwKCAjwqJSaBhBUEiwAg5W9p_zJ8fuTlad1BC4fy9Y2CZ4crFX5zON47142QInLYr-MzGapRF-NrxoCOmIQAvD_BwE)