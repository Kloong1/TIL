import java.util.*;
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

        Integer[] integerArr = {5, 5, 3, 9, 2, 11};

        Arrays.stream(integerArr) // Stream<Integer>
                .filter(n -> n < 10) // 중간 연산을 연쇄적으로 적용
                .distinct()
                .sorted()
                .limit(3)
                .forEach(System.out::println); // 최종 연산

        String[] strArr = {"1", "2", "3"};

        Arrays.stream(strArr)
                .map(str -> Integer.parseInt(str))
                .forEach(System.out::print);
    }
}
