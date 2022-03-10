package com.kloong.MemberManaging.repository;

import com.kloong.MemberManaging.domain.Member;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface MemberRepository
{
    Member save(Member member);
    Optional<Member> findById(Long id);
    Optional<Member> findByName(String name);
    List<Member> findAll();
}
