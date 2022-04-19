# HTTP 메시지

HTTP 메시지로 HTML, text, 이미지, 영상 등 거의 모든 형식의 데이터를 보낼 수 있다.

HTTP 메시지는 2가지로 분류가 가능하다.
- Request 메시지
- Response 메시지

### HTTP 메시지 구조
![](스크린샷%202022-04-19%20오후%207.40.14.png)
![](스크린샷%202022-04-19%20오후%207.40.58.png)


## 1. Start-line
### HTTP Request Message에서
- Request-line 이라고도 한다.
- Request-line = `<Method> <Request-target> <HTTP-version> CRLF`

##### HTTP method
- 종류: GET, POST, PUT, DELETE
- 서버가 수행해야 할 동작을 지정한다.
	- GET: 리소스 조회 요청
	- POST: 요청 내역 처리

#### Reqeust target
- `절대 경로?쿼리` 형태
	- "/"로 시작하는 절대 경로
- http://... 로 시작하는 전체 경로를 쓰는 경우도 있다.


### HTTP Response Message에서
- Status-line 이라고도 한다.
- Status-line = `<HTTP-version> <Status-code> <Reason-phrase>`

#### Status code
클라이언트의 요청에 대한 성공/실패 여부를 나타낸다.
- 200: 성공
- 400: 클라이언트 요청 오류 
- 500: 서버 내부 오류

#### Reason phrase
Status code를 설명해주는 사람이 이해할 수 있는 추가적인 문구


## 2. HTTP Header
### 포맷
- header-field = `field-name: OWS <field-value> OWS`
	- OWS: 띄어쓰기 허용
- field-name은 대소문자 구분을 하지 않는다 (field-value는 구분 한다!)

![](스크린샷%202022-04-19%20오후%207.55.16.png)

### 특징
- HTTP 메시지 전송에 필요한 모든 부가정보를 담고 있다.
- ex) 메시지 바디의 내용, 메시지 바디의 크기, 압축, 인증, 요청 클라이언트(브라우저) 정보, 서버 애플리케이션 정보, 캐시 관리 정보...
- 표준 헤더가 매우 많다!
- 필요시 임의의 헤더를 추가하는 것도 가능하다.


## 3. HTTP Message Body
- 실제 전송할 데이터
- byte로 표현할 수 있는 모든 데이터를 담을 수 있다.
	- HTML 문서, 이미지, 영상, JSON 등...


## 정리
- HTTP는 단순하다! Spec도 읽어볼 만 하다.
- HTTP 메시지 포맷도 매우 단순하다.
	- Start-line, header, body
- 단순하고 확장에 유용하다.

