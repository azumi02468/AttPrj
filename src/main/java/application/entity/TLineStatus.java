package application.entity;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * LINEステータス情報エンティティ
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TLineStatus extends AbstractEntity {

    /** LINE識別子 */
    private String lineId;

    /** ユーザID */
    private Integer userId;

    /** メニューコード */
    private String menuCd;

    /** アクション名 */
    private String actionName;

    /** コンテンツ */
    private String contents;

    /** リクエスト時刻 */
    private Date requestTime;
}
