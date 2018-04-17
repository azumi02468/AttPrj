package application.utils;

import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * ユーティリティメソッド群。
 */
public final class CommonUtils {

    /** ランダム生成。 */
    private static final SecureRandom RANDOM = new SecureRandom();

    /** タイムゾーン：日本。 */
    public static final TimeZone JST = TimeZone.getTimeZone("JST");

    /**
     * 非公開コンストラクタ。
     */
    private CommonUtils() {
    }

    /**
     * ランダム文字列を生成する。
     * @return ランダム文字列。
     */
    public static String getToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        return token;
    }

    /**
     * 空の場合、別値を返す。
     * @param str 検査文字列
     * @param emptyVal 空の場合の文字列
     * @return strそのもの。strがnullか空文字の場合emptyVal。
     */
    public static String emptyToVal(String str, String emptyVal) {
        if (StringUtils.isEmpty(str)) {
            return emptyVal;
        }
        return str;
    }

    /**
     * 本日をyyyyMMdd形式で取得する。
     * @return 変換後文字列
     */
    public static String toYyyyMmDd() {
        return toYyyyMmDd(new Date());
    }

    /**
     * 日時をyyyyMMdd形式で取得する。
     * @param date 日時
     * @return 変換後文字列
     */
    public static String toYyyyMmDd(Date date) {
        return DateFormatUtils.format(date, "yyyyMMdd", JST);
    }

    /**
     * 本日に最も近い年のyyyyMMdd形式を取得する。
     * @param mmdd 月日
     * @return 最近の年月日(yyyyMMdd)
     */
    public static String toYyyyMmDdByMmDd(String mmdd) {
        String res = null;
        Date now = new Date();
        String yyyy = DateFormatUtils.format(now, "yyyy", JST);
        Date date = parseDate(yyyy + mmdd, "yyyyMMdd");
        if (date != null) {
            // 未来日付は去年の年月に戻す
            if (now.getTime() < date.getTime()) {
                Calendar cal = Calendar.getInstance(JST);
                cal.setTime(date);
                cal.add(Calendar.YEAR, -1);
                date = cal.getTime();
            }
            res = DateFormatUtils.format(date, "yyyyMMdd");
        }
        return res;
    }

    /**
     * 日時をM/d H:mm形式で取得する。
     * @param datetime 変換する日時
     * @return 変換後文字列
     */
    public static String toMDhMm(Date datetime) {
        return DateFormatUtils.format(datetime, "M/d H:mm", JST);
    }

    /**
     * 日時をH:mm形式で取得する。
     * @param time 変換する時刻
     * @return 変換後文字列
     */
    public static String toHMm(Date time) {
        return DateFormatUtils.format(time, "H:mm", JST);
    }

    /**
     * 人が理解する年月をyyyyMM形式に変換する。
     * @param yearMonth 年月表現
     * @return yyyyMM形式。変換できない場合null.
     */
    public static String toYearMonth(String yearMonth) {
        String res = null;
        if (StringUtils.isEmpty(yearMonth)) {
            return res;
        }

        if (yearMonth.contains("/")) {
            res = parseDateText(yearMonth, "yyyy/M", "yyyyMM");
        } else if (yearMonth.contains("-")) {
            res = parseDateText(yearMonth, "yyyy-M", "yyyyMM");
        } else if (yearMonth.length() == 6) {
            res = parseDateText(yearMonth, "yyyyMM", "yyyyMM");
        }
        return res;
    }

    /**
     * 人が理解する月日をMMdd形式に変換する。
     * @param monthDate 月日表現
     * @return MMdd形式。変換できない場合null.
     */
    public static String toMonthDate(String monthDate) {
        String res = null;
        if (StringUtils.isEmpty(monthDate)) {
            return res;
        }

        if (monthDate.contains("/")) {
            res = parseDateText(monthDate, "M/d", "MMdd");
        } else if (monthDate.contains("-")) {
            res = parseDateText(monthDate, "M-d", "MMdd");
        } else if (monthDate.length() == 4) {
            res = parseDateText(monthDate, "MMdd", "MMdd");
        }
        return res;
    }

    /**
     * 人が理解する時分をHHmm形式に変換する。
     * @param hourMinute 時分表現
     * @return HHmm形式。変換できない場合null.
     */
    public static String toHourMinute(String hourMinute) {
        String res = null;
        if (StringUtils.isEmpty(hourMinute)) {
            return res;
        }

        if (hourMinute.contains("/")) {
            res = parseDateText(hourMinute, "H:m", "HHmm");
        } else if (hourMinute.length() == 4) {
            res = parseDateText(hourMinute, "HHmm", "HHmm");
        }
        return res;
    }

    /**
     * 日時表現の文字列を指定フォーマットに変換する。
     * @param source 変換元
     * @param fromFormat sourceのフォーマット
     * @param toFormat 変換先のフォーマット
     * @return 変換後文字列, 変換できない場合null
     */
    public static String parseDateText(String source, String fromPattern, String toPattern) {
        Date date = parseDate(source, fromPattern);
        String res = null;
        if (date != null) {
            res = DateFormatUtils.format(date, toPattern, JST);
        }
        return res;
    }

    /**
     * 日時表現の文字列をDateに変換する。
     * @param source 変換元
     * @param pattern sourceのフォーマット
     * @return 変換後Date
     */
    public static Date parseDate(String source, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setTimeZone(JST);
        sdf.setLenient(false);
        Date res = null;
        try {
            res = sdf.parse(source);
        } catch (ParseException e) {
        }
        return res;
    }

    /**
     * セパレータ文字列の指定要素をIntegerで取得する。
     * @param text セパレートされた文字列
     * @param seprator セパレータ
     * @param idx 取得するインデクス(0-N)
     * @return 取得した数値,取得できない場合null
     */
    public static Integer toIntegerSeprator(String text, String seprator, int idx) {
        Integer res = null;
        if (StringUtils.isEmpty(text)) {
            return res;
        }
        String[] arr = text.split(seprator);
        if (arr.length > idx) {
            try {
                res = Integer.parseInt(arr[idx], 10);
            } catch (NumberFormatException e) {
            }
        }
        return res;
    }

}
