package com.kloong.corebasic1.order;

import com.kloong.corebasic1.annotation.MainDiscountPolicy;
import com.kloong.corebasic1.discount.DiscountPolicy;
import com.kloong.corebasic1.member.Member;
import com.kloong.corebasic1.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{

    private final MemberRepository memberRepository;
    private final DiscountPolicy discountPolicy;

    @Override
    public Order createOrder(Long memberId, String itemName, int itemPrice) {
        Member member = memberRepository.findById(memberId);
        int discountPrice = discountPolicy.discount(member, itemPrice);

        return new Order(memberId, itemName, itemPrice, discountPrice);
    }

    //테스트 용도 코드
    public MemberRepository getMemberRepository() {
        return memberRepository;
    }
}
