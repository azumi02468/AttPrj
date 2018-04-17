package application.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ユーザマスタエンティティ
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MUser extends AbstractEntity {

    /** ユーザID */
    private Integer userId;

    /** パスワード */
    private String password;

    /** ユーザ氏名 */
    private String name;

    /** メールアドレス(LINEアカウントと紐づけるメールアドレス ) */
    private String mail;

    /** 権限コード(汎用区分:1を参照) */
    private String authCd;

    /** 組織コード(組織マスタ.組織コードのFK) */
    private String orgCd;

    /** 上司ID */
    private Integer managerId;

    /** LINE識別子(LINEアカウントと紐づけた識別子) */
    private String lineId;

}
