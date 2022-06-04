# RequestMappingHandlerAdapter 구조

이전 강의에서 배운 HttpMessageConverter는 어디서 호출되고 동작하는 것일까?

##### Spring MVC 구조
![](스크린샷%202022-06-04%20오후%2010.40.03.png)

위 그림에서는 찾아볼 수가 없다.

사실 모든 비밀은 애노테이션 기반의 컨트롤러, 즉 `@RequestMapping` 핸들러를 처리하는 핸들러 어댑터 `RequestMappingHandlerAdapter` 에 있다.