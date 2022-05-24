# MVC 패턴 - 적용

## 역할 분담
- **Controller**: Servlet이 담당한다.
- **View**: JSP가 담당한다.
- **Model**: HttpServletRequest 객체를 사용한다. 이 객체는 내부에 데이터 저장소를 가지고 있는데, `request.setAttribute()` , `request.getAttribute()` 를 사용하면 데이터를 보관하고, 조회할 수 있다.


## 회원 정보 입력
### 회원 정보 입력 Form - Controller
##### MvcMemberFromServlet.java
```Java
package com.kloong.servlet.web.servletmvc;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "mvcMemberFormServlet", urlPatterns = "/servlet-mvc/members/new-form")
public class MvcMemberFromServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String viewPath = "/WEB-INF/views/new-form.jsp";
        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
        dispatcher.forward(request, response);
    }
}
```
- 회원 정보 입력 화면의 경우 서블릿이 실행해야 할 로직이 존재하지 않는다. 따라서 요청이 들어오면 바로 view를 호출한다.
- `dispatcher.forward()` 메서드로 다른 서블릿이나 JSP로 이동할 수 있다. 이 때 **서버 내부에서 다시 호출이 발생한다 (Redirecting이 아닌 Forwarding).**

##### 참고: Redirect vs Forward
Redirect는 클라이언트(웹 브라우저)의 요청에 의해 응답이 가고, 응답 메시지에 들어있는 redirect 경로로 클라이언트가 다시 요청을 하는 경우다. 따라서 **클라이언트가 redirect 여부를 인지할 수 있고, URL 경로도 실제로 변경된다.** 반면에 **Forward는 서버 내부에서 일어나는 호출이기 때문에 클라이언트가 전혀 인지하지 못한다.**

>**/WEB-INF**
>이 경로안에 JSP 파일이 있으면 외부에서 해당 경로로 요청을 보내서 직접 JSP를 호출할 수 없다. 우리가 기대하는 것은 항상 컨트롤러를 통해서 JSP 파일을 호출하는 것이기 때문에, 외부 직접 호출을 막기 위해 이 경로 안에 JSP 파일을 넣었다.

### 회원 정보 입력 Form - View
##### main/webapp/WEB-INF/views/new-form.jsp
```HTML
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
<meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
<!-- 상대경로 사용, [현재 URL이 속한 계층 경로 + /save] -->
<form action="save" method="post">
    username:   <input type="text" name="username" />
    age:        <input type="text" name="age" />
    <button type="submit">전송</button>
</form>
</body>
</html>
```

여기서 form의 action을 보면 절대 경로(/ 로 시작)가 아니라 상대경로(/ 로 시작 X)인 것을 확인할 수있다. 이렇게 상대경로를 사용하면 Form 전송시 `현재 URL이 속한 계층 경로 + /save` URL이 호출된다. 보통 절대 경로를 쓰는데, 여기서는 추후 프로젝트에서 재사용하기 위해 상대 경로를 사용했다.
현재 URL 계층 경로: `/servlet-mvc/members/`  
전송 버튼 누른 후: `/servlet-mvc/members/save`


## 회원 정보 저장

### 회원 정보 저장 - Controller
##### MvcMemberSaveServlet.java
```Java
package com.kloong.servlet.web.servletmvc;

import com.kloong.servlet.domain.member.Member;
import com.kloong.servlet.domain.member.MemberRepository;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "mvcMemberSaveServlet", urlPatterns = "/servlet-mvc/members/save")
public class MvcMemberSaveServlet extends HttpServlet {

    private MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        int age = Integer.parseInt(request.getParameter("age"));

        Member member = new Member(username, age);
        memberRepository.save(member);

        //Model에 데이터 보관
        request.setAttribute("member", member);

        String viewPath = "/WEB-INF/views/save-result.jsp";
        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
        dispatcher.forward(request, response);
    }
}
```
- HttpServletRequest 객체를 Model로 사용한다.
	- `request.setAttribute()` 메서드를 사용해서 request 객체에 데이터를 보관할 수 있다.
	- View 에서는 `request.getAttribute()` 메서드로 데이터를 꺼내서 쓰면 된다.
	- 즉 view는 비즈니스 로직에 의존하지 않고 오로지 model에서 데이터만 꺼내서 쓰면 된다.

### 회원 정보 저장 - View
##### main/webapp/WEB-INF/views/save-result.jsp
```HTML
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
<meta charset="UTF-8">
</head>
<body>
성공
<ul>
    <li>id=${member.id}</li>
    <li>username=${member.username}</li>
    <li>age=${member.age}</li>
</ul>
<a href="/index.html">메인</a>
</body>
</html>
```
- JSP에서 자바 코드를 실행해서 `<%=((Member)request.getAttribute("member")).getId()%>` 이런 식으로 request 객체에 저장된 Member 객체를 꺼내서 쓰는 방법도 있다.
- 하지만 너무 복잡하므로, JSP에서 지원하는 property 접근 방식의 문법을 활용하는 것이 좋다.
	- `${attribute이름.변수이름}`: 이렇게만 하면 request에서 데이터를 꺼내와서, 변수 이름에 해당하는 getter를 호출하는 것과 동일한 결과를 얻을 수 있다.

이런 식으로 Controller 로직과 View 로직을 확실하게 분리할 수 있다. 이후에 화면에 수정이 필요하면 view 로직만 변경하면 된다.


## 회원 목록 조회

### 회원 목록 조회 - Controller
##### MvcMeberListServlet.java
```Java
package com.kloong.servlet.web.servletmvc;

import com.kloong.servlet.domain.member.Member;
import com.kloong.servlet.domain.member.MemberRepository;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "mvcMemberListServlet", urlPatterns = "/servlet-mvc/members")
public class MvcMemberListServlet extends HttpServlet {

    MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Member> members = memberRepository.findAll();

        request.setAttribute("members", members);

        String viewPath = "/WEB-INF/views/members.jsp";
        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
        dispatcher.forward(request, response);
    }
}
```

### 회원 목록 조회 - View
##### main/webapp/WEB-INF/views/members.jsp
```HTML
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!-- JSP가 지원하는 특수한 태그를 사용하기 위한 import문이라고 보면 된다-->

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
    <!-- JSP가 지원하는 특수한 태그로 루프 적용 -->
    <c:forEach var="member" items="${members}">
        <tr>
        <td>${member.id}</td>
        <td>${member.username}</td>
        <td>${member.age}</td>
        </tr>
    </c:forEach>
    </tbody>
</table>
</body>
</html>
```
- 모델에 담아둔 members를 JSP가 제공하는 taglib기능을 사용해서 반복하면서 출력했다.
- members list의 요소를 순서대로 꺼내서 member변수에 담고 출력하는 과정을 반복한다.
- `<c:forEach>` 기능을 사용하려면 다음과 같이 선언해야 한다.
	- `<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>`
- 기존처럼 `<% %>` 와 자바 코드를 사용해서 출력해도 되지만, 위 기능을 사용하는 것보다 훨씬 길고 지저분하다.