# HTTP 요청 데이터 - 개요
HTTP 요청 메시지를 통해 클라이언트에서 서버로 데이터를 전달하는 방법을 알아보자. 크게 다음의 3가지 방식으로 구분할 수 있다.

## GET - 쿼리 파라미터
- `<URL>?username=hello&age=20`
- 메시지 바디 없이 URL의 쿼리 파라미터에 데이터를 포함해서 전달하는 방식
- 검색, 필터, 페이징 등에서 많이 사용하는 방식이다


## POST - HTML Form
- 메시지 바디에 데이터를 담아서 전달한다.
	- **바디에 담긴 데이터의 형식이 쿼리 파라미터 형식과 비슷하다.**
	- 예) `username=hello&age=20`
- `content-type: application/x-www-form-urlencoded` 헤더가 있으면 HTML Form 방식의 데이터 전달임을 의미한다.
- 예) 회원 가입, 상품 주문, HTML Form 사용


## HTTP 메시지 바디에 데이터를 직접 담아서 요청
- HTTP API에서 주로 사용한다.
- JSON, XML, TEXT 등의 형태의 데이터를 바디에 담아서 전달한다.
	- 주로 JSON을 사용한다.
- POST, PUT, PATCH 메소드에서 사용 가능하다.