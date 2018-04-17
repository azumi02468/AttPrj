package application.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.oltu.oauth2.common.utils.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.client.LineMessagingClientBuilder;
import com.linecorp.bot.client.LineSignatureValidator;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.action.Action;
import com.linecorp.bot.model.action.PostbackAction;
import com.linecorp.bot.model.event.CallbackRequest;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.MessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.template.ButtonsTemplate;
import com.linecorp.bot.model.response.BotApiResponse;
import com.linecorp.bot.servlet.LineBotCallbackException;
import com.linecorp.bot.servlet.LineBotCallbackRequestParser;

import application.controller.APIController;
import application.emuns.MenuCd;
import application.utils.ClientUtils;
import application.webapi.line.AccessToken;
import application.webapi.line.IdToken;
import application.webapi.line.LineAPI;
import application.webapi.line.Verify;
import retrofit2.Call;

/**
 * LINE API サービス。
 */
@Service
public class LineAPIService {
    /** このクラスのロガー。 */
    private static final Logger logger = LoggerFactory.getLogger(APIController.class);

    private static final String GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code";
    private static final String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";

    @Value("${line.linelogin.channelId}")
    private String channelId;
    @Value("${line.linelogin.channelSecret}")
    private String channelSecret;
    @Value("${line.linelogin.callbackUrl}")
    private String callbackUrl;

    private static String botChannelToken;

    @Value("${line.bot.channelToken}")
    private void setBotChannelToken(String aBotChannelToken) {
        LineAPIService.botChannelToken = aBotChannelToken;
    }

    private static String botChannelSecret;

    @Value("${line.bot.channelSecret}")
    private void setBotChannelSecret(String aBotChannelSecret) {
        LineAPIService.botChannelSecret = aBotChannelSecret;
    }

    @Autowired
    private AttendanceMenuService attendanceMenuService;

    public AccessToken accessToken(String code) {
        return getClient(t -> t.accessToken(
                GRANT_TYPE_AUTHORIZATION_CODE,
                channelId,
                channelSecret,
                callbackUrl,
                code));
    }

    public AccessToken refreshToken(final AccessToken accessToken) {
        return getClient(t -> t.refreshToken(
                GRANT_TYPE_REFRESH_TOKEN,
                accessToken.refresh_token,
                channelId,
                channelSecret));
    }

    public Verify verify(final AccessToken accessToken) {
        return getClient(t -> t.verify(
                accessToken.access_token));
    }

    public void revoke(final AccessToken accessToken) {
        getClient(t -> t.revoke(
                accessToken.access_token,
                channelId,
                channelSecret));
    }

    public IdToken idToken(String id_token) {
        try {
            DecodedJWT jwt = JWT.decode(id_token);
            return new IdToken(
                    jwt.getClaim("iss").asString(),
                    jwt.getClaim("sub").asString(),
                    jwt.getClaim("aud").asString(),
                    jwt.getClaim("ext").asLong(),
                    jwt.getClaim("iat").asLong(),
                    jwt.getClaim("nonce").asString(),
                    jwt.getClaim("name").asString(),
                    jwt.getClaim("picture").asString());
        } catch (JWTDecodeException e) {
            throw new RuntimeException(e);
        }
    }

    public String getLineWebLoginUrl(String state, String nonce, List<String> scopes) {
        final String encodedCallbackUrl;
        final String scope = String.join("%20", scopes);

        try {
            encodedCallbackUrl = URLEncoder.encode(callbackUrl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        return "https://access.line.me/oauth2/v2.1/authorize?response_type=code"
                + "&client_id=" + channelId
                + "&redirect_uri=" + encodedCallbackUrl
                + "&state=" + state
                + "&scope=" + scope
                + "&nonce=" + nonce;
    }

    public boolean verifyIdToken(String id_token, String nonce) {
        try {
            JWT.require(
                    Algorithm.HMAC256(channelSecret))
                    .withIssuer("https://access.line.me")
                    .withAudience(channelId)
                    .withClaim("nonce", nonce)
                    .build()
                    .verify(id_token);
            return true;
        } catch (UnsupportedEncodingException e) {
            // UTF-8 encoding not supported
            return false;
        } catch (JWTVerificationException e) {
            // Invalid signature/claims
            return false;
        }
    }

    /**
     * LINEからのPOSTリクエストを処理する。
     * @param req httpリクエスト
     */
    public void requestLinePost(HttpServletRequest req) {
        try {
            LineSignatureValidator vlidator = new LineSignatureValidator(botChannelSecret.getBytes());
            LineBotCallbackRequestParser parser = new LineBotCallbackRequestParser(vlidator);
            CallbackRequest cbRequest = parser.handle(req);
            requestEventList(cbRequest.getEvents());
        } catch (IOException | LineBotCallbackException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * LINEイベントを処理する。
     * @param events LINEイベントリスト
     */
    public void requestEventList(List<Event> events) {
        for (Event event : events) {
            // 受信内容処理
            if (event instanceof MessageEvent) {
                requestEvent((MessageEvent<?>) event);
            } else {
                logger.warn("未知のLINEイベント:" + event);
            }
        }
    }

    /**
     * (メッセージ)LINEイベントを１件処理する。
     * @param evt LINEイベント
     */
    public void requestEvent(MessageEvent<?> evt) {
        MessageContent content = evt.getMessage();
        if (content instanceof TextMessageContent) {
            requestEvent(evt, (TextMessageContent) content);
        } else {
            logger.warn("未知のLINEメッセージイベント:" + evt);
        }
    }

    /**
     * 「String」のLINEメッセージイベントを処理する。
     * @param evt その他のイベント
     * @param message メッセージ内容
     */
    public void requestEvent(MessageEvent<?> evt, TextMessageContent message) {
        String text = message.getText();
        logger.debug("requestEvent(MessageEvent, TextMessageContent) message={}", text);

        // メニュー操作判定
        MenuCd menuCd = MenuCd.getByLineMenuCd(text);
        if (menuCd != null) {
            attendanceMenuService.requestMenu(menuCd, evt, message);
        } else {
            // 文字列操作
            attendanceMenuService.requestText(text, evt, message);
        }
    }

    /**
     * リプライでテキストを送信する。
     * @param replyToken リプライトークン
     * @param msg メッセージ
     * @return LINEのレスポンス
     */
    public static BotApiResponse repryMessage(String replyToken, String msg) {
        try {
            TextMessage textMsg = new TextMessage(msg);
            ReplyMessage replyMessage = new ReplyMessage(replyToken, textMsg);
            LineMessagingClient line = new LineMessagingClientBuilder(botChannelToken).build();
            BotApiResponse lineResponse = line.replyMessage(replyMessage).get();

            logger.debug("LINE Response=[" + lineResponse.toString() + "]");
            return lineResponse;
        } catch (InterruptedException | ExecutionException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * プッシュでテキストを送信する。
     * @param lineId 送信先LINE識別子
     * @param msg メッセージ
     * @return LINEのレスポンス
     */
    public static BotApiResponse pushMessage(String lineId, String msg) {
        // メッセージ生成
        TextMessage textMsg = new TextMessage(msg);
        PushMessage message = new PushMessage(lineId, textMsg);
        // 送信
        return pushMessage(message);
    }

    /**
     * プッシュでメッセージを送信する。
     * @param message メッセージ
     * @return LINEのレスポンス
     */
    public static BotApiResponse pushMessage(PushMessage message) {
        try {
            LineMessagingClient line = new LineMessagingClientBuilder(botChannelToken).build();
            BotApiResponse res = line.pushMessage(message).get();
            return res;
        } catch (InterruptedException | ExecutionException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * プッシュで複数の選択肢を送信する。
     * @param lineId 送信先のLINE識別子
     * @param templateTitle タイトル
     * @param buttonNames ボタン名(最大4個,1個あたり20文字まで)
     * @return LINEレスポンス
     */
    public static BotApiResponse pushButtons(String lineId, String templateTitle, List<String> buttonNames) {
        //最大長分割
        List<String> thisButtons = buttonNames;
        List<String> nextButtons = null;
        if (buttonNames.size() > 4) {
            thisButtons = buttonNames.subList(0, 4);
            nextButtons = buttonNames.subList(4, buttonNames.size());
        }

        String thumbnailImageUrl = null; // null可、画像URL(https必須, jpg/png, 縦:横=1:1.51, 最大横1024px, 最大1MB)
        String title = null; // null可、太文字部 最大40文字
        String templateText = templateTitle; // 必須, 内容文字列, 最大60文字(title無しの場合160文字まで可)

        // ボタン(アクション)定義
        ArrayList<Action> actionList = new ArrayList<>(); // 最大4アクション
        int btnIndex = 0;
        for (String buttonLabel : thisButtons) {
            if (actionList.size() >= 4) {
                break;
            }
            // 最大長にカット
            buttonLabel = StringUtils.truncate(buttonLabel, 20);
            // アクション定義
            // ボタンタップで送信する内容
            Map<String, Object> backMap = new HashMap<>();
            backMap.put("id", btnIndex);
            backMap.put("label", buttonLabel);
            // STATE管理していない場合、backDataにキー情報をすべて含める必要がある
            String backData = JSONUtils.buildJSON(backMap); // 必須、ポストバックデータ(最大300文字)
            // STATE管理している場合、backTextを使うと手入力と同じフローで進められる
            String backText = buttonLabel; // null可、LINEユーザからBOTへの送信テキスト(最大300文字)
            actionList.add(new PostbackAction(buttonLabel, backData, backText));
            btnIndex++;
        }

        // メッセージ成形
        ButtonsTemplate buttonsTemplate = new ButtonsTemplate(thumbnailImageUrl, title, templateText, actionList);
        TemplateMessage templateMsg = new TemplateMessage(templateText, buttonsTemplate);
        PushMessage message = new PushMessage(lineId, templateMsg);
        // 送信
        BotApiResponse res = pushMessage(message);

        if (nextButtons != null) {
            // 続きのボタンを送信
            pushButtons(lineId, templateTitle, nextButtons);
        }
        return res;
    }

    private <R> R getClient(final Function<LineAPI, Call<R>> function) {
        return ClientUtils.getClient("https://api.line.me/", LineAPI.class, function);
    }

}
