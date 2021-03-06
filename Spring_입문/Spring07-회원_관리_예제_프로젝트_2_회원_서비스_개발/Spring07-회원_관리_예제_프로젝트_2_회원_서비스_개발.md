# 회원 관리 예제 프로젝트 2 - 회원 서비스 개발 및 테스트

#### 프로젝트 디렉토리 구조
![400](스크린샷%202022-03-02%20오후%203.54.43.png)

## 1. 회원 서비스 개발

#### MemberService.java
![](스크린샷%202022-03-02%20오전%2012.49.53.png)

#### 서비스 코드 작성 시 주의할 점
서비스 코드는 비즈니스 로직을 구현한 코드이다. 즉 실제 서비스에 가장 가까운 코드이기 때문에, method 명을 실제 서비스에 가깝게 지어야 한다. 그래야 "로그인 에서 문제가 있다"는 보고가 있을 때, 바로 `join` method를 찾아서 확인할 수 있기 때문이다. 

## 2. 회원 서비스 테스트

#### MemberServiceTest.java
![](스크린샷%202022-03-02%20오후%203.53.24.png)
![](스크린샷%202022-03-02%20오후%203.53.39.png)
![](스크린샷%202022-03-02%20오후%203.53.49.png)

#### given - when - then 구조
테스트 method 작성 시 **given - when - then** 구조를 따르는 것이 method 작성에 편리하다. 물론 안 맞는 경우도 있긴 하지만 대부분 적용 가능하므로, 테스트 method를 작성할 때 주석으로 아래와 같이 틀을 만들어 둔 후 작성하자.

```Java
@Test
void testMethod()
{
	//given

	//when

	//then

}
```

#### assertThrows()
테스트 하려는 method에서 예외 발생 시 `throws` 를 하는 경우, 해당 예외가 정상적으로 발생하는지 확인하는 것을 테스트 할 때 `assertThrows` method를 사용하는 것이 편리하다. `try - catch` 구문을 쓰는 것은 코드도 길어지고 가독성도 떨어지기 때문.

#### \*Dependency Injection (DI)
위 코드의 주석을 참조할 것.