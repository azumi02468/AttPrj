package application.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * ユーザ情報DTO
 * @author 作成者氏名
 */
@Data
public class UserInfoDto implements Serializable {

    /** ユーザID */
    private String userId;

    /** ユーザ氏名 */
    private String name;

    /** メールアドレス */
    private String mail;

    /** 権限名 */
    private String authName;

    /** 組織名 */
    private String orgName;
}
