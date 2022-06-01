# Request Mapping - API 예시

회원 관리 기능을 HTTP API로 만든다고 생각하고, 요청 매핑을 어떻게 하는 것이 좋을지 알아보자. 실제 데이터를 넘기거나 하지는 않고 요청 매핑만 해볼 것이다.

### 회원 관리 API 스펙
- 회원 목록 조회: GET `/users`
- 회원 등록: POST `/users`
- 회원 조회: GET `/users/{userId}`
- 회원 수정: PATCH `/users/{userId}`
- 회원 삭제: DELETE `/users/{userId}`

##### MappingApiController.java
```Java
package com.kloong.springmvc.basic.requestmapping;  
  
import org.springframework.web.bind.annotation.*;  
  
@RestController  
@RequestMapping("/mapping/users")  
public class MappingApiController {  
  
    @GetMapping  
    public String users() {  
        return "get users";  
    }  
  
    @PostMapping  
    public String addUser() {  
        return "post user";  
    }  
  
    @GetMapping("/{userId}")  
    public String findUser(@PathVariable String userId) {  
        return "get userId = " + userId;  
    }  
  
    @PatchMapping("/{userId}")  
    public String updateUser(@PathVariable String userId) {  
        return "update userId = " + userId;  
    }  
  
    @DeleteMapping("/{userId}")  
    public String deleteUser(@PathVariable String userId) {  
        return "delete userId = " + userId;  
    }  
}
```

### Postman으로 테스트
- 회원 목록 조회: GET `/mapping/users`
- 회원 등록: POST `/mapping/users`
- 회원 조회: GET `/mapping/users/id1`
- 회원 수정: PATCH `/mapping/users/id1`
- 회원 삭제: DELETE `/mapping/users/id1`