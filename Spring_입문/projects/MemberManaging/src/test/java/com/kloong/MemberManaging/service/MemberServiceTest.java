package com.kloong.MemberManaging.service;

import com.kloong.MemberManaging.domain.Member;
import com.kloong.MemberManaging.repository.MemoryMemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MemberServiceTest
{
    MemberService memberService;
    MemoryMemberRepository memberRepository;

/*  이렇게 해도 동작은 하지만, 엄연히 따지면 memberService의 멤버 변수 memberRepository와
    아래의 변수 memberRepository는 다른 인스턴스이다! 물론 store가 static 변수라
    clearStore()에는 문제가 없지만, 나중에 문제가 생길 여지도 있고 직관성도 떨어짐!

    따라서 이 문제를 해결하기 위해 MemberService에서 constructor를 만들어서
    외부에서 MemberRepository를 받아오게 하면서 인스턴스를 만들어주게끔 한다.

    이 것을 Dependency Injection(DI) 라고 한다.

    MemberService memberService = new MemberService();
    MemoryMemberRepository memberRepository = new MemoryMemberRepository();
*/

    @BeforeEach
    void beforeEach()
    {
        memberRepository = new MemoryMemberRepository();
        memberService = new MemberService(memberRepository);
    }

    @AfterEach
    void afterEach()
    {
        memberRepository.clearStore();
    }

    @Test
    void join()
    {
        //given - 어떤 상황이 주어짐
        Member member = new Member();
        member.setName("yohkim");

        //when - 그 상황에서 이 것을 실행했을 때
        Long memberId = memberService.join(member);

        //then - 이런 결과가 나와야 한다
        Member findMember = memberService.findOne(memberId).get();
        Assertions.assertThat(member.getName()).isEqualTo(findMember.getName());
    }

    @Test
    void joinDuplicatedMemberTest()
    {
        //given
        Member member1 = new Member();
        member1.setName("yohkim");

        Member member2 = new Member();
        member2.setName("yohkim");

        //when
        memberService.join(member1);
        //assertThrows()와 lambda 를 써서 이렇게 간단하게 구현 가능하다.
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> memberService.join(member2));

/*      이렇게 쓰는 건 너무 불편하다!
        try
        {
            memberService.join(member2);
            fail("예외가 발생해야 합니다!\n");
        }
        catch (IllegalStateException e)
        {
            Assertions.assertThat(e.getMessage()).isEqualTo("이미 존재하는 회원입니다.");
        }
*/
        //then
        Assertions.assertThat(e.getMessage()).isEqualTo("이미 존재하는 회원입니다.");
    }

    @Test
    void findMembers()
    {
        //given
        Member member1 = new Member();
        member1.setName("yohkim1");

        Member member2 = new Member();
        member2.setName("yohkim2");

        Member member3 = new Member();
        member3.setName("yohkim3");

        //when
        memberService.join(member1);
        memberService.join(member2);
        memberService.join(member3);

        List<Member> members = memberService.findMembers();

        //then
        Assertions.assertThat(members.size()).isEqualTo(3);
    }

    @Test
    void findOne()
    {
        //given
        Member member = new Member();
        member.setName("yohkim");

        //when
        Long saveId = memberService.join(member);
        Member result = memberService.findOne(saveId).get();

        //then
        Assertions.assertThat(member.getName()).isEqualTo(result.getName());
    }
}