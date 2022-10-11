@FunctionalInterface
public interface MyFunction {

    int max(int a, int b);

    default int min(int a, int b) {
        return Math.min(a, b);
    }
}
