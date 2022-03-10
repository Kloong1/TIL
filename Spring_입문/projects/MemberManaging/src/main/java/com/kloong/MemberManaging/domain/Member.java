package com.kloong.MemberManaging.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Member
{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //고객이 정하는 id가 아닌 고유값으로써의 id
    private String name;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
