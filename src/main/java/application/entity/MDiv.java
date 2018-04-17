package application.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 汎用区分マスタエンティティ
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MDiv extends AbstractEntity {

    /** 汎用区分ID */
    private Integer divId;

    /** 区分名 */
    private String divName;

    /** 区分物理名 */
    private String divPhysicalName;

    /** 変更可能フラグ */
    private String allowUpdateFlg;

}
