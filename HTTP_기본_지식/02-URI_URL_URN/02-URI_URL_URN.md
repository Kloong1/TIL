# URI, URL, URN

## URI (Uniform Resource Identifier)

- Uniform: 리소스를 식별하는 통일된 방식임을 나타낸다.
- Resource: 자원, URI로 식별할 수 있는 모든 것.
- Identifier: 식별자.

##### URI, URL, URN의 관계
![](스크린샷%202022-04-17%20오후%2010.06.44.png)

#### URI는 URL과 URN을 포함하는 가장 큰 개념이다.
- URI를 통해 자원을 식별할 수 있다.
- URL: 자원의 위치를 지정한다.
- URN: 자원의 이름을 부여한다. (자원의 이름만으로는 자원의 위치를 식별하기가 어렵다).
- 위치는 변할 수 있지만, 이름은 변하지 않는다.
- 하지만 이름(URN) 만으로는 실제 리소스를 찾을 수 있는 방법이 보편화 되어있지 않다. 즉 URN을 실제로 사용하기는 어렵다.
- **따라서 URI와 URL을 같은 의미로 받아들여도 된다.**

![](스크린샷%202022-04-17%20오후%2010.09.04.png)


## URL (Uniform Resource Locator)

### 전체 문법
`scheme://[userinfo@]host[:port][/path][?query][#fragment]`

예시: https://www.google.com:443/search?q=hello&hl=ko

- 프로토콜(https)
- 호스트명(www.google.com)
- 포트 번호(443)
- 패스(/search)
- 쿼리 파라미터(q=hello&hl=ko)

### Scheme
- 주로 프로토콜에 사용됨 (Ex. http, https, ftp 등등)
- http는 80 포트, https는 443 포트를 주로 사용. Well-know port인 경우 포트는 생략 가능.

### Userinfo
- URL에 사용자정보를 포함해서 인증
- 거의 사용하지 않음

### Host
- 호스트명
- 도메인명 또는 IP 주소를 직접 사용가능

### Port
- 일반적으로 생략, 생략시 http는 80, https는 443

### Path
- 리소스의 경로 (path).
- 계층적 구조를 가지고 있음.
	- /home/file1.jpg
	- /members
	- /members/100, /items/iphone12

### Query
- 웹서버에 제공하는 일종의 파라미터이다.
- 항상 문자열 형태로 전달된다.
-  `key=value` 형태
- ?로 시작하고, 연속된 쿼리는 &로 추가 가능하다.
- 예시: `?keyA=valueA&keyB=valueB`
• query parameter, query string 등으로 불린다.

### Fragment
- html 내부 북마크 등에 사용
- 서버에 전송하는 정보 아님
- 잘 사용하지 않음.