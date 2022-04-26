# HTTP 인증 헤더

## Authorization
클라이언트의 인증 정보를 서버에 전달함

- 예) Authorization: Basic xxxxxxxxxxxxxxxx
- 인증 방식에 따라 헤더의 value 값이 천차만별이다.
- 인증 방식이 어떻든 간에 이 헤더를 사용할 수 있게 제공


### WWW-Authenticate
리소스 접근시 필요한 인증 방법을 정의

- 401 Unauthorized 응답과 함께 사용된다
	- 클라이언트가 요청을 했는데 인증이 안된 경우 이 헤더를 넣어준다
	- 클라이언트에게 인증 방식을 알려줘서 클라이언트가 인증을 할 수 있게 도와준다
	- 예) WWW-Authenticate: Newauth realm="apps", type=1, title="Login to \"apps\"", Basic realm="simple"