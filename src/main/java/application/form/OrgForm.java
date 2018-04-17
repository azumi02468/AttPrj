package application.form;

import java.io.Serializable;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import lombok.Data;

/**
 * 組織情報登録フォーム
 */
@Data
public class OrgForm implements Serializable {
	@NotEmpty
	@Pattern(regexp = "[0-9a-zA-Z]{2}")
    private String orgCd;
	@NotEmpty
	@Size(min=0, max=5)
    private String orgName;
    private String location;
	@NotNull
	@DecimalMin(value="0")
	@DecimalMax(value="99")
    private Integer dispSeq;
}
