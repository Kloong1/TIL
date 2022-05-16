package com.kloong.corebasic1.autowired;

import com.kloong.corebasic1.AutoAppConfig;
import com.kloong.corebasic1.discount.DiscountPolicy;
import com.kloong.corebasic1.member.Grade;
import com.kloong.corebasic1.member.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;
import java.util.Map;

public class AllBeansTest {
    @Test
    void findAllBean() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(AutoAppConfig.class, DiscountService.class);

        DiscountService discountService = ac.getBean(DiscountService.class);

        Member member1 = new Member(1L, "user1", Grade.VIP);
        int discountPrice1 = discountService.discount(member1, 10000, "fixDiscountPolicy");

        Member member2 = new Member(1L, "user2", Grade.VIP);
        int discountPrice2 = discountService.discount(member2, 20000, "rateDiscountPolicy");

        Assertions.assertThat(discountService).isInstanceOf(DiscountService.class);
        Assertions.assertThat(discountPrice1).isEqualTo(1000);
        Assertions.assertThat(discountPrice2).isEqualTo(2000);
    }

    static class DiscountService {
        private final Map<String, DiscountPolicy> policyMap;
        private final List<DiscountPolicy> policies;

        public DiscountService(Map<String, DiscountPolicy> policyMap, List<DiscountPolicy> policies) {
            this.policyMap = policyMap;
            this.policies = policies;
            System.out.println("policyMap = " + policyMap);
            System.out.println("policies = " + policies);
        }

        public int discount(Member member, int price, String discountCode) {
            DiscountPolicy discountPolicy = policyMap.get(discountCode);
            return discountPolicy.discount(member, price);
        }
    }
}
