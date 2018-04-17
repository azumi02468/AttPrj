package application.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 組織マスタエンティティ
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MOrg extends AbstractEntity {

    /** 組織コード */
    private String orgCd;

    /** 組織名 */
    private String orgName;

    /** 拠点 */
    private String location;

    /** 表示順序 */
    private Integer dispSeq;

}
