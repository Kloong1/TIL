# HTTP 표현 헤더

실제 리소스는 매우 추상적이다. 해당 리소스를 서버-클라이언트가 서로 이해할 수 있는 형식으로 표현해서 HTTP 바디에 담아야한다. 그래서 "표현" 이라는 용어를 쓴다.

- **Content-Type**: 표현 데이터의 형식
- **Content-Encoding:** 표현 데이터의 압축 방식
- **Content-Language**: 표현 데이터의 자연 언어
- **Content-Length**: 표현 데이터의 길이

**표현 헤더는 전송과 응답에서 둘 다 사용할 수 있다.**


## Content-Type
표현 데이터의 형식을 설명한다. 리소스가 어떤 형식으로 표현되어서 메시지 바디에 담겼는지 알려줘야지 상대방이 메시지 바디를 해석할 수 있다.

- 미디어 타입, 문자 인코딩 등
	- text/html; charset=utf-8 (text이기 때문에 인코딩도 함께 명시함)
	- application/json (json은 기본이 utf-8)
	- image/png


## Content-Encoding
표현 데이터의 인코딩을 설명한다.

![](스크린샷%202022-04-25%20오후%205.40.16.png)

- 표현 데이터를 압축하기 위해 사용한다.
- 데이터를 전달하는 곳에서 데이터를 압축 ,후 압축 방식을 명시하는 인코딩 헤더를 추가한다.
- 데이터를 읽는 쪽에서 인코딩 헤더의 정보로 압축을 해제한다.
- 예시
	- gzip
	- deflate
	- identity (압축을 안함)


## Content-Language
표현 데이터의 자연 언어 종류를 설명한다.

![](스크린샷%202022-04-25%20오후%205.41.44.png)

- 표현 데이터의 자연 언어를 설명한다.
- 클라이언트가 한국어를 사용하는데, 특정 웹페이지에서 `Content-Language: en` 헤더로 응답을 준다면, 웹 브라우저에서 한국어로 자동 번역을 해준다던지 하는 기능을 구현할 수 있다.
- 예시
	- ko
	- en
	- en-US


## Content-Length
표현데이터의 길이를 설명한다.

- 바이트 단위
- Transfer-Encoding(전송 코딩)을 사용하면 Content-Length를 사용하면 안됨
	- 이미 length 정보가 다 들어있기 때문. 추후에 다시 설명.