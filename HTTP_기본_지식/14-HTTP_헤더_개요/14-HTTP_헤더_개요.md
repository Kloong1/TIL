# HTTP 헤더 개요

## HTTP 헤더 포맷
- **header-field** = `field-name ":" OWS field-value OWS (OWS:띄어쓰기 허용)`
- field-name 은 대소문자 구분이 없음


## 용도
- HTTP 전송에 필요한 모든 부가정보를 헤더에 포함시킨다.
	- 예) 메시지 바디의 내용, 메시지 바디의 크기, 압축, 인증, 요청 클라이언트, 서버 정보, 캐시 관리 정보 등등...
- 표준 헤더 필드 종류는 매우 많음
	- https://en.wikipedia.org/wiki/List_of_HTTP_header_fields
- 필요시 임의의 헤더 추가 가능
	- 예) helloworld: hihi


## HTTP 헤더의 분류

### RFC2616 기준 (과거)
![](스크린샷%202022-04-25%20오후%205.10.55.png)

- **General 헤더**: 메시지 전체에 적용되는 정보. 예) Connection: close
- **Request 헤더**: 요청 정보. 예) User-Agent: Mozilla/5.0 (Macintosh; ..)
- **Response 헤더**: 응답 정보. 예) Server: Apache
- **Entity 헤더**: 엔티티 바디 정보. 예) Content-Type: text/html, Content-Length: 3423

##### message body
![](스크린샷%202022-04-25%20오후%205.12.21.png)
- 메시지 본문(message body)은 엔티티 본문(entity body)을 전달하는데 사용
- 엔티티 본문은 요청이나 응답에서 전달할 실제 데이터
- **엔티티 헤더는 엔티티 본문의 데이터를 해석할 수 있는 정보 제공**
	- 데이터 유형(html, json), 데이터 길이, 압축 정보 등

### RFC7230~7235의 등장
- 1999년에 등장한 RFC2616이 폐기되어버린다.
- **엔티티(Entity) -> 표현(Representation)**
- Representation = representation Metadata + Representation Data
	- 표현 = 표현 메타데이터 + 표현 데이터

##### message body
![](스크린샷%202022-04-25%20오후%205.16.36.png)

- 메시지 본문(message body)을 통해 표현 데이터 전달
- 메시지 본문을 **페이로드(payload)** 라고도 부른다.
- 표현은 요청이나 응답에서 전달할 실제 데이터
- **표현 헤더는 표현 데이터를 해석할 수 있는 정보 제공**
	- 데이터 유형(html, json), 데이터 길이, 압축 정보 등
	- 참고: 표현 헤더는 표현 메타데이터와, 페이로드 메시지를 구분해야 하지만, 여기서는 생략

>참고: 왜 Representation(표현)인가?
>실제 전송하고 싶은 리소스를 HTTP 메시지에 담아서 전송하기 위해 특정한 표현을 선택한다. JSON, text, HTML, XML 등등... 따라서 "표현"이라는 용어를 쓰는 것이다. REST(Representational State Transfer) 에서 Reprentational이 여기서 온 것이다.

