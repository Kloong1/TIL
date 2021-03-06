# 쿠키와 HTTP 쿠키 헤더

## 쿠키
- Set-Cookie: 서버에서 클라이언트로 쿠키 전달(응답)
- Cookie: 클라이언트가 서버에서 받은 쿠키를 저장하고, HTTP 요청시 서버로 전달

### 쿠키를 사용하지 않는 상황 예시
##### 사용자가 처음 welcome 페이지에 접근
![](스크린샷%202022-04-26%20오후%201.09.08.png)

##### 사용자가 로그인을 함
![](스크린샷%202022-04-26%20오후%201.09.22.png)
- 사용자가 서버에 로그인 관련 정보를 넘겨줘서 로그인을 함 (실제로는 더 많은 정보를 넘겨줌)

##### 로그인 이후 다시 welcome 페이지에 접근
![](스크린샷%202022-04-26%20오후%201.11.00.png)
- 사용자는 "안녕하세요. 홍길동님"응답을 기대함.
- 하지만 서버는 "안녕하세요. 손님"으로 응답함.
- 서버 입장에서는 로그인한 홍길동 클라이언트의 `GET /welcome HTTP/1.1` 요청이, 로그인한 사용자의 요청인지 구분할 방법이 없음 -> **HTTP는 Stateless이기 때문!**


### HTTP는 Stateless
- HTTP는 무상태(Stateless) 프로토콜이다.
- 클라이언트와 서버가 요청과 응답을 주고 받으면 연결이 끊어진다.
- 클라이언트가 다시 요청하면 서버는 이전 요청을 기억하지 못한다.
- 클라이언트와 서버는 서로 상태를 유지하지 않는다.
- 따라서 위의 예시에서의 문제(사용자의 로그인 여부를 서버가 기억하지 못하는 문제)가 발생한다.


### 대안 - 모든 요청에 사용자 정보를 포함한다면?
![](스크린샷%202022-04-26%20오후%201.14.23.png)
![](스크린샷%202022-04-26%20오후%201.14.51.png)

##### 문제의 소지가 너무 많다!
- 보안에서의 위험성
- 브라우저를 완전히 종료하고 다시 열면?
- 개발이 너무 까다롭다
	- 모든 요청에 사용자 정보가 포함되도록 개발 해야한다


### 쿠키를 사용하자!
##### 사용자 로그인에 대해 쿠키 적용
![](스크린샷%202022-04-26%20오후%201.16.55.png)
- 로그인한 사용자의 정보를 쿠키에 넣어서 서버가 응답을 보낸다.
- 웹 브라우저의 쿠키 저장소에 서버가 준 쿠키를 저장해둔다.

##### 로그인 이후 welcome 페이지 접근
![](스크린샷%202022-04-26%20오후%201.18.14.png)
- 웹 브라우저는 해당 서버에 요청을 보낼 때마다 쿠키를 꺼내서 헤더에 포함시킨 뒤 요청을 보낸다.
- 서버는 쿠키를 보고 클라이언트가 로그인한 유저임을 확인할 수 있다.

##### 모든 요청에 쿠키 정보를 자동으로 포함시킴
![](스크린샷%202022-04-26%20오후%201.19.41.png)
- 정말로 모든 요청에 쿠키를 보내면 보안상 위험성이 있으므로, 필요한 경우 쿠키 전송에 제약을 줄 수 있다.

### 쿠키의 특징
쿠키 사용 예시: `set-cookie: sessionId=abcde1234; expires=Sat, 26-Dec-2020 00:00:00 GMT; path=/; domain=.google.com; Secure`

##### 쿠키의 주된 사용처
- 사용자 로그인 세션 관리 
	- 사용자가 로그인을 하면 서버에서 해당 사용자에 대한 sessionId를 생성 (사용자의 정보를 그대로 사용하는 것은 보안상 위험하기 때문)
	- 서버에서는 sessionId와 사용자의 정보를 mapping해서 DB 등에 저장해 둠
	- 서버가 클라이언트에 sessionId를 넘겨줌 (쿠키를 넘겨줌)
- 광고 정보 트래킹
	- 사용자가 어떤 광고를 주로 보는지 확인 가능

##### 쿠키 정보는 모든 요청에 포함된다
- 네트워크 트래픽을 유발한다
- 따라서 최소한의 정보만 사용해야한다 (세션 id, 인증 토큰 등)
- 항상 서버에 전송하지 않고, 웹 브라우저 내부에 데이터를 저장하고만 싶으면 웹 스토리지를 사용하면 된다
	- localStorage, sessionStorage

 ##### 보안에 민감한 데이터는 절대로 쿠키에 저장하면 안된다! (주민번호, 신용카드 번호 등)


### 쿠키의 생명 주기
- 쿠키를 웹 브라우저에서 계속 보관하고 있을 수 없다.
- `expires`와 `max-age` 로 쿠키의 만료일을 설정 가능하다.
- `Set-Cookie: expires=Sat, 26-Dec-2020 04:39:21 GMT`
	- 만료일이 되면 쿠키를 삭제한다.
- `Set-Cookie: max-age=3600` (3600초)
	- 지정한 초 후에 쿠키가 삭제된다.
	- 0이나 음수를 지정하면 쿠키가 삭제된다.
- **세션 쿠키**: 만료 날짜를 생략하면 브라우저 종료시 까지만 유지
- **영속 쿠키**: 만료 날짜를 입력하면 해당 날짜까지 유지


### 쿠키의 도메인
모든 서버에 쿠키를 생성하고 보낸다면 보안상 위험성이 너무 크다. 쿠키에 도메인을 지정해서 이런 문제를 해결할 수 있다.

- 예: `domain=example.org`
- **domain을 명시한 경우 - 명시한 웹 페이지의 도메인 + 서브 도메인 포함**
	- domain=example.org 를 명시해서 쿠키 생성
	- example.org를 포함해서, dev.example.org 등 서브 도메인에서도 쿠키를 전송함
- **domain을 생략한 경우 - 현재 웹 페이지의 도메인만 적용**
	- example.org 에서 쿠키를 생성하고 domain 지정을 생략
	- example.org 에서만 쿠키 접근, dev.example.org 는 쿠키 미접근


### 쿠키 경로
- 예: `path=/home`
- **명시한 경로를 포함한 하위 경로 페이지만 쿠키 접근 가능**
- **일반적으로 `path=/` 이렇게 root path로 지정한다.**
- `path=/home` 한 경우
	- /home -> 가능
	- /home/level1 -> 가능
	- /home/level1/level2 -> 가능
	- /hello -> 불가능
- domain으로 한 번 필터링을 하고, 다시 한 번 path로 필터링이 된다.


### 쿠키 보안
- **Secure**
	- 쿠키는 http, https를 구분하지 않고 전송된다.
	- Secure를 적용하면 https인 경우에만 전송한다.
- **HttpOnly**
	- XSS 공격을 방지하기 위해 사용한다
	- 자바스크립트에서 접근 불가능하다
	- HTTP 전송에만 쿠키를 사용할 수 있다
- **SameSite**
	- XSRF 공격을 방지하기 위해 사용한다
	- 요청 도메인과 쿠키에 설정된 도메인이 같은 경우만 쿠키를 전송할 수 있다