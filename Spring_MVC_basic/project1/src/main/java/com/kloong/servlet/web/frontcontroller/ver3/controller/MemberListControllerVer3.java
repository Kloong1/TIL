package com.kloong.servlet.web.frontcontroller.ver3.controller;

import com.kloong.servlet.domain.member.Member;
import com.kloong.servlet.domain.member.MemberRepository;
import com.kloong.servlet.web.frontcontroller.ModelView;
import com.kloong.servlet.web.frontcontroller.ver3.ControllerVer3;

import java.util.List;
import java.util.Map;

public class MemberListControllerVer3 implements ControllerVer3 {
    private MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    public ModelView process(Map<String, String> paramMap) {
        List<Member> members = memberRepository.findAll();
        ModelView mv = new ModelView("members");
        mv.getModel().put("members", members);

        return mv;
    }
}
