# Lambda Expression

## 서론 - 함수형 프로그래밍 언어
Java는 객체지향언어로 만들어졌지만, JDK 1.8 부터 추가된 다양한 기능으로 인해 객체지향언어인 동시에 함수형 언어가 되었다.

Lambda expression, Stream 등을 잘 활용하면 Java에서도 함수형 프로그래밍이 가능하다.

함수형 언어가 무엇인지, 그리고 함수형 언어의 장점이 무엇인지 궁금하다면 아래 링크를 참조하면 되겠다.

[함수형 프로그래밍 언어가 대체 무엇일까?](https://velog.io/@jeanbaek/%ED%95%A8%EC%88%98%ED%98%95-%ED%94%84%EB%A1%9C%EA%B7%B8%EB%9E%98%EB%B0%8D-%EC%96%B8%EC%96%B4%EA%B0%80-%EB%8C%80%EC%B2%B4-%EB%AC%B4%EC%97%87%EC%9D%BC%EA%B9%8C)
[함수형 프로그래밍이란?](https://jongminfire.dev/%ED%95%A8%EC%88%98%ED%98%95-%ED%94%84%EB%A1%9C%EA%B7%B8%EB%9E%98%EB%B0%8D%EC%9D%B4%EB%9E%80)

위 링크들의 핵심 키워드만 정리하자면 다음과 같다.

1. 순수 함수 (함수 외부로 Side-effect가 없음)
2. Stateless (함수에 상태가 존재하지 않음. 따라서 같은 인자가 들어오면 항상 같은 결과를 반환함)
3. Immutable (데이터의 불변성. 변경이 필요하면 복사해서 사용)
4. 고차 함수(함수를 값처럼 사용. 인자로 전달하거나 반환값으로 사용할 수 있다)
5. Lazy evalutation (함수를 값처럼 전달할 수 있기 때문에 함수의 결과값이 실제로 필요할 때 함수를 실행하면 된다. 자원 절약과 최적화에 도움이 된다)

## 1. 람다식(Lambda Expression) 이란 무엇인가?
일단 가장 먼저 "lamda" 가 아니라 "lambda" 임에 주목하자. 한국말로 발음하면 "람다" 라서 'b' 가 있다는 사실을 잊어버리기 쉽다.

아무튼 Lambda expression, 즉 람다식은 간단히 말해서 Java의 **method를 하나의 식(Expression)으로 표현한 것**이라고 할 수 있다.

람다식은 메소드와 달리 이름도 없고 반환형도 없으므로, 람다식을 익명 함수(Anonymous function) 이라고 한다.

객체지향언어에서는 function 대신 method라는 용어를 사용한다. method는 객체의 행위를 의미하는데, 이는 method가 객체에 종속되어 있음을 암시한다. 객체가 중심이 되는 객체지향언어이기 때문에 당연하다.

하지만 람다식은 그 자체로 하나의 독립적인 기능을 할 수 있다. 람다식을 만들기 위해 클래스를 작성하고 객체를 생성할 필요가 없다. 람다식은 특정 객체에 종속되지 않는다. 따라서 람다식은 method가 아니라 function이라고 불릴 수 있게 된 것이다!

## 2. 람다식 작성하기
람다식은 메소드에서 이름과 반환형을 제거한 형태와 비슷하다. 매개변수 선언부를 `()`로 감싸고, 메소드 내용을 `{}` 로 감싼 뒤 둘 사이를 `->` 로 연결해주면 된다.

```Java
int max(int a, int b) {
	return a > b ? a : b;
}
```

위 메소드를 다음과 같이 변경할 수 있다.

```Java
(int a, int b) -> { return a > b ? a : b; }
```

반환값이 있는 경우 `return` 대신 식(Expression) 형태로 작성 가능하다. 이 경우에는 문장(Statement)가 아닌 식이므로 끝에 `;` 을 붙이지 않는다.

```Java
(int a, int b) -> a > b ? a : b
```

람다식에 선언된 매개변수 타입은 추론이 가능한 경우 생략할 수 있는데, 대부분이 생략 가능하다. 반환타입이 따로 없는 이유도 항상 추론이 가능하기 때문이라고 한다.

```Java
(a, b) -> a > b ? a : b
```

또 매개변수가 하나 뿐이면 `()` 괄호를 생략할 수 있다. 단 매개변수의 타입이 있으면 생략할 수 없다.

```Java
val -> val * val
```

또 `{}` 안의 문장이 하나일 때는 `{}` 를 생략할 수 있다.

```Java
name -> System.out.println(name)
```

이 문법들을 다 외울 필요는 없다. 근데 이 문법들을 알고 있으면 이 문법을 사용한 람다식을 쉽게 읽을 수 있고, 또 필요에 따라서 람다식을 짧게 만들 수 있는 것 같다. 물론 짧다고 항상 좋은 건 아니지만 말이다.

## 3. 함수형 인터페이스(Functional Interface)
그런데 갑자기 의문이 하나 생긴다. Java는 분명히 객체지향언어이다. 따라서 모든 method는 클래스 내에 포함되어 있어야 한다. 그런데 이름도, 소속도 없는 람다식은 도대체 어떤 클래스에 포함되어 있는 것일까?

위의 설명만 보면 람다식이 독립적인 메소드 같은 존재처럼 보이지만, **사실 내부적으로는 익명 클래스의 객체이다.**

```Java
@FunctionalInterface
interface MyFunction {
	int max(int a, int b);
}

public class App {
	public static void main(String[] args) {
		MyFunction f1 = new MyFunction() {
			public int max(int a, int b) {
				return a > b ? a : b;
			}
		};
		int big1 = f1.max(5, 3); //big1 == 5
		
		MyFunction f2 = (a, b) -> a > b ? a : b;
		int big2 = f2.max(5, 3); //big2 == 5
	}
}
```

`MyFunction` 인터페이스를 구현한 익명 클래스의 객체 `f1` 을 생성한 뒤 `f1.max()` 를 호출했다.

그런데 람다식 `(a, b) -> a > b ? a : b` 을 `MyFunction` 인터페이스 타입의 변수 `f2` 로 참조시킨 뒤 `f2.max()` 를 호출하는 것도 가능하다.

왜냐하면 **람다식은 실제로 익명 객체이고, `max()` 메소드의 매개변수의 타입과 개수, 그리고 반환형이 람다식의 그것과 일치하기 때문이다.** 

람다식을 이렇게 하나의 메서드가 선언된 인터페이스로 다루는 것은 기존의 객체지향언어인 자바와 잘 어우러진다.

이런 인터페이스를 **함수형 인터페이스(Functional Interface)** 라고 한다.

함수형 인터페이스에는 반드시 하나의 추상 메소드만 정의되어 있어야 한다는 제약이 있다. 그래야 람다식과 메소드가 1:1로 대응되기 때문이다 (단 static 메소드와 default 메소드의 개수에는 제약이 없다).

>참고: 함수형 인터페이스에 `@FunctionalInterface` 어노테이션을 붙이면 컴파일러가 함수형 인터페이스를 올바르게 정의했는지 확인해주므로 사용하는 것이 좋다.

### 함수형 인터페이스 타입의 매개변수와 반환형
함수형 인터페이스를 매개변수와 반환형으로 사용함으로써 람다식을 매개변수로 넘기고, 반환값으로 전달받는 것이 가능해진다. 이는 위에서 언급했던 함수형 언어의 특징을 만족시킨다.

```Java
@FunctionalInterface
interface MyFunction {
	void run();
}

public class MyClass {

	static void myMethod(MyFunction f) {
		f.run();
	}

	static MyFunction returnMyFunction() {
		return () -> System.out.println("hello");
	}

	public static void main(String[] args) {
		myMethod(() -> System.out.println("hello"));
		returnMyFunction().run();
		
	}
}
```

위와 같은 것들이 가능해진다. 지금 당장은 별로 쓸모 없어 보이는 것 같지만, 당장 Stream만 사용하더라도 위의 코드처럼 람다식을 매개변수로 넘기는 일이 부지기수이다. 적재적소에 사용하기만 하면 매우 편리하다는 것을 알 수 있을 것이다.

## 4. java.util.function 패키지 - 함수형 인터페이스 모음
대부분의 메서드는 형태가 비슷하다. 일반적으로 0~2개의 매개변수를 가지고, 반환값은 없거나 1개이다. Generic을 사용하면 매개변수 타입이나 반환형이 달라도 상관 없다.

그래서 Java는 `java.util.function` 패키지에 일반적으로 자주 쓰이는 형태의 함수형 인터페이스를 정의해두었다. 람다식을 쓸 때 매번 새로운 함수형 인터페이스를 정의할 필요 없이 이 패키지의 함수형 인터페이스를 가져다 쓰면 된다.

실제로 패키지를 열어보면 수십개의 함수형 인터페이스가 들어있는 것을 확인할 수 있다. 자주 쓰이는 가장 기본적인 함수형 인터페이스만 확인해보자.

```Java
@FunctionalInterface  
public interface Supplier<T> {  
	T get();  
}

@FunctionalInterface  
public interface Consumer<T> {
	void accept(T t);
	//기타 default, static method 생략...
}

@FunctionalInterface 
public interface Function<T, R> {
	R apply(T t);
	//기타 default, static method 생략...
}

@FunctionalInterface  
public interface Predicate<T> {
	boolean test(T t);
	//기타 default, static method 생략...
}
```

## 5. 메소드 참조(Method Reference)
하나의 메서드만 호출하는 람다식은 메서드 참조를 통해서 더 간결하게 표현이 가능하다.

```Java
Function<String, Integer> f = s -> Integer.parseInt(s);
```

컴파일러는 다음과 같은 경우 `Integer.parseInt()` 메소드의 선언부와 `Function<T, R>` 의 generic type 을 통해 매개변수의 개수와 타입, 반환형의 타입을 추론할 수 있다.

따라서 다음과 같은 메소드 참조 문법을 제공한다.

```
Function<String, Integer> f = Integer::parseInt;
```

다음과 같은 경우도 마찬가지로 메소드 참조가 가능하다.

```Java
//기존 람다식
BiFunction<String, String, Boolean> f = (s1, s2) -> s1.equals(s2);

//메소드 참조
BiFunction<String, String, Boolean> f = String::equals;
```

메소드의 선언부와 generic type을 통해 추론이 가능하기 때문에 이렇게 간단하게 표현이 가능해진다.

이미 생성된 객체 인스턴스의 메소드를 사용하는 람다식의 경우에도 메소드 참조가 가능하다.

```Java
MyClass obj = new MyClass();

//람다식
Function<String, Boolean> f = x -> obj.equals(x);

//메소드 참조
Function<String, Boolean> f = obj::equals;
```

### 생성자의 메소드 참조
생성자를 호출하는 람다식도 메소드 참조로 변환할 수 있다.

```Java
//람다식
Supplier<MyClass> s = () -> new MyClass();

//메소드 참조
Supplier<MyClass> s = MyClass::new;

/* 배열 생성 */
//람다식
Function<Integer, int[]> f = n -> new int[n];

//메소드 참조
Function<Integer, int[]> f = int[]::new;
```

## 6. 마치며
Java를 가지고 굳이 순수한 함수형 프로그래밍을 추구하지 않는 이상은 람다식의 필요성을 느끼지 못할 수 있다. 코드가 간결해지는 정도의 장점만 가지고 있는 것처럼 보이지만, 함수형 인터페이스를 지원하는 객체, 메소드와 함께 사용하면 람다식의 강력함을 알 수 있다.

Stream과 람다식을 잘 조합하면 아주 간결하고 명확한 동시에, side-effect가 없고, stateless 하고, 데이터를 변경하지 않으며(immutable), lazy evaluation을 통해 최적화까지 가능한 코드를 작성할 수 있다.

자세한 내용은 곧 포스팅할 예정인 Stream과 Optional에 대한 글을 보면 알 수 있을 것이다.