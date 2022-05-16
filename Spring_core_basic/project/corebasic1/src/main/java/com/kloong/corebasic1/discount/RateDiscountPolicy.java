package com.kloong.corebasic1.discount;

import com.kloong.corebasic1.annotation.MainDiscountPolicy;
import com.kloong.corebasic1.member.Grade;
import com.kloong.corebasic1.member.Member;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
@MainDiscountPolicy
public class RateDiscountPolicy implements DiscountPolicy{

    private int discountRate = 10;

    @Override
    public int discount(Member member, int price) {
        if (member.getGrade() == Grade.VIP)
            return price * discountRate / 100;

        return 0;
    }
}
