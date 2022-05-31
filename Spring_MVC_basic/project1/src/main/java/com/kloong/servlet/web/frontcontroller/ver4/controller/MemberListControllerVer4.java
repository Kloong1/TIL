package com.kloong.servlet.web.frontcontroller.ver4.controller;

import com.kloong.servlet.domain.member.Member;
import com.kloong.servlet.domain.member.MemberRepository;
import com.kloong.servlet.web.frontcontroller.ver4.ControllerVer4;

import java.util.List;
import java.util.Map;

public class MemberListControllerVer4 implements ControllerVer4 {

    private MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    public String process(Map<String, String> paramMap, Map<String, Object> model) {
        List<Member> members = memberRepository.findAll();
        model.put("members", members);
        return "members";
    }
}
