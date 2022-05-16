package com.kloong.corebasic1.lifecycle;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class NetworkClient {
    private String url;

    public NetworkClient() {
        System.out.println("생성자 호출, url = " + url);
    }

    public void setUrl(String url) {
        System.out.println("NetworkClient.setUrl - url = " + url);
        this.url = url;
    }

    //서비스 시작 시 호출
    public void connect() {
        System.out.println("connect: " + url);
    }

    public void sendMessage(String message) {
        System.out.println("call: " + url + " message = " + message);
    }

    //서비스 종료 시 호출
    public void disconnect() {
        System.out.println("disconnect " + url);
    }

    @PostConstruct
    //빈이 등록되고, 의존관계 주입이 끝나면 스프링이 이 메소드를 호출해준다.
    public void init() throws Exception {
        System.out.println("NetworkClient.init");
        connect();
        sendMessage("초기화 연결 메시지");
    }

    @PreDestroy
    //빈 소멸 전에 스프링이 이 메소드를 호출해준다.
    public void close() throws Exception {
        System.out.println("NetworkClient.close");
        disconnect();
    }
}
