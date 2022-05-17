# HTTP 요청 데이터 - POST & HTML Form
HTML의 Form을 사용해서 클라이언트에서 서버로 데이터를 전송해보자.

### 특징
- 메시지 바디에 쿼리 파리미터 형식으로 데이터를 전달한다.
	- 예) `username=hello&age=20`
- `Content-Type: application/x-www-form-urlencoded`

HTML Form을 사용한 데이터 전송을 위해 `src/main/webapp/basic/hello-form.html` 을 생성하자.

##### src/main/webapp/basic/hello-form.html
```HTML
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Title</title>
</head>
<body>
<form action="/request-param" method="post">
	username: <input type="text" name="username" />
	age: <input type="text" name="age" />
	<button type="submit">전송</button>
</form>
</body>
</html>
```
- POST 메소드를 사용하고, 요청을 보낼 URL은 `/request-param` 임을 확인할 수 있다.

요청을 보낸 뒤 콘솔 출력을 확인해보면,
```text
[전체 파라미터 조회] - start
username = kloongPost
age = 20
[전체 파라미터 조회] - end

[단일 파라미터 조회] - start
username = kloongPost
age = 20
[단일 파라미터 조회] - end

[이름이 같은 복수 파라미터 조회] - start
username = kloongPost
[이름이 같은 복수 파라미터 조회] - end
```
HTML Form에 담은 내용들이 잘 출력되는 것을 확인할 수 있다.

GET & 쿼리 파라미터 조회를 위해 작성했던 `RequestParamServlet` 객체를 그대로 사용할 수 있는 것을 알 수 있다.

### 정리
`Content-Type: application/x-www-form-urlencoded` 형식은 앞서 GET에서 살펴본 쿼리 파라미터의 데이터 형식과 같다. **따라서 쿼리 파라미터 조회 메서드를 그대로 사용할 수 있다.**  
클라이언트(웹 브라우저) 입장에서는 두 방식에 차이가 있지만, 서버 입장에서는 두 방식의 데이터 포맷이 동일하므로, `request.getParameter()` 로 두 방식 모두 조회할 수 있다.  

즉 `request.getParameter()` 는 GET URL 쿼리 파라미터 형식도 지원하고, POST HTML Form
형식도 지원한다.

>**참고**
>Content-Type 헤더는 HTTP 메시지 바디의 데이터 형식을 지정한다.
>GET URL 쿼리 파라미터 형식으로 클라이언트에서 서버로 데이터를 전달할 때는, HTTP 메시지 바디를 사용하지 않기 때문에 content-type 헤더가 존재하지 않는다.
>반면에 POST HTML Form 형식으로 데이터를 전달하게 되면, HTTP 메시지 바디에 해당 데이터를 포함해서 보내기 때문에 바디에 포함된 데이터의 포맷 정보를 서버에 함께 전달해야 한다. 즉 content-type 헤더를 꼭 넣어줘야 한다.
>이렇게 HTML Form으로 데이터를 전송하는 형식을 `application/x-www-form-urlencoded` 라 한다.


##### Postman을 사용한 테스트
HTML Form 테스트를 위해 HTML 파일을 만드는 것은 너무 귀찮은 일이다. 이 떄는 Postman을 사용하면 된다.