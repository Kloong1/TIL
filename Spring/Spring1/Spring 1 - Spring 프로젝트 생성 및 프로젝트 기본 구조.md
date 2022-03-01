# Spring 프로젝트 생성 및 프로젝트 기본 구조


## 1. Spring 프로젝트 생성

### Spring initializr
![](스크린샷%202022-02-24%20오후%202.05.48%201.png)
https://start.spring.io/

Spring에서 운영하고 있는 사이트. 프로젝트를 직접 만드는 것이 아니라, Spring initializr를 이용해서 Spring Boot 기반의 프로젝트를 만들 수 있음. 프로젝트 초기 세팅을 다 해주기 때문에 이 사이트를 이용해서 만드는 것이 편리하다.

프로젝트 설정을 마친 뒤 Generate를 하면 압축 파일이 다운로드 된다. 압축을 풀면 나오는 파일 중 `build.gradle` 이 있는데, IDE로 해당 파일을 project로 열면 된다.

### Maven & Gradle
Dependency가 있는 라이브러리를 땡겨와주고, 빌드도 도와주는 툴.
과거에는 Maven을 많이 썼으나, 최근에는 Gradle을 쓰는 추세이다.


## 2. Spring 프로젝트 구조

### src 폴더
![300](스크린샷%202022-02-24%20오후%202.11.00%201.png)
java 소스 파일이 들어있다. test 코드의 중요성이 대두되면서 main 소스 폴더와 test 소스 폴더가 나뉘어져서 프로젝트가 생성된다.
resources 폴더에는 java 파일 외의 다른 파일들이 들어있다.

### build.gradle
아직은 build 설정에 대한 파일이라고만 알고 있으면 된다. Spring initializr에서 이 파일을 따로 만들어 주는 것이다. 원래는 프로그래머가 직접 작성해야 했다고 함. Spring 버전, dependency 목록 등의 내용을 확인할 수 있다.

### 기타
`.gitignore`, `setting.gradle` 등


## 3.  프로젝트 실행
![](스크린샷%202022-02-24%20오후%202.15.36%201.png)

### Tomcat
Spring Boot Application을 실행시키면, 내장하고 있는 Tomcat이라는 웹 서버를 실행시키면서, 그 위에 Spring Boot Application을 올리는 방식. 따로 웹서버를 설치 및 설정한 뒤 자바 소스 코드를 밀어넣어서 그 위에서 실행시킬 필요가 없다. (과거에는 그렇게 했었기 때문에 웹 서버에 올리는 과정도 매우 힘들었다고 함!)

위의 사진을 보면 Tomcat이 port 8080에서 시작되었다는 문구를 발견할 수 있다.


## 4. IntelliJ Gradle 대신 Java로 직접 실행하기
아직은 뭔지 잘 모르겠지만 Gradle로 실행하면 느리기 때문에 Java로 바로 실행해서 실행속도를 높일 수 있다고 한다.

IntelliJ에서
Preferences -> Build, Execution, Deployment -> Build Tools -> Gradle
![](스크린샷%202022-02-24%20오후%202.31.16%201.png)
빨간 네모 친 부분은 Gradle 에서 IntelliJ IDEA로 바꿔주면 된다.


#### Tag
[[Spring]]