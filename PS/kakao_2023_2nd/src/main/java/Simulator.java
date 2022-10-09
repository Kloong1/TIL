import api.*;
import dto.ReplyReservationDto;
import dto.ReservationDto;
import dto.RoomAssignDto;
import reservation.ReservationManager;

import java.io.IOException;
import java.util.List;

public class Simulator {

    private int problem;
    private int dayLimit;
    private String authKey;

    private ReservationManager reservationManager;

    private NewRequestsApi newRequestsApi;
    private ReplyApi replyApi;
    private SimulateApi simulateApi;
    private ScoreApi scoreApi;

    public Simulator(int problem) {
        this.problem = problem;
        this.dayLimit = problem == 1 ? 200 : 1000;
        this.reservationManager = new ReservationManager(problem);
    }

    public void simulate() throws IOException {
        init();

        int day = 1;

        while (day <= dayLimit) {
            System.out.println("\nday = " + day);
            List<ReservationDto> reservationList = newRequestsApi.getReservationList();
            System.out.println("reservationList = " + reservationList);

            List<ReplyReservationDto> replyReservationList = reservationManager.takeReservations(reservationList);
            System.out.println("replyReservationList = " + replyReservationList);

            replyApi.replyReservations(replyReservationList);

            List<RoomAssignDto> roomAssignList = reservationManager.assignRooms(day);
            System.out.println("roomAssignList = " + roomAssignList);

            day = simulateApi.assignRoom(roomAssignList);
        }

        scoreApi.printScore();
    }

    private void init() throws IOException {
        StartApi startApi = new StartApi();
        authKey = startApi.getAuthorizationKey(problem);
        System.out.println("authKey = " + authKey);

        newRequestsApi = new NewRequestsApi(authKey);
        replyApi = new ReplyApi(authKey);
        simulateApi = new SimulateApi(authKey);
        scoreApi = new ScoreApi(authKey);
    }
}
