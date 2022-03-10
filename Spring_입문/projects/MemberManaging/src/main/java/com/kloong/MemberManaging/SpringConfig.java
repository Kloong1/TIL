package com.kloong.MemberManaging;

import com.kloong.MemberManaging.aop.TimeTraceAop;
import com.kloong.MemberManaging.repository.*;
import com.kloong.MemberManaging.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

@Configuration
public class SpringConfig
{
//    private DataSource dataSource;
//
//    @Autowired
//    public SpringConfig(DataSource dataSource)
//    {
//        this.dataSource = dataSource;
//    }

//    private EntityManager em;
//
//    @Autowired
//    public SpringConfig(EntityManager em)
//    {
//        this.em = em;
//    }

    private final MemberRepository memberRepository;

    @Autowired
    public SpringConfig(MemberRepository memberRepository)
    {
        this.memberRepository = memberRepository;
    }

    @Bean
    public MemberService memberService()
    {
        return new MemberService(memberRepository);
    }

//    @Bean
//    public TimeTraceAop timeTraceAop()
//    {
//        return new TimeTraceAop();
//    }

//    @Bean
//    public MemberRepository memberRepository()
//    {
//        //return new JpaMemberRepository(em);
//        //return new JdbcTemplateMemberRepository(dataSource);
//        //return new JdbcMemberRepository(dataSource);
//        //return new MemoryMemberRepository();
//    }
}
