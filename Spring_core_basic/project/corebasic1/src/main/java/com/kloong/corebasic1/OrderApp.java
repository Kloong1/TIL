package com.kloong.corebasic1;

import com.kloong.corebasic1.member.Grade;
import com.kloong.corebasic1.member.Member;
import com.kloong.corebasic1.member.MemberService;
import com.kloong.corebasic1.member.MemberServiceImpl;
import com.kloong.corebasic1.order.Order;
import com.kloong.corebasic1.order.OrderService;
import com.kloong.corebasic1.order.OrderServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class OrderApp {
    public static void main(String[] args) {
        AppConfig appConfig = new AppConfig();

        //return new MemberServiceImpl(new MemoryMemberRepository());
//        MemberService memberService = appConfig.memberService();
        //return new OrderServiceImpl(new MemoryMemberRepository(), new FixDiscountPolicy());
//        OrderService orderService = appConfig.orderService();

        ApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);

        MemberService memberService = ac.getBean("memberService", MemberService.class);
        OrderService orderService = ac.getBean("orderService", OrderService.class);

        Long memberId = 1L;
        Member member = new Member(memberId, "memberA", Grade.VIP);
        memberService.join(member);

        Order order = orderService.createOrder(memberId, "itemA", 10000);

        System.out.println(order);
    }
}
