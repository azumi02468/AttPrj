package application.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import application.context.AppMesssageSource;
import application.dao.TAttendanceDao;
import application.emuns.AttenanceCd;
import application.emuns.DelFlag;
import application.entity.TAttendance;
import application.entity.TLineStatus;
import application.utils.CommonUtils;

/**
 * 勤怠情報「修正」操作サービス。
 */
@Service
@Transactional
public class AttendanceRewritingService extends AbstractAttendanceService {
    /** このクラスのロガー。 */
    private static final Logger logger = LoggerFactory.getLogger(AttendanceRewritingService.class);

    /** 勤怠情報DAO。 */
    @Autowired
    private TAttendanceDao tAttendanceDao;

    /**
     * メニュー「修正」を開始する。
     * @param replyToken リプライTOKEN
     */
    public void startRewriting(String replyToken) {
        logger.debug("startRewriting() {}", replyToken);
        String msgRewriting = AppMesssageSource.getMessage("line.editMonthDate");
        LineAPIService.repryMessage(replyToken, msgRewriting);
    }

    /**
     * メニュー「修正」の後続操作を処理する。
     * @param lineId LINE識別子
     * @param replyToken リプライTOKEN
     * @param lineStatus 前回のLINE操作
     * @param text 入力内容
     */
    public void editAction(String lineId, String replyToken, TLineStatus lineStatus, String text) {
        String nextAction = null;
        String msg;
        switch (StringUtils.trimToEmpty(lineStatus.getActionName())) {
        case ACTION_OPEN_MENU:
            // 月日を特定
            String mmDd = CommonUtils.toMonthDate(text);
            if (mmDd == null) {
                msg = AppMesssageSource.getMessage("line.editMonthDate");
                LineAPIService.repryMessage(replyToken, msg);
                return;
            }
            // ボタン送信
            String title = AppMesssageSource.getMessage("line.selectAttendanceCd");
            List<String> buttons = new ArrayList<>();
            buttons.add(AttenanceCd.ARRIVAL.getName());
            buttons.add(AttenanceCd.CLOCK_OUT.getName());
            LineAPIService.pushButtons(lineId, title, buttons);
            lineStatus.setContents(mmDd);
            nextAction = ACTION_EDIT_TYPE_SELECTION;
            break;
        case ACTION_EDIT_TYPE_SELECTION:
            AttenanceCd cd = AttenanceCd.getByName(text);
            Integer userId = toUserId(lineId);
            String mmdd = lineStatus.getContents();
            String yyyymmdd = CommonUtils.toYyyyMmDdByMmDd(mmdd);
            if (cd == null) {
                break;
            }
            // 登録済勤怠を取得
            TAttendance attendance = tAttendanceDao.getByPk(userId, cd.getCode(), yyyymmdd);
            String mmddhhmm;
            if (attendance != null) {
                mmddhhmm = CommonUtils.toMDhMm(attendance.getAttendanceTime());
            } else {
                mmddhhmm = AppMesssageSource.getMessage("word.noneInput");
            }
            // メッセージ送信
            msg = AppMesssageSource.getMessage("line.currentAttendance", cd.getName(), mmddhhmm) + '\n'
                    + AppMesssageSource.getMessage("line.newAttendanceInput", cd.getName());
            LineAPIService.repryMessage(replyToken, msg);
            // 次のアクション指定
            lineStatus.setContents(yyyymmdd);
            if (cd == AttenanceCd.ARRIVAL) {
                nextAction = ACTION_EDIT_INPUT_TIME_ARRIVAL;
            } else {
                nextAction = ACTION_EDIT_INPUT_TIME_CLOCKOUT;
            }
            break;
        case ACTION_EDIT_INPUT_TIME_ARRIVAL:
            nextAction = saveAttendance(lineId, replyToken, lineStatus, text, AttenanceCd.ARRIVAL);
            break;
        case ACTION_EDIT_INPUT_TIME_CLOCKOUT:
            nextAction = saveAttendance(lineId, replyToken, lineStatus, text, AttenanceCd.CLOCK_OUT);
            break;
        }

        if (nextAction == null) {
            // 対象無し
            msg = AppMesssageSource.getMessage("line.selectMenu");
            LineAPIService.repryMessage(replyToken, msg);
        }

        // ステータス更新
        lineStatus.setActionName(nextAction);
        setLineSutatus(lineStatus);
    }

    /**
     * 勤怠情報を保存する。
     * @param lineId LINE識別子
     * @param replyToken リプライTOKEN
     * @param lineStatus 前回のLINE操作
     * @param text 入力内容
     * @param attenanceCd 勤怠区分
     * @return 次のアクションコード
     */
    private String saveAttendance(
            String lineId, String replyToken, TLineStatus lineStatus, String text, AttenanceCd attendanceCd) {
        String nextAction = null;
        String hhmm = CommonUtils.toHourMinute(text);
        Integer userId = toUserId(lineId);
        String yyyyMMdd = lineStatus.getContents();

        if (hhmm == null) {
            String msg = AppMesssageSource.getMessage("line.newAttendanceInput", attendanceCd.getName());
            LineAPIService.repryMessage(replyToken, msg);
            return lineStatus.getActionName();
        }

        // 勤怠取得
        TAttendance attendance = getTAttendance(userId, attendanceCd.getCode(), yyyyMMdd);
        // 勤怠保存
        attendance.setEditFlg(DelFlag.ON.getVal());
        attendance.setAttendanceTime(CommonUtils.parseDate(yyyyMMdd + hhmm, "yyyyMMddHHmm"));
        tAttendanceDao.save(attendance);
        // 保存通知
        String dateTime = CommonUtils.toMDhMm(attendance.getAttendanceTime());
        String msg = AppMesssageSource.getMessage("line.saveAttendance", attendanceCd.getName(), dateTime);
        LineAPIService.repryMessage(replyToken, msg);

        nextAction = ACTION_EDIT_DATE;
        return nextAction;
    }
}
