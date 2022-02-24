# Spring 라이브러리 살펴보기 (라이브러리 찍먹)


## 1. 프로젝트에 나도 모르는 라이브러리가?
![](스크린샷%202022-02-24%20오후%202.36.20%201%201.png)
Spring initializr로 프로젝트를 만들 때 내가 추가한 라이브러리는 build.gradle의 dependencies를 보면 확인할 수 있다.

그런데 External Libraries를 보면 내가 추가하지 않은 수많은 라이브러리가 존재함을 발견할 수 있다.

![300](스크린샷%202022-02-24%20오후%202.34.35%201%201.png)

## 2. Dependency 관리
왜냐하면 바로 dependency를 관리해주는 tool인 gradle(혹은 Maven)이 dependency를 확인해서 필요한 라이브러리를 전부 땡겨와 주기 때문!

예를 들어 sptring boot starter web을 땡겨오면, 거기에 필요한 tomcat도 땡겨오고, 그러면 tomcat이 필요한 dependency를 또 땡겨오고... 이런식으로 재귀적으로 전부 땡겨오게 된다.

이런 작업을 gradle 같은 tool이 해준다.

![](스크린샷%202022-02-24%20오후%202.41.18%201.png)

Spring Boot 라이브러리를 땡겨 오면, Spring core 라이브러리까지 땡겨오고, 로깅을 하는 logback과 slf4j 라이브러리도 함께 땡겨 온다.

![](스크린샷%202022-02-24%20오후%202.46.25%201.png)



[[Spring]]