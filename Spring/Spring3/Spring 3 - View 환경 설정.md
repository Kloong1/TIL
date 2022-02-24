# View 환경 설정

## 0. Spring Boot Documents
Spring Boot는 아주 큰 규모의 프레임워크이기 때문에, 모든 기능을 다 외우고 있는 것은 불가능하다. 따라서 원하는 기능을 잘 찾는 능력이 매우 중요하다!

https://spring.io/ -> Projects -> Spring Boot -> 화면 중앙의 Learn -> 원하는 버전의 Reference Doc.

여기서 원하는 기능을 찾기 위해 키워드로 검색을 하거나 해당하는 document를 찾을 줄 알아야 한다.


## 1. Welcome page - static page

프로젝트의 src/main/resources/static 디렉토리에 index.html 파일을 넣어 두면 welcome page로 제공할 수 있다.


## 2. Welcome page - templete engine 사용

### 0) thymeleaf 템플릿 엔진
템플릿 엔진(여기서는 thymeleaf)으로 welcome page를 제공하는 방법은 아래 페이지에서 찾아볼 수 있다. 
 1. thymeleaf 공식 사이트 (https://www.thymeleaf.org/) 에 접속해서 튜토리얼 보기
 2. Spring 공식 튜토리얼 (https://spring.io/guides/gs/serving-web-content/)
 3. Spring Boot 메뉴얼 (https://docs.spring.io/spring-boot/docs/current/reference/html/web.html#web.servlet.spring-mvc.template-engines)


### 1) Controller 만들기
src/main/java에 controller package를 만든다 (src/main/java/controller). 이 패키지에 HelloController.java 파일을 만든다.

![400](스크린샷%202022-02-24%20오후%2011.22.01.png)

HelloController.java의 내용

![](스크린샷%202022-02-24%20오후%2011.19.08%201.png)

### 2) html 파일 만들기 (실제로 보여줄 화면))
src/resources/templates에 hello.html (여기서 파일 명은 위에서 작성한 method의 return 문자열과 동일해야 함)을 만든다.

hello.html의 내용

![](스크린샷%202022-02-24%20오후%2011.24.49.png)

thymeleaf 문법을 사용하겠다고 선언한다.
그리고 controller에서 model에 추가한 attribute인 "data" attribute의 value를 불러왔다 (${data} 부분)

### 3) thymeleaf 템플릿 엔진의 동작 확인하기
Spring Boot Application을 실행하고, 웹브라우저에서 localhost:8080/hello로 요청을 보내면

![400](스크린샷%202022-02-24%20오후%2011.29.06.png)

이렇게 ${data}가 "data" attribute의 value인 "hello!!"로 치환됨을 확인할 수 있다. 이렇게 thymeleaf 템플릿 엔진이 동작하고 있음을 볼 수 있다.


## 3. 동작 원리 (간단한 버전)
![](Pasted%20image%2020220224233145.png)

 1. 웹 브라우저에서 localhost:8080/hello로 요청을 보냄
 2. Spring Boot에서 내장하고 있는 웹서버인 Tomcat이 해당 요청을 받음
 3. Tomcat이 받은 요청을 Spring 한테 던짐
 4. Spring이 HelloCotroller에 GetMapping("hello") - hello에 대한 GET 요청이 들어오면 해당되는 method 실행 - 된 method를 실행
 5. HelloController가 모델에 "data" attribute를 추가함. value는 "hello!!"
 6. "hello"를 return함.
 7. 컨트롤러에서 문자열을 반환하면, viewResolver가 해당 문자열에 해당하는 화면을 찾아서 그 화면을 처리해서 보여준다. Spring Boot에서 템플릿 엔진의 기본 viewName mapping은  resources/templates/ + {viewName} + .html 이다. (화면 처리 부분을 thymeleaf가 한다)
 9. 처리된 hello.html을 사용자가 확인한다.


#### Tag
[[Spring]]