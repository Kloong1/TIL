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
    <li>id=<%=member.getId()%></li>
    <li>username=<%=member.getUsername()%></li>
    <li>age=<%=member.getAge()%></li>
</ul>
<a href="/index.html">메인</a>
</body>
</html>