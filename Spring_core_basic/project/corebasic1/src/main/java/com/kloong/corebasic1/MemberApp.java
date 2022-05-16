package com.kloong.corebasic1;

import com.kloong.corebasic1.member.Grade;
import com.kloong.corebasic1.member.Member;
import com.kloong.corebasic1.member.MemberService;
import com.kloong.corebasic1.member.MemberServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MemberApp {
    public static void main(String[] args) {

//        AppConfig appConfig = new AppConfig();
//        MemberService memberService = appConfig.memberService();

        //AppConfig의 구성 정보를 보고 Spring이 bean을 등록해서 관리해준다.
        ApplicationContext applicationContext =
                new AnnotationConfigApplicationContext(AppConfig.class);

        //Spring bean은 기본적으로 메소드 이름으로 등록된다. name 파라미터에 메소드 이름을 넣어준다.
        MemberService memberService = applicationContext.getBean("memberService", MemberService.class);

        Member member = new Member(1L, "memberA", Grade.VIP);
        memberService.join(member);

        Member findMember = memberService.findMember(1L);
        System.out.println("new member = " + member.getName());
        System.out.println("find Member = " + findMember.getName());
    }
}
