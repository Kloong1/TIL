# @Qulifier annotation 만들기
`@Qulifier` 를 사용할 때 구분자 이름을 지정해야 한다. 문제는 구분자 이름이 문자열 형태라는 것. `@Qualifier("mainDiscountPolicy") `이렇게 문자열로 구분자를 적으면 컴파일 시 타입 체크가 불가능하다.

다음과 같은 annotation을 만들어서 문제를 해결할 수 있다.

#### MainDiscountPolicy.java
```Java
package com.kloong.corebasic1.annotation;

import org.springframework.beans.factory.annotation.Qualifier;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Qualifier("mainDiscountPolicy")
public @interface MainDiscountPolicy {
}
```

이 annotation에 붙은 annotation들은 전부 `Quilifier` annotation에서 복사해온 것이다.

#### Quilifier.java
```Java
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Qualifier {
	String value() default "";
}
```

이제 MainDiscountPolicy annotation을 RateDiscountPolicy에 적용해보자.

기존의 `@Quilifier` 를 적용한 코드는 다음과 같다.
```Java
//package, import 생략

@Component
@Primary
@Qualifier("mainDiscountPolicy")
public class RateDiscountPolicy implements DiscountPolicy{ /* 생략 */ }
```

구분자를 지정해 줄 때, 문자열 형태로 지정하기 때문에 오타가 발생해도 컴파일 타임에 오류를 잡을 수 없다. 물론 이 문제는 의존관계를 주입 받는 곳에서도 동일하게 적용된다 (주입 받을 때도 코드에`@Qulifier("mainDiscountPolicy")` 이렇게 문자열 형태로 작성해야 하기 때문).

하지만 방금 직접 만든 MainDiscountPolicy annotation을 적용하면,

```Java
package com.kloong.corebasic1.discount;
//package, import 생략

@Component
@Primary
@MainDiscountPolicy
public class RateDiscountPolicy implements DiscountPolicy{ /* 생략 */ }
}
```

오타가 발생하면 당연하게도 컴파일 에러가 난다.

해당 빈을 주입 받는 코드에서도 MainDiscountPolicy annotation을 동일하게 사용하면 된다.

#### OrderServiceImpl.java
```Java
//package, import 생략

@Component
public class OrderServiceImpl implements OrderService{

    private final MemberRepository memberRepository;
    private final DiscountPolicy discountPolicy;

    public OrderServiceImpl(MemberRepository memberRepository,
    @MainDiscountPolicy DiscountPolicy discountPolicy) {
        this.memberRepository = memberRepository;
        this.discountPolicy = discountPolicy;
    }

	//생략
}
```

또 annotation을 만들어서 사용하면, IDE를 통해 해당 annotation을 사용하는 코드를 추적할 수도 있기 때문에 편리하다.

>annotation에는 상속이라는 개념이 없다. 이렇게 여러 annotation을 모아서 사용하는 기능은 스프링이 지원해주는 기능이다. `@Qulifier` 뿐만 아니라 다른 annotation들도 함께 조합해서 사용할 수 있다. 단적으로 `@Autowired` 도 재정의 할 수 있다. 물론 스프링이 제공하는 기능을 뚜렷한 목적 없이 무분별하게 재정의 하는 것은 유지보수에 더 혼란만 가중할 수 있다.