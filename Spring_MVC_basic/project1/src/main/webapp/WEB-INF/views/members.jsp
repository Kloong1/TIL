<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%> <!-- JSP가 지원하는 특수한 태그를 사용하기 위한 import문이라고 보면 된다-->
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