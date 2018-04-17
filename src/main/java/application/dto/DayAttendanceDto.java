package application.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Builder;
import lombok.Data;

/**
 * 1日分の勤怠情報DTO
 * @author 作成者氏名
 */
@Data
@Builder
@JsonPropertyOrder({ "ユーザID", "出勤日", "出勤時刻", "退勤時刻" })
public class DayAttendanceDto implements Serializable {
    /** ユーザID */
    @JsonProperty("ユーザID")
    private Integer userId;

    /** 出勤日(yyyymmdd) */
    @JsonProperty("出勤日")
    private String attendanceDay;

    /** 出勤時刻 */
    @JsonProperty("出勤時刻")
    @JsonFormat(pattern = "HH:mm")
    private LocalDateTime arrivalTime;

    /** 退勤時刻 */
    @JsonProperty("退勤時刻")
    @JsonFormat(pattern = "HH:mm")
    private LocalDateTime clockOutTime;

    /**
     * デフォルトコンストラクタ.
     */
    public DayAttendanceDto() {
    }

    /**
     * コンストラクタ.
     * @param userId ユーザID
     * @param attendanceDay 出勤日
     * @param arrivaTime 出勤時刻
     * @param clockOutTime 退勤時刻
     */
    public DayAttendanceDto(Integer userId, String attendanceDay, LocalDateTime arrivaTime,
            LocalDateTime clockOutTime) {
        this.userId = userId;
        this.attendanceDay = attendanceDay;
        this.arrivalTime = arrivaTime;
        this.clockOutTime = clockOutTime;
    }
}
