package dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RoomAssignDto {
    private Integer id;
    @JsonProperty("room_number")
    private Integer roomNumber;

    public RoomAssignDto(Integer id, Integer roomNumber) {
        this.id = id;
        this.roomNumber = roomNumber;
    }
}
