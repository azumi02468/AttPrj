package application.controller;

import java.util.Arrays;
import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.linecorp.bot.model.profile.UserProfileResponse;

import application.entity.MUser;
import application.form.LoginForm;
import application.service.LineAPIService;
import application.service.UserService;
import application.utils.CommonUtils;
import application.utils.LineUtils;
import application.webapi.line.AccessToken;
import application.webapi.line.IdToken;

/**
 * 一般ユーザ向け機能用 画面コントローラ。
 */
@Controller
@RequestMapping(value = "/user")
public class WebController {

    /** このクラスのロガー。 */
    private static final Logger logger = LoggerFactory.getLogger(WebController.class);

    /** アクセストークン。 */
    static final String ACCESS_TOKEN = "accessToken";
    /** 使い捨てキー。 */
    private static final String NONCE = "nonce";

    /** LINE@のID。 */
    @Value("${line.bot.lineAtId}")
    private String lineAtId;

    /** ユーザ情報操作サービス。 */
    @Autowired
    private UserService userService;

    /** LINE API サービス。 */
    @Autowired
    private LineAPIService lineAPIService;

    /** HTTPセッション。 */
    @Autowired
    private HttpSession httpSession;

    /**
     * ログイン画面を開く。
     * @param loginForm 入力値フォーム
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(LoginForm loginForm) {
        return "user/login";
    }

    /**
     * LINEログイン画面に遷移する。
     * @param loginForm 入力値フォーム
     * @param result エラー項目保持
     * @param model UIモデル
     * @return 遷移先指示
     */
    @RequestMapping(value = "/linelogin", method = RequestMethod.POST)
    public String goToAuthPage(@ModelAttribute(value = "loginForm") @Valid LoginForm loginForm, BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            return "user/login";
        }

        if (!userService.getUserByMail(loginForm.getMail()).isPresent()) {
            model.addAttribute("errorMessage", "メールアドレスが存在しません");
            return "user/login";
        }

        logger.debug("mail address :" + loginForm.getMail());

        final String state = loginForm.getMail();
        final String nonce = CommonUtils.getToken();
        final String url = lineAPIService.getLineWebLoginUrl(state, nonce, Arrays.asList("openid", "profile"));
        return "redirect:" + url;
    }

    /**
     * LINE認証結果受信。
     * @param code LINE認証結果のコード
     * @param state LINEログイン時に渡した隠しパラメータ
     * @param scope 権限
     * @param error エラー
     * @param errorCode エラーコード
     * @param errorMessage エラーメッセージ
     * @return 遷移先指示
     */
    @RequestMapping(value = "/auth", method = RequestMethod.GET)
    public String auth(@RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "scope", required = false) String scope,
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "errorCode", required = false) String errorCode,
            @RequestParam(value = "errorMessage", required = false) String errorMessage) {

        if (logger.isDebugEnabled()) {
            logger.debug("parameter code : " + code);
            logger.debug("parameter state : " + state);
            logger.debug("parameter scope : " + scope);
            logger.debug("parameter error : " + error);
            logger.debug("parameter errorCode : " + errorCode);
            logger.debug("parameter errorMessage : " + errorMessage);
        }

        // パラメータチェック
        if (error != null || errorCode != null || errorMessage != null) {
            return "redirect:/user/loginCancel";
        }

        logger.debug("【mail=state】:" + state);
        String mail = state;

        if (StringUtils.isEmpty(mail)) {
            return "redirect:/user/loginError";
        }

        Optional<MUser> userOpt = userService.getUserByMail(mail);
        if (!userOpt.isPresent()) {
            return "redirect:/user/loginError";
        }
        Integer userId = userOpt.get().getUserId();

        // LINEログイン情報取得
        AccessToken token = lineAPIService.accessToken(code);
        UserProfileResponse profile = LineUtils.getProfileByAccessToken(token.access_token);
        String lineId = profile.getUserId();
        logger.debug("【userId -> lineId】:{} -> {}", userId, lineId);

        if (logger.isDebugEnabled()) {
            logger.debug("scope : " + token.scope);
            logger.debug("access_token : " + token.access_token);
            logger.debug("token_type : " + token.token_type);
            logger.debug("expires_in : " + token.expires_in);
            logger.debug("refresh_token : " + token.refresh_token);
            logger.debug("id_token : " + token.id_token);
        }

        httpSession.setAttribute(ACCESS_TOKEN, token);
        logger.debug("access token: " + token);

        boolean isSuccess = userService.registerLineId(userId, lineId);
        if (isSuccess) {
            return "redirect:/user/success";
        }
        return "redirect:/user/line_unavailable";
    }

    /**
     * LINE認証成功の画面を開く。
     * @param model UIモデル。
     * @return 表示画面
     */
    @RequestMapping("/success")
    public String success(Model model) {
        logger.debug("【success start】:");
        AccessToken token = (AccessToken) httpSession.getAttribute(ACCESS_TOKEN);
        if (token == null) {
            logger.debug("【success A】:");
            return "redirect:/";
        }

        httpSession.removeAttribute(NONCE);
        IdToken idToken = lineAPIService.idToken(token.id_token);
        if (logger.isDebugEnabled()) {
            logger.debug("userId : " + idToken.sub);
            logger.debug("displayName : " + idToken.name);
            logger.debug("pictureUrl : " + idToken.picture);
        }
        model.addAttribute("idToken", idToken);
        model.addAttribute("lineAtId", lineAtId);

        logger.debug("【success C】:");
        return "user/add_friend";
    }

    /**
     * LINE認証画面のキャンセルを受信。
     * @return 表示画面
     */
    @RequestMapping("/loginCancel")
    public String loginCancel() {
        return "user/login_cancel";
    }

    /**
     * LINEアカウントを紐付けできない状況の受信。
     * @return 表示画面
     */
    @RequestMapping("/line_unavailable")
    public String lineUnavailable() {
        return "user/line_unavailable";
    }

    /**
     * セッション状況変化など認証できない状況の受信。
     * @return 表示画面
     */
    @RequestMapping("/sessionError")
    public String sessionError() {
        return "user/session_error";
    }

}
