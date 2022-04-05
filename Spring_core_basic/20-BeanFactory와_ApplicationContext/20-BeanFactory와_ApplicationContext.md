# BeanFactory와 ApplicationContext
#### BeanFactory와 ApplicationContext의 계층 구조
![](Pasted%20image%2020220405180140.png)

### BeanFactory
- 스프링 컨테이너의 최상위 인터페이스다.
- 스프링 빈을 관리하고 조회하는 역할을 담당한다(getBean() 등).
- 지금까지 우리가 사용했던 대부분의 기능은 BeanFactory가 제공하는 기능이다.

### ApplicationContext
BeanFactory 기능을 모두 상속받아서 제공한다.

빈을 관리하고 검색하는 기능을 BeanFactory가 제공해주는데, 그러면 둘의 차이가 뭘까?
-> 실제 애플리케이션을 개발할 때는 빈은 관리하고 조회하는 기능은 물론이고, 수많은 부가기능이 필요하다.

### ApplicationContext가 제공하는 부가 기능
애플리케이션을 개발할 때 일반적으로 필요한 공통적인 기능들을 BeanFactory의 기능에 더해서 제공한다.

![](Pasted%20image%2020220405180713.png)

#### MessageSource를 활용한 국제화 기능
예를 들어 웹사이트를 한국에서 접속하면 한국어로 출력하고, 영어권에서 들어오면 영어로 출력하는 기능

#### 환경변수
실무 개발에서는 1.로컬 개발 환경(개발자 PC), 2.개발 환경(테스트 서버), 3.운영 환경(실제 서버) 크게 3가지 개발 환경이 존재한다. 각 환경 별로 어떤 DB에 연결해야 할지 등의 환경변수와 관련된 정보를 처리해주는 기능을 제공한다.

#### 애플리케이션 이벤트
이벤트를 발행하고 구독하는 모델을 편리하게 지원한다.

#### 편리한 리소스 조회
파일이나 클래스패스, 외부 등에서 리소스를 편리하게 조회할 수 있게 해준다.


## 정리
- ApplicationContext는 BeanFactory의 기능을 상속받는다.
- ApplicationContext는 BeanFactory의 빈 관리기능 + 실제 애플리케이션 개발에 필요한 부가 기능을 제공한다.
- BeanFactory를 직접 사용할 일은 거의 없다. 부가기능이 포함된 ApplicationContext를 사용한다.
- BeanFactory나 ApplicationContext를 스프링 컨테이너라 한다.