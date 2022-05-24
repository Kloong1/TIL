# 회원 관리 Web App 요구사항

## 요구사항
#### 회원 정보
- 이름: `username`
- 나이: `Age`

#### 기능 요구사항
- 회원 정보 저장
- 회원 목록 조회


## 회원 도메인 모델
##### Member.java
```Java
package com.kloong.servlet.domain.member;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Member {

    private Long id;
    private String username;
    private int age;

    public Member() {
    }

    public Member(String username, int age) {
        this.username = username;
        this.age = age;
    }
}
```


## 회원 저장소
##### MemberRepository.java
```Java
package com.kloong.servlet.domain.member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemberRepository {

    private static final Map<Long, Member> store = new HashMap<>();
    private static long sequence = 0L;

    //MemberRepository 객체를 싱글톤으로 관리한다
    private static final MemberRepository instance = new MemberRepository();
    private MemberRepository() {
    }

    public static MemberRepository getInstance() {
        return instance;
    }

    public Member save(Member member) {
        member.setId(++sequence);
        store.put(member.getId(), member);
        return member;
    }

    public Member findById(Long id) {
        return store.get(id);
    }

    public List<Member> findAll() {
        return new ArrayList<>(store.values());
    }

    public void clearStore() {
        store.clear();
    }
}
```