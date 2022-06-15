package kloong.itemservice.domain.item;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ItemRepository {

    private static final Map<Long, Item> store = new HashMap<>();
    private static long sequence = 0L;

    public Item save(Item item) {
        item.setId(++sequence);
        store.put(item.getId(), item);
        return item;
    }

    public Item findById(Long id) {
        return store.get(id);
    }

    public List<Item> findAll() {
        return new ArrayList<>(store.values());
    }

    //원래는 update를 위한 parameter를 갖고 있는 객체를 새로 만드는게 맞다.
    //ex. ItemParamDto 객체
    //updateParam은 id도 갖고 있고, 심지어 값을 set 할수도 있어서 사용자가 혼란스러울 수 있다.
    //프로젝트가 작으니까 그냥 이렇게 하는 것이다.
    //중복인 것 같아도 명확하게 하기 위해 프로젝트가 커지면 객체를 새로 만드는게 좋다.
    public void update(Long itemId, Item updateParam) {
        Item targetItem = findById(itemId);
        targetItem.setItemName(updateParam.getItemName());
        targetItem.setPrice(updateParam.getPrice());
        targetItem.setQuantity(updateParam.getQuantity());
    }

    public void clearStore() {
        store.clear();
    }
}
