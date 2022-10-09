import java.io.IOException;

public class HotelBookingApp {
    public static void main(String[] args) throws IOException {
        Simulator simulator = new Simulator(2);
        simulator.simulate();
    }
}
