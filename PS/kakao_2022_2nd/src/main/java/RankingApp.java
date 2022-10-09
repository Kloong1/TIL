import java.io.*;
import java.util.ArrayList;

public class RankingApp {

    public static void main(String[] args) throws IOException {
        ArrayList<Thread> simulatorThreadList = new ArrayList<>();

        for (int i = 0; i < 1; i++) {
            simulatorThreadList.add(new Thread(new Simulator(/*params*/)));
        }

        for (Thread thread : simulatorThreadList) {
            thread.start();
        }

        for (Thread thread : simulatorThreadList) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
