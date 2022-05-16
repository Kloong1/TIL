package com.kloong.corebasic1;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LombokTest {

    private String name;

    public static void main(String[] args) {
        LombokTest lombokTest = new LombokTest();

        lombokTest.setName("kim");
        String name = lombokTest.getName();
        System.out.println("name = " + name);
    }
}
