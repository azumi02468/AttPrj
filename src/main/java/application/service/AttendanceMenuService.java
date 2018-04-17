package application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;

import application.context.AppMesssageSource;
import application.dao.MUserDao;
import application.dao.TLineStatusDao;
import application.emuns.MenuCd;
import application.entity.MUser;
import application.entity.TLineStatus;

/**
 * 勤怠情報の総合メニューサービス。
 */
@Service
@Transactional
public class AttendanceMenuService extends AbstractAttendanceService {
    /** このクラスのロガー。 */
    private static final Logger logger = LoggerFactory.getLogger(AttendanceMenuService.class);

    /** LINEステータス情報DAO。 */
    @Autowired
    private TLineStatusDao tLineStatusDao;

    /** ユーザマスタDAO。 */
    @Autowired
    private MUserDao mUserDao;

    /** 「出勤・退勤」操作サービス。 */
    @Autowired
    private AttendanceInOutService attendanceInOutService;

    /** 「編集」操作サービス。 */
    @Autowired
    private AttendanceRewritingService attendanceRewritingService;

    /** 「リスト」操作サービス */
    @Autowired
    private AttendanceListService attendanceListService;

    /**
     * LINEメニュー操作を受け付ける。
     * @param menuCd メニュー
     * @return 区分明細情報リスト
     */
    public void requestMenu(MenuCd menuCd, MessageEvent<?> evt, TextMessageContent message) {
        logger.debug("requestMenu() {}", menuCd);
        String replyToken = evt.getReplyToken();
        String lineId = evt.getSource().getUserId();
        // ステータス設定
        TLineStatus lineStatus = getLineSutatus(lineId);
        lineStatus.setMenuCd(menuCd.getDivCd());
        lineStatus.setActionName(ACTION_OPEN_MENU);
        lineStatus.setContents(message.getText());
        setLineSutatus(lineStatus);

        switch (menuCd) {
        case ARRIVAL:
            // 出勤
            attendanceInOutService.putArrivalNow(lineId, replyToken);
            break;
        case CLOCK_OUT:
            // 退勤
            attendanceInOutService.putClockOutNow(lineId, replyToken);
            break;
        case REWRITING:
            // 修正
            attendanceRewritingService.startRewriting(replyToken);
            break;
        case LIST_OUTPUT:
            // リスト
            attendanceListService.startList(replyToken);
            break;
        }
    }

    /**
     * LINEからの文字列入力を受け付ける。
     * @param text 入力内容
     */
    public void requestText(String text, MessageEvent<?> evt, TextMessageContent message) {
        logger.debug("requestText() {}", text);
        String replyToken = evt.getReplyToken();
        String lineId = evt.getSource().getUserId();
        TLineStatus lineStatus = getLineSutatus(lineId);
        if (lineStatus.getUserId() == null) {
            //接続検証時はパス
            return;
        }

        MenuCd menuCd = MenuCd.get(lineStatus.getMenuCd());
        boolean isDefault = false;
        if (menuCd == null) {
            isDefault = true;
        } else {
            switch (menuCd) {
            case REWRITING:
                attendanceRewritingService.editAction(lineId, replyToken, lineStatus, text);
                break;
            case LIST_OUTPUT:
                attendanceListService.listAction(lineId, replyToken, lineStatus, text);
                break;
            default:
                isDefault = true;
            }
        }
        if (isDefault) {
            // ステータス設定
            lineStatus.setMenuCd("empty");
            lineStatus.setActionName(null);
            lineStatus.setContents(text);
            tLineStatusDao.save(lineStatus);

            // 対象なし
            String msg = AppMesssageSource.getMessage("line.selectMenu");
            LineAPIService.repryMessage(replyToken, msg);
        }

    }

    /**
     * 前回のLINE操作を取得する。
     * @param lineId 送信元LINE識別子
     * @return LINEステータス。存在しない場合、初期値をセットした新規行
     */
    private TLineStatus getLineSutatus(String lineId) {
        TLineStatus res = tLineStatusDao.getByPk(lineId);
        if (res == null) {
            res = new TLineStatus();
            res.setLineId(lineId);
            MUser user = mUserDao.getByLineId(lineId);
            if (user != null) {
                res.setUserId(user.getUserId());
            }
        }
        return res;
    }

}
