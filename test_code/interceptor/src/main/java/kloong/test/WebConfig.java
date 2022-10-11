package kloong.test;

import kloong.test.interceptor.HandlerInterceptor1;
import kloong.test.interceptor.HandlerInterceptor2;
import kloong.test.interceptor.HandlerInterceptor3;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor1())
                .order(1)
                .addPathPatterns("/**");

        registry.addInterceptor(new HandlerInterceptor2())
                .order(2)
                .addPathPatterns("/**");

        registry.addInterceptor(new HandlerInterceptor3())
                .order(3)
                .addPathPatterns("/**");
    }
}
