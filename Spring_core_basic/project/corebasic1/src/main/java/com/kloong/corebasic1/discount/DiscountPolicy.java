package com.kloong.corebasic1.discount;

import com.kloong.corebasic1.member.Member;

public interface DiscountPolicy {

    //@return 할인 대상 금액
    int discount(Member member, int price);
}
