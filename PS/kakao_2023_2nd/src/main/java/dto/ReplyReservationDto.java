package dto;

import lombok.Data;

@Data
public class ReplyReservationDto {
    private Integer id;
    private String reply;

    public ReplyReservationDto(Integer id, String reply) {
        this.id = id;
        this.reply = reply;
    }
}
