public class Outer {
    int val = 10;

    class Inner {
        int val = 50;

        void method(final int i) {

            MyFunc f = () -> {
                System.out.println(i);
                System.out.println(++this.val);
            };

        }
    }

}

interface MyFunc {
    void run();
}