package yeoksamstationexit1.usermanage.room.roomDTO;

import lombok.Data;

@Data
public class StationDTO {
    private int stationId;
    private String stationName;
    private Double longitude;
    private Double latitude;
    private Integer infraCount;
}