package com.kloong.corebasic1.singleton;

import com.kloong.corebasic1.AppConfig;
import com.kloong.corebasic1.member.MemberService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PureDIContainerTest {

    @Test
    @DisplayName("스프링 없는 순수 DI 컨테이너")
    void pureDIContainer() {
        AppConfig appConfig = new AppConfig();

        //1. 조회: 호출할 때마다 객체를 생성
        MemberService memberService1 = appConfig.memberService();

        //2. 조회: 호출할 때마다 객체를 생성
        MemberService memberService2 = appConfig.memberService();

        //두 객체의 주소가 다른 것을 확인
        System.out.println("memberService1 = " + memberService1);
        System.out.println("memberService2 = " + memberService2);

        Assertions.assertThat(memberService1).isNotSameAs(memberService2);
    }
}
