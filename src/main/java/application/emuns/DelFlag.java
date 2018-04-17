package application.emuns;

/**
 * 論理削除フラグ
 *
 * @author 作成者氏名
 *
 */
public enum DelFlag {
    OFF("0"), ON("1");

    private String val;

    DelFlag(String val) {
        this.val = val;
    }

    public String getVal() {
        return this.val;
    }

}
