package application.entity;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 勤怠情報エンティティ
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TAttendance extends AbstractEntity {
    /** ユーザID */
    private Integer userId;

    /** 出勤日(yyyymmdd) */
    private String attendanceDay;

    /** 勤怠時刻 */
    private Date attendanceTime;

    /** 勤怠区分コード(汎用区分:2を参照) */
    private String attendanceCd;

    /** 修正フラグ */
    private String editFlg;
}
