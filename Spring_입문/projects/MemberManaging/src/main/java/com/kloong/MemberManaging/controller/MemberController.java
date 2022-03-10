package com.kloong.MemberManaging.controller;

import com.kloong.MemberManaging.domain.Member;
import com.kloong.MemberManaging.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class MemberController
{
   /*
   MemberConroller에 @Controller annotaion을 달아서 Spring bean 형태로 관리할 건데
   MemberService를 이렇게 new로 인스턴스 생성을 한 뒤 MemberService의 기능을 사용하면
   문제가 생긴다. Spring Container에 등록하고 Spring Container로부터 받아 쓰도록 바꿔야 한다.
   MemberController 말고도 다른 Controller에서도 MemberService를 받아 쓸 가능성이 있기 때문에
   인스턴스를 여러개 만들 필요 없이 하나만 만들고 받아 쓰는 것이 더 좋다.
   */
    //private final MemberService memberService = new MemberService();

    private final MemberService memberService;

    //MemberController는 @Controller 가 붙어있으므로 Spring이 올라갈 때 Spring Container에
    //생성됨. 그 때 생성자가 호출되는데, 생성자에 @Autowired 가 붙어있으면
    //memberService를 String Container로부터 가져와서 연결을 시켜준다.
    @Autowired
    public MemberController(MemberService memberService)
    {
        this.memberService = memberService;
        System.out.println("memberService = " + memberService.getClass());
    }

    @GetMapping("members/new")
    public String createForm()
    {
        return "members/createMemberForm";
    }

    @PostMapping("/members/new")
    public String create(MemberForm form)
    {
        Member member = new Member();
        member.setName(form.getName());

        memberService.join(member);

        return "redirect:/";
    }

    @GetMapping("members")
    public String memberList(Model model)
    {
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);
        return "members/memberList";
    }
}
