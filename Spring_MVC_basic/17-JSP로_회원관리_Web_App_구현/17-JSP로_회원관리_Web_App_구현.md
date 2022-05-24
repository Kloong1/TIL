# JSP로 회원관리 Web App 구현

## JSP 라이브러리 추가
JSP를 사용하기 위해 라이브러리를 추가하자.

##### build.gradle
```
//생략

dependencies {
	implementation 'org.springframework.boot:spring-boot-starter-web'

	//JSP 추가 시작
	implementation 'org.apache.tomcat.embed:tomcat-embed-jasper'
	implementation 'javax.servlet:jstl'
	//JSP 추가 끝

	compileOnly 'org.projectlombok:lombok'
	annotationProcessor 'org.projectlombok:lombok'
	providedRuntime 'org.springframework.boot:spring-boot-starter-tomcat'
	testImplementation 'org.springframework.boot:spring-boot-starter-test'

}

//생략
```


## 회원 정보 입력 폼 JSP
`main/webapp/` 의 하위 경로에 jsp 파일을 만들어야 한다.  
`main/webapp/jsp/members/` 에 `new-form.jsp` 파일을 만들자.

##### new-form.jsp
```html
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<form action="/jsp/members/save.jsp" method="post">
    username:   <input type="text" name="username" />
    age:        <input type="text" name="age" />
    <button type="submit">전송</button>
</form>
</body>
</html>
```
- `<%@ page contentType="text/html;charset=UTF-8" language="java" %>`
	- 해당 문서가 JSP 문서라는 것을 의미한다. JSP 문서는 이렇게 시작되어야 한다.
- `http://localhost:8080/jsp/members/new-form.jsp` 로 요청을 보내면 이 파일을 얻을 수 있다.
	- `main/webapp` 의 하위 경로의 모든 JSP 파일을 위와 같은 방식으로 요청할 수 있다.
	- `.jsp` 확장자를 반드시 함께 기입해야 한다.
- 회원 등록 폼 JSP를 보면 첫 줄을 제외하고는 완전히 HTML와 똑같다. JSP는 서버 내부에서 서블릿으로 변환되는데, 우리가 만들었던 MemberFormServlet과 거의 비슷한 모습으로 변환된다.


## 회원 정보 저장 JSP
`main/webapp/jsp/members/` 에 `save.jsp` 파일을 만들자.

##### save.jsp
```HTML
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.kloong.servlet.domain.member.MemberRepository" %>
<%@ page import="com.kloong.servlet.domain.member.Member" %>

<%
    //request, response (HttpServletRequest, HttpServletResponse 객체)는
    //jsp에서도 사용 가능하다. servlet으로 자동으로 변환되기 때문.

    MemberRepository memberRepository = MemberRepository.getInstance();

    System.out.println("save.jsp");
    String username = request.getParameter("username");
    int age = Integer.parseInt(request.getParameter("age"));

    Member member = new Member(username, age);
    System.out.println("member = " + member);
    memberRepository.save(member);

%>

<html>
<head>
<meta charset="UTF-8">
</head>
<body>
성공
<ul>
	<!-- 자바 코드의 결과값 그대로 출력 -->
    <li>id=<%=member.getId()%></li>
    <li>username=<%=member.getUsername()%></li>
    <li>age=<%=member.getAge()%></li>
</ul>
<a href="/index.html">메인</a>
</body>
</html>
```

- JSP에서는 자바 코드를 그대로 다 사용할 수 있다.
- `<%@ page import="hello.servlet.domain.member.MemberRepository" %>`
	- 자바의 import 문과 같다.
- `<% ~~ %>`
	- 이 표시 안에서 자바 코드를 입력할 수 있다.
- `<%= ~~ %>`
	- 이 표시 안에서 자바 코드의 결과값을 출력할 수 있다.
- 회원 저장 JSP를 보면, 회원 저장 서블릿 코드와 같다. 다른 점이 있다면, HTML을 중심으로 하고, 자바 코드를 부분부분 입력해주었다. `<% ~ %>` 를 사용해서 HTML 중간에 자바 코드를 출력하고 있다.

## 회원 목록 JSP
`main/webapp/jsp/` 에 `members.jsp` 파일을 만들자.

##### members.jsp
```HTML
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.kloong.servlet.domain.member.MemberRepository" %>
<%@ page import="com.kloong.servlet.domain.member.Member" %>

<%
    MemberRepository memberRepository = MemberRepository.getInstance();
    List<Member> members = memberRepository.findAll();
%>

<html>
<head>
<meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
<a href="/index.html">메인</a>
<table>
    <thead>
    <th>id</th>
    <th>username</th>
    <th>age</th>
    </thead>
    <tbody>
    <%
        for (Member member : members) {
            out.write(" <tr>");
            out.write(" <td>" + member.getId() + "</td>");
            out.write(" <td>" + member.getUsername() + "</td>");
            out.write(" <td>" + member.getAge() + "</td>");
            out.write(" </tr>");
        }
    %>
    </tbody>
</table>
</body>
</html>
```


## Servlet과 JSP의 한계
서블릿으로 개발할 때는 뷰(View) 화면을 위한 HTML을 만드는 작업이 자바 코드에 섞여서 지저분하고 복잡했다.

그래서 서블릿 대신 JSP를 사용했는데, 이 때는 뷰를 생성하는 HTML 작업을 깔끔하게 가져가고, 중간중간 동적으로 변경이 필요한 부분에만 자바 코드를 적용했다.

그런데 이렇게 해도 해결되지 않는 몇가지 고민이 남는다.

회원 저장 JSP를 보자. 코드의 상위 절반은 회원을 저장하기 위한 비즈니스 로직이고, 나머지 하위 절반만 결과를 HTML로 보여주기 위한 뷰 영역이다. 회원 목록의 경우에도 마찬가지다.

코드를 잘 보면 JAVA 코드, 데이터를 조회하는 리포지토리 등등 다양한 코드가 모두 JSP에 노출되어 있다.

JSP가 너무 많은 역할을 한다. 이렇게 작은 프로젝트도 벌써 머리가 아파오는데, 수백 수천줄이 넘어가는 JSP 파일을 개발하고 유지보수하려면 정말 끔찍할 것이다.


## MVC 패턴의 등장
비즈니스 로직은 서블릿처럼 다른 곳에서 처리하고, JSP는 목적에 맞게 HTML로 화면(View)을 그리는 일에 집중하도록 하는 것이 좋지 않을까? 과거 개발자들도 모두 비슷한 고민이 있었고, 그래서 MVC 패턴이 등장했다. 우리도 직접 MVC 패턴을 적용해서 프로젝트를 리팩터링 해보자.