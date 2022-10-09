package reservation;

import dto.ReplyReservationDto;
import dto.ReservationDto;
import dto.RoomAssignDto;

import java.util.*;

public class ReservationManager {

    private final RoomManager roomManager;

    private List<AcceptedReservationDto> acceptedReservationList = new LinkedList<>();

    private static final String ACCEPTED = "accepted";
    private static final String REFUSED = "refused";

    public ReservationManager(int problem) {
        roomManager = new RoomManager(problem);
    }

    public List<ReplyReservationDto> takeReservations(List<ReservationDto> reservationList) {
        List<ReplyReservationDto> replyReservationList = new ArrayList<>();

        for (ReservationDto reservation : reservationList) {
            Integer emptyRoomNumber = roomManager.getEmptyRoomNumber(reservation.getAmount(), reservation.getCheckInDate(), reservation.getCheckOutDate());
            if (emptyRoomNumber == null) {
                replyReservationList.add(new ReplyReservationDto(reservation.getId(), REFUSED));
            } else {
                replyReservationList.add(new ReplyReservationDto(reservation.getId(), ACCEPTED));
                acceptedReservationList.add(new AcceptedReservationDto(reservation, emptyRoomNumber));
            }
        }

        return replyReservationList;
    }

    public List<RoomAssignDto> assignRooms(int day) {
        List<RoomAssignDto> roomAssignList = new ArrayList<>();

        for (AcceptedReservationDto reservation : acceptedReservationList) {
            if (reservation.getReservation().getCheckInDate() == day) {
                roomAssignList.add(new RoomAssignDto(reservation.getReservation().getId(), reservation.getRoomNumber()));
            }
        }

        for (int i = (acceptedReservationList.size() - 1); i >= 0 ; i--) {
            if (acceptedReservationList.get(i).getReservation().getCheckInDate() == day) {
                acceptedReservationList.remove(i);
            }
        }

        return roomAssignList;
    }
}
