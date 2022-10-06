package dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class StartDto {
    @JsonProperty("auth_key")
    private String authKey;
    private Integer problem;
    private Integer time;
}
