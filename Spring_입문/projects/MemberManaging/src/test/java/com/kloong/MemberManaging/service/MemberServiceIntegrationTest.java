package com.kloong.MemberManaging.service;

import com.kloong.MemberManaging.domain.Member;
import com.kloong.MemberManaging.repository.MemberRepository;
import com.kloong.MemberManaging.repository.MemoryMemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class MemberServiceIntegrationTest
{
    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;

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
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> memberService.join(member2));

        //then
        Assertions.assertThat(e.getMessage()).isEqualTo("이미 존재하는 회원입니다.");
    }
}