package application.service;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import application.dao.MUserDao;
import application.dao.TAttendanceDao;
import application.dao.TLineStatusDao;
import application.emuns.DelFlag;
import application.entity.MSetting;
import application.entity.MUser;
import application.entity.TAttendance;
import application.entity.TLineStatus;

/**
 * 勤怠情報サービスの親クラス。
 */
abstract public class AbstractAttendanceService {
    /** このクラスのロガー。 */
    private static final Logger logger = LoggerFactory.getLogger(AbstractAttendanceService.class);

    /** アクション定義：メニュー選択。 */
    public static final String ACTION_OPEN_MENU = "openMenu";

    /** アクション定義：修正 月日入力。 */
    public static final String ACTION_EDIT_DATE = "editDate";
    /** アクション定義：修正 出勤/退勤選択。 */
    public static final String ACTION_EDIT_TYPE_SELECTION = "editTypeSelection";
    /** アクション定義：修正 出勤時間入力。 */
    public static final String ACTION_EDIT_INPUT_TIME_ARRIVAL = "editInputTimeArrival";
    /** アクション定義：修正 退勤時間入力。 */
    public static final String ACTION_EDIT_INPUT_TIME_CLOCKOUT = "editInputTimeClockout";

    /** アクション定義：リスト ユーザ選択(SKIPのケースあり)。 */
    public static final String ACTION_LIST_USER_SELECTION = "listUserSelection";

    /** 勤怠情報DAO。 */
    @Autowired
    private TAttendanceDao tAttendanceDao;

    /** LINEステータス情報DAO。 */
    @Autowired
    private TLineStatusDao tLineStatusDao;

    /** ユーザマスタDAO。 */
    @Autowired
    private MUserDao mUserDao;

    /**
     * 勤怠情報を取得する。
     * @param userId ユーザID
     * @param attendancdCd 勤怠区分コード
     * @param attendanceDay 出勤日(yyyymmdd形式)
     * @return 勤怠情報。存在しない場合、初期値をセットした新規行
     */
    protected TAttendance getTAttendance(Integer userId, String attendancdCd, String attendanceDay) {
        TAttendance res = tAttendanceDao.getByPk(userId, attendancdCd, attendanceDay);
        if (res == null) {
            res = new TAttendance();
            res.setUserId(userId);
            res.setAttendanceCd(attendancdCd);
            res.setAttendanceDay(attendanceDay);
            res.setEditFlg(DelFlag.OFF.getVal());
        }
        return res;
    }

    /**
     * 営業曜日を取得する。
     * @param setting 設定
     * @return 営業曜日セット＜Calendarクラスの曜日＞
     */
    protected Set<Integer> getBusinessDay(MSetting setting) {
        Set<Integer> res = new HashSet<>();
        final String ON = DelFlag.ON.getVal();
        if (ON.equals(setting.getBusinessFlagSun())) {
            res.add(Calendar.SUNDAY);
        }
        if (ON.equals(setting.getBusinessFlagMon())) {
            res.add(Calendar.MONDAY);
        }
        if (ON.equals(setting.getBusinessFlagTue())) {
            res.add(Calendar.TUESDAY);
        }
        if (ON.equals(setting.getBusinessFlagWed())) {
            res.add(Calendar.WEDNESDAY);
        }
        if (ON.equals(setting.getBusinessFlagThu())) {
            res.add(Calendar.THURSDAY);
        }
        if (ON.equals(setting.getBusinessFlagFri())) {
            res.add(Calendar.FRIDAY);
        }
        if (ON.equals(setting.getBusinessFlagSat())) {
            res.add(Calendar.SATURDAY);
        }
        return res;
    }

    /**
     * LINE操作を保存する。
     * @param lineStatus LINEステータス
     */
    protected void setLineSutatus(TLineStatus lineStatus) {
        logger.debug("setLineSutatus() {}", lineStatus);
        lineStatus.setRequestTime(new Date());
        tLineStatusDao.save(lineStatus);
    }

    /**
     * LINE識別子をユーザIDに変換する。
     * @param lineId LINE識別子
     * @return ユーザID
     */
    protected Integer toUserId(String lineId) {
        MUser user = mUserDao.getByLineId(lineId);
        Integer res = null;
        if (user != null) {
            res = user.getUserId();
        }
        return res;
    }

}
