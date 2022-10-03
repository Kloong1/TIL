package hello.itemservice.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
//@Table(name = "item")
public class Item {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@Column 생략하면 자동으로 매칭해 줌. snake case도 camel case로 변경해준다.
    //item_name 은 그냥 예시로 쓴 것. 생략해도 알아서 매칭해준다.
    @Column(name = "item_name", length = 10)
    private String itemName;
    private Integer price;
    private Integer quantity;

    //JPA 는 public 혹은 protected 의 기본 생성자가 필요하다. JPA 스펙임.
    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
