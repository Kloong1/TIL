package com.kloong.hellospring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloController
{
    //WebApp에서 /hello로 요청을 보내면 이 method를 spring에서 호출해준다.
    //여기서 Get은 HTTP Request에서의 GET을 의미한다.
    @GetMapping("hello")
    public String hello(Model model) //MVC에서의 Model이다.
    {
        //model에 attribute를 추가함.
        model.addAttribute("data", "hello!!");
        //이 return 값의 의미는
        //resources/templates/hello.html을 찾아서 그 화면을 보여줘라! 라는 의미
        return "hello";
    }

    @GetMapping("hello-mvc")
    //@RequestParam("name")은 웹 브라우저로부터 "name"이라는
    //parameter를 전달받아서 쓴다는 의미.
    //name parameter의 값이 String 형태로 name 변수에 저장된다.
    //그거 말고는 위의 hello method와 동일하다고 보면 된다.
    public String helloMVC(@RequestParam(name = "name", required = false) String name, Model model)
    {
        model.addAttribute("name", name);
        return "hello-template";
    }

    //@ResponseBody annotaion을 쓰면
    //view를 찾아서 (html 파일을 찾아서) 거기다 작업을 한 다음 보여주는 것이 아니라
    //웹브라우저에 HTTP 응답 메세지를 보낼 때 body에 return값을 그대로 넘겨준다
    @GetMapping("hello-string")
    @ResponseBody
    public String helloString(@RequestParam("name") String name)
    {
        return "hello " + name;
    }

    //@ResponseBody annotation을 쓰는데 객체를 return하는 method의 경우
    //Spring이 해당 객체의 정보를 자동으로 JSON 형태로 변환시켜서
    //HTTP 메세지의 body에 담아서 넘겨줌
    //아래의 경우 {"name" : "<값>"} 이런 형태가 됨.
    @GetMapping("hello-api")
    @ResponseBody
    public Hello helloAPI(@RequestParam("name") String name)
    {
        Hello hello = new Hello();
        hello.setName(name);
        return hello;
    }

static class Hello
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
}
