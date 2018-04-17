package application.utils;

import org.springframework.util.StringUtils;

/**
 * SQL関連のユーティリティ。
 */
public class SqlUtil {

    /**
     * 非公開のコンストラクタ。
     */
    private SqlUtil() {
    }

    /**
     * 文字列中に含まれるSQL特殊文字を指定エスケープ文字でエスケープする。
     * @param str 文字列
     * @param escapeStr エスケープ文字
     * @return エスケープ後の文字列
     */
    public static String escape(String str, String escapeStr) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        return str.replace(escapeStr, escapeStr + escapeStr)
                .replace("%", escapeStr + "%")
                .replace("_", escapeStr + "_")
                .replace("'", "''");
    }
}
