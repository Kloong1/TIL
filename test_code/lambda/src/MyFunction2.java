@FunctionalInterface
public interface MyFunction2 {

    void run(Object o);

    static void other() {
        System.out.println("hello");
    }
}
