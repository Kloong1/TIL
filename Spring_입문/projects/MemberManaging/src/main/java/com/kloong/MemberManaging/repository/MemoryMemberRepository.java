package com.kloong.MemberManaging.repository;

import com.kloong.MemberManaging.domain.Member;
import org.springframework.stereotype.Repository;

import java.util.*;

public class MemoryMemberRepository implements MemberRepository
{
    //conquerency 문제 때문에 아래 멤버 변수들은 다른 객체를 써야한다고 함.
    //그런 문제를 해결해주는 객체가 있다고 함. 예제니까 그냥 이거 쓰는거임.
    private static Map<Long, Member> store = new HashMap<>();
    private static long sequence = 0L;

    @Override
    public Member save(Member member)
    {
        member.setId(++sequence);
        store.put(member.getId(), member);
        return member;
    }

    @Override
    public Optional<Member> findById(Long id)
    {
        //Optional을 사용해서 NullPointerException 문제 해결.
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<Member> findByName(String name)
    {
        //람다 사용해서 반복하며 탐색.
        //name이 같은 Member 하나라도 찾으면 반환.
        //Optional로 wrapping 후 반환하기 때문에 못찾아도 처리 가능.
        return store.values().stream()
                .filter(member -> member.getName().equals(name))
                .findAny();
    }

    @Override
    public List<Member> findAll()
    {
        return new ArrayList<>(store.values());
    }

    public void clearStore()
    {
        store.clear();
    }
}
