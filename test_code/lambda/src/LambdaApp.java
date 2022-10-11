import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class LambdaApp {
    public static void main(String[] args) {
        MyFunction myFunction = (a, b) -> a > b ? a : b;

        List<Integer> list = new ArrayList<>();

        list.add(3);
        list.add(5);
        list.add(7);

        Iterator<Integer> iterator = list.iterator();
        while (iterator.hasNext()) {
            Integer next = iterator.next();
            System.out.println("next = " + next);
        }

        list.forEach(e -> System.out.println("e = " + e));

        MyFunction2 myFunction2 = name -> System.out.println("name = " + name);
    }
}
