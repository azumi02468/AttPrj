package application.service;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import application.context.AppMesssageSource;
import application.dao.MSettingDao;
import application.dao.MUserDao;
import application.emuns.AttenanceCd;
import application.emuns.DelFlag;
import application.entity.MSetting;
import application.entity.MUser;
import application.utils.CommonUtils;

/**
 * 勤怠の「アラート」操作サービス。
 */
@Service
@Transactional
public class AttendanceAlertService extends AbstractAttendanceService {
    /** このクラスのロガー。 */
    private static final Logger logger = LoggerFactory.getLogger(AttendanceAlertService.class);

    /** 送信済アラートの時刻(最近の数件のみ保持)。 */
    public static final LinkedList<Date> ALERT_SEND_TIME = new LinkedList<>();

    /** ユーザマスタDAO。 */
    @Autowired
    private MUserDao mUserDao;

    /** 設定マスタDAO。 */
    @Autowired
    private MSettingDao mSettingDao;

    /**
     * アラートを対象者に送信する。
     * @param beginTime 適合開始時刻
     * @param endTime 適合終了時刻
     * @return 送信数
     */
    public int pushAlerts(Date beginTime, Date endTime) {
        logger.debug("pushAlerts() {}, {}", beginTime, endTime);
        int res = 0;
        // 設定値取得
        String yyyyMMdd = CommonUtils.toYyyyMmDd();
        MSetting setting = mSettingDao.get();
        final String OFF = DelFlag.OFF.getVal();

        if (OFF.equals(setting.getAlertFlag())) {
            // アラート無効
            return res;
        }

        // 出勤アラート
        String openDateText = yyyyMMdd + setting.getAlertOpenTime() + setting.getAlertOpenMinutes();
        Date openDate = CommonUtils.parseDate(openDateText, "yyyyMMddHHmm");
        boolean doneOpenAlert = ALERT_SEND_TIME.contains(openDate);
        if (!doneOpenAlert && beginTime.compareTo(openDate) <= 0 && openDate.compareTo(endTime) <= 0) {
            res += pushOpenAlert(openDate);
            ALERT_SEND_TIME.add(openDate);
        }

        // 退勤アラート
        String closeDateText = yyyyMMdd + setting.getAlertCloseTime() + setting.getAlertCloseMinutes();
        Date closeDate = CommonUtils.parseDate(closeDateText, "yyyyMMddHHmm");
        boolean doneCloseAlert = ALERT_SEND_TIME.contains(closeDate);
        if (!doneCloseAlert && beginTime.compareTo(closeDate) <= 0 && closeDate.compareTo(endTime) <= 0) {
            res += pushCloseAlert(closeDate);
            ALERT_SEND_TIME.add(closeDate);
        }

        // 古い送信履歴を排除
        if (ALERT_SEND_TIME.size() > 2) {
            ALERT_SEND_TIME.removeFirst();
        }

        return res;
    }

    /**
     * 出勤時刻アラートを対象者に送信する。
     * @param openDate 出勤アラート日時
     * @return 送信数
     */
    public int pushOpenAlert(Date openDate) {
        return pushAlertByAttendance(openDate, AttenanceCd.ARRIVAL);
    }

    /**
     * 退勤時刻アラートを対象者に送信する。
     * @param openDate 退勤アラート日時
     * @return 送信数
     */
    public int pushCloseAlert(Date closeDate) {
        return pushAlertByAttendance(closeDate, AttenanceCd.CLOCK_OUT);
    }

    /**
     * アラートを対象者に送信する。
     * @param  cd 勤怠区分コード
     * @return 送信数
     */
    private int pushAlertByAttendance(Date alertDate, AttenanceCd attendanceCd) {
        int res = 0;
        // 対象者検索
        String yyyyMMdd = CommonUtils.toYyyyMmDd(alertDate);
        List<MUser> userList = mUserDao.findNoneAttendance(yyyyMMdd, attendanceCd.getCode());
        String msg = AppMesssageSource.getMessage("line.alertNotFoundAttendance", attendanceCd.getName());

        // 送信
        for (MUser user : userList) {
            String lineId = user.getLineId();
            LineAPIService.pushMessage(lineId, msg);
            res++;
        }
        return res;
    }
}
