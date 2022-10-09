package reservation;

import dto.ReservationDto;
import lombok.Data;

@Data
public class AcceptedReservationDto {
    private ReservationDto reservation;
    private int roomNumber;

    public AcceptedReservationDto(ReservationDto reservation, int roomNumber) {
        this.reservation = reservation;
        this.roomNumber = roomNumber;
    }
}
