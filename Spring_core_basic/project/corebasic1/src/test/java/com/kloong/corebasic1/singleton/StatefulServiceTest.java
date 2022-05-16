package com.kloong.corebasic1.singleton;

import com.kloong.corebasic1.order.OrderService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import static org.junit.jupiter.api.Assertions.*;

class StatefulServiceTest {

    @Test
    void statefulServiceSingleton() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(TestConfig.class);

        StatefulService statefulService1 = ac.getBean("statefulService", StatefulService.class);
        StatefulService statefulService2 = ac.getBean("statefulService", StatefulService.class);

        //Thread A에서 userA가 10000원 주문
        statefulService1.order("userA", 10000);
        //Thread B에서 userB가 20000원 주문 - userA의 작업이 전부 끝나기 전에 userB의 작업이 끼어들었다.
        statefulService2.order("userA", 20000);

        //Thread A에서 userA가 주문 금액 조회. 10000원이 나와야 하는데 과연?
        int price1 = statefulService1.getPrice();
        //Thread B에서 userB가 주문 금액 조회
        int price2 = statefulService2.getPrice();

        //스프링 컨테이너에 의해 같은 객체를 공유하기 때문에 둘 다 20000이 출력된다.
        System.out.println("price1 = " + price1);
        System.out.println("price2 = " + price2);

        //이게 통과하면 오히려 문제가 있는 것. 이 서비스는 망했다.
        //실제 thread를 적용해서 테스트하려고 하면 테스트도 어렵다.
        Assertions.assertThat(statefulService1.getPrice()).isEqualTo(20000);
    }

    static class TestConfig {
        @Bean
        public StatefulService statefulService() {
            return new StatefulService();
        }
    }
}