package application.form;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import lombok.Data;

/**
 * 管理者ログイン用Formクラス.
 */
@Data
public class LoginForm {
    /**
     * メールアドレス。
     */
    @NotBlank
    @Email
    private String mail;
}
