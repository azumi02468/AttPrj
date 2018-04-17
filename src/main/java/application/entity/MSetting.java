package application.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 設定マスタエンティティ。
 * システムデフォルト値を持つ。
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MSetting extends AbstractEntity {

    /** 始業時刻（時） */
    private String openTime = "09";

    /** 始業時刻（分） */
    private String openMinutes = "00";

    /** 終業時刻（時） */
    private String closeTime = "20";

    /** 終業時刻（分） */
    private String closeMinutes = "00";

    /** アラート出勤時刻（時） */
    private String alertOpenTime = "08";

    /** アラート出勤時刻（分） */
    private String alertOpenMinutes = "55";

    /** アラート退勤時刻（時） */
    private String alertCloseTime = "20";

    /** アラート退勤時刻（分） */
    private String alertCloseMinutes = "00";

    /** 営業日フラグ(月) */
    private String businessFlagMon = "1";

    /** 営業日フラグ(火) */
    private String businessFlagTue = "1";

    /** 営業日フラグ(水) */
    private String businessFlagWed = "1";

    /** 営業日フラグ(木) */
    private String businessFlagThu = "1";

    /** 営業日フラグ(金) */
    private String businessFlagFri = "1";

    /** 営業日フラグ(土) */
    private String businessFlagSat = "0";

    /** 営業日フラグ(日) */
    private String businessFlagSun = "0";

    /** 打刻漏れ防止アラートフラグ */
    private String alertFlag = "1";
}
