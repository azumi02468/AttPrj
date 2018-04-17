package application.context;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

import application.entity.MUser;

/**
 * ログイン管理ユーザ
 */
public class AdminUser extends User {
  /** ユーザエンティティ.*/
  private MUser user;

  /**
   * コンストラクタ.
   * @param mUser ユーザエンティティ
   */
  public AdminUser(MUser mUser) {
      super(mUser.getUserId().toString(), mUser.getPassword(), AuthorityUtils.createAuthorityList("ROLE_ADMIN"));
      this.user = mUser;
  }

  /**
   * ユーザ情報を取得
   * @return ユーザ情報
   */
  public MUser getUser() {
      return this.user;
  }
}
