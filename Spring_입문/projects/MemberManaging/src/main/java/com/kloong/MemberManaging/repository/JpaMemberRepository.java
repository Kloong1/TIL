package com.kloong.MemberManaging.repository;

import com.kloong.MemberManaging.domain.Member;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class JpaMemberRepository implements MemberRepository
{
    private final EntityManager em;

    @Autowired
    public JpaMemberRepository(EntityManager em)
    {
        this.em = em;
    }

    @Override
    public Member save(Member member)
    {
        em.persist(member);
        return member;
    }

    @Override
    public Optional<Member> findById(Long id)
    {
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member);
    }

    @Override
    public Optional<Member> findByName(String name)
    {
        List<Member> result = em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();

        return result.stream().findAny();
    }

    @Override
    public List<Member> findAll()
    {
        //JPQL. Entity를 대상으로 쿼리를 날리면 SQL로 자동으로 변환됨.
        //객체 자체를 select 하는 모습을 볼 수 있다.
        return em.createQuery("select m from Member m", Member.class).getResultList();
    }
}
