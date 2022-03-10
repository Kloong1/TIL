package com.kloong.MemberManaging.repository;

import com.kloong.MemberManaging.domain.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

//public 으로 안해도 된다.
class MemoryMemberRepositoryTest
{
    MemoryMemberRepository repository = new MemoryMemberRepository();

    //각 test method가 실행된 후에 이 method가 실행된다.
    @AfterEach
    public  void afterEach()
    {
        repository.clearStore();
    }

    //repository의 save 기능을 테스트하는 method
    @Test
    public void save()
    {
        Member member = new Member();
        member.setName("yohkim");

        repository.save(member);

        //repository.findById()는 Optional<Member> 객체를 반환하기 때문에
        //Unwrapping 한다. 원래는 null 체크를 해야하지만 여기서는
        //null이 아니라고 가정하고 그냥 .get() 하는 것이다.
        Member result = repository.findById(member.getId()).get();

        //객체 주소를 hashing 해서 저장하는 방식이기 때문에 이런 코드가 가능하다.
        //두 값이 같지 않으면 exception이 발생한다.
        Assertions.assertThat(member).isEqualTo(result);
    }

    //repository의 findByName 기능을 테스트하는 method
    @Test
    public void findByName()
    {
        Member member1 = new Member();
        member1.setName("yohkim1");
        repository.save(member1);

        Member member2 = new Member();
        member2.setName("yohkim2");
        repository.save(member2);

        Member result = repository.findByName("yohkim1").get();

        Assertions.assertThat(member1).isEqualTo(result);
    }

    //repository의 findAll 기능을 테스트하는 method
    @Test
    public void findAll()
    {
        Member member1 = new Member();
        member1.setName("yohkim1");
        repository.save(member1);

        Member member2 = new Member();
        member2.setName("yohkim2");
        repository.save(member2);

        List<Member> result = repository.findAll();

        //간단하게 size만 비교.
        Assertions.assertThat(result.size()).isEqualTo(2);
    }
}
