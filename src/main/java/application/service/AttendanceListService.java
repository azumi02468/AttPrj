package application.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import application.context.AppMesssageSource;
import application.dao.MSettingDao;
import application.dao.MUserDao;
import application.dao.TAttendanceDao;
import application.emuns.AttenanceCd;
import application.emuns.AuthCd;
import application.emuns.DelFlag;
import application.entity.MSetting;
import application.entity.MUser;
import application.entity.TAttendance;
import application.entity.TLineStatus;
import application.utils.CommonUtils;

/**
 * 勤怠情報「リスト」操作サービス。
 */
@Service
@Transactional
public class AttendanceListService extends AbstractAttendanceService {
    /** このクラスのロガー。 */
    private static final Logger logger = LoggerFactory.getLogger(AttendanceListService.class);

    /** 勤怠情報DAO。 */
    @Autowired
    private TAttendanceDao tAttendanceDao;

    /** ユーザマスタDAO。 */
    @Autowired
    private MUserDao mUserDao;

    /** 設定マスタDAO。 */
    @Autowired
    private MSettingDao mSettingDao;

    /**
     * メニュー「リスト」を開始する。
     * @param replyToken リプライTOKEN
     */
    public void startList(String replyToken) {
        logger.debug("startList() {}", replyToken);
        String msgOutp0ut = AppMesssageSource.getMessage("line.listYearMonth");
        LineAPIService.repryMessage(replyToken, msgOutp0ut);
    }

    /**
     * メニュー「リスト」を処理する。
     * @param lineId リクエスト送信者のLINE識別子
     * @param replyToken リプライTOKEN
     * @param lineStatus 前回のLINE操作
     * @param text 入力内容
     */
    public void listAction(String lineId, String replyToken, TLineStatus lineStatus, String text) {
        String nextAction = null;
        String yyyyMm = null;
        MUser user = mUserDao.getByLineId(lineId);
        String msg;
        switch (StringUtils.trimToEmpty(lineStatus.getActionName())) {
        case ACTION_OPEN_MENU:
            // 年月を特定
            yyyyMm = CommonUtils.toYearMonth(text);
            lineStatus.setContents(yyyyMm);
            if (yyyyMm == null) {
                msg = AppMesssageSource.getMessage("line.listYearMonth");
                LineAPIService.repryMessage(replyToken, msg);
                return;
            }
            if (AuthCd.ADMIN.getCode().equals(user.getAuthCd())) {
                // 全社員の選択肢を送信
                nextAction = pushUserSelectionForAdmin(lineId, yyyyMm);
                if (nextAction == null) {
                    // 選択肢が存在しない場合、自分を出力
                    replyAttendanceList(replyToken, yyyyMm, user.getUserId());
                    nextAction = ACTION_OPEN_MENU;
                }
            } else if (AuthCd.MANAGER.getCode().equals(user.getAuthCd())) {
                // 管理下メンバーのみ
                nextAction = pushUserSelectionForManager(lineId, yyyyMm);
                if (nextAction == null) {
                    // 選択肢が存在しない場合、自分を出力
                    replyAttendanceList(replyToken, yyyyMm, user.getUserId());
                    nextAction = ACTION_OPEN_MENU;
                }
            } else {
                // 管理下メンバーなし(自分のみ)
                replyAttendanceList(replyToken, yyyyMm, user.getUserId());
                // 再入力を許容
                nextAction = ACTION_OPEN_MENU;
            }

            break;
        case ACTION_LIST_USER_SELECTION:
            // 選択したユーザの勤怠を表示
            yyyyMm = lineStatus.getContents();
            Integer targetUserId = CommonUtils.toIntegerSeprator(text, " ", 0);
            if (targetUserId != null) {
                replyAttendanceList(replyToken, yyyyMm, targetUserId);
                // 再選択を許容する
                nextAction = ACTION_LIST_USER_SELECTION;
            } else {
                // 不良操作
                nextAction = null;
            }
            break;
        default:
            // 対象無し
            msg = AppMesssageSource.getMessage("line.selectMenu");
            LineAPIService.repryMessage(replyToken, msg);
            break;
        }
        // ステータス更新
        lineStatus.setActionName(nextAction);
        setLineSutatus(lineStatus);
    }

    /**
     * １ユーザの勤怠リストをリプライで出力する。
     * @param yyyyMm 対象年月
     * @param userId ユーザID
     */
    private void replyAttendanceList(String replyToken, String yyyyMm, Integer userId) {
        // 管理下メンバーなし(自分のみ)
        String list = getList(userId, yyyyMm);
        LineAPIService.repryMessage(replyToken, list);
    }

    /**
     * 1ヵ月分の勤怠情報を取得する。
     * @param userId ユーザID
     * @param yyyyMm 取得対象年月
     * @return 1ヵ月分の勤怠情報
     */
    private String getList(Integer userId, String yyyyMm) {
        StringBuilder res = new StringBuilder();
        final String ON = DelFlag.ON.getVal();
        // 保存値取得
        MUser user = mUserDao.getByPk(userId);
        if (user == null) {
            return AppMesssageSource.getMessage("line.api.err.notFoundAttendance");
        }

        MSetting setting = mSettingDao.get();
        List<TAttendance> list = tAttendanceDao.selectByMonth(userId, yyyyMm);

        // 日付順のセット＜出勤日＞
        Set<String> dateKeySet = new TreeSet<>();
        // 出勤マップ＜出勤日, 勤怠行＞
        Map<String, TAttendance> openMap = new HashMap<>();
        // 退勤マップ＜出勤日, 勤怠行＞
        Map<String, TAttendance> closeMap = new HashMap<>();
        // 特記マップ＜出勤日, 特記(遅刻早退など)＞
        Map<String, String> optionMap = new HashMap<>();
        // 各マップに集計
        for (TAttendance row : list) {
            String key = row.getAttendanceDay();
            dateKeySet.add(key);
            if (AttenanceCd.ARRIVAL.getCode().equals(row.getAttendanceCd())) {
                openMap.put(key, row);
                String option = getAttendanceOption(setting, row) + optionMap.getOrDefault(key, "");
                optionMap.put(key, option);
            } else {
                closeMap.put(key, row);
                String option = optionMap.getOrDefault(key, "") + getAttendanceOption(setting, row);
                optionMap.put(key, option);
            }
        }

        // タイトル
        res.append(user.getName()).append(' ');
        res.append(CommonUtils.parseDateText(yyyyMm, "yyyyMM", "yyyy年M月"));
        res.append('\n');
        // 表示形式に変換
        for (String attendanceDay : dateKeySet) {
            String date = CommonUtils.parseDateText(attendanceDay, "yyyyMMdd", "M/d");
            // 日付
            res.append(date).append(' ');
            TAttendance open = openMap.get(attendanceDay);
            if (open != null) {
                String openHhMm = CommonUtils.toHMm(open.getAttendanceTime());
                // 出勤日時
                res.append(openHhMm);
                if (ON.equals(open.getEditFlg())) {
                    res.append(AppMesssageSource.getMessage("mark.edit"));
                }
            }
            res.append('～');
            TAttendance close = closeMap.get(attendanceDay);
            if (close != null) {
                String closeHhMm = CommonUtils.toHMm(close.getAttendanceTime());
                // 退勤日時
                res.append(closeHhMm);
                if (ON.equals(close.getEditFlg())) {
                    res.append(AppMesssageSource.getMessage("mark.edit"));
                }
            }
            String option = optionMap.get(attendanceDay);
            if (StringUtils.isNotEmpty(option)) {
                res.append(' ').append(option);
            }

            // LINEの改行
            res.append('\n');
        }
        if (dateKeySet.isEmpty()) {
            res.append(AppMesssageSource.getMessage("line.api.err.notFoundAttendance"));
        }

        return res.toString();
    }

    /**
     * 特記事項を取得する。
     * @param setting 設定
     * @param attendance 勤怠
     * @return 特記事項(早退,遅刻,休出)
     */
    private String getAttendanceOption(MSetting setting, TAttendance attendance) {
        StringBuilder res = new StringBuilder();

        Set<Integer> businessDay = getBusinessDay(setting);
        Calendar datetime = Calendar.getInstance(CommonUtils.JST);
        datetime.setTime(attendance.getAttendanceTime());
        Integer day = datetime.get(Calendar.DAY_OF_WEEK);
        boolean isBusinessDay = businessDay.contains(day);

        if (AttenanceCd.ARRIVAL.getCode().equals(attendance.getAttendanceCd())) {
            // 出勤
            if (isBusinessDay) {
                String openStr = attendance.getAttendanceDay() + setting.getOpenTime() + setting.getOpenMinutes();
                Date openDatetime = CommonUtils.parseDate(openStr, "yyyyMMddHHmm");
                if (openDatetime.getTime() < attendance.getAttendanceTime().getTime()) {
                    // 遅刻
                    res.append(AppMesssageSource.getMessage("word.lateStart"));
                }
            } else {
                // 休出
                res.append(AppMesssageSource.getMessage("word.holidayWork"));
            }
        } else {
            // 退勤
            if (isBusinessDay) {
                String closeStr = attendance.getAttendanceDay() + setting.getCloseTime() + setting.getCloseMinutes();
                Date closeDatetime = CommonUtils.parseDate(closeStr, "yyyyMMddHHmm");
                if (attendance.getAttendanceTime().getTime() < closeDatetime.getTime()) {
                    // 早退
                    res.append(AppMesssageSource.getMessage("word.leaveEarly"));
                }
            }
        }
        return res.toString();
    }

    /**
     * 管理者として選択可能なユーザの選択肢をプッシュする。<br>
     * ユーザが存在しない場合、自分自身のリストを出力する。
     * @param lineId LINE識別子
     * @param yyyyMm 対象の年月
     * @return 実施アクション名, 選択肢が存在しない場合null
     */
    private String pushUserSelectionForAdmin(String lineId, String yyyyMm) {
        List<MUser> userList = mUserDao.findAdminMembers(lineId, yyyyMm);
        String nextAction = null;
        if (userList.size() >= 2) {
            List<String> buttons = new ArrayList<>();
            for (MUser user : userList) {
                buttons.add(user.getUserId() + " " + user.getName());
            }
            String title = AppMesssageSource.getMessage("line.selectUserByList");
            LineAPIService.pushButtons(lineId, title, buttons);
            nextAction = ACTION_LIST_USER_SELECTION;
        }
        return nextAction;
    }

    /**
     * 上長として選択可能なユーザの選択肢をプッシュする。<br>
     * ユーザが存在しない場合、自分自身のリストを出力する。
     * @param lineId LINE識別子
     * @param yyyyMm 対象の年月
     * @param user 上長
     * @return 実施アクション名, 選択肢が存在しない場合null
     */
    private String pushUserSelectionForManager(String lineId, String yyyyMm) {
        List<MUser> userList = mUserDao.findManagerMembers(lineId, yyyyMm);
        String nextAction = null;
        if (userList.size() >= 2) {
            List<String> buttons = new ArrayList<>();
            for (MUser user : userList) {
                buttons.add(user.getUserId() + " " + user.getName());
            }
            String title = AppMesssageSource.getMessage("line.selectUserByList");
            LineAPIService.pushButtons(lineId, title, buttons);
            nextAction = ACTION_LIST_USER_SELECTION;
        }
        return nextAction;
    }

}
