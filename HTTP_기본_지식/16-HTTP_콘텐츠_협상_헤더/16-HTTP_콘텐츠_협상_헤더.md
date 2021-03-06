# 콘텐츠 협상 헤더

### Contents negotiation 
- 클라이언트가 자신이 선호하는 표현을 서버에게 요청을 한다.
- 서버가 클라이언트가 원하는 우선순위에 맞춰서 표현 데이터를 만드는 것을 시도한다.

- **Accept**: 클라이언트가 선호하는 미디어 타입
- **Accept-Charset**: 클라이언트가 선호하는 문자 인코딩
- **Accept-Encoding**: 클라이언트가 선호하는 압축 인코딩
- **Accept-Language**: 클라이언트가 선호하는 자연 언어

**협상 헤더는 요청시에만 사용한다.**

>표현 헤더의 종류 (Content-Type, Content-Encoding, Content-Language) 와 비슷하다.


### 협상 헤더를 사용하는 예시
##### Accept-Language 적용 전
![](스크린샷%202022-04-25%20오후%205.51.37.png)
- 서버가 한국어와 영어를 모두 지원하는 서버다. 기본 언어는 영어이다.
- 클라이언트는 한국어 브라우저를 사용하지만, 자신이 선호하는 언어에 대한 정보를 요청에 담지 않은 채로 서버에 요청을 보낸다.
- 따라서 서버는 그냥 기본 언어인 영어로 리소스를 표현해서 클라이언트에게 전달한다.

##### Accept-Language 적용 후
![](스크린샷%202022-04-25%20오후%205.53.46.png)
- 클라이언트가 자신이 선호하는 언어를 요청에 담아서 보낸다.
- 서버는 기본 언어가 영어지만, 한국어도 지원하기 때문에 클라이언트의 요청에 따라 리소스를 한국어로 표현해서 보낸다.

##### Accept-Language 복잡한 예시
![](스크린샷%202022-04-25%20오후%205.55.06.png)
- 독일어를 기본적으로 지원하고, 영어도 함께 지원하는 서버가 있다.
- 독일어는 친숙하지 않다. 한국어를 선호하는 클라이언트는 한국어로 표현된 리소스를 받기 원하지만, 서버가 한국어를 지원하지 않는다면 영어로 표현된 리소스를 줬으면 좋겠다.
- 하지만 서버는 한국어를 지원하지 않기 때문에 한국어 요청에 대해서 기본 지원 언어인 독일어로 표현 데이터를 보낸댜.

이런 문제를 해결하기 위해 **우선순위**를 사용할 수 있다.


### 협상과 우선순위 1
![](스크린샷%202022-04-25%20오후%205.59.21.png)
- 우선순위를 표현하기 위해 Quality Values(q) 값을 사용한다.
- **0~1 사이의 값**으로 표현한다. **값이 클수록 우선순위가 높다.**
- 이 값을 생략하면 **우선순위는 1이 된다.**
- `Accept-Language: ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7` 의 의미
	- ko-KR;q=1 (대한민국에서 쓰는 한국어. q 생략했으므로 우선순위 1)
	- ko;q=0.9 (공통 한국어)
	- en-US;q=0.8 (영국에서 쓰는 영어)
	- en:q=0.7 (공통 영어)

##### Accept-Language 복잡한 예시를 우선순위 적용하여 해결
![](스크린샷%202022-04-25%20오후%206.02.29.png)
- 서버는 한국어를 지원하지 않지만, 클라이언트가 적어도 독일어보다는 영어를 선호한다는 사실을 알 수 있다.
- 따라서 영어로 표현된 리소스를 보내준다.


### 협상과 우선순위 2
![](스크린샷%202022-04-25%20오후%206.04.54.png)

- 구체적인 것이 우선순위가 더 높다.
- `Accept: text/*, text/plain, text/plain;format=flowed, */*` 에서 우선순위가 높은 순서는?
	- text/plain;format=flowed
	- text/plain
	- text/\*
	- \*/\*


### 협상과 우선순위 3
![](스크린샷%202022-04-25%20오후%206.07.10.png)

