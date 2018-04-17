package application.emuns;

/**
 * 勤怠区分コード。
 * @author 作成者氏名
 */
public enum AttenanceCd {
    ARRIVAL("01", "出勤"), //
    CLOCK_OUT("02", "退勤");

    /** 勤怠区分コード。 */
    private String code;
    /** 勤怠区分名。 */
    private String name;

    AttenanceCd(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return this.code;
    }

    /**
     * 勤怠区分名を取得する。
     * @return 勤怠区分名
     */
    public String getName() {
        return this.name;
    }

    /**
     * 勤怠区分コード文字列から勤怠区分コードを取得する。
     * @return 勤怠区分コード
     */
    public static AttenanceCd getByCode(String code) {
        for (AttenanceCd item : values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }

    /**
     * 勤怠区分名から勤怠区分コードを取得する。
     * @return 勤怠区分コード
     */
    public static AttenanceCd getByName(String name) {
        for (AttenanceCd item : values()) {
            if (item.name.equals(name)) {
                return item;
            }
        }
        return null;
    }
}
