# 빌드하고 실행하기

## 1. 빌드하고 실행하기
 1. 프로젝트 폴더로 이동하기
 2. `./gradlew build`
 3. `cd ./build/libs`
 4. `java -jar <생성된 .jar 파일>`

![](스크린샷%202022-03-01%20오후%204.20.14.png)

 ## 2. 서버를 배포할 때는?
 아주 간단하다. `./gradlew build` 로 생성된 .jar 파일을 서버에 올리고 실행시키면 끝!

  ## 3. build가 안된다면?
  `./gradlew clean` 해서 build 디렉토리를 날리고 `./gradlew build` 하면 된다.
  `./gradlew clean build` 이렇게 한번에 하는 것도 가능하다.

  