package application.emuns;

/**
 * メニューコード。
 * @author 作成者氏名
 */
public enum MenuCd {
    ARRIVAL("01", "Arrival"), // 出勤
    CLOCK_OUT("02", "Clock-out"), // 退勤
    REWRITING("03", "Rewriting"), // 修正
    LIST_OUTPUT("04", "ListOutput"); // リスト

    /** 汎用区分コード。 */
    private String divCd;

    /** LINEメニューコード。 */
    private String lineCode;

    /**
     * 唯一のコンストラクタ。
     * @param divCd 汎用区分コード
     * @param lineCode LINEメニューコード
     */
    MenuCd(String divCd, String lineCode) {
        this.divCd = divCd;
        this.lineCode = lineCode;
    }

    /**
     * 汎用区分コードを取得する。
     * @return 汎用区分コード
     */
    public String getDivCd() {
        return this.divCd;
    }

    /**
     * LINEメニューコードを取得する。
     * @return LINEメニューコード
     */
    public String getLineCode() {
        return this.lineCode;
    }

    /**
     * メニューコード(divCd)からenumを取得する。
     * @param menuCd メニューコード
     * @return MenuCd。 存在しない場合null
     */
    public static MenuCd get(String divCd) {
        for (MenuCd item : values()) {
            if (item.divCd.equals(divCd)) {
                return item;
            }
        }
        return null;
    }

    /**
     * LINEメニューコードからenumを取得する。
     * @param lineMenuCd LINEメニューコード
     * @return MenuCd。 存在しない場合null
     */
    public static MenuCd getByLineMenuCd(String lineMenuCd) {
        for (MenuCd item : values()) {
            if (item.lineCode.equals(lineMenuCd)) {
                return item;
            }
        }
        return null;
    }
}
