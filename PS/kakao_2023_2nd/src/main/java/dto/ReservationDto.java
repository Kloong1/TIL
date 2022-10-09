package dto;

import lombok.Data;

@Data
public class ReservationDto {
    private Integer id;
    private Integer amount;
    private Integer checkInDate;
    private Integer checkOutDate;

    public ReservationDto(Integer id, Integer amount, Integer checkInDate, Integer checkOutDate) {
        this.id = id;
        this.amount = amount;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
    }
}
