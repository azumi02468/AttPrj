package application.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.oltu.oauth2.common.utils.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import application.service.AttendanceAlertService;
import application.service.LineAPIService;
import application.utils.CommonUtils;

/**
 * APIコントローラ。
 * @author 作成者氏名
 */
@RestController
@RequestMapping(value = "api/")
public class APIController {
    /** このクラスのロガー。 */
    private static final Logger logger = LoggerFactory.getLogger(APIController.class);

    /** レスポンス型定義：JSON。 */
    public static final String PRODUCES_JSON = "application/json; charset=UTF-8";

    /** アラート対象とする範囲(設定時刻から±ミリ秒)。 */
    public static final long ALERT_MAX_DIFF_MILLS = 6 * 60 * 1000; // 6分

    /** LINE API サービス。 */
    @Autowired
    private LineAPIService lineAPIService;

    /** 勤怠の「アラート」操作サービス。 */
    @Autowired
    private AttendanceAlertService attendanceAlertService;

    /**
     * LineのWebhook受信。
     * @param model モデル
     * @param req httpリクエスト
     * @return 受信結果
     */
    @RequestMapping(value = "line", method = { RequestMethod.POST }, produces = PRODUCES_JSON)
    @ResponseBody
    public String lineWebhook(Model model, HttpServletRequest req) {
        lineAPIService.requestLinePost(req);
        return "{status: \"OK\"}";
    }

    /**
     * 打刻漏れ防止アラートを発行する。<br>
     * 定期発行設定例 crontab -e<br>
     *  *／10 * * * * curl http://localhost:9000/api/alerts
     *  *／10 * * * * curl http://192.168.56.1:9000/api/alerts
     * @return 発行結果
     */
    @RequestMapping(value = "alerts", method = { RequestMethod.GET }, produces = PRODUCES_JSON)
    @ResponseBody
    public String alerts() {
        logger.debug("alerts()");
        Map<String, Object> res = new HashMap<>();
        // アラート範囲を特定
        long now = System.currentTimeMillis();
        Date beginTime = new Date(now - ALERT_MAX_DIFF_MILLS);
        Date endTime = new Date(now + ALERT_MAX_DIFF_MILLS);

        res.put("begin", CommonUtils.toHMm(beginTime));
        res.put("end", CommonUtils.toHMm(endTime));

        // アラート対象を送信
        int pushCnt = attendanceAlertService.pushAlerts(beginTime, endTime);
        // 結果戻し
        res.put("pushCount", pushCnt);
        return JSONUtils.buildJSON(res);
    }
}
