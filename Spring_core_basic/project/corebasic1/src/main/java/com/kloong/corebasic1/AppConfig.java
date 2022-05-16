package com.kloong.corebasic1;

import com.kloong.corebasic1.discount.DiscountPolicy;
import com.kloong.corebasic1.discount.RateDiscountPolicy;
import com.kloong.corebasic1.member.MemberRepository;
import com.kloong.corebasic1.member.MemberService;
import com.kloong.corebasic1.member.MemberServiceImpl;
import com.kloong.corebasic1.member.MemoryMemberRepository;
import com.kloong.corebasic1.order.OrderService;
import com.kloong.corebasic1.order.OrderServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//Spring에게 이 클래스가 application의 구성 정보를 담당하는 클래스라는 것을 알려줌
@Configuration
public class AppConfig {

    @Bean
    public MemberRepository memberRepository() {
        System.out.println("call AppConfig.memberRepository");
        return new MemoryMemberRepository();
    }

    @Bean
    public DiscountPolicy discountPolicy() {
        return new RateDiscountPolicy();
    }

    @Bean
    public MemberService memberService() {
        System.out.println("call AppConfig.memberService");
        return new MemberServiceImpl(memberRepository());
    }

    @Bean
    public OrderService orderService() {
        System.out.println("call AppConfig.orderService");
        return new OrderServiceImpl(memberRepository(), discountPolicy());
    }
}
