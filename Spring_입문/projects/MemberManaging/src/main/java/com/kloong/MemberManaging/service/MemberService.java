package com.kloong.MemberManaging.service;

import com.kloong.MemberManaging.domain.Member;
import com.kloong.MemberManaging.repository.MemberRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
public class MemberService
{
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository)
    {
        this.memberRepository = memberRepository;
    }

    //회원 가입
    //단, 같은 이름이 있는 회원이 존재해선 안된다는 가정
    public Long join(Member member)
    {
        validateDuplicatedMember(member); //중복 회원 검증
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicatedMember(Member member)
    {
        memberRepository.findByName(member.getName())
                        .ifPresent(m -> {
                            throw new IllegalStateException("이미 존재하는 회원입니다.");
                        });
    }

    public List<Member> findMembers()
    {
        return memberRepository.findAll();
    }

    public Optional<Member> findOne(Long memberId)
    {
        return memberRepository.findById(memberId);
    }
}
