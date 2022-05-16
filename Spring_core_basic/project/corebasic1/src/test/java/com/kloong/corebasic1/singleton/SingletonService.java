package com.kloong.corebasic1.singleton;

public class SingletonService {
    //SingletonService의 객체는 클래스 영역에 하나만 존재하게 된다.
    private static final SingletonService instance = new SingletonService();

    public static SingletonService getInstance() {
        return instance;
    }

    //private 생성자로 외부에서 객체 생성을 막는다
    private SingletonService() {}

    public void logic() {
        //instance를 가지고 할 수 있는 임의의 작업
        System.out.println("싱글톤 객체 로직 호출");
    }
}
