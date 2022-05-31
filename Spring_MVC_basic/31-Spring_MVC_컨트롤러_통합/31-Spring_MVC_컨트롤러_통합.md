# Spring MVC - 컨트롤러 통합

지난번에 등록했던 3개의 Ver.1 컨트롤러 `SpringMemberFormControllerVer1`, `SpringMemberSaveControllerVer1`, `SpringMemberListControllerVer1` 를 살펴보면, `@ReqeustMapping` 애노테이션이 클래스 레벨이 아닌 메서드 레벨에 적용되어 있는 것을 확인할 수 있다.

따라서 `@Controller` 애노테이션이 붙은 컨트롤러 클래스를 하나만 만들고, 하나의 클래스 안에 `@RequestMapping` 되어 있는 메서드들을 전부 넣어서 3개의 Ver.1 컨트롤러를 하나로 통합할 수 있다.

##### SpringMemberControllerVer2.java
```Java
package com.kloong.servlet.web.springmvc.ver2;

import ... //생략

@Controller
public class SpringMemberControllerVer2 {

    private MemberRepository memberRepository = MemberRepository.getInstance();

    @RequestMapping("/springmvc/v2/members/new-form")
    public ModelAndView newForm() {
        return new ModelAndView("new-form");
    }

    @RequestMapping("/springmvc/v2/members/save")
    public ModelAndView save(HttpServletRequest request, HttpServletResponse response) {
        String username = request.getParameter("username");
        int age = Integer.parseInt(request.getParameter("age"));

        Member member = new Member(username, age);
        memberRepository.save(member);

        ModelAndView mv = new ModelAndView("save-result");
        mv.addObject("member", member);
        return mv;
    }
    
    @RequestMapping("/springmvc/v2/members")
    public ModelAndView members() {
        List<Member> members = memberRepository.findAll();
        ModelAndView mv = new ModelAndView("members");
        mv.addObject("members", members);
        return mv;
    }
}
```

이렇게 의미/기능적으로 공통점이 있는 `@RequestMapping` 메서드들을 하나의 컨트롤러로 통합하면 유지보수가 편하다.

### @RequestMapping URL 중복 제거
위의 컨트롤러 코드를 살펴보면, `@RequestMapping("URL")` 의 URL 경로에 중복이 있는 것을 발견할 수 있다.

```Java
@RequestMapping("/springmvc/v2/members/new-form")
@RequestMapping("/springmvc/v2/members")
@RequestMapping("/springmvc/v2/members/save")
```

`/springmvc/v2/members` 가 반복적으로 나타난다. 물론 특별히 문제가 있는건 아니지만, 이런 중복은 제거하는 것이 나중에 유지보수할 때 좋다.

##### SpringMemberControllerVer2.java
```Java
package com.kloong.servlet.web.springmvc.ver2;

import ... //생략

@Controller
@RequestMapping("/springmvc/v2/members")
public class SpringMemberControllerVer2 {

    private MemberRepository memberRepository = MemberRepository.getInstance();

    @RequestMapping("/new-form")
    public ModelAndView newForm() {
        return new ModelAndView("new-form");
    }

    @RequestMapping("/save")
    public ModelAndView save(HttpServletRequest request, HttpServletResponse response) {
        String username = request.getParameter("username");
        int age = Integer.parseInt(request.getParameter("age"));

        Member member = new Member(username, age);
        memberRepository.save(member);

        ModelAndView mv = new ModelAndView("save-result");
        mv.addObject("member", member);
        return mv;
    }

    @RequestMapping
    public ModelAndView members() {
        List<Member> members = memberRepository.findAll();
        ModelAndView mv = new ModelAndView("members");
        mv.addObject("members", members);
        return mv;
    }
}
```

클래스 레벨에 중복된 URL 경로에 대해서 `@RequestMapping` 애노테이션을 추가하고, 각 메서드에는 중복된 URL을 제거했다.

```Java
@RequestMapping("/springmvc/v2/members")
public class SpringMemberControllerVer2 {
	@RequestMapping("/new-form")
    public ModelAndView newForm() {}

    @RequestMapping("/save")
    public ModelAndView save(HttpServletRequest request,
    HttpServletResponse response) {}

    @RequestMapping
    public ModelAndView members() {}
```

`members()` 의 경우 `/springmvc/v2/members` 로 요청이 왔을 때 호출되는 메소드이므로, URL 없이 `@RequestMapping` 만 붙여주면 된다.

- 클래스 레벨 `@RequestMapping("/springmvc/v2/members")`
	- 메서드 레벨 `@RequestMapping("/new-form")` -> `/springmvc/v2/members/new-form`
	- 메서드 레벨 `@RequestMapping("/save")` -> `/springmvc/v2/members/save`
	- 메서드 레벨 `@RequestMapping` -> `/springmvc/v2/members`
