package application.service;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import application.context.AppMesssageSource;
import application.dao.TAttendanceDao;
import application.emuns.AttenanceCd;
import application.entity.TAttendance;
import application.utils.CommonUtils;

/**
 * 勤怠情報「出勤・退勤」操作サービス。
 */
@Service
@Transactional
public class AttendanceInOutService extends AbstractAttendanceService {
    /** このクラスのロガー。 */
    private static final Logger logger = LoggerFactory.getLogger(AttendanceInOutService.class);

    /** 勤怠情報DAO。 */
    @Autowired
    private TAttendanceDao tAttendanceDao;

    /**
     * 出勤を保存する。
     * @param lineId LINE識別子
     * @param replyToken リプライTOKEN
     */
    public void putArrivalNow(String lineId, String replyToken) {
        logger.debug("putArrivalNow() {}, {}", lineId, replyToken);
        Integer userId = toUserId(lineId);
        String attendanceDay = CommonUtils.toYyyyMmDd();
        // 出勤済かチェック
        TAttendance entity = getTAttendance(userId, AttenanceCd.ARRIVAL.getCode(), attendanceDay);
        if (entity.getAttendanceTime() != null) {
            String msg = AppMesssageSource.getMessage("line.api.err.savedArrival");
            LineAPIService.repryMessage(replyToken, msg);
            return;
        }
        // 退勤済かチェック
        TAttendance clockOutEntity = getTAttendance(userId, AttenanceCd.CLOCK_OUT.getCode(), attendanceDay);
        if (clockOutEntity.getAttendanceTime() != null) {
            String msg = AppMesssageSource.getMessage("line.api.err.oldClockOut");
            LineAPIService.repryMessage(replyToken, msg);
            return;
        }

        // 保存
        Date attendanceTime = new Date();
        entity.setAttendanceTime(attendanceTime);
        tAttendanceDao.save(entity);

        // 完了メッセージ
        String msg = AppMesssageSource.getMessage("line.arrival", CommonUtils.toHMm(attendanceTime));
        LineAPIService.repryMessage(replyToken, msg);
    }

    /**
     * 退勤を保存する。
     * @param lineId LINE識別子
     * @param replyToken リプライTOKEN
     */
    public void putClockOutNow(String lineId, String replyToken) {
        Integer userId = toUserId(lineId);
        String attendanceDay = CommonUtils.toYyyyMmDd();
        // 退勤済かチェック
        TAttendance entity = getTAttendance(userId, AttenanceCd.CLOCK_OUT.getCode(), attendanceDay);
        if (entity.getAttendanceTime() != null) {
            String msg = AppMesssageSource.getMessage("line.api.err.savedClockOut");
            LineAPIService.repryMessage(replyToken, msg);
            return;
        }

        // 保存
        Date attendanceTime = new Date();
        entity.setAttendanceTime(attendanceTime);
        tAttendanceDao.save(entity);
        // 完了メッセージ
        StringBuilder msg = new StringBuilder();
        msg.append(AppMesssageSource.getMessage("line.clockOut", CommonUtils.toHMm(attendanceTime)));

        // 出勤忘れのお知らせ
        TAttendance arrivalEntity = getTAttendance(userId, AttenanceCd.ARRIVAL.getCode(), attendanceDay);
        if (arrivalEntity.getAttendanceTime() == null) {
            msg.append("\n");
            msg.append(AppMesssageSource.getMessage("line.api.warn.notFoundOpen"));
        }
        LineAPIService.repryMessage(replyToken, msg.toString());

    }

}
