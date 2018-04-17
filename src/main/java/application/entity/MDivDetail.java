package application.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 汎用区分明細マスタエンティティ
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MDivDetail extends AbstractEntity {

    /** 汎用区分ID */
    private Integer divId;

    /** 汎用区分コード */
    private String divCd;

    /** 汎用区分コード内容 */
    private String divCdContent;

    /** フリー属性１ */
    private String freeAttribute01;

    /** フリー属性２ */
    private String freeAttribute02;

    /** フリー属性３ */
    private String freeAttribute03;

    /** フリー属性４ */
    private String freeAttribute04;

    /** フリー属性５ */
    private String freeAttribute05;

    /** フリー属性６ */
    private String freeAttribute06;

    /** フリー属性７ */
    private String freeAttribute07;

    /** フリー属性８ */
    private String freeAttribute08;

    /** フリー属性９ */
    private String freeAttribute09;

    /** フリー属性１０ */
    private String freeAttribute10;

    /** 選択不可フラグ(プルダウン表示対象にするかどうか) */
    private String selFlg;

    /** 変更可能フラグ */
    private String allowUpdateFlg;

}
