# 회원 관리 예제 프로젝트 3 - 웹 MVC 개발

### 프로젝트 구조
![400](스크린샷%202022-03-03%20오후%208.58.58.png)

## 1. 홈 화면 추가

#### HomeController.java
```Java
package com.kloong.MemberManaging.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController
{
    @GetMapping("/")
    public String home()
    {
        return "home";
    }
}
```
`localhost:8080` 요청에 대한 Controller.

#### home.html
```HTML
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<body>
<div class="container">
    <div>
        <h1>Hello Spring</h1>
        <p>회원 기능</p>
        <p>
            <a href="/members/new">회원 가입</a>
            <a href="/members">회원 목록</a>
        </p>
    </div>
</div> <!-- /container -->
</body>
</html>
```


## 2. 회원 등록 화면 추가

#### MemberController.java
```Java
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
    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService)
    {
        this.memberService = memberService;
    }

	//localhost:8080/members/new 에 대한 GET 요청을 mapping
	//홈 화면의 "회원 가입" 링크를 누르면 이 method가 호출되고
	//members/createMemberForm.html 화면을 뿌린다.
    @GetMapping("members/new")
    public String createForm()
    {
        return "members/createMemberForm";
    }

	//localhost:8080/members/new 에 대한 POST 요청을 mapping
	//웹 브라우저에서 보내는 정보는 Spring에 의해 MemberForm 객체에 자동으로 담겨짐.
	//새로운 Member를 만들고, join()을 한 뒤 홈 화면으로 redirect
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
```

#### createMemberFrom.html
```HTML
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<body>

<div class="container">

	<!-- submit button을 누르면 /members/new에 대해 POST 요청을 보냄 -->
    <form action="/members/new" method="post">
        <div class="form-group">
            <label for="name">이름</label>
			<!--input에 입력되는 값이 name="name"에 의해 "name"이란 key를 가지고 서버로 보내짐-->
            <input type="text" id="name" name="name" placeholder="이름을 입력하세요">
        </div>
        <button type="submit">등록</button>
    </form>

</div> <!-- /container -->

</body>
</html>
```

"등록" 버튼을 누르면, input에 입력되는 값이 `name="name"` 속성에 의해 "name"이란 key를 가지고 POST request에 담겨서 보내진다.

그러면 MemberController의 `@PostMapping("/members/new")` 에 의해 `create` method가 호출된다.

이 때 웹 페이지의 \<input\>에 입력된 값은 Spring이 자동으로 `create` method의 매개변수인 `form` 에 넣어준다.

#### MemberForm.java
```Java
package com.kloong.MemberManaging.controller;

public class MemberForm
{
    private String name;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}

```

#### Spring이 어떻게 \<form\> 태그에 입력되는 값을 전달받는 것일까? 
MemberForm의 name 변수와, \<input\> 태그의 `name="name"`이 매칭되어서 (Spring이 알아서 찾아준다), name의 setter인 `setName` method를 호출하여 input에 입력된 값이 name 변수로 들어간다.

이 값을 `create` method에서 사용하게 되는 것이다.


## 3. 회원 조회 화면 추가

#### MemberController.java
```Java
//package, import 생략

@Controller
public class MemberController
{
    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService)
    {
        this.memberService = memberService;
    }

	//중략...

	//홈 화면의 "회원 조회" 링크를 누르면 호출되는 method
	//가입 되어있는 모든 회원을 List<Member> 형태로 받아서 model에 추가.
    @GetMapping("members")
    public String memberList(Model model)
    {
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);
        return "members/memberList";
    }
}
```

#### memberList.html
```HTML
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<body>
<div class="container">
    <div>
        <table>
            <thead> <!-- table의 맨 윗줄 -->
            <tr>
                <th>#</th>
                <th>이름</th>
            </tr>
            </thead>
            <tbody> <!-- table의 내용. tr은 row, td는 row의 각 cell을 의미. -->
			<!-- thymeleaf 문법 사용. MemberController에서 model에 추가한
				List<Member> members를 하나씩 탐색하면서 (반복문을 돌면서)
				<tr>의 내용을 넣어주는 것임. -->
            <tr th:each="member : ${members}">
				<!-- id와 name은 Member 클래스의 멤버 변수 이름.
					private 변수이므로 getter를 이용해서 접근함. -->
                <td th:text="${member.id}"></td>
                <td th:text="${member.name}"></td>
            </tr>
            </tbody>
        </table>
    </div>
</div> <!-- /container -->
</body>
</html>
```

회원 조회 화면의 경우, 모든 회원 정보를 화면에 뿌려야 하는데 회원 수는 유동적이므로 여기서는 템플릿 엔진인 thymeleaf 문법을 사용해서 html 화면을 만들어줘야 한다.

MemberController에서 model에 추가했던 회원 리스트인 members로 loop를 돌면서 \<tr\>을 만들어준다.