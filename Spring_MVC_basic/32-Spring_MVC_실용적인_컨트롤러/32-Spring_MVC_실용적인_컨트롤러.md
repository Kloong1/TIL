# Spring MVC - 실용적인 컨트롤러

MVC 프레임워크를 직접 만들었을 때, Ver.3은 컨트롤러에서 ModelView를 직접 생성해서 반환해야 했었기 때문에 불편했었다. 그래서 Ver.4 에서 그 부분을 실용적으로 개선했었다.

지난번에 만든 Spring MVC 컨트롤러도 Ver.3과 마찬가지로 `ModelAndView` 객체를 직접 생성해서 반환한다. Spring MVC는 개발자가 더 편리하게 개발할 수 있도록 수 많은 편의 기능을 제공하기 때문에, 그 기능을 활용해서 컨트롤러를 실용적으로 개선해보자.

**실무에서는 지금부터 설명하는 방식을 주로 사용한다.**

## Controller 개선
##### SpringMemberControllerVer3.java
```Java
package com.kloong.servlet.web.springmvc.ver3;

import ... //생략

@Controller
@RequestMapping("/springmvc/v3/members")
public class SpringMemberControllerVer3 {
    private MemberRepository memberRepository = MemberRepository.getInstance();

    @RequestMapping("/new-form")
    public String newForm() {
        return "new-form";
    }

    @RequestMapping("/save")
    public String save(
            @RequestParam("username") String username,
            @RequestParam("age") int age,
            Model model) {

        Member member = new Member(username, age);
        memberRepository.save(member);

        model.addAttribute("member", member);
        return "save-result";
    }

    @RequestMapping
    public String members(Model model) {
        List<Member> members = memberRepository.findAll();
        model.addAttribute("members", members);
        return "members";
    }
}
```

### 개선된 Controller 특징
#### 1. String 타입의 뷰 논리 이름 반환
더 이상 `ModelAndView` 객체를 직접 생성해서 반환하지 않는다. String 타입의 뷰의 논리 이름을 반환하기만 하면 끝난다. 이렇게 해도 핸들러 어댑터가 `ModelAndView` 객체를 알아서 생성해준다.

#### 2. Model 파라미터
더이상 Model 데이터와 뷰 논리 이름을 담은 `ModelAndView` 객체를 생성해서 반환하지 않는다. 따라서 Model 데이터를 담을 `Model` 객체를 파라미터로 받아서 해당 객체에 데이터를 저장한다.

#### 3. @RequestParam 사용
Spring MVC 컨트롤러는 HTTP 요청 파라미터를 받기 위해 `HttpServletRequest` 객체 전체를 파라미터로 받을 필요가 없다. `@RequestParam` 애노테이션으로 원하는 요청 파라미터만 받을 수 있다.

`@RequestParam("username")` 은 `request.getParameter("username")` 과 거의 같은 코드라고 보면 된다. 필요한 요청 파라미터만 받을 수 있어서 직관적이고, 파싱도 알아서 해주기 때문에 더 편리하다.

물론 GET의 쿼리 파라미터와 POST Form 방식을 모두 지원한다.


## @GetMapping 과 @PostMapping
지금까지는 (Servlet, 직접 만든 MVC 프레임워크를 모두 포함해서) HTTP 요청 URL만 매핑 정보로 사용했지, HTTP Method는 전혀 고려하지 않았다.

예를 들어 `/springmvc/v3/members/new-form` 에 대한 요청이 GET으로 들어오든 POST로 들어오든 간에 동일한 뷰를 보여준다. 하지만 실무에서 RESTful API를 설계할 떄는 HTTP Method를 고려해서 설계하기 때문에 URL만 가지고 매핑하는 것은 좋지 않다.

##### POST로 /springmvc/v3/members/new-form 요청
![](스크린샷%202022-05-31%20오후%205.34.41.png)

설계 의도대로라면, `/springmvc/v3/members/new-form` 에 대한 요청은 GET 메소드만 허용하는 것이 더 적합할 것이다. 하지만 우리가 작성한 컨트롤러는 POST 메소드로 요청을 해도 쓸데없이 응답을 잘 해준다.

이런 점을 고려해서 약간의 수정을 해보자.

`@RequestMapping` 은 URL만 매핑하는 것이 아니라, HTTP Method 정보도 함께 활용해서 매핑이 가능하다.

예를 들어서 요청 URL이 `/new-form` 이고, HTTP Method가 GET인 경우를 모두 만족하는 매핑을 하려면 다음과 같이 처리하면 된다.

```Java
@RequestMapping(value = "/new-form", method = RequestMethod.GET)
```

위 애노테이션을 `@GetMapping` 으로 더 편리하게 사용할 수 있다. 참고로 Get, Post, Put, Delete, Patch 모두 해당되는 애노테이션이 존재한다.

>참고
>`@GetMapping` 내부를 살펴보면 `@RequestMapping(method = RequestMethod.GET)` 가 들어있다. Spring은 개발자의 편의를 위해 위와 같은 방식으로 애노테이션을 조합해서 제공하기도 한다.

##### SpringMemberControllerVer3.java
```Java
package com.kloong.servlet.web.springmvc.ver3;

import ... //생략

@Controller
@RequestMapping("/springmvc/v3/members")
public class SpringMemberControllerVer3 {
    private MemberRepository memberRepository = MemberRepository.getInstance();

    @GetMapping("/new-form")
    public String newForm() {
        return "new-form";
    }

    @PostMapping("/save")
    public String save(
            @RequestParam("username") String username,
            @RequestParam("age") int age,
            Model model) {

        Member member = new Member(username, age);
        memberRepository.save(member);

        model.addAttribute("member", member);
        return "save-result";
    }

    @GetMapping
    public String members(Model model) {
        List<Member> members = memberRepository.findAll();
        model.addAttribute("members", members);
        return "members";
    }
}
```

`newForm()` 과 `members()` 는 GET 메소드에만 매핑하고, `save()` 는 POST 메소드에만 매핑했다.

이제 `http://localhost:8080/springmvc/v3/members/new-form` 로 POST 요청을 보내면 다음과 같은 응답이 온다.

```
{"timestamp":"2022-05-31T08:40:40.575+00:00","status":405,"error":"Method Not Allowed","path":"/springmvc/v3/members/new-form"}
```